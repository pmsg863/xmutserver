package edu.xmu.hwb.extype;

import edu.xmu.hwb.jt808base.StreamBuffer;
import edu.xmu.hwb.streamtype.Offset;

import java.util.Calendar;

class BcdDateTime7Binary
  implements StreamBuffer
{
  private static int binaryLength = 7;
  private long value;

  public long getValue()
  {
    return this.value;
  }

  public void setValue(long value)
  {
    this.value = value;
  }

  public int getBinaryLength() {
    return binaryLength;
  }

  public byte[] array() {
    return getArray(this.value);
  }

  public BcdDateTime7Binary parse(byte[] data, Offset position) {
    this.value = getTimeInMilis(data, position.getPosition());
    position.forword(binaryLength);
    return this;
  }

  public static byte[] getArray(long value)
  {
    Calendar tmpDate = Calendar.getInstance();
    tmpDate.setTimeInMillis(value);
    int year = tmpDate.get(1);
    int month = tmpDate.get(2);
    int date = tmpDate.get(5);
    int hour = tmpDate.get(11);
    int minute = tmpDate.get(12);
    int second = tmpDate.get(13);
    byte[] b = new byte[binaryLength];
    b[0] = (byte)date;
    b[1] = (byte)month;
    b[2] = (byte)(year >> 8);
    b[3] = (byte)year;
    b[4] = (byte)hour;
    b[5] = (byte)minute;
    b[6] = (byte)second;
    return b;
  }

  public long getTimeInMilis(byte[] data, int position)
  {
    byte day = data[(position + 0)];
    byte month = data[(position + 1)];
    short sYear = (short)((0xFF & data[(position + 2)]) * 256 + (0xFF & data[(position + 3)]));
    byte hour = data[(position + 4)];
    byte min = data[(position + 5)];
    byte sec = data[(position + 6)];

    Calendar tmpDate = Calendar.getInstance();
    tmpDate.set(sYear, month - 1, day, hour, min, sec);

    return tmpDate.getTimeInMillis();
  }
}