package edu.xmu.hwb.filter;

import edu.xmu.hwb.jt808base.OriginalBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Herrfe on 14-8-20.
 */
public class ReadWriteLoggingFilter extends IoFilterAdapter implements Runnable {
    private static String[] HexCode = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
    private LinkedBlockingQueue<Log> logQueue;
    private ExecutorService logThread;

    Logger RECEIVE_LOGGER;
    Logger SENT_LOGGER;
    Logger EXCEPTION_LOGGER;
    Logger EVENT_LOGGER;

    public ReadWriteLoggingFilter(String logname, int capacity)
    {
        logQueue = new LinkedBlockingQueue<Log>(capacity);
        this.RECEIVE_LOGGER = LoggerFactory.getLogger(logname+ ".Recv");
        this.SENT_LOGGER = LoggerFactory.getLogger(logname+ ".Sent");
        this.EXCEPTION_LOGGER = LoggerFactory.getLogger(logname+ ".Exception");
        this.EVENT_LOGGER = LoggerFactory.getLogger(logname+ ".Event");
    }

    public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
            throws Exception
    {
        if (this.RECEIVE_LOGGER.isInfoEnabled() && (message instanceof OriginalBuffer)) {

            append(RECEIVE_LOGGER, Category.RECV,session, ((OriginalBuffer) message).getMessage());
        }

        super.messageReceived(nextFilter, session, message);
    }

    public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
            throws Exception
    {
        Object message = writeRequest.getMessage();
        if (this.SENT_LOGGER.isInfoEnabled() && (message instanceof OriginalBuffer)) {
            append(SENT_LOGGER, Category.SENT,session, ((OriginalBuffer) message).getMessage());
        }

        super.messageSent(nextFilter, session, writeRequest);
    }

    public void exceptionCaught(IoFilter.NextFilter nextFilter,IoSession session, Throwable cause)
    {
        if (this.EXCEPTION_LOGGER.isInfoEnabled())
            append(this.EXCEPTION_LOGGER, Category.EXCEPTION, session, cause);
    }

    public void sessionCreated(IoFilter.NextFilter nextFilter,IoSession session)
    {
        if (this.EVENT_LOGGER.isInfoEnabled())
            append(this.EVENT_LOGGER, Category.EVENT, session, "Session Created");
    }

    public void sessionOpened(IoFilter.NextFilter nextFilter,IoSession session)
    {
        if (this.EVENT_LOGGER.isInfoEnabled())
            append(this.EVENT_LOGGER, Category.EVENT, session, "Session Opened");
    }

    public void sessionClosed(IoFilter.NextFilter nextFilter,IoSession session)
    {
        if (this.EVENT_LOGGER.isInfoEnabled())
            append(this.EVENT_LOGGER, Category.EVENT, session, "Session Closed");
    }

    /***********************************************************************/
    private static String getStackTrace(Throwable cause)
    {
        StringBuilder dumpInfo = new StringBuilder(cause.toString() + " ");
        for (StackTraceElement thr : cause.getStackTrace()) {
            dumpInfo.append(" ").append(thr.toString());
            dumpInfo.append("\r\n");
        }
        return dumpInfo.toString();
    }

    private static String getSessionInfo(IoSession session)
    {
        if (session != null) {
            return "[" +  (session.getAttribute("ID")==null? String.valueOf(session.getId()) : session.getAttribute("ID")) + "]"+"[" + session.getId() + "] => " + session.getRemoteAddress();
        }
        return "[NULL] => NULL";
    }

    private static String ByteArrayToHexString(byte[] b)
    {
        return ByteArrayToHexString(b, 0);
    }

    private static String ByteArrayToHexString(byte[] b, int pos)
    {
        String result = "";
        for (int i = pos; i < b.length; i++) {
            result = result + ByteToHexChar(b[i]) + " ";
        }

        return result.trim();
    }

    private static String ByteToHexChar(byte b)
    {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }

        int d1 = n / 16;
        int d2 = n % 16;
        return HexCode[d1] + HexCode[d2];
    }
    /***********************************************************************/


    private void append(Logger logger, Category category, IoSession session, Object message)
    {
        try
        {
            this.logQueue.add(new Log(logger, category, session, message));
        }
        catch (IllegalStateException e) {
            return;
        }

        if (this.logThread == null) {
            this.logThread = Executors.newSingleThreadExecutor(new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "LoggingThread");
                    return t;
                }
            });
            this.logThread.execute(this);
        }
    }

    public void run()
    {
        while (true)
            try {
                Log log = (Log)this.logQueue.take();

                Object msg = log.message;
                String head = getSessionInfo(log.session) + " \t" + log.category + ": \t";

                if ((msg instanceof String))
                    log.logger.info(head + (String)msg);
                else if ((msg instanceof byte[]))
                    log.logger.info(head + ByteArrayToHexString((byte[])msg) );
                else if ((msg instanceof Throwable)) {
                    if ((msg instanceof IOException))
                        log.logger.info(head + ((Throwable)msg).getMessage());
                    else
                        log.logger.info(head + getStackTrace((Throwable)msg));
                }
                else {
                    log.logger.info(head + msg.toString());
                }
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                this.EXCEPTION_LOGGER.error(getStackTrace(e));
            }
    }

    private class Log
    {
        private Logger logger;
        private Category category;
        private IoSession session;
        private Object message;

        public Log(Logger logger, Category category, IoSession session, Object message)
        {
            this.logger = logger;
            this.category = category;
            this.session = session;
            this.message = message;
        }
    }

    private static enum Category
    {
        RECV,
        SENT,
        EXCEPTION,
        EVENT;
    }
}
