package com.yy.ipc.demo3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SocketService extends Service {
    private static final String TAG = "SocketService";
    //此service是否destory
    private boolean mIsServiceDestoryed=false;
    //

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed=true;
        super.onDestroy();
    }

    private class TcpServer implements Runnable{

        @Override
        public void run() {
            ServerSocket serverSocket=null;
            try {
                serverSocket=new ServerSocket(8888);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            //侦听client的请求
            while (!mIsServiceDestoryed){
                try {
                    final Socket client = serverSocket.accept();
                    System.out.println("accept");
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private void responseClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
        //第一次进来推欢迎语
        out.println("welcome to service");

        while (!mIsServiceDestoryed){
            String str=in.readLine();
            Log.d(TAG, "client say: "+str);
            if(str==null)break;
            //回消息
            out.println("youu say "+str+":"+new Random().nextInt(100));
        }
        System.out.println("client quite.");

        out.close();
        in.close();
        client.close();
    }
}
