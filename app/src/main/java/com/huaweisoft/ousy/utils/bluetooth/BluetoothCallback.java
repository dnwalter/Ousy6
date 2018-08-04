package com.huaweisoft.ousy.utils.bluetooth;

/**
 * Created by ousy on 2016/9/29.
 */

// 蓝牙回调接口
public interface BluetoothCallback
{
    int SHOW = 0; // 展示
    int START_SEARCH = 1; // 展示
    int UPDATE = 2; // 更新
    int END_SEARCH = 3; // 搜索结束

    // 更新蓝牙列表
    void handleList(int type);
}