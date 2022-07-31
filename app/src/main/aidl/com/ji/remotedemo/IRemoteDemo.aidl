// IRemoteDemo.aidl
package com.ji.remotedemo;

// Declare any non-default types here with import statements

interface IRemoteDemo {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

    int register(com.ji.remotedemo.IRemoteCallback callback);

    int unregister(com.ji.remotedemo.IRemoteCallback callback);

    String getData();

    int setData(String data);
}
