package edu.xmu.hwb.jt808base;

import edu.xmu.hwb.streamtype.Offset;

public abstract interface StreamBuffer {
    public abstract int getBinaryLength();

    public abstract StreamBuffer parse(byte[] paramArrayOfByte, Offset paramOffSet)
            throws Exception;

    public abstract byte[] array()
            throws Exception;
}