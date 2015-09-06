package edu.xmu.hwb.jt808body;


import edu.xmu.hwb.jt808base.JT808MsgBody;
import edu.xmu.hwb.streamtype.*;

import java.io.ByteArrayOutputStream;

class JT8080x0100Body extends JT808MsgBody {
    private boolean newProtocol = true;

    private WordBinary province = new WordBinary();

    private WordBinary city = new WordBinary();

    private ByteArrayBinary producer = new ByteArrayBinary(5);
    private ByteArrayBinary deviceType;
    private ByteArrayBinary deviceID = new ByteArrayBinary(7);

    private ByteBinary plateColor = new ByteBinary();
    private GbkStringBinary plate;

    public JT8080x0100Body() {
        this.deviceType = new ByteArrayBinary(20);
    }

    public JT8080x0100Body(boolean newProtocol) {
        this.newProtocol = newProtocol;

        if (newProtocol)
            this.deviceType = new ByteArrayBinary(20);
        else
            this.deviceType = new ByteArrayBinary(8);
    }

    public boolean isNewProtocol() {
        return this.newProtocol;
    }

    public void switchProtocol(boolean newProtocol) {
        if (this.newProtocol == newProtocol)
            return;
        ByteArrayBinary temp;
        if (newProtocol)
            temp = new ByteArrayBinary(20);
        else {
            temp = new ByteArrayBinary(8);
        }
        temp.setValue(this.deviceType.getValue());

        this.deviceType = temp;
        this.newProtocol = newProtocol;
    }

    public int getMsgType() {
        return 256;
    }

    public int getBinaryLength() {
        int length = 17 + this.deviceType.getBinaryLength();
        if (this.plate != null) {
            length += this.plate.getBinaryLength();
        }
        return length;
    }

    public JT8080x0100Body parse(byte[] data, Offset position)
            throws Exception {
        this.province.parse(data, position);

        this.city.parse(data, position);

        this.producer.parse(data, position);

        this.deviceType.parse(data, position);

        this.deviceID.parse(data, position);

        this.plateColor.parse(data, position);

        if (data.length > position.getPosition()) {
            this.plate = new GbkStringBinary(data.length - position.getPosition());
            this.plate.parse(data, position);
        }

        return this;
    }

    public byte[] array() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(getBinaryLength());

        stream.write(this.province.array());

        stream.write(this.city.array());

        stream.write(this.producer.array());

        stream.write(this.deviceType.array());

        stream.write(this.deviceID.array());

        stream.write(this.plateColor.array());

        if (this.plate != null) {
            stream.write(this.plate.array());
        }

        return stream.toByteArray();
    }

    public int getProvince() {
        return this.province.getValue();
    }

    public void setProvince(int province) {
        this.province.setValue(province);
    }

    public int getCity() {
        return this.city.getValue();
    }

    public void setCity(int city) {
        this.city.setValue(city);
    }

    public byte[] getProducer() {
        return this.producer.getValue();
    }

    public void setProducer(byte[] producer) {
        this.producer.setValue(producer);
    }

    public byte[] getDeviceType() {
        return this.deviceType.getValue();
    }

    public void setDeviceType(byte[] deviceType) {
        this.deviceType.setValue(deviceType);
    }

    public byte[] getDeviceID() {
        return this.deviceID.getValue();
    }

    public void setDeviceID(byte[] deviceID) {
        this.deviceID.setValue(deviceID);
    }

    public byte getPlateColor() {
        return this.plateColor.getValue();
    }

    public void setPlateColor(ByteBinary plateColor) {
        this.plateColor = plateColor;
    }

    public String getPlate() {
        return this.plate != null ? this.plate.getValue() : null;
    }

    public void setPlate(String plate) {
        this.plate = new GbkStringBinary(plate);
    }
}