package edu.xmu.hwb.streamtype;

import edu.xmu.hwb.jt808base.StreamBuffer;

/**
 * Created by Herrfe on 14-8-22.
 */
public class AttachedData {
    /**
     * 附加信息ID
     */
    private ByteBinary id = new ByteBinary();
    /**
     * 附加信息
     */
    private StreamBuffer content;

    /**
     * 构造附加消息项实例
     * @param id		附加信息ID
     * @param content	附加信息
     */
    public AttachedData(byte id, StreamBuffer content) {
        this.id.setValue(id);
        this.content = content;
    }

    /**
     * 获取该数据类型二进制数据长度
     */
    public int getBinaryLength() {
        return content.getBinaryLength() + 2;
    }

    /**
     * 获取该数据类型实例的二进制数据，其结构如下：
     * 附加信息ID + 附加信息长度  + 附加信息；
     * 其中：附加信息ID、附加信息长度均占一个字节，附加信息视具体消息而定。
     *
     * @return 该数据类型实例的二进制数据
     * @throws Exception
     */
    public byte[] array() throws Exception {
        byte length = (byte) content.getBinaryLength();
        byte[] b = new byte[length + 2];

        b[0] = id.getValue();
        b[1] = length;

        byte[] src = content.array();
        System.arraycopy(src, 0, b, 2, length);

        return b;
    }

    /**
     * 获取附加信息ID
     */
    public byte getID() {
        return id.getValue();
    }

    /**
     * 获取附加信息
     */
    public StreamBuffer getContent() {
        return content;
    }

}
