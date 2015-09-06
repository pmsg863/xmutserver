package edu.xmu.hwb.jt808body;


import edu.xmu.hwb.jt808base.JT808MsgBody;
import edu.xmu.hwb.streamtype.ByteBinary;
import edu.xmu.hwb.streamtype.GbkStringBinary;
import edu.xmu.hwb.streamtype.Offset;
import edu.xmu.hwb.streamtype.WordBinary;

import java.io.ByteArrayOutputStream;

public class JT8080x8100Body extends JT808MsgBody {
    private WordBinary ackSerial = new WordBinary();

    private ByteBinary result = new ByteBinary();
    private GbkStringBinary authCode;

    public int getMsgType() {
        return 33024;
    }

    public int getBinaryLength() {
        return 3 + (this.authCode == null ? 0 : this.authCode.getBinaryLength());
    }

    public JT8080x8100Body parse(byte[] data, Offset position)
            throws Exception {
        this.ackSerial.parse(data, position);

        this.ackSerial.parse(data, position);

        if ((this.result.getValue() == 0) && (data.length > position.getPosition())) {
            this.authCode = new GbkStringBinary(data.length - position.getPosition());
            this.authCode.parse(data, position);
        }

        return this;
    }

    public byte[] array() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(getBinaryLength());

        stream.write(this.ackSerial.array());

        stream.write(this.result.array());

        if (this.authCode != null) {
            stream.write(this.authCode.array());
        }

        return stream.toByteArray();
    }

    public int getAckSerial() {
        return this.ackSerial.getValue();
    }

    public void setAckSerial(int ackSerial) {
        this.ackSerial.setValue(ackSerial);
    }

    public byte getResult() {
        return this.result.getValue();
    }

    public void setResult(byte ackResult) {
        this.result.setValue(ackResult);
    }

    public String getAuthCode() {
        return this.authCode != null ? this.authCode.getValue() : null;
    }

    public void setAuthCode(String authCode) {
        this.authCode = new GbkStringBinary(authCode);
    }
}