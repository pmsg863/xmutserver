package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

public class ByteBinary
        implements StreamBuffer {
    private static int binaryLength = 1;
    private byte value;

    public ByteBinary() {
    }

    public ByteBinary(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public int getBinaryLength() {
        return binaryLength;
    }

    public ByteBinary parse(byte[] data, Offset position) {
        this.value = data[position.getPosition()];
        position.forword(binaryLength);
        return this;
    }

    public byte[] array() {
        byte[] b = new byte[1];
        b[0] = this.value;
        return b;
    }
}