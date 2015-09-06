package edu.xmu.hwb.jt808body;

import edu.xmu.hwb.jt808base.JT808MsgBody;
import edu.xmu.hwb.streamtype.ByteBinary;
import edu.xmu.hwb.streamtype.Offset;
import edu.xmu.hwb.streamtype.WordBinary;

import java.io.ByteArrayOutputStream;

class JT8080x0001Body extends JT808MsgBody
{
  private WordBinary ackSerial = new WordBinary();

  private WordBinary ackMsgID = new WordBinary();

  private ByteBinary ackResult = new ByteBinary();

  public int getMsgType()
  {
    return 1;
  }

  public int getBinaryLength()
  {
    return 5;
  }

  public JT8080x0001Body parse(byte[] data, Offset position)
    throws Exception
  {
    this.ackSerial.parse(data, position);

    this.ackMsgID.parse(data, position);

    this.ackResult.parse(data, position);

     return this;
  }

  public byte[] array() throws Exception
  {
    ByteArrayOutputStream stream = new ByteArrayOutputStream(getBinaryLength());

    stream.write(this.ackSerial.array());

    stream.write(this.ackMsgID.array());

    stream.write(this.ackResult.array());

    return stream.toByteArray();
  }

  public int getAckSerial()
  {
    return this.ackSerial.getValue();
  }

  public void setAckSerial(int ackSerial)
  {
    this.ackSerial.setValue(ackSerial);
  }

  public int getAckMsgID()
  {
    return this.ackMsgID.getValue();
  }

  public void setAckMsgID(int ackMsgID)
  {
    this.ackMsgID.setValue(ackMsgID);
  }

  public byte getAckResult()
  {
    return this.ackResult.getValue();
  }

  public void setAckResult(byte ackResult)
  {
    this.ackResult.setValue(ackResult);
  }
}