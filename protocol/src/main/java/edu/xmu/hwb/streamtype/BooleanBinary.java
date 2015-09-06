package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

public class BooleanBinary
        implements StreamBuffer {
    private static int binaryLength = 1;
    private boolean value;

    public BooleanBinary() {
    }

    public BooleanBinary(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public int getBinaryLength() {
        return binaryLength;
    }

    public BooleanBinary parse(byte[] data, Offset position) {
        this.value = (data[position.getPosition()] != 0);
        position.forword(binaryLength);
        return this;
    }

    public byte[] array() {
        byte[] b = new byte[1];
        b[0] = (byte) (this.value ? 1 : 0);
        return b;
    }
}