package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

import java.util.Calendar;

public class BcdDateTimeBinary
        implements StreamBuffer {
    private static int binaryLength = 6;
    private long value;

    public BcdDateTimeBinary() {
    }

    public BcdDateTimeBinary(long value) {
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

    public BcdDateTimeBinary parse(byte[] data, Offset position) {
        this.value = getTimeInMilis(data, position.getPosition());
        position.forword(binaryLength);
        return this;
    }

    public byte[] array() {
        return getArray(this.value);
    }

    public static byte[] getArray(long value) {
        Calendar tmpDate = Calendar.getInstance();
        tmpDate.setTimeInMillis(value);
        int year = tmpDate.get(1) % 100;
        int month = tmpDate.get(2) + 1;
        int date = tmpDate.get(5);
        int hour = tmpDate.get(11);
        int minute = tmpDate.get(12);
        int second = tmpDate.get(13);

        byte[] b = new byte[6];
        b[0] = (byte) ((year / 10 << 4) + year % 10);
        b[1] = (byte) ((month / 10 << 4) + month % 10);
        b[2] = (byte) ((date / 10 << 4) + date % 10);
        b[3] = (byte) ((hour / 10 << 4) + hour % 10);
        b[4] = (byte) ((minute / 10 << 4) + minute % 10);
        b[5] = (byte) ((second / 10 << 4) + second % 10);
        return b;
    }

    public static long getTimeInMilis(byte[] data, int position) {
        byte year = (byte) ((data[(position + 0)] >> 4) * 10 + (data[(position + 0)] & 0xF));
        byte month = (byte) ((data[(position + 1)] >> 4) * 10 + (data[(position + 1)] & 0xF));
        byte date = (byte) ((data[(position + 2)] >> 4) * 10 + (data[(position + 2)] & 0xF));
        byte hour = (byte) ((data[(position + 3)] >> 4) * 10 + (data[(position + 3)] & 0xF));
        byte minite = (byte) ((data[(position + 4)] >> 4) * 10 + (data[(position + 4)] & 0xF));
        byte second = (byte) ((data[(position + 5)] >> 4) * 10 + (data[(position + 5)] & 0xF));

        int millis = 0;

        Calendar tmpDate = Calendar.getInstance();
        tmpDate.set(year + tmpDate.get(1) / 100 * 100, month - 1, date, hour, minite, second);
        tmpDate.set(14, millis);

        return tmpDate.getTimeInMillis();
    }
}