package com.huaweisoft.ousy.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.huaweisoft.ousy.MainApplication;
import com.huaweisoft.ousy.R;

/**
 * 提示信息帮助类
 * Created by ousy on 2016/5/18.
 */
public class HintHelper
{
    public static final int NO_ICON = -1;
    private static final int ASK_ICON = NO_ICON;
    private static final int ERROR_ICON = NO_ICON;
    private static final int WARN_ICON = NO_ICON;
    private static final String ASK_TITLE = "询问";
    private static final String ERROR_TITLE = "错误";
    private static final String WARN_TITLE = "警告";
    private static HintHelper sHelper;
    private int mIconId;
    // 确认字段
    private String mPStr;
    // 取消字段
    private String mNStr;
    private boolean[] mCheckedItems;
    private String mDate;
    private String mTime;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public HintHelper()
    {
        initDefaultStr();
        initDefaultIconId();
    }

    // 初始化默认字段
    private void initDefaultStr()
    {
        mPStr = MainApplication.getContext().getString(R.string.dialog_positive);
        mNStr = MainApplication.getContext().getString(R.string.dialog_negative);
    }

    // 初始化默认图标
    private void initDefaultIconId()
    {
        mIconId = NO_ICON;
    }

    public synchronized static HintHelper getInstance(Context context)
    {
        if (null == sHelper)
        {
            synchronized (HintHelper.class)
            {
                sHelper = new HintHelper();
            }
        }
		// 注：使用前要修改为该activity的context
        sHelper.setContext(context);

        return sHelper;
    }

	
    private void setContext(Context context)
    {
        mContext = context;
    }

    /**
     * 自定义取消字段
     *
     * @param nStr 所要求文字
     */
    public void setNegativeStr(String nStr)
    {
        mNStr = nStr;
    }

    /**
     * 自定义确认字段
     *
     * @param pStr 所要求文字
     */
    public void setPositiveStr(String pStr)
    {
        mPStr = pStr;
    }

    /***
     * 自定义图标
     * 默认没图标
     *
     * @param iconId 图标id
     */
    public void setIconId(int iconId)
    {
        mIconId = iconId;
    }

    /***
     * 短提示信息
     *
     * @param hint 提示信息
     */
    public void toastShort(String hint)
    {
        Toast toast = Toast.makeText(MainApplication.getContext(), hint, Toast.LENGTH_SHORT);
        toast.show();
    }

    /***
     * 长提示信息
     *
     * @param hint 提示信息
     */
    public void toastLong(String hint)
    {
        Toast toast = Toast.makeText(MainApplication.getContext(), hint, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * 不含进度条对话框
     *
     * @param title 标题
     * @param msg   提示信息
     */
    public void progressDialogOne(String title, String msg)
    {
        mProgressDialog = ProgressDialog.show(mContext, title, msg, false, false);
    }

    /**
     * 含进度条对话框
     *
     * @param title 标题
     * @param msg   提示信息
     */
    public void progressDialogTwo(String title, String msg)
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMax(100);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mProgressDialog.show();
    }

    /**
     * 设置进度条百分比
     *
     * @param percent 百分比
     */
    public void setProgress(int percent)
    {
        if (null != mProgressDialog && mProgressDialog.isShowing())
        {
            mProgressDialog.setProgress(percent);
        }
    }

    /**
     * 取消进度提示框
     */
    public void cancelProgressDialog()
    {
        if (null != mProgressDialog && mProgressDialog.isShowing())
        {
            mProgressDialog.cancel();
        }
    }

    /**
     * 询问对话框
     *
     * @param msg     提示信息
     * @param pAction 确定执行事件
     * @param nAction 取消执行事件
     */
    public void askDialog(String msg, IDialogAction pAction, IDialogAction nAction)
    {
        mIconId = ASK_ICON;
        showSimpleDialog(ASK_TITLE, msg, pAction, nAction, true);
    }

    /**
     * 警告对话框
     *
     * @param msg 警告信息
     */
    public void warnDialog(String msg)
    {
        mIconId = WARN_ICON;
        showSimpleDialog(WARN_TITLE, msg, null, null, false);
    }

    /**
     * 错误提示对话框
     *
     * @param msg 错误信息
     */
    public void errorDialog(String msg)
    {
        mIconId = ERROR_ICON;
        showSimpleDialog(ERROR_TITLE, msg, null, null, false);
    }

    /**
     * 简单对话框
     * 含取消按键
     *
     * @param title          标题
     * @param msg            提示信息
     * @param pAction        确定执行事件
     * @param nAction        取消执行事件
     * @param isHaveNegative 是否有取消按钮
     */
    private void showSimpleDialog(String title, String msg, final IDialogAction pAction,
                                  final IDialogAction nAction, boolean isHaveNegative)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(mPStr, new DialogOnClick(pAction));
        if (isHaveNegative)
        {
            builder.setNegativeButton(mNStr, new DialogOnClick(nAction));
        }
        if (NO_ICON != mIconId)
        {
            builder.setIcon(mIconId);
            initDefaultIconId();
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        initDefaultStr();
    }

    /**
     * 列表对话框
     *
     * @param title  标题
     * @param items  列表的item
     * @param action 点击item执行事件
     */
    public void itemDialog(String title, String[] items, IDialogAction action)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setItems(items, new DialogOnClick(action));
        if (NO_ICON != mIconId)
        {
            builder.setIcon(mIconId);
            initDefaultIconId();
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 多项对话框
     *
     * @param title        标题
     * @param items        列表item内容
     * @param checkedItems item是否被点击的布尔值数组
     * @param action       确定执行事件
     */
    public void multiDialog(String title, String[] items, boolean[] checkedItems, final IMultiDialogAction action)
    {
        mCheckedItems = checkedItems;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setNegativeButton(mNStr, null);
        if (NO_ICON != mIconId)
        {
            builder.setIcon(mIconId);
            initDefaultIconId();
        }
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked)
            {
                mCheckedItems[which] = isChecked;
            }
        });
        builder.setPositiveButton(mPStr, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                action.action(mCheckedItems);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        initDefaultStr();
    }

    /***
     * 日期选择对话框
     *
     * @param title   标题
     * @param date    日期
     * @param pAction 确定执行事件
     */
    public void dateDialog(String title, String date, final IDialogAction pAction)
    {
        final DialogOnClick dialogOnClick = new DialogOnClick(pAction);
        String[] dates = date.split("-");
        int year = Integer.valueOf(dates[0]);
        int month = Integer.valueOf(dates[1]);
        int day = Integer.valueOf(dates[2]);

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_date, null);
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.dialog_datepicker);
        datePicker.init(year, month - 1, day, new DatePicker.OnDateChangedListener()
        {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                mDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                dialogOnClick.setObject(mDate);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setView(view)
                .setNegativeButton(mNStr, null)
                .setPositiveButton(mPStr, dialogOnClick);
        if (NO_ICON != mIconId)
        {
            builder.setIcon(mIconId);
            initDefaultIconId();
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        initDefaultStr();
    }

    /**
     * 时间选择对话框
     *
     * @param title   标题
     * @param pAction 确定执行事件
     */
    public void timeDialog(String title, final IDialogAction pAction)
    {
        final DialogOnClick dialogOnClick = new DialogOnClick(pAction);

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_time, null);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.dialog_timepicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
        {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
            {
                mTime = hourOfDay + ":" + minute;
                dialogOnClick.setObject(mTime);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setView(view)
                .setNegativeButton(mNStr, null)
                .setPositiveButton(mPStr, dialogOnClick);
        if (NO_ICON != mIconId)
        {
            builder.setIcon(mIconId);
            initDefaultIconId();
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        initDefaultStr();
    }

    /**
     * 自定义对话框点击事件类
     */
    private class DialogOnClick implements DialogInterface.OnClickListener
    {
        private IDialogAction mAction;
        // 回调传递的数据
        private Object mObject;

        public DialogOnClick(IDialogAction action)
        {
            mAction = action;
        }

        public void setObject(Object object)
        {
            mObject = object;
        }

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            if (null != mAction)
            {
                mAction.action(which, mObject);
            }
        }
    }

    // 对话框回调接口
    public interface IDialogAction
    {
        /**
         * 执行事件
         *
         * @param which  点击第几项
         * @param object 传递的数据
         */
        void action(int which, Object object);
    }

    // 多选对话框回调接口
    public interface IMultiDialogAction
    {
        /**
         * 执行事件
         *
         * @param checkedItems 所有选项的布尔值数组
         */
        void action(boolean[] checkedItems);
    }
}
