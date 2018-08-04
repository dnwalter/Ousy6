package com.huaweisoft.ousy.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;

import com.huaweisoft.ousy.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义switch按钮
 * <p/>
 * Created by ousy on 2016/5/1.
 */
public class SwitchView extends CompoundButton
{
    private static final int TAG_CLICK = 0;
    private static final int TAG_LEFT = 1;
    private static final int TAG_RIGHT = 2;
    private static final int TAG_CANCEL = 3;
    // thumb与背景之间的间隙
    private static final int INTERVAL = 2;
    private Paint mPaint = new Paint();
    private float mRadius;
    private float mCurrentX;
    private float mCurrentY;
    // 按下时的x坐标
    private float mDownX;
    // 连续滑动时，分别的前一次和后一次x坐标
    private float mMoveX1;
    private float mMoveX2;
    // 放开时x的坐标
    private float mUpX;
    // thumb能滑到最左和最右的坐标
    private float mStart;
    private float mEnd;
    private Timer mTimer;
    private MyTimerTask mTask;
    // 用于判断该操作是否是点击的时间差标准
    private int mClickTimeout;
    // 用于判断该操作是否是点击的滑动差标准
    private int mTouchSlop;
    // 按下和放开的时间差，主要用于判断该操作是否是点击
    private float mTime;
    // 按下和放开的x距离差，主要用于判断该操作是滑动还是点击
    private float mDeltaX;
    // thumb每次移动的距离，只取1或-1，分别代表右运动和左运动
    private int mMove = 1;
    // 判断thumb是否正在向右的标志
    private boolean mIsToRight;

    public SwitchView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setClickable(true);
        setBackgroundResource(R.drawable.switchview_bg_selector);
        init();
    }

    // 初始化变量
    private void init()
    {
        mTimer = new Timer();
        mTask = new MyTimerTask();
        mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (isChecked())
        {
            mIsToRight = false;
        }
        else
        {
            mIsToRight = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // 获取整个空间的宽高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mRadius = heightSize / 2;
        mStart = 0 + mRadius;
        mEnd = widthSize - mRadius;
        if (isChecked())
        {
            mCurrentX = mEnd;
        }
        else
        {
            mCurrentX = mStart;
        }
        mCurrentY = mRadius;

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);

        canvas.drawCircle(mCurrentX, mCurrentY, mRadius - INTERVAL, mPaint);
    }

    /**
     * 没有点击去改变switchView的状态
     * @param checked
     */
    public void setCheckedByNoClick(boolean checked)
    {
        super.setChecked(checked);
        if (checked)
        {
            mCurrentX = mEnd;
        }
        else
        {
            mCurrentX = mStart;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mMoveX1 = mDownX;
                mTask.cancel();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX2 = event.getX();
                float delta = mMoveX2 - mMoveX1;
                mCurrentX = mCurrentX + delta;
                moving();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mUpX = event.getX();
                mDeltaX = Math.abs(mUpX - mDownX);
                mTime = event.getEventTime() - event.getDownTime();
                upping();
                break;
            default:
                break;
        }

        return true;
    }

    // 手指滑动操作
    private void moving()
    {
        if (mCurrentX > mStart - 1 && mCurrentX < mEnd + 1)
        {
            invalidate();
            if (mCurrentX > (mStart + mEnd) / 2)
            {
                setChecked(true);
            }
            else
            {
                setChecked(false);
            }
        }
        mMoveX1 = mMoveX2;
    }

    // 手指放开后的操作
    private void upping()
    {
        if (mIsToRight)
        {
            mMove = 1;
            mIsToRight = false;
        }
        else
        {
            mMove = -1;
            mIsToRight = true;
        }

        if (mUpX <= mStart)
        {
            mCurrentX = mStart;
        }
        else if (mUpX >= mEnd)
        {
            mCurrentX = mEnd;
        }

        mTask = new MyTimerTask();
        mTimer.schedule(mTask, 0, 10);
    }

    // 处理不同事件的自定义TimerTask类
    private class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            Message message = new Message();
            // 点击事件
            if (mDeltaX < mTouchSlop && mTime < mClickTimeout)
            {
                message.what = TAG_CLICK;
            }
            // 滑到两端
            else if (mCurrentX > mEnd || mCurrentX < mStart)
            {
                message.what = TAG_CANCEL;
            }
            // 没到达两端，且没滑过一半时松开
            else if (mCurrentX <= (mStart + mEnd) / 2)
            {
                message.what = TAG_LEFT;
                mMove = -1;
            }
            // 没到达两端，且滑过一半时松开
            else if (mCurrentX > (mStart + mEnd) / 2)
            {
                message.what = TAG_RIGHT;
                mMove = 1;
            }

            mHandler.sendMessage(message);
        }
    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case TAG_CLICK:
                    onClickSlipping();
                    break;
                case TAG_LEFT:
                case TAG_RIGHT:
                    slipping();
                    break;
                case TAG_CANCEL:
                    stopFling();
                    break;
            }
        }
    };

    private void slipping()
    {
        mCurrentX += mMove;
        invalidate();
        if (mCurrentX > (mStart + mEnd) / 2)
        {
            setChecked(true);
        }
        else
        {
            setChecked(false);
        }
    }

    // 点击进行滑动的结束条件处理
    private void onClickSlipping()
    {
        slipping();
        if (!mIsToRight)   // 这里的条件表示是向右的时候，因为前面点击后会把mIsToRight设为相反
        {
            if (mCurrentX > mEnd)
            {
                stopFling();
            }
        }
        else
        {
            if (mCurrentX < mStart)
            {
                stopFling();
            }
        }
    }

    // 停止线程
    private void stopFling()
    {
        if (mCurrentX <= (mStart + mEnd) / 2)
        {
            mCurrentX = mStart;
        }
        else
        {
            mCurrentX = mEnd;
        }
        invalidate();
        mTask.cancel();
    }
}
