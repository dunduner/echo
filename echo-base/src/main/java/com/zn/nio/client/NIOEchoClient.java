package com.zn.nio.client;


import com.zn.info.HostInfo;
import com.zn.util.InputUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 新io的 客户端
 */
public class NIOEchoClient {

    public static void main(String[] args) {
        try {
            SocketChannel clientChannel = SocketChannel.open();
            clientChannel.connect(new InetSocketAddress(HostInfo.HOST_NAME,HostInfo.PORT));
            ByteBuffer byteBuffer = ByteBuffer.allocate(50);
            boolean flag = true;
            while (flag){
                byteBuffer.clear();
                String say = InputUtil.getKeyInfoString("要对服务器说的话：").trim();
                byteBuffer.put(say.getBytes());
                byteBuffer.flip();
                clientChannel.write(byteBuffer);
                byteBuffer.clear();// 在读取之前进行缓冲区清空
                int readCount = clientChannel.read(byteBuffer);
                byteBuffer.flip();
                System.out.println("服务器回话内容："+new String(byteBuffer.array(),0,readCount));
                if("byebye".equalsIgnoreCase(say)){
                    flag =false;
                }
            }
            clientChannel.close();
        } catch (IOException e) {
            System.out.println("客户端通道启动失败！");
            e.printStackTrace();
        }

    }

}
