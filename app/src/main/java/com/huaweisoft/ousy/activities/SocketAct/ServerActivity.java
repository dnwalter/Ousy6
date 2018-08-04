package com.huaweisoft.ousy.activities.SocketAct;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016/8/13.
 */

public class ServerActivity extends AppCompatActivity
{
    private static final int RECEIVE = 0;
    private static final int SEND = 1;
    private TextView text1;
    private Button btn;
    private Button btnServer;
    private EditText edit1;
    private StringBuilder mBuilder = new StringBuilder();
    private Socket mClient = null;
    private ServerSocket mServerSocket = null;
    private BufferedReader in;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        text1 = (TextView) findViewById(R.id.text1);
        btn = (Button) findViewById(R.id.btn);
        btnServer = (Button) findViewById(R.id.startServer);
        edit1 = (EditText) findViewById(R.id.edit);
        try
        {
            mServerSocket = new ServerSocket(30000);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        btnServer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                text1.setText("等待接收用户连接：");
                new Thread(mReceiveTask).start();
            }
        });
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
                //                new Thread(mSendTask, edit1.getText().toString()).start();
                new Thread(mSendTask, "6,14").start();
                new Thread(mSendTask, "4,1").start();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        if (null != mClient)
        {
            try
            {
                mClient.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            switch (msg.what)
            {
                case RECEIVE:
                    mBuilder.append("\n你：" + val);
                    ToastUtil.toastShort(val);
                    if (val.equals("291"))
                    {
                        new Thread(mSendTask, "6,14").start();
                        new Thread(mSendTask, "4,1").start();
                    }
                    break;
                case SEND:
                    mBuilder.append("\n我：" + val);
                    edit1.setText("");
                    break;
                default:
                    break;
            }
            // UI界面的更新等相关操作
            text1.setText(mBuilder.toString());
        }
    };

    Runnable mReceiveTask = new Runnable()
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    //接受客户端请求
                    mClient = mServerSocket.accept();
                    //接受客户端信息
                    in = new BufferedReader(new InputStreamReader(mClient.getInputStream()));
                    String str = in.readLine();
                    if (str != null)
                    {
                        if (!str.equals("received"))
                        {
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("value", str);
                            msg.setData(data);

                            msg.what = RECEIVE;
                            handler.sendMessage(msg);
                            //向服务器发送信息
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClient
                                    .getOutputStream()
                            )), true);
                            out.println("received");
                            out.close();
                        }
                    }
                    //                    else
                    //                    {
                    //                        data.putString("value", "数据错误");
                    //                        msg.setData(data);
                    //                    }

                    //                    mClient.shutdownInput();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    Log.e("ousy", "receive: " + ex.getMessage());
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
                } catch (InterruptedException e)
                {
                    Log.e("ousy", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };

    private void sendMsg(String mesg)
    {
        try
        {
            //向客户端发送消息
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClient.getOutputStream())),
                    true);

            out.println(mesg);
            Log.e("ousy", mesg + ":");
            Message msg = new Message();
            Bundle data = new Bundle();
            if (mesg != null)
            {
                data.putString("value", mesg);
                msg.setData(data);
            }
            else
            {
                data.putString("value", "数据错误");
                msg.setData(data);
            }
            msg.what = SEND;
            handler.sendMessage(msg);
            out.flush();
            mClient.shutdownOutput();
            Log.e("ousy1", "send: " + mClient.isOutputShutdown());
            Log.e("ousy1", "send: " + mClient.isInputShutdown());
        } catch (IOException e)
        {
            Log.e("ousy", e.getMessage());
            Log.e("ousy1", "send: " + mClient.isOutputShutdown());
            e.printStackTrace();
        }
    }
}
