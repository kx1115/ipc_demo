package com.yy.ipc.demo2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yy.ipc.R;

import java.util.List;

public class AIDLActivity extends AppCompatActivity {
    private static final String TAG = "AIDLActivity";

    private IBookManager bookManager;

    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bookManager=IBookManager.Stub.asInterface(iBinder);
            try {
                bookManager.addListener(mOnNewBookArrivedListener);
                //调用远程操作可能是一个耗时操作，最好在线程里去执行
                List<Book> list=bookManager.getBookList();
                Log.i(TAG, "book list:"+list.toString());
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.d(TAG, "receive new book: "+msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private IOnNewBookArrivedListener mOnNewBookArrivedListener=new IOnNewBookArrivedListener.Stub(){
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            Log.d(TAG, "onNewBookArrived: "+newBook);
            mHandler.obtainMessage(1,newBook).sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);


        Intent intent=new Intent(this,BookManagerService.class);
        bindService(intent,mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        //取消侦听
        if(bookManager!=null&&bookManager.asBinder().isBinderAlive()){
            try {
                bookManager.removeListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //解绑
        unbindService(mConn);
        super.onDestroy();
    }



}
