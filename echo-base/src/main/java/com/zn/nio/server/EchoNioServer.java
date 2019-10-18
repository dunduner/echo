package com.zn.nio.server;

import com.zn.info.HostInfo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoNioServer {
    private static class EchoClientHandler implements Runnable {
        private SocketChannel clientChannel;//客户端通道
        private boolean flag = true;

        public EchoClientHandler(SocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(50);
            try {
                while (this.flag) {
                    buffer.clear();
                    //从缓冲区读取的数据的长度
                    int readCount = this.clientChannel.read(buffer);
                    String readMessage = new String(buffer.array(), 0, readCount);
                    System.out.println("{客户端说}："+readMessage);
                    //回应数据
                    String writeMessage = "{echo响应数据}" + readMessage + "\n";
                    if ("byebye".equalsIgnoreCase(readMessage)) {
                        writeMessage = "通讯结束！";
                        this.flag = false;
                    }
                    // 数据输入通过缓存的形式完成，而数据的输出同样需要进行缓存操作
                    buffer.clear();// 为了写入新的返回数据而定义
                    buffer.put(writeMessage.getBytes());// 发送内容
                    buffer.flip();// 重置缓冲区
                    this.clientChannel.write(buffer);
                }
                this.clientChannel.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        //1、nio的实现考虑到性能问题，以及相应的时间问题，需要设置一个线程池，采用fixTheadPool
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //2.NIO的处理是基于channl通道控制的，所有有一个selector，就是负责管理所有的通道的
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3 需要为其设置非阻塞状态
        serverSocketChannel.configureBlocking(false);
        //4.服务器上需要提供一个由网络监听的端口
        serverSocketChannel.bind(new InetSocketAddress(HostInfo.PORT));
        //5.需要设置一个selecttor 作为选择器的出现 目的是管理所有统统
        Selector selector = Selector.open();
        //6.将当前的channel 注册到选择器管理者中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器已经启动成功，服务器的监听端口为：" + HostInfo.PORT);
        //7.NIO采用的是轮训模式，每当发现用户连接的时候，就需要启动一个线程，当然是用线程池管理
        int keySelect = 0;
        while ((keySelect = selector.select()) > 0) {//实现了轮序处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectIterator = selectionKeys.iterator();
            while (selectIterator.hasNext()) {
                SelectionKey selectionKey = selectIterator.next();// 获取每一个Key的信息
                if (selectionKey.isAcceptable()) {//为监听模式，连接模式
                    SocketChannel clientChannel = serverSocketChannel.accept();
                    if (clientChannel != null) {
                        //TODO
                        executorService.submit(new EchoClientHandler(clientChannel));
                    }
                }
            }
        }


    }

}
