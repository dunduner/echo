package com.zn.bio.server;

import com.zn.info.HostInfo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOEchoServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(HostInfo.PORT);
        System.out.println("监听端口号"+HostInfo.PORT+",服务器端已经启动！");
        boolean flag = true;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        while (flag){
            Socket server = serverSocket.accept();
            executorService.submit(new EchoClientHandler(server));
        }
        executorService.isShutdown();
        serverSocket.close();

    }

    public static class EchoClientHandler implements Runnable {

        private Socket client;
        private Scanner scanner;
        private PrintStream out;
        private Boolean flag = true ;
        public EchoClientHandler(Socket client) throws IOException {
            this.client = client;
            this.scanner = new Scanner(this.client.getInputStream());
            this.out = new PrintStream(this.client.getOutputStream());
        }

        @Override
        public void run() {
            while (flag){
                if(scanner.hasNext()){
                    String val = scanner.next().trim();
                    System.out.println("服务器端："+val);
                    if("byebye".equalsIgnoreCase(val)){
                        out.println("再见！");
                        flag =false;
                    }else{
                        out.println("echo："+val);
                    }
                }
            }
            scanner.close();
            out.close();
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
