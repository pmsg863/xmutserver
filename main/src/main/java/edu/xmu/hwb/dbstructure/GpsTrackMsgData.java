package edu.xmu.hwb.dbstructure;


import edu.xmu.hwb.implement.ProtocolUtil;
import edu.xmu.hwb.jt808base.JT808Message;
import edu.xmu.hwb.jt808body.GnssPosition;
import edu.xmu.hwb.storage.PROC;
import edu.xmu.hwb.streamtype.AttachedData;
import edu.xmu.hwb.streamtype.IntBinary;
import edu.xmu.hwb.streamtype.Offset;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Herrfe on 14-7-9.
 */
//@TABLE(name = "COM_GPS_TRACK")
@PROC(name = "pro_addgpsdate")
public class GpsTrackMsgData {
    String VEHICLE_SIM;
    Date GPS_TIME;
    int LONGITUDE;
    int LATITUDE;
    int ALTITUDE;
    int DIRECTION;
    int SPEED;
    boolean GPS_STATUS;
    int TOTAL_KMS;
    int STATUS;
    int ALARM_STATUS;
    Date SERVER_TIME;

    public void setTest(){
        VEHICLE_SIM = "123123";
        GPS_TIME = new Date(System.currentTimeMillis());
        SERVER_TIME = new Date(System.currentTimeMillis());

         LONGITUDE = 11213123;
         LATITUDE =11213123;
         ALTITUDE = 123;
         DIRECTION = 456;
         SPEED = 222 ;
         TOTAL_KMS = 45678;
         STATUS = 212;
         ALARM_STATUS =323;

    }

    public static GpsTrackMsgData vauleOf(Object busData) {
        GpsTrackMsgData trackMsgData = new GpsTrackMsgData();

        JT808Message head = (JT808Message)busData;
        GnssPosition position = new GnssPosition();
        Offset offset = new Offset();
        position.parse( head.getMsgBody(),offset);
        ArrayList<AttachedData> attachedDatas = ProtocolUtil.parseAttached(head.getMsgBody(), offset);

        trackMsgData.VEHICLE_SIM = head.getSim();
        trackMsgData.GPS_TIME = new Date(position.getGpsTime());
        trackMsgData.LONGITUDE = position.getLongitude();
        trackMsgData.LATITUDE = position.getLatitude();
        trackMsgData.ALTITUDE =  position.getHeight();
        trackMsgData.DIRECTION =  position.getDirection();
        trackMsgData.SPEED = position.getSpeed();
        trackMsgData.GPS_STATUS =(( 0x000000002&position.getVehicleStatus()) ==0x000000002);
        for(AttachedData attachedData:attachedDatas){
            //里程
            if(attachedData.getID() == 0x01)
                trackMsgData.TOTAL_KMS =  ((IntBinary)attachedDatas.get(0).getContent()).getValue();
        }

        trackMsgData.STATUS = position.getVehicleStatus();
        trackMsgData.ALARM_STATUS = position.getAlarmStatus();
        trackMsgData.SERVER_TIME = new Date(System.currentTimeMillis());


        return trackMsgData;
    }

    public String getVEHICLE_SIM() {
        return VEHICLE_SIM;
    }

    public Date getGPS_TIME() {
        return GPS_TIME;
    }

    public int getLONGITUDE() {
        return LONGITUDE;
    }

    public int getLATITUDE() {
        return LATITUDE;
    }

    public int getALTITUDE() {
        return ALTITUDE;
    }

    public int getDIRECTION() {
        return DIRECTION;
    }

    public int getSPEED() {
        return SPEED;
    }

    public boolean isGPS_STATUS() {
        return GPS_STATUS;
    }

    public int getTOTAL_KMS() {
        return TOTAL_KMS;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public int getALARM_STATUS() {
        return ALARM_STATUS;
    }

    public Date getSERVER_TIME() {
        return SERVER_TIME;
    }
}
