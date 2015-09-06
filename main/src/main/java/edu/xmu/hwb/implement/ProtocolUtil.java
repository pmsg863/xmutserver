package edu.xmu.hwb.implement;

import edu.xmu.hwb.jt808base.JT808Message;
import edu.xmu.hwb.jt808body.JT8080x8001Body;
import edu.xmu.hwb.streamtype.AttachedData;
import edu.xmu.hwb.streamtype.ByteBinary;
import edu.xmu.hwb.streamtype.IntBinary;
import edu.xmu.hwb.streamtype.Offset;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Herrfe on 14-8-27.
 */
public class ProtocolUtil {

    private static AtomicInteger serial = new AtomicInteger();

    public static JT808Message getResponse(JT808Message message) {
        return getResponse(message,0);
    }

    static int nextSerial(){ return serial.getAndIncrement();}

    static JT808Message getResponse(JT808Message message,int result) {
        // 登录应答包消息体
        JT8080x8001Body msgBody = new JT8080x8001Body();
        // 应答流水号
        msgBody.setAckSerial(message.getSerial());
        // 应答ID
        msgBody.setAckMsgID(message.getMsgID());
        // 登录结果
        msgBody.setAckResult((byte)result);	// 登录成功
        //msgBody.setAckResult((byte) 1);	// 登录失败

        JT808Message response = new JT808Message();
        response.setID( 0x8001 );
        response.setSerial( serial.getAndIncrement() );
        response.setSim(message.getSim());
        try {
            response.setMsgBody(msgBody.array());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public static ArrayList<AttachedData> parseAttached(byte[] data, Offset position) {
        ArrayList<AttachedData> attached = new ArrayList<>();			// 附加数据列表

        ByteBinary id = new ByteBinary();
        ByteBinary length = new ByteBinary();
        while (data.length - position.getPosition() > 2) {
            id.parse(data, position);
            length.parse(data, position);

            // 里程，DWORD，1/10km，对应车上里程表读数
            if ((id.getValue() & 0xFF) == 0x01) {
                IntBinary value = new IntBinary();
                value.parse(data, position);

                attached.add(new AttachedData(id.getValue(), value));
                continue;
            }

            // 未知信息，跳过其处理
            position.forword(length.getValue());
        }

        return attached;
    }
}
