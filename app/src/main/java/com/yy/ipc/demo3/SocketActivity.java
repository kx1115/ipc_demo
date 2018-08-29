package com.yy.ipc.demo3;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yy.ipc.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int MSG_RECEIVE_MSG=1;
    private static final int MSG_CONNECTED=2;

    private EditText mInput;
    private TextView mMessageTxt;

    //
    private Socket mClient;
    private PrintWriter mPrintWrite;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_CONNECTED:
                    mMessageTxt.append("client is connected~");
                    break;
                case MSG_RECEIVE_MSG:
                    String str= (String) msg.obj;
                    if(!TextUtils.isEmpty(str)){
                        mMessageTxt.append("\n receive:");
                        mMessageTxt.append(str);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        mInput=this.findViewById(R.id.et);
        mMessageTxt=this.findViewById(R.id.text);
        findViewById(R.id.btn).setOnClickListener(this);

        //启动服务
        startService(new Intent(this,SocketService.class));
        //启动本地client
        new Thread(){
            @Override
            public void run() {
                connectTcpServer();
            }
        }.start();

    }


    @Override
    protected void onDestroy() {
        //关闭
        if(mClient!=null){
            try {
                mClient.shutdownInput();
                mClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onDestroy();
    }

    //启动本地tcp连接
    private void connectTcpServer(){
        Socket socket=null;
        while (socket==null){
            try {
                socket=new Socket("localhost",8888);
                mClient=socket;
                mHandler.obtainMessage(MSG_CONNECTED).sendToTarget();
                mPrintWrite=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                System.out.println("connect server success");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //读取Server端的数据
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!SocketActivity.this.isFinishing()){
                String msg=br.readLine();
                System.out.println("receive :"+msg);
                if(msg!=null){
                    mHandler.obtainMessage(MSG_RECEIVE_MSG,msg).sendToTarget();
                }
            }

            //关闭
            mPrintWrite.close();
            br.close();
            mClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        final String msg=mInput.getText().toString();
        if(!TextUtils.isEmpty(msg)&&mPrintWrite!=null){
            mPrintWrite.write(msg);
            mInput.setText("");
            mMessageTxt.append("\n self:"+msg);
        }
    }
}
