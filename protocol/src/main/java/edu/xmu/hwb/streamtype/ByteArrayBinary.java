package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

public class ByteArrayBinary
        implements StreamBuffer {
    private byte[] value;

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        int length = value.length > this.value.length ? this.value.length : value.length;

        System.arraycopy(value, 0, this.value, 0, length);
    }

    public ByteArrayBinary(int length) {
        this.value = new byte[length];
    }

    public ByteArrayBinary(byte[] value) {
        this.value = value;
    }

    public int getBinaryLength() {
        return this.value != null ? this.value.length : 0;
    }

    public ByteArrayBinary parse(byte[] data, Offset position) {
        System.arraycopy(data, position.getPosition(), this.value, 0, this.value.length);
        position.forword(this.value.length);
        return this;
    }

    public byte[] array() {
        return this.value;
    }
}