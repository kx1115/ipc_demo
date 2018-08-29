package com.yy.ipc.demo1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yy.ipc.MyConstants;
import com.yy.ipc.R;

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG="MessengerActivity";
    //服务端的Messenger
    private Messenger mService;
    //
    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService=new Messenger(iBinder);

            Message msg= Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
            Bundle bundle=new Bundle();
            bundle.putString("msg","client is connect");
            msg.setData(bundle);

            //=此句重点=传一个Messenger过去，让服务端可以传消息过来
            msg.replyTo=mReplyMessenger;

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    //服务端的回调
    private Messenger mReplyMessenger=new Messenger(new ReplyHandler());
    private static class ReplyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstants.MSG_FROM_SERVICE:
                    Log.d(TAG,"receive msg from service:"+msg.getData().getString("msg"));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Intent intent=new Intent(this,MessengerService.class);

        bindService(intent,mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConn);
        super.onDestroy();
    }
}
