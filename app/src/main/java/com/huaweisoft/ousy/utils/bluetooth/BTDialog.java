package com.huaweisoft.ousy.utils.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.adapters.BluetoothAdapter;
import com.huaweisoft.ousy.helpers.WindowSizeHelper;
import com.huaweisoft.ousy.interfaces.RecyclerViewCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙列表对话框
 * Created by ousy on 2016/10/13.
 */
public class BTDialog extends Dialog implements RecyclerViewCallback
{
    private RecyclerView mRecyclerView;
    private Context mContext;
    private BluetoothAdapter mAdapter;
    private List<BluetoothDevice> mList = new ArrayList<>();

    public BTDialog(Context context)
    {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 去掉title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_bluetooth, null);
        this.setContentView(layout);

        initView(layout);
        initEvent();


        //获取对话框的Window对象
        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        //透明的范围
        lp.alpha = 0.95f;
        lp.width = WindowSizeHelper.getWindowWidth() / 6 * 5;
        //        lp.height = WindowSizeHelper.getWindowHeight() / 12 * 5;
        //设置对话框在屏幕的底部显示，也可以在左、上、右位置
        mWindow.setGravity(Gravity.CENTER);
        //设置Window的属性
        mWindow.setAttributes(lp);
    }

    // 设置列表数据
    public void setList(List<BluetoothDevice> list)
    {
        mList = list;
    }

    public void refreshList(List<BluetoothDevice> list)
    {
        mList = list;
        mAdapter.refreshData(mList);
    }

    private void initView(View layout)
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview_bluetooth);
    }

    private void initEvent()
    {
        if (mList.size() > 5)
        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mRecyclerView.getLayoutParams();
            layoutParams.height = (int) mContext.getResources().getDimension(R.dimen.bluetooth_dialog_height);
            mRecyclerView.setLayoutParams(layoutParams);
        }
        mAdapter = new BluetoothAdapter(mContext, mList);
        mAdapter.setCallback(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public void setProgressVisiable(boolean visiable)
    {
        mAdapter.setProgressVisiable(visiable);
    }

    @Override
    public void onItemViewClick(int viewId, int position)
    {
        BluetoothUtil.INSTANCE.stopDiscovery();
        if (null != mList.get(position))
        {
            BluetoothUtil.INSTANCE.connect(position);
            dismiss();
        }
    }
}
