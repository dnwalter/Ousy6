package com.huaweisoft.ousy.socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * scoket客户端
 * Created by ousy on 2016/8/19.
 */
public class Client
{
    public static final String TAG = Client.class.getSimpleName();
    public static final int RECEIVE = 0;
    public static final int SEND = 1;
    private String mIp;
    private int mPort;
    private Socket mSocket = null;
    boolean mIsReceive = true;
    private IConnectResultListener mIConnectResultListener;
    private Handler mHandler;

    public Client(Handler handler)
    {
        this.mHandler = handler;
    }

    /**
     * 设置连接结果监听器
     *
     * @param mIConnectResultListener
     */
    public void setmIConnectResultListener(IConnectResultListener mIConnectResultListener)
    {
        this.mIConnectResultListener = mIConnectResultListener;
    }

    /**
     * 连接服务端
     *
     * @param ip
     * @param port
     */
    public void connnetToServer(String ip, int port)
    {
        mIp = ip;
        mPort = port;
        ConnectRunnable connectRunnable = new ConnectRunnable();
        new Thread(connectRunnable).start();
    }

    /**
     * 启动数据接收线程
     */
    public void startReceive()
    {
        new Thread(mReceiveTask).start();
    }


    /**
     * x
     * 连接指定ip跟端口的服务器
     */
    private void connect()
    {
        try
        {
            mSocket = new Socket(mIp, mPort);
            mIConnectResultListener.onConnectSuccess();
        } catch (IOException e)
        {
            e.printStackTrace();
            mIConnectResultListener.onConnectFailed();
            close();
        }
    }

    /**
     * 发送指令到服务端
     *
     * @param msg
     */

    public void send(String msg)
    {
        new Thread(mSendTask, msg).start();
    }

    Runnable mReceiveTask = new Runnable()
    {
        @Override
        public void run()
        {
            while (mIsReceive)
            {
                try
                {
                    mSocket = new Socket(mIp, mPort);
                    //接受服务器的信息
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    // 得到服务器信息
                    String msg = bufferedReader.readLine();
                    if (msg != null)
                    {
                        handleData(msg, Client.RECEIVE);
                    }
                    bufferedReader.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable mSendTask = new Runnable()
    {
        @Override
        public void run()
        {
            synchronized (this)
            {

                sendMsg(Thread.currentThread().getName());
                try
                {
                    Thread.sleep(500);
                    Log.e("ousy", "1miao");
                } catch (InterruptedException e)
                {
                    Log.e("ousy", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };

    private void sendMsg(String msg)
    {
        if (mSocket.isConnected())
        {
            // 把用户输入的内容发送给server
            if (!mSocket.isOutputShutdown())
            {
                try
                {
                    //向服务器发送信息
                    PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()
                    )), true);
                    printWriter.println(msg);
                    handleData(msg, Client.SEND);
                    printWriter.close();
                } catch (IOException e)
                {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    /**
     * 处理数据
     *
     * @param msg  信息
     * @param type 类型
     */
    private void handleData(String msg, int type)
    {
        Message message = new Message();
        Bundle data = new Bundle();
        message.what = type;
        String[] msgs = msg.split(",");
        data.putInt("type", Integer.parseInt(msgs[0]));
        data.putInt("cmd", Integer.parseInt(msgs[1]));
        message.setData(data);
        mHandler.sendMessage(message);
    }

    /**
     * 暂停接收
     */
    public void pause()
    {
        mIsReceive = false;
    }

    /*
    重新开始接收数据
     */
    public void restart()
    {
        mIsReceive = true;
    }


    /**
     * 关闭socket
     */
    public void close()
    {
        if (mSocket != null)
        {
            try
            {
                mSocket.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接线程
     */
    private class ConnectRunnable implements Runnable
    {
        @Override
        public void run()
        {
            connect();
        }
    }

    /**
     * 判断socket是否连接
     *
     * @return
     */
    public boolean getSocketConnectState()
    {
        if (mSocket == null)
        {
            return false;
        }
        return mSocket.isConnected();
    }
}
