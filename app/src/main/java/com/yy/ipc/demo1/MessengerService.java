package com.yy.ipc.demo1;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yy.ipc.MyConstants;

import java.util.Timer;
import java.util.TimerTask;

public class MessengerService extends Service {
    private static final String TAG="MessengerService";
    //
    private static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstants.MSG_FROM_CLIENT:
                    Log.d(TAG,"receive msg from client:"+msg.getData().getString("msg"));
                    //下面为回复
                    Messenger client=msg.replyTo;
                    Message rMsg=Message.obtain(null,MyConstants.MSG_FROM_SERVICE);

                    Bundle bundle=new Bundle();
                    bundle.putString("msg","i see you");
                    rMsg.setData(bundle);

                    try {
                        client.send(rMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    //
    private final Messenger mMessenger=new Messenger(new MyHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
