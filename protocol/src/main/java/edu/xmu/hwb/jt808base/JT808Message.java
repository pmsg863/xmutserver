package edu.xmu.hwb.jt808base;

import edu.xmu.hwb.streamtype.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JT808Message
        implements StreamBuffer, OriginalBuffer {
    private WordBinary ID = new WordBinary();

    private BcdStringBinary sim = new BcdStringBinary(6);

    private WordBinary serial = new WordBinary();

    private WordBinary msgPackageTotal = new WordBinary();

    private WordBinary msgPackageCur = new WordBinary();
    private ByteArrayBinary msgBody;
    private BooleanBinary isMultiPackage = new BooleanBinary();

    private ByteBinary encryption = new ByteBinary();

    private byte[] originalBuffe;

    public int getMsgID() {
        return this.ID.getValue();
    }

    @Override
    public int getBinaryLength() {
        return 0;
    }

    @Override
    public byte[] array() throws IOException {
        int length = (this.isMultiPackage.getValue() ? 16 : 12) + (this.msgBody != null ? this.msgBody.getBinaryLength() : 0);

        ByteArrayOutputStream buf = new ByteArrayOutputStream(length);

        buf.write(this.ID.array());

        buf.write(getMsgAttribute());

        buf.write(this.sim.array());

        buf.write(this.serial.array());

        if (this.isMultiPackage.getValue()) {
            buf.write(this.msgPackageTotal.array());

            buf.write(this.msgPackageCur.array());
        }

        if (this.msgBody != null) {
            buf.write(this.msgBody.array());
        }

        return buf.toByteArray();
    }


    @Override
    public JT808Message parse(byte[] paramArrayOfByte, Offset paramOffSet) throws Exception {
        this.initialize(paramArrayOfByte);
        return this;
    }

    public boolean initialize(byte[] data) {
        if ((data == null) || (data.length < 12)) {
            return false;
        }

        Offset offSet = new Offset();
        WordBinary msgBodyLength = new WordBinary();

        this.ID.parse(data, offSet);

        WordBinary msgAttr = new WordBinary();
        msgAttr.parse(data, offSet);

        byte[] attr = msgAttr.array();
        this.isMultiPackage.setValue((attr[0] & 0x20) > 0);
        this.encryption.setValue((byte) (attr[0] & 0x1C));

        attr[0] = (byte) (attr[0] & 0x3);
        msgBodyLength.parse(attr, new Offset());

        this.sim.parse(data, offSet);

        this.serial.parse(data, offSet);

        if (this.isMultiPackage.getValue()) {
            if (data.length < 16) {
                return false;
            }

            this.msgPackageTotal.parse(data, offSet);

            this.msgPackageCur.parse(data, offSet);
        }

        if (data.length - offSet.getPosition() >= msgBodyLength.getValue()) {
            this.msgBody = new ByteArrayBinary(msgBodyLength.getValue());
            this.msgBody.parse(data, offSet);
        }
        return true;
    }

    private byte[] getMsgAttribute() {
        WordBinary sb = new WordBinary();

        short msgLength = 0;
        if (this.msgBody != null) {
            msgLength = (short) this.msgBody.getBinaryLength();
        }
        sb.setValue(msgLength);

        byte[] b = sb.array();

        if (this.isMultiPackage.getValue()) {
            int tmp48_47 = 0;
            byte[] tmp48_46 = b;
            tmp48_46[tmp48_47] = (byte) (tmp48_46[tmp48_47] | 0x20);
        }
        int tmp57_56 = 0;
        byte[] tmp57_55 = b;
        tmp57_55[tmp57_56] = (byte) (tmp57_55[tmp57_56] | this.encryption.getValue() & 0x1C);

        return b;
    }

    public int getID() {
        return this.ID.getValue();
    }

    public void setID(int id) {
        this.ID.setValue(id);
    }

    public String getSim() {
        return this.sim.getValue();
    }

    public void setSim(String sim) {
        this.sim.setValue(sim);
    }

    public int getSerial() {
        return this.serial.getValue();
    }

    public void setSerial(int serial) {
        this.serial.setValue(serial);
    }

    public int getMsgPackageTotal() {
        return this.msgPackageTotal.getValue();
    }

    public void setMsgPackageTotal(int msgPackageTotal) {
        this.msgPackageTotal.setValue(msgPackageTotal);
    }

    public int getMsgPackageCur() {
        return this.msgPackageCur.getValue();
    }

    public void setMsgPackageCur(int msgPackageCur) {
        this.msgPackageCur.setValue(msgPackageCur);
    }

    public byte[] getMsgBody() {
        if (this.msgBody != null) {
            return this.msgBody.getValue();
        }
        return null;
    }

    public void setMsgBody(byte[] msgBody) {
        if (this.msgBody != null)
            this.msgBody.setValue(msgBody);
        else
            this.msgBody = new ByteArrayBinary(msgBody);
    }

    public boolean isMultiPackage() {
        return this.isMultiPackage.getValue();
    }

    public void setMultiPackage(boolean isMultiPackage) {
        this.isMultiPackage.setValue(isMultiPackage);
    }

    public byte getEncryption() {
        return this.encryption.getValue();
    }

    public void setEncryption(byte encryption) {
        this.encryption.setValue(encryption);
    }

    @Override
    public byte[] getMessage() {
        return originalBuffe;
    }

    @Override
    public JT808Message setMessage(byte[] message) {
        originalBuffe = message;
        return this;
    }
}
