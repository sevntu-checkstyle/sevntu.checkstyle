package com.blabla;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;

public class StreamingLogger extends java.io.OutputStream
{

    private final Log log;

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private String enc;

    public StreamingLogger(Log log, String enc)
    {
        this.log = log;
        this.enc = enc;
    }

    public StreamingLogger(Log log)
    {
        this(log, null);
    }

    @Override
    public void write(int b)
            throws IOException
    {
        if (b == '\r')
        {
            // ignore
        }
        else if (b == '\n')
        {
            if (buffer.size() != 0)
            {
                String s;
                if (enc != null)
                {
                    s = buffer.toString(enc);
                }
                else
                {
                    s = buffer.toString();
                }
                buffer.reset();
                log.error(s);
            }
        }
        else
        {
            buffer.write(b);
        }
    }

}
