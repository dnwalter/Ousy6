package com.huaweisoft.ousy.activities.BlueTooth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.utils.ToastUtil;
import com.huaweisoft.ousy.utils.bluetooth.BluetoothService;
import com.huaweisoft.ousy.utils.bluetooth.BluetoothUtil;

/**
 * Created by ousy on 2016/9/27.
 */

public class ServerChatAct extends AppCompatActivity
{
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private TextView text1;
    private Button btn;
    private EditText edit1;
    private BluetoothService mService = null;
    private StringBuilder mBuilder = new StringBuilder();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_chat);

        text1 = (TextView) findViewById(R.id.text1);
        btn = (Button) findViewById(R.id.btn);
        edit1 = (EditText) findViewById(R.id.edit);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                send(edit1.getText().toString());
//                send("hahahah");
//                send("我我我");
            }
        });

        initService();
    }

    @Override
    protected void onDestroy()
    {
        if (mService != null)
            mService.stop();
        super.onDestroy();
    }

    private void initService()
    {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mService = new BluetoothService(this, mHandler);
        if (mService.getState() == BluetoothService.STATE_NONE) {
            // Start the Bluetooth chat services
            mService.start();
        }
    }

    private void send(String message) {
        // Check that we're actually connected before trying anything
//        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
//            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mService.write(send);
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1)
                    {
                        case BluetoothService.STATE_CONNECTED:
                            //                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    mBuilder.append("\n我："+ writeMessage);
                    text1.setText(mBuilder.toString());
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mBuilder.append("\n你："+ readMessage);
                    text1.setText(mBuilder.toString());
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String deviceName = msg.getData().getString(DEVICE_NAME);
                    ToastUtil.toastShort("已连上" + deviceName);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
