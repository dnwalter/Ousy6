package com.huaweisoft.ousy.socket;

/**
 * 连接结果监听
 * Created by macc on 2016/7/14.
 */

public interface IConnectResultListener
{
    void onConnectSuccess();

    void onConnectFailed();
}
