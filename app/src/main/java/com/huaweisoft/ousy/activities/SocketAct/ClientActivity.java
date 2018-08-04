package com.huaweisoft.ousy.activities.SocketAct;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016/8/13.
 */

public class ClientActivity extends AppCompatActivity
{
    private static final int RECEIVE = 0;
    private static final int SEND = 1;
    private TextView text1;
    private Button btn;
    private EditText edit1;
    private final String DEBUG_TAG = "mySocketAct";
    private StringBuilder mBuilder = new StringBuilder();
    private Socket mSocket = null;
    private BufferedReader br;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        text1 = (TextView) findViewById(R.id.text1);
        btn = (Button) findViewById(R.id.btn);
        edit1 = (EditText) findViewById(R.id.edit);

        new Thread(mReceiveTask).start();
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
                new Thread(mSendTask, edit1.getText().toString()).start();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            if (null != mSocket)
            {
                mSocket.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
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
                    mSocket = new Socket("192.168.43.1", 30000);
                    //接受服务器的信息
                    br = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    String mstr = br.readLine();
                    if (mstr != null)
                    {
                        if (!mstr.equals("received"))
                        {
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("value", mstr);
                            msg.setData(data);

                            msg.what = RECEIVE;
                            handler.sendMessage(msg);
                            //向服务器发送信息
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket
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

                    //                    mSocket.shutdownInput();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 网络操作相关的子线程
     */
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

    private void sendMsg(String str)
    {
        String mesg = str;
        //        String mesg = edit1.getText().toString();
        try
        {
            //向服务器发送信息
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()
            )), true);
            out.println(mesg);

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
            mSocket.shutdownOutput();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
