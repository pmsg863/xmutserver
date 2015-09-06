package edu.xmu.hwb.jt808body;

import edu.xmu.hwb.jt808base.StreamBuffer;
import edu.xmu.hwb.streamtype.*;

import java.io.ByteArrayOutputStream;

public class GnssPosition
        implements StreamBuffer {
    private IntBinary alarmStatus = new IntBinary();
    private IntBinary vehicleStatus = new IntBinary();
    private IntBinary latitude = new IntBinary();
    private IntBinary longitude = new IntBinary();
    private WordBinary height = new WordBinary();
    private WordBinary speed = new WordBinary();
    private WordBinary direction = new WordBinary();

    private BcdDateTimeBinary gpsTime = new BcdDateTimeBinary();

    public int getBinaryLength() {
        return 28;
    }

    public GnssPosition parse(byte[] data, Offset position) {
        this.alarmStatus.parse(data, position);

        this.vehicleStatus.parse(data, position);

        this.latitude.parse(data, position);

        this.longitude.parse(data, position);

        this.height.parse(data, position);

        this.speed.parse(data, position);

        this.direction.parse(data, position);

        this.gpsTime.parse(data, position);
        return this;
    }

    public byte[] array() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(getBinaryLength());

        stream.write(this.alarmStatus.array());

        stream.write(this.vehicleStatus.array());

        stream.write(this.latitude.array());

        stream.write(this.longitude.array());

        stream.write(this.height.array());

        stream.write(this.speed.array());

        stream.write(this.direction.array());

        stream.write(this.gpsTime.array());

        return stream.toByteArray();
    }

    public long getGpsTime() {
        return this.gpsTime.getValue();
    }

    public void setGpsTime(long gpsTime) {
        this.gpsTime.setValue(gpsTime);
    }

    public int getLongitude() {
        return this.longitude.getValue();
    }

    public void setLongitude(int longitude) {
        this.longitude.setValue(longitude);
    }

    public int getLatitude() {
        return this.latitude.getValue();
    }

    public void setLatitude(int latitude) {
        this.latitude.setValue(latitude);
    }

    public int getHeight() {
        return this.height.getValue();
    }

    public void setHeight(int height) {
        this.height.setValue(height);
    }

    public int getSpeed() {
        return this.speed.getValue();
    }

    public void setSpeed(int speed) {
        this.speed.setValue(speed);
    }

    public int getDirection() {
        return this.direction.getValue();
    }

    public void setDirection(int direction) {
        this.direction.setValue(direction);
    }

    public int getVehicleStatus() {
        return this.vehicleStatus.getValue();
    }

    public void setVehicleStatus(int vehicleStatus) {
        this.vehicleStatus.setValue(vehicleStatus);
    }

    public int getAlarmStatus() {
        return this.alarmStatus.getValue();
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus.setValue(alarmStatus);
    }
}