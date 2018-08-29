package com.yy.ipc.demo2;

import com.yy.ipc.demo2.Book;
import com.yy.ipc.demo2.IOnNewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void addListener(IOnNewBookArrivedListener listener);
    void removeListener(IOnNewBookArrivedListener listener);
}
