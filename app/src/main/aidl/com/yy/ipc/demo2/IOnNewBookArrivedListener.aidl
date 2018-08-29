// IOnNewBookArrivedListener.aidl
package com.yy.ipc.demo2;

import com.yy.ipc.demo2.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
