package edu.xmu.hwb.extype;

import edu.xmu.hwb.jt808base.StreamBuffer;
import edu.xmu.hwb.streamtype.Offset;

public class HexStringBinary
        implements StreamBuffer {
    private static String[] HexCode = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    private int binaryLength = 0;
    private String value;

    public HexStringBinary() {
    }

    public HexStringBinary(String value) {
        this.value = value;
        this.binaryLength = (value.length() > 0 ? value.length() / 3 + 1 : 0);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
        this.binaryLength = (value.length() > 0 ? value.length() / 3 + 1 : 0);
    }

    public int getBinaryLength() {
        return this.binaryLength;
    }

    public HexStringBinary parse(byte[] data, Offset position) throws Exception {
        this.value = ByteArrayToHexString(data, position.getPosition());
        this.binaryLength = (this.value.length() > 0 ? this.value.length() / 3 + 1 : 0);
        position.forword(this.binaryLength);
        return this;
    }

    public byte[] array() throws Exception {
        return HexStringToByteArray(this.value);
    }

    public static String ByteArrayToHexString(byte[] b) {
        return ByteArrayToHexString(b, 0);
    }

    public static String ByteArrayToHexString(byte[] b, int pos) {
        String result = "";
        for (int i = pos; i < b.length; i++) {
            result = result + ByteToHexChar(b[i]) + " ";
        }

        return result.trim();
    }

    public static String ByteToHexChar(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }

        int d1 = n / 16;
        int d2 = n % 16;
        return HexCode[d1] + HexCode[d2];
    }

    public static byte[] HexStringToByteArray(String hex) {
        hex = hex.trim();
        if ((hex == null) || (hex.length() == 0)) {
            return new byte[0];
        }

        int len = hex.length() / 3 + 1;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 3;
            result[i] = (byte) (HexChartoByte(achar[pos]) << 4 | HexChartoByte(achar[(pos + 1)]));
        }
        return result;
    }

    public static byte HexChartoByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}