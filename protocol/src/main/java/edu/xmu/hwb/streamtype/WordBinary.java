package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

public class WordBinary
        implements StreamBuffer {
    private static int binaryLength = 2;
    private int value;

    public WordBinary() {
    }

    public WordBinary(int value) {
        this.value = (value & 0xFFFF);
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = (value & 0xFFFF);
    }

    public int getBinaryLength() {
        return binaryLength;
    }

    public WordBinary parse(byte[] data, Offset position) {
        this.value = getWord(data, position.getPosition());
        position.forword(binaryLength);
        return this;
    }

    public byte[] array() {
        return getArray(this.value);
    }

    public static byte[] getArray(int value) {
        byte[] b = new byte[2];
        b[0] = (byte) (value >> 8);
        b[1] = (byte) value;
        return b;
    }

    public static int getWord(byte[] data, int position) {
        return data[(position + 1)] & 0xFF | (data[position] & 0xFF) << 8;
    }
}