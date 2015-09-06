package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

public class IntBinary
        implements StreamBuffer {
    private static int binaryLength = 4;
    private int value;

    public IntBinary() {
    }

    public IntBinary(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getBinaryLength() {
        return binaryLength;
    }

    public IntBinary parse(byte[] data, Offset position) {
        this.value = getInt(data, position.getPosition());
        position.forword(binaryLength);
        return this;
    }

    public byte[] array() {
        return getArray(this.value);
    }

    public static byte[] getArray(int value) {
        byte[] b = new byte[4];
        b[0] = (byte) (value >> 24);
        b[1] = (byte) (value >> 16);
        b[2] = (byte) (value >> 8);
        b[3] = (byte) value;
        return b;
    }

    public static int getInt(byte[] data, int position) {
        return data[(position + 3)] & 0xFF | (data[(position + 2)] & 0xFF) << 8 | (data[(position + 1)] & 0xFF) << 16 | (data[(position + 0)] & 0xFF) << 24;
    }
}