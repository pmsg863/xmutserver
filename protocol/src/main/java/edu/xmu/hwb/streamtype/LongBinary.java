package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

public class LongBinary
        implements StreamBuffer {
    private static int binaryLength = 8;
    private long value;

    public LongBinary() {
    }

    public LongBinary(long value) {
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public int getBinaryLength() {
        return binaryLength;
    }

    public LongBinary parse(byte[] data, Offset position) {
        this.value = getLong(data, position.getPosition());
        position.forword(binaryLength);
        return this;
    }

    public byte[] array() {
        return getArray(this.value);
    }

    public static byte[] getArray(long value) {
        byte[] b = new byte[binaryLength];
        for (int i = 0; i < binaryLength; i++) {
            b[i] = (byte) (int) (value >> (binaryLength - i - 1) * 8);
        }
        return b;
    }

    public static long getLong(byte[] data, int position) {
        long v = 0L;
        for (int i = 0; i < binaryLength; i++) {
            long temp = data[(position + i)] & 0xFF;
            v += (temp << (binaryLength - i - 1) * 8);
        }
        return v;
    }
}