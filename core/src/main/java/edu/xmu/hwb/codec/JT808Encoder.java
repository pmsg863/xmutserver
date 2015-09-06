package edu.xmu.hwb.codec;


import edu.xmu.hwb.jt808base.JT808Message;
import edu.xmu.hwb.jt808base.StreamBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.util.HashMap;

public class JT808Encoder extends ProtocolEncoderAdapter
{
    private static final byte BEGIN = 126;
    private static final byte END = 126;
    /**
     * 转义列表
     */
    @SuppressWarnings("serial")
    private static final HashMap<Byte, byte[]> ESCAPEMAP = new HashMap<Byte, byte[]>() {
        {
            put(Byte.valueOf((byte)0x7E), new byte[]{0x7D, 0x02});
            put(Byte.valueOf((byte)0x7D), new byte[]{0x7D, 0x01});
        }
    };

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
            throws Exception
    {
        if (!(message instanceof StreamBuffer)) {
            throw new ProtocolEncoderException("编码器不支持的消息类型。当前只支持StreamBuffer的实现类编码");
        }

        JT808Message msgBuf = (JT808Message)message;
        byte[] data = msgBuf.array();
        byte CRC = getCRC(data);

        IoBuffer buffer = IoBuffer.allocate(data.length + 3).setAutoExpand(true);

        buffer.put((byte)126);

        for (int i = 0; i < data.length; i++) {
            byte[] escapeValue = (byte[])ESCAPEMAP.get(Byte.valueOf(data[i]));
            if (escapeValue != null)
                buffer.put(escapeValue);
            else {
                buffer.put(data[i]);
            }

        }

        byte[] escapeValue = (byte[])ESCAPEMAP.get(Byte.valueOf(CRC));
        if (escapeValue != null)
            buffer.put(escapeValue);
        else {
            buffer.put(CRC);
        }

        buffer.put((byte)126);

        buffer.flip();

        byte[] codecBuffer = new byte[buffer.limit()];

        System.arraycopy(buffer.array(), 0, codecBuffer, 0, buffer.limit());

        msgBuf.setMessage(codecBuffer);

        out.write(buffer);
    }

    private byte getCRC(byte[] data)
    {
        if ((data != null) && (data.length > 0)) {
            byte crc = data[0];
            for (int i = 1; i < data.length; i++) {
                crc = (byte)(crc ^ data[i]);
            }
            return crc;
        }
        return 0;
    }
}
