package edu.xmu.hwb.extype;

import edu.xmu.hwb.jt808base.StreamBuffer;
import edu.xmu.hwb.streamtype.Offset;

import java.util.Calendar;

class BcdDateBinary
        implements StreamBuffer {
    private static int binaryLength = 4;
    private long value;

    public BcdDateBinary() {
    }

    public BcdDateBinary(long value) {
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

    public BcdDateBinary parse(byte[] data, Offset position) {
        this.value = getDateInMilis(data, position.getPosition());
        position.forword(binaryLength);
        return this;
    }

    public byte[] array() {
        return getArray(this.value);
    }

    public static byte[] getArray(long value) {
        Calendar tmpDate = Calendar.getInstance();
        tmpDate.setTimeInMillis(value);
        int yearH = tmpDate.get(1) / 100;
        int yearL = tmpDate.get(1) % 100;
        int month = tmpDate.get(2) + 1;
        int date = tmpDate.get(5);

        byte[] b = new byte[6];
        b[0] = (byte) ((yearH / 10 << 4) + yearH % 10);
        b[1] = (byte) ((yearL / 10 << 4) + yearL % 10);
        b[2] = (byte) ((month / 10 << 4) + month % 10);
        b[3] = (byte) ((date / 10 << 4) + date % 10);
        return b;
    }

    public static long getDateInMilis(byte[] data, int position) {
        byte yearH = (byte) ((data[(position + 0)] >> 4) * 10 + (data[(position + 0)] & 0xF));
        byte yearL = (byte) ((data[(position + 1)] >> 4) * 10 + (data[(position + 1)] & 0xF));
        byte month = (byte) ((data[(position + 2)] >> 4) * 10 + (data[(position + 2)] & 0xF));
        byte date = (byte) ((data[(position + 3)] >> 4) * 10 + (data[(position + 3)] & 0xF));

        Calendar tmpDate = Calendar.getInstance();
        tmpDate.clear();
        tmpDate.set(yearL + yearH * 100, month - 1, date);

        return tmpDate.getTimeInMillis();
    }
}