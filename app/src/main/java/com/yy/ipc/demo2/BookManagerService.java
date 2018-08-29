package com.yy.ipc.demo2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yy.ipc.demo2.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {
    private static final String TAG="BookManagerService";
    private CopyOnWriteArrayList<Book> mBookList=new CopyOnWriteArrayList<>();
    //
    private AtomicBoolean mIsServiceDestoryed=new AtomicBoolean(false);
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList=new RemoteCallbackList<>();

    private Binder mBinder=new IBookManager.Stub(){
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void addListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
        }

        @Override
        public void removeListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }
    };

    private void onNewBookArrived(Book book) throws RemoteException {
        Log.d(TAG, "onNewBookArrived: "+book);
        mBookList.add(book);
        // RemoteCallbackList的遍历
        final int len=mListenerList.beginBroadcast();
        Log.d(TAG, "onNewBookArrived: len:"+len);
        for(int i=0;i<len;i++){
            IOnNewBookArrivedListener l=mListenerList.getBroadcastItem(i);
            if(l!=null){
                Log.d(TAG, "onNewBookArrived call");
                l.onNewBookArrived(book);
            }else{
                Log.d(TAG, "l is null");
            }
        }
        mListenerList.finishBroadcast();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //开启线程定时增加
        new Thread(new AddBookWork()).start();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }


    private class AddBookWork implements Runnable{
        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()){
                SystemClock.sleep(5000);
                int bookId=mBookList.size()+1;
                Book book=new Book(bookId,"book#"+bookId);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
