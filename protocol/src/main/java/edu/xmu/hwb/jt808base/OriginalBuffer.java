package edu.xmu.hwb.jt808base;

/**
 * Created by Herrfe on 14-8-20.
 */
public interface OriginalBuffer {
    /**
     * 获取编码前/解码后的消息
     */
    public byte[] getMessage() ;

    /**
     * 设置编码前/解码后的消息
     */
    public StreamBuffer setMessage(byte[] message) ;
}
