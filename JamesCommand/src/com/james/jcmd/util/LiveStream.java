package com.james.jcmd.util;


import java.awt.Dimension;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import java.io.IOException;

public class LiveStream implements PushBufferStream, Runnable {

    protected ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);
    protected int maxDataLength;
    protected byte[] data;
    protected Dimension size;
    protected RGBFormat rgbFormat;
    protected AudioFormat audioFormat;
    protected boolean started;
    protected Thread thread;
    protected float frameRate = 20f;
    protected BufferTransferHandler transferHandler;
    protected Control[] controls = new Control[0];

    protected boolean videoData = true;
    
    public LiveStream() {
	if (videoData) {
	    int x, y, pos, revpos;
	    
	    size = new Dimension(320, 240);
	    maxDataLength = size.width * size.height * 3;
	    rgbFormat = new RGBFormat(size, maxDataLength,
				      Format.byteArray,
				      frameRate,
				      24,
				      3, 2, 1,
				      3, size.width * 3,
				      VideoFormat.FALSE,
				      Format.NOT_SPECIFIED);
	    
	    // generate the data
	    data = new byte[maxDataLength];
	    pos = 0;
	    revpos = (size.height - 1) * size.width * 3;
	    for (y = 0; y < size.height / 2; y++) {
		for (x = 0; x < size.width; x++) {
		    byte value = (byte) ((y*2) & 0xFF);
		    data[pos++] = value;
		    data[pos++] = 0;
		    data[pos++] = 0;
		    data[revpos++] = value;
		    data[revpos++] = 0;
		    data[revpos++] = 0;
		}
		revpos -= size.width * 6;
	    }
	} else { // audio data
	    audioFormat = new AudioFormat(AudioFormat.LINEAR,
					  8000.0,
					  8,
					  1,
					  Format.NOT_SPECIFIED,
					  AudioFormat.SIGNED,
					  8,
					  Format.NOT_SPECIFIED,
					  Format.byteArray);
	    maxDataLength = 1000;
	}

	thread = new Thread(this);
    }

    /***************************************************************************
     * SourceStream
     ***************************************************************************/
    
    public ContentDescriptor getContentDescriptor() {
	return cd;
    }

    public long getContentLength() {
	return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
	return false;
    }

    /***************************************************************************
     * PushBufferStream
     ***************************************************************************/

    int seqNo = 0;
    double freq = 2.0;
    
    public Format getFormat() {
	if (videoData)
	    return rgbFormat;
	else
	    return audioFormat;
    }

    public void read(Buffer buffer) throws IOException {
	synchronized (this) {
	    Object outdata = buffer.getData();
	    if (outdata == null || !(outdata.getClass() == Format.byteArray) ||
		((byte[])outdata).length < maxDataLength) {
		outdata = new byte[maxDataLength];
		buffer.setData(outdata);
	    }
	    if (videoData) {
		buffer.setFormat( rgbFormat );
		buffer.setTimeStamp( (long) (seqNo * (1000 / frameRate) * 1000000) );
		int lineNo = (seqNo * 2) % size.height;
		int chunkStart = lineNo * size.width * 3;
		System.arraycopy(data, chunkStart,
				 outdata, 0,
				 maxDataLength - (chunkStart));
		if (chunkStart != 0) {
		    System.arraycopy(data, 0,
				     outdata, maxDataLength - chunkStart,
				     chunkStart);
		}
	    } else {
		buffer.setFormat( audioFormat );
		buffer.setTimeStamp( 1000000000 / 8 );
		for (int i = 0; i < 1000; i++) {
		    ((byte[])outdata)[i] = (byte) (Math.sin(i / freq) * 32);
		    freq = (freq + 0.01);
		    if (freq > 10.0)
			freq = 2.0;
		}
	    }
	    buffer.setSequenceNumber( seqNo );
	    buffer.setLength(maxDataLength);
	    buffer.setFlags(0);
	    buffer.setHeader( null );
	    seqNo++;
	}
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
	synchronized (this) {
	    this.transferHandler = transferHandler;
	    notifyAll();
	}
    }

    void start(boolean started) {
	synchronized ( this ) {
	    this.started = started;
	    if (started && !thread.isAlive()) {
		thread = new Thread(this);
		thread.start();
	    }
	    notifyAll();
	}
    }

    /***************************************************************************
     * Runnable
     ***************************************************************************/

    public void run() {
	while (started) {
	    synchronized (this) {
		while (transferHandler == null && started) {
		    try {
			wait(1000);
		    } catch (InterruptedException ie) {
		    }
		} // while
	    }

	    if (started && transferHandler != null) {
		transferHandler.transferData(this);
		try {
		    Thread.currentThread().sleep( 10 );
		} catch (InterruptedException ise) {
		}
	    }
	} // while (started)
    } // run

    // Controls
    
    public Object [] getControls() {
	return controls;
    }

    public Object getControl(String controlType) {
       try {
          Class  cls = Class.forName(controlType);
          Object cs[] = getControls();
          for (int i = 0; i < cs.length; i++) {
             if (cls.isInstance(cs[i]))
                return cs[i];
          }
          return null;

       } catch (Exception e) {   // no such controlType or such control
         return null;
       }
    }
}