// IRemoteDemo.aidl
package com.ji.remotedemo;

interface IRemoteDemo {
    int register(com.ji.remotedemo.IRemoteCallback callback);

    int unregister(com.ji.remotedemo.IRemoteCallback callback);

    String getData();

    int setData(String data);
}
