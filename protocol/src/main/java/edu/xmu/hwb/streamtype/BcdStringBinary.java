package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

public class BcdStringBinary
        implements StreamBuffer {
    private int binaryLength;
    private String value;

    public BcdStringBinary(int length) {
        this(length, null);
    }

    public BcdStringBinary(String value) {
        this(value.length() / 2 + value.length() % 2, value);
    }

    public BcdStringBinary(int length, String value) {
        this.value = value;
        this.binaryLength = length;
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

    public BcdStringBinary parse(byte[] data, Offset position) {
        int dataLength = data.length - position.getPosition();
        int length = this.binaryLength > dataLength ? dataLength : this.binaryLength;
        this.value = Bcd2String(data, position.getPosition(), length);
        position.forword(this.binaryLength);
        return this;
    }

    public byte[] array() {
        byte[] b = new byte[this.binaryLength];
        byte[] src = String2Bcd(this.value);
        int length = this.binaryLength > src.length ? src.length : this.binaryLength;
        int srcPos = this.binaryLength > src.length ? 0 : src.length - this.binaryLength;
        int destPos = this.binaryLength > src.length ? this.binaryLength - src.length : 0;
        System.arraycopy(src, srcPos, b, destPos, length);
        return b;
    }

    public static byte[] String2Bcd(String txt) {
        if ((txt == null) || (txt.length() == 0)) {
            return new byte[0];
        }

        if (txt.length() % 2 != 0) {
            txt = "0" + txt;
        }

        byte[] txtArray = txt.getBytes();
        byte[] bcdArray = new byte[txtArray.length / 2];

        for (int pos = 0; pos < txtArray.length / 2; pos++) {
            int first;
            if ((txtArray[(2 * pos)] >= 48) && (txtArray[(2 * pos)] <= 57)) {
                first = txtArray[(2 * pos)] - 48;
            } else {
                if ((txtArray[(2 * pos)] >= 97) && (txtArray[(2 * pos)] <= 122)) {
                    first = txtArray[(2 * pos)] - 97 + 10;
                } else
                    first = txtArray[(2 * pos)] - 65 + 10;
            }
            int second;
            if ((txtArray[(2 * pos + 1)] >= 48) && (txtArray[(2 * pos + 1)] <= 57)) {
                second = txtArray[(2 * pos + 1)] - 48;
            } else {
                if ((txtArray[(2 * pos + 1)] >= 97) && (txtArray[(2 * pos + 1)] <= 122)) {
                    second = txtArray[(2 * pos + 1)] - 97 + 10;
                } else {
                    second = txtArray[(2 * pos + 1)] - 65 + 10;
                }
            }
            bcdArray[pos] = (byte) ((first << 4) + second);
        }
        return bcdArray;
    }

    public static String Bcd2String(byte[] bytes, int position, int length) {
        StringBuffer temp = new StringBuffer(length * 2);

        for (int i = position; i < length + position; i++) {
            temp.append((byte) ((bytes[i] & 0xF0) >>> 4));
            temp.append((byte) (bytes[i] & 0xF));
        }

        String txt = temp.toString();
        int pos = 0;
        for (int i = 0; i < txt.length(); i++) {
            if (txt.charAt(i) != '0') {
                pos = i;
                break;
            }
        }
        if (pos != 0) {
            txt = txt.substring(pos);
        }
        return txt;
    }
}