package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

import java.io.UnsupportedEncodingException;

public class GbkStringBinary
        implements StreamBuffer {
    private int binaryLength;
    private String value;

    public GbkStringBinary(int length) {
        this(length, null);
    }

    public GbkStringBinary(String value) {
        this(getGbkStringBinaryLength(value), value);
    }

    public GbkStringBinary(int length, String value) {
        this.binaryLength = length;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getBinaryLength() {
        return this.binaryLength;
    }

    public GbkStringBinary parse(byte[] data, Offset position) throws Exception {
        int dataLength = data.length - position.getPosition();
        int length = this.binaryLength > dataLength ? dataLength : this.binaryLength;
        this.value = new String(data, position.getPosition(), length, "GBK");
        position.forword(this.binaryLength);
        return this;
    }

    public byte[] array() throws Exception {
        byte[] b = new byte[this.binaryLength];
        if (this.value != null) {
            byte[] src = this.value.getBytes("GBK");
            int length = this.binaryLength > src.length ? src.length : this.binaryLength;
            System.arraycopy(src, 0, b, 0, length);
        }
        return b;
    }

    public static int getGbkStringBinaryLength(String gbk) {
        if (gbk != null) {
            try {
                return gbk.getBytes("GBK").length;
            } catch (UnsupportedEncodingException e) {
                return 0;
            }
        }
        return 0;
    }
}