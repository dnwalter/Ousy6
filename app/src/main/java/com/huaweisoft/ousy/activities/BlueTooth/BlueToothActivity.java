package com.huaweisoft.ousy.activities.BlueTooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.receiver.BluetoothChangeReceiver;
import com.huaweisoft.ousy.utils.bluetooth.BTDialog;
import com.huaweisoft.ousy.utils.bluetooth.BluetoothCallback;
import com.huaweisoft.ousy.utils.bluetooth.BluetoothUtil;
import com.huaweisoft.ousy.utils.ToastUtil;

import java.util.List;

/**
 * 蓝牙
 * Created by ousy on 2016/9/27.
 */

public class BlueToothActivity extends AppCompatActivity implements View.OnClickListener, BluetoothCallback, BluetoothChangeReceiver.BluetoothCallback

{
    private Button btnClient;
    private Button btnServer;
    private Button btnConnect;
    private BTDialog mDialog;
    private boolean mIsShowDialog = false;
    private List<BluetoothDevice> mBluetoothList;
    private BluetoothChangeReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        btnClient = (Button) findViewById(R.id.btn_client_chat);
        btnServer = (Button) findViewById(R.id.btn_server_chat);
        btnConnect = (Button) findViewById(R.id.btn_connect);

        btnClient.setOnClickListener(this);
        btnServer.setOnClickListener(this);
        btnConnect.setOnClickListener(this);

        // 初始化蓝牙
        BluetoothUtil.INSTANCE.init(this, BlueToothActivity.this);
        // 初始化监听蓝牙开关接收器
        mReceiver = new BluetoothChangeReceiver(this);
        mReceiver.setCallback(this);
    }

    @Override
    protected void onDestroy()
    {
        BluetoothUtil.INSTANCE.cancel();
        mReceiver.unregister();
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent();
        switch (v.getId())
        {
            case R.id.btn_client_chat:
                intent.setClass(this, ClientChatAct.class);
                startActivity(intent);
//                BluetoothUtil.getInstance().setName("aaaaa");
                break;
            case R.id.btn_server_chat:
                intent.setClass(this, ServerChatAct.class);
                startActivity(intent);
                break;
            case R.id.btn_connect:
                mIsShowDialog = true;
                if (BluetoothUtil.INSTANCE.isOpen())
                {
                    initDialog();
                }
                else
                {
                    BluetoothUtil.INSTANCE.openBluetooth("hahahah");
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            //            case REQUEST_CONNECT_DEVICE:
            //                // When DeviceListActivity returns with a device to connect
            //                if (resultCode == Activity.RESULT_OK) {
            //                    // Get the device MAC address
            //                    String address = data.getExtras()
            //                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            //                    // Get the BLuetoothDevice object
            //                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            //                    // Attempt to connect to the device
            //                    mChatService.connect(device);
            //                }
            //                break;
            case BluetoothUtil.REQUEST_ENABLE_BLUETOOTH:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {
                    ToastUtil.toastShort("成功");
                    BluetoothUtil.INSTANCE.initBluetooth();
                    if (mIsShowDialog)
                    {
                        initDialog();
                    }
                    //                    setupChat();
                }
                else
                {
                    //                    finish();
                }
        }
    }

    @Override
    public void handleList(int type)
    {
        switch (type)
        {
            case BluetoothCallback.SHOW:
                initDialog();
                break;
            case BluetoothCallback.START_SEARCH:
                if (null != mDialog && mDialog.isShowing())
                {
                    mDialog.setProgressVisiable(true);
                }
                break;
            case BluetoothCallback.UPDATE:
                if (null != mDialog && mDialog.isShowing())
                {
                    mBluetoothList = BluetoothUtil.INSTANCE.getBluetoothList();
                    mDialog.refreshList(mBluetoothList);
                }
                break;
            case BluetoothCallback.END_SEARCH:
                if (null != mDialog && mDialog.isShowing())
                {
                    mDialog.setProgressVisiable(false);
                }
                break;
            default:
                break;
        }
    }

    private void initDialog()
    {
        BluetoothUtil.INSTANCE.clearList();
        BluetoothUtil.INSTANCE.initBluetoothList();
        mBluetoothList = BluetoothUtil.INSTANCE.getBluetoothList();
        mDialog = new BTDialog(this);
        mDialog.setList(mBluetoothList);
        mDialog.show();
        BluetoothUtil.INSTANCE.doDiscovery();
    }

    @Override
    public void state(int type)
    {

    }
}
