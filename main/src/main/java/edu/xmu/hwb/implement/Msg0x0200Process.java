package edu.xmu.hwb.implement;

import edu.xmu.hwb.jt808base.JT808Message;
import edu.xmu.hwb.storage.StorageManagerFilter;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Herrfe on 14-8-22.
 */
public class Msg0x0200Process extends  edu.xmu.hwb.process.Process<JT808Message,JT808Message> {

    StorageManagerFilter storageManagerFilter;

    ApiClient client ;

    @Override
    public JT808Message parse(JT808Message srcmsg, IoSession session) {

        if(srcmsg.getMsgID()==0x0200 ){
            if( storageManagerFilter!=null)
                storageManagerFilter.store("0x0200",srcmsg);
            if(client!=null)
                client.store(srcmsg);
        }
        //应答
        session.write(ProtocolUtil.getResponse(srcmsg));

        return null;
    }

    public void setStorageManagerFilter(StorageManagerFilter storageManagerFilter) {
        this.storageManagerFilter = storageManagerFilter;
    }

    public void setClient(ApiClient client) {
        this.client = client;
    }
}
