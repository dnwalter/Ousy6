package com.huaweisoft.ousy.utils.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.huaweisoft.ousy.utils.ToastUtil;
import com.huaweisoft.ousy.views.BasePopupWindow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 权限：android.permission.BLUETOOTH
 * android.permission.BLUETOOTH_ADMIN
 * Created by ousy on 2016/9/27.
 */

public enum  BluetoothUtil
{
    INSTANCE;
    public static final int REQUEST_ENABLE_BLUETOOTH = 1;
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    private static final String TAG = BluetoothUtil.class.getSimpleName();
    private String mPin = "1234";
    private int mState = 0;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothReceiver mReceiver;
    private BluetoothCallback mCallback;
    private List<BluetoothDevice> mBluetoothList; // 总的蓝牙设备列表
    private List<BluetoothDevice> mBondedDevices; // 配对过的蓝牙设备
    private List<BluetoothDevice> mNewDevices; // 新搜索到的蓝牙设备
    // 当前要连接的设备地址
    public String mAddress;

    public void setCallback(BluetoothCallback callback)
    {
        mCallback = callback;
    }

    public BluetoothDevice getDevice()
    {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);

        return device;
    }

    // 一定要先初始化
    public void init(Context context, BluetoothCallback callback)
    {
        mContext = context;
        mCallback = callback;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            ToastUtil.toastShort("Bluetooth is not available");
        }
        else
        {
            mBluetoothList = new ArrayList<>();
            mBondedDevices = new ArrayList<>();
            mNewDevices = new ArrayList<>();
            if (!mBluetoothAdapter.isEnabled())
            {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) mContext).startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                // Otherwise, setup the chat session
            }
            else
            {
                initBluetooth();
            }
        }
    }

    // 初始化蓝牙
    public void initBluetooth()
    {
        initBluetoothList();
        initReceiver();
        //        doDiscovery();
    }

    // 初始化蓝牙列表
    public void initBluetoothList()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                mBondedDevices.add(device);
            }
        }
        mBluetoothList.addAll(mBondedDevices);
        // 把曾经匹配过的第一个设备的地址，设为当前连接的地址
        mAddress = mBondedDevices.get(0).getAddress();
    }

    private void initReceiver()
    {
        mReceiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND); // 搜索过程接收器进的action
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // 搜索完毕接收器进的action
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST); // 配对过程中接收器进的action
        mContext.registerReceiver(mReceiver, filter);
    }

    public boolean isOpen()
    {
        return mBluetoothAdapter.isEnabled();
    }

    public void openBluetooth()
    {
        if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mContext).startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            // Otherwise, setup the chat session
        }
        else
        {
            initBluetooth();
            mCallback.handleList(BluetoothCallback.SHOW);
        }
    }

    // 不问是否的打开蓝牙
    public void openBluetooth(final String name)
    {
        if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.enable();
            // 要等待蓝牙开启完成才能成功的改名字
            // 所以延迟2500ms再设置蓝牙的名字
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mBluetoothAdapter.setName(name);
                }
            }, 2500);
        }
        else
        {
            initBluetooth();
        }
    }

    public void setName(String name)
    {
        if (null != mBluetoothAdapter)
        {
            mBluetoothAdapter.setName(name);
        }
    }

    public void closeBluetooth()
    {
        if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.disable();
        }
    }

    public List<BluetoothDevice> getBluetoothList()
    {
        return mBluetoothList;
    }

    public void clearList()
    {
        mBluetoothList.clear();
        mBondedDevices.clear();
        mNewDevices.clear();
    }

    /**
     * 搜索蓝牙设备
     */
    public void doDiscovery()
    {
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering())
        {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
        mCallback.handleList(BluetoothCallback.START_SEARCH);
    }

    /**
     * 停止搜索
     */
    public void stopDiscovery()
    {
        if (mBluetoothAdapter.isDiscovering())
        {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    // 删除已匹配过的设备
    public void unpairDevice(BluetoothDevice device)
    {
        try
        {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public boolean connect(int position)
    {
        //        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Cancel any thread attempting to make a connection
        //        if (mState == STATE_CONNECTING)
        //        {
        //            if (mConnectThread != null)
        //            {
        //                mConnectThread.cancel();
        //                mConnectThread = null;
        //            }
        //        }
        //
        //        // Cancel any thread currently running a connection
        //        if (mConnectedThread != null)
        //        {
        //            mConnectedThread.cancel();
        //            mConnectedThread = null;
        //        }
        //
        //        // Start the thread to connect with the given device
        //        mConnectThread = new ConnectThread(device);
        //        mConnectThread.start();
        //        setState(STATE_CONNECTING);
        BluetoothDevice device = null;
        boolean ret = false;
        if (position < mBluetoothList.size())
        {
            device = mBluetoothList.get(position);
            mAddress = device.getAddress();
        }

        if (null != device)
        {
            if (device.getBondState() == BluetoothDevice.BOND_NONE)
            {
                try
                {
                    //通过工具类ClsUtils,调用createBond方法
                    ret = ClsUtils.createBond(device.getClass(), device);
                    //                mDevice = device;
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (device.getBondState() == BluetoothDevice.BOND_BONDED)
            {
                ret = true;
            }
        }

        return ret;
    }

    public int getState()
    {
        return mState;
    }

    public void cancel()
    {
        if (mBluetoothAdapter != null)
        {
            mBluetoothAdapter.disable();
            mBluetoothAdapter.cancelDiscovery();
        }
        if (null != mReceiver)
        {
            mContext.unregisterReceiver(mReceiver);
        }
    }


    private class BluetoothReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // When discovery finds a device
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    if (!isRepeatDevice(device))
                    {
                        mNewDevices.add(device);
                        mBluetoothList.add(device);
                        mCallback.handleList(BluetoothCallback.UPDATE);
                    }
                }
            }
            // When discovery is finished, change the Activity title
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                mCallback.handleList(BluetoothCallback.END_SEARCH);
                if (mNewDevices.size() <= 0)
                {
                    //                    String noDevices = getResources().getText(R.string.none_found).toString();
                    //                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
            // 进行ClsUtils.createBond(device.getClass(), device)
            // 接收器会接到这个action
            else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action))
            {
                //                if (device.getName().contains("HUAWEI G9 Youth"))
                //                {
                try
                {
                    //1.确认配对
                    ClsUtils.setPairingConfirmation(device.getClass(), device, true);
                    //2.终止有序广播
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    //3.调用setPin方法进行配对...
                    boolean ret = ClsUtils.setPin(device.getClass(), device, mPin);
                    Log.e("bluetooth", ret + ":");
                } catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //                }
            }
        }
    }


    //
    private boolean isRepeatDevice(BluetoothDevice device)
    {
        boolean isRepeat = false;
        for (BluetoothDevice i : mNewDevices)
        {
            if (i.getAddress().equals(device.getAddress()))
            {
                isRepeat = true;
                break;
            }
        }

        return isRepeat;
    }
}
