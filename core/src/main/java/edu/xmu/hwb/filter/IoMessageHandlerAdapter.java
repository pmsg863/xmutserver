package edu.xmu.hwb.filter;

import edu.xmu.hwb.jt808base.StreamBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import edu.xmu.hwb.process.Process;

import java.util.List;

/**
 * Created by Herrfe on 14-8-20.
 */
public class IoMessageHandlerAdapter extends IoHandlerAdapter {

    private List<Process> processes;

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        //TODO
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
            for (Process proc : processes) {
                do {
                    Object parseResult = proc.parse((StreamBuffer) message, session);
                    if (parseResult != null) {
                        //TODO some process
                    } else
                        break;
                } while ((proc = proc.nextPro()) != null);
            }
        }

    public void setProcesses(List processes) {
        if (processes == null)
            throw new NullPointerException("processes is null");
        this.processes = processes;
    }
}
