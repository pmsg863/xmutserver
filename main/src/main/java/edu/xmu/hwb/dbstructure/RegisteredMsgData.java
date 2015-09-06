package edu.xmu.hwb.dbstructure;

import edu.xmu.hwb.jt808base.JT808Message;
import edu.xmu.hwb.storage.PROC;
import edu.xmu.hwb.streamtype.GbkStringBinary;
import edu.xmu.hwb.streamtype.Offset;

/**
 * Created by Herrfe on 14-9-2.
 */
@PROC(name = "pro_addgpsvehicle")
public class RegisteredMsgData {
    String SIM;
    String CARNUMBER;

    public static RegisteredMsgData vauleOf(Object busData) {
        RegisteredMsgData trackMsgData = new RegisteredMsgData();

        JT808Message head = (JT808Message)busData;
        //新旧兼容
        Offset offset = new Offset();
        if(head.getMsgBody().length-37>0)
            offset.forword(37);
        else
            offset.forword(25);
        // 车牌号
        GbkStringBinary carnumble = new GbkStringBinary(head.getMsgBody().length-offset.getPosition());
        try {
            carnumble.parse(head.getMsgBody(),offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackMsgData.SIM = head.getSim();
        trackMsgData.CARNUMBER = carnumble.getValue();

        return trackMsgData;
    }
}
