package edu.xmu.hwb.codec;


import edu.xmu.hwb.extype.HexStringBinary;
import edu.xmu.hwb.jt808base.JT808Message;
import edu.xmu.hwb.streamtype.Offset;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class JT808Decoder extends CumulativeProtocolDecoder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JT808Decoder.class);
    private static final byte BEGIN = 126;
    private static final byte END = 126;
    private static final byte ESCAPE = 125;
    /**
     * 转义还原列表
     */
    @SuppressWarnings("serial")
    private static final HashMap<Byte, Byte> ANTIESCAPEMAP = new HashMap<Byte, Byte>() {
        {
            put(Byte.valueOf((byte) 0x01), Byte.valueOf((byte) 0x7D));
            put(Byte.valueOf((byte) 0x02), Byte.valueOf((byte) 0x7E));
        }
    };

    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception
    {
        boolean takeBegin = false;
        boolean takeEnd = false;
        int dataLength = 0;

        for (int i = 0; i < in.limit(); i++) {
            byte curByte = in.get(i);
            if ((curByte == 126) &&
                    (!takeBegin)) {
                in.position(i);
                takeBegin = true;
            }
            else
            {
                if ((curByte == 126) &&
                        (takeBegin))
                {
                    dataLength = i - in.position() + 1;
                    takeEnd = true;
                }

                if ((takeBegin) && (takeEnd))
                {
                    byte[] codecBuffer = new byte[dataLength];

                    in.get(codecBuffer, 0, dataLength - 1);
                    codecBuffer[(dataLength - 1)] = 126;

                    if (codecBuffer.length > 2)
                    {
                        byte[] antiEscapedData = antiEscape(codecBuffer, 1, dataLength - 2);

                        if (getCRC(antiEscapedData) == 0)
                        {
                            byte[] message = new byte[antiEscapedData.length - 1];
                            System.arraycopy(antiEscapedData, 0, message, 0, message.length);

                            out.write(new JT808Message().parse(message,new Offset()).setMessage(codecBuffer));
                        }
                        else if (LOGGER.isErrorEnabled()) {
                            String head;
                            if (session != null)
                                head = "[" + session.getId() + "] => " + session.getRemoteAddress();
                            else {
                                head = "[unkown] => " + session.getRemoteAddress();
                            }
                            LOGGER.error(head + " CRC校验失败！长度 = " + dataLength+" "+HexStringBinary.ByteArrayToHexString(codecBuffer));
                        }

                    }

                    takeBegin = false;
                    takeEnd = false;

                    i -= 1;
                }
            }
        }
        if (in.remaining() > 0) {
            return false;
        }
        return true;
    }

    private byte[] antiEscape(byte[] data, int pos, int length)
    {
        if (data == null) {
            return null;
        }
        byte[] tmpData = new byte[data.length];
        int offset = 0;
        int endPos = pos + length;
        for (int i = pos; i < endPos; i++) {
            if ((data[i] == 125) && (i + 1 < data.length)) {
                Byte antiEscapeValue = (Byte)ANTIESCAPEMAP.get(Byte.valueOf(data[(i + 1)]));
                if (antiEscapeValue != null) {
                    tmpData[(offset++)] = antiEscapeValue.byteValue();
                    i++;
                } else {
                    tmpData[(offset++)] = data[i];
                }
            } else {
                tmpData[(offset++)] = data[i];
            }
        }

        byte[] result = new byte[offset];

        System.arraycopy(tmpData, 0, result, 0, offset);

        return result;
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