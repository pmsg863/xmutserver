package edu.xmu.hwb.jt808base;


import edu.xmu.hwb.streamtype.Offset;

public abstract class JT808MsgBody
  implements StreamBuffer
{
  public abstract int getMsgType();

  public abstract int getBinaryLength();

  public abstract JT808MsgBody parse(byte[] paramArrayOfByte, Offset paramCursor)
    throws Exception;

  public abstract byte[] array()
    throws Exception;
}