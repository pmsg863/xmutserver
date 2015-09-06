package edu.xmu.hwb.implement;

import edu.xmu.hwb.filter.AuthorizeResult;
import edu.xmu.hwb.jt808base.JT808Message;
import edu.xmu.hwb.jt808body.JT8080x8100Body;
import edu.xmu.hwb.storage.StorageManagerFilter;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Herrfe on 14-8-22.
 */
public class AuthorizeProcess extends edu.xmu.hwb.process.Process<AuthorizeResult, JT808Message> {

    StorageManagerFilter storageManagerFilter;
    @Override
    public AuthorizeResult parse(JT808Message srcmsg, IoSession session) {
        AuthorizeResult result = new AuthorizeResult();
        //终端注册
        if(srcmsg.getMsgID()==0x0100){
            try {
                JT808Message response = getResponse(srcmsg, session, null);
                session.write(response);
                storageManagerFilter.store("0x0100",srcmsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //终端鉴权
        if(srcmsg.getMsgID()==0x0102){
            try {
                // 登录结果
                String code = new String(srcmsg.getMsgBody(), "GBK");
                if ("123456".equals(code)) {
                    result.setAuthorized(true);
                    result.setId(Long.valueOf(srcmsg.getSim()));	// 登录成功
                }
                session.write(ProtocolUtil.getResponse( srcmsg, result.isAuthorized()? 0:1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    JT808Message getResponse(JT808Message message,IoSession session, Object vehicle) throws Exception {
        // 注册应答包消息体
        JT8080x8100Body msgBody = new JT8080x8100Body();
        // 应答流水号
        msgBody.setAckSerial(message.getSerial());
        // 注册结果
        msgBody.setResult((byte) 0x00);	// 成功
        msgBody.setAuthCode("123456");	// 鉴权码
/*        if (vehicle != null *//*&& !vehicle.isRegistered()*//*) {
            msgBody.setResult((byte) 0x00);	// 成功
            msgBody.setAuthCode("123465");	// 鉴权码
        } else if (vehicle != null) {
            msgBody.setResult((byte) 0x01);	// 车辆已被注册
        } else {
            msgBody.setResult((byte) 0x02);	// 数据库无该车辆
        }*/

        JT808Message response = new JT808Message();
        response.setID( 0x8100 );
        response.setSerial( ProtocolUtil.nextSerial() );
        response.setSim(message.getSim());
        response.setMsgBody(msgBody.array());

        return response;
    }

    public void setStorageManagerFilter(StorageManagerFilter storageManagerFilter) {
        this.storageManagerFilter = storageManagerFilter;
    }
}
