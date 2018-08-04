package com.huaweisoft.ousy.test;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.views.BasePopupWindow;

/**
 * Created by ousy on 2016/5/20.
 */
public class TestPopupWindow extends BasePopupWindow implements View.OnClickListener
{
    private LinearLayout llytCamera;
    private LinearLayout llytPhoto;

    public TestPopupWindow(Context context)
    {
        super(context);
        initUI();
        initEvent();
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.popup_window_test;
    }

    private void initUI()
    {
        llytCamera = (LinearLayout) mView.findViewById(R.id.useractivity_llyt_camera);
        llytPhoto = (LinearLayout) mView.findViewById(R.id.useractivity_llyt_photo);
    }

    private void initEvent()
    {
        llytCamera.setOnClickListener(this);
        llytPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (null != mCallback)
        {
            mCallback.onItemClick(v.getId());
        }
    }
}
