package com.huaweisoft.ousy.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.huaweisoft.ousy.R;

/**
 * 弹出窗口基类
 * exmple:
 * int navigationBarHeight = PhoneHelper.getNavigationBarHeight(MainActivity.this);
 * TestPopupWindow popupWindow=new TestPopupWindow(this);
 * popupWindow.setCallback(this);
 * popupWindow.showAtLocation(findViewById(R.id.layout_main), Gravity.BOTTOM, 0,navigationBarHeight);
 * Created by ousy on 2016/5/19.
 */
public abstract class BasePopupWindow extends PopupWindow
{
    private Context mContext;
    protected View mView;
    protected PopupWindowCallback mCallback;

    public BasePopupWindow(Context context)
    {
        mContext = context;

        mView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);

        // 设置视图
        this.setContentView(mView);
        // 设置弹出窗体的宽和高
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.PopupWindowAnim);

        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = 0.7f;
        ((Activity) mContext).getWindow().setAttributes(lp);
        this.setOnDismissListener(new OnDismissListener()
        {

            @Override
            public void onDismiss()
            {
                WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity) mContext).getWindow().setAttributes(lp);
            }
        });
    }

    protected abstract int getLayoutId();

    public interface PopupWindowCallback
    {
        void onItemClick(int id);
    }

    public void setCallback(PopupWindowCallback callback)
    {
        mCallback=callback;
    }
}
