package edu.xmu.hwb.process;

import edu.xmu.hwb.jt808base.StreamBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Herrfe on 14-8-21.
 */
public abstract class Process<T ,M extends StreamBuffer> {

    private Process nextPro;

    public abstract T parse(M srcmsg,IoSession session) ;

    public void setNextPro(Process nextPro) {
        this.nextPro = nextPro;
    }

    public Process nextPro() {
        return nextPro;
    }

}
