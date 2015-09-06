package edu.xmu.hwb.filter;

import edu.xmu.hwb.jt808base.StreamBuffer;
import edu.xmu.hwb.process.Process;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import java.rmi.UnexpectedException;

/**
 * Created by Herrfe on 14-8-20.
 */
public class AuthorizeFilter extends IoFilterAdapter {

    /**
     * 获取身份验证超时时间，单位为毫秒
     */
    private int authorizeTimeout = 3*60*1000;

    Process<AuthorizeResult, StreamBuffer> authorizeProcess;

    public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
            throws Exception {
        Process<AuthorizeResult, StreamBuffer> proc = authorizeProcess;

        if (session.getAttribute("ID") != null)
            nextFilter.messageReceived(session, message);
        else {
            if (session.getCreationTime() + authorizeTimeout < System.currentTimeMillis()) {
                session.close(false);
                nextFilter.exceptionCaught(session,new UnexpectedException(session.toString()+" authorize Time Out"));
                return;
            }

            do {
                AuthorizeResult parseResult = proc.parse((StreamBuffer) message, session);
                if (parseResult != null && parseResult.isAuthorized()) {
                    session.setAttribute("ID",parseResult.getID());
                } else
                    break;
            } while ((proc = proc.nextPro()) != null);
        }
    }

    public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
            throws Exception {
        //if (result != null)
        nextFilter.messageSent(session, writeRequest);
    }

    public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
            throws Exception {
        // todo   nextFilter.filterWrite
        nextFilter.sessionOpened(session);
    }

    public void setAuthorizeProcess(Process<AuthorizeResult, StreamBuffer> authorizeProcess) {
        this.authorizeProcess = authorizeProcess;
    }

    public void setAuthorizeTimeout(int authorizeTimeout) {
        this.authorizeTimeout = authorizeTimeout;
    }
}
