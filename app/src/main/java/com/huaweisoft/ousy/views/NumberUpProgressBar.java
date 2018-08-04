package com.huaweisoft.ousy.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaweisoft.ousy.R;

/**
 * 数字在进度条上面的进度条
 * Created by ousy on 2016/8/11.
 */
public class NumberUpProgressBar extends LinearLayout
{
    // 显示的数字
    private TextView tvPercent;
    // tvPercent左边的控件，通过改变其长度从而改变数字的位置
    private ImageView ivPercent;
    // 进度条
    private ProgressBar mProgressBar;
    private TypedArray mTypedArray;
    private int mPercent = 0;
    private int mMax = 100;

    public NumberUpProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.numberup_progressbar, this, true);
        mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberUpProgressBar);
        initView();
        initTvPercent();
        initProgressBar();
    }

    private void initView()
    {
        tvPercent = (TextView) findViewById(R.id.tv_percent);
        ivPercent = (ImageView) findViewById(R.id.iv_percent_width);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    // 初始化数字的textview
    private void initTvPercent()
    {
        int color = mTypedArray.getColor(R.styleable.NumberUpProgressBar_android_textColor, Color.parseColor
                ("#000000"));
        float size = mTypedArray.getDimension(R.styleable.NumberUpProgressBar_android_textSize, 0);
        float textPad = mTypedArray.getDimension(R.styleable.NumberUpProgressBar_textPadding, 0);
        tvPercent.setTextColor(color);
        if (size > 0)
        {
            tvPercent.setTextSize(size);
        }
        tvPercent.setPadding(0, 0, 0, (int) textPad);
    }

    // 初始化进度条
    private void initProgressBar()
    {
        int max = mTypedArray.getInt(R.styleable.NumberUpProgressBar_android_max, 100);
        mPercent = mTypedArray.getInt(R.styleable.NumberUpProgressBar_android_progress, 0);
        float height = mTypedArray.getDimension(R.styleable.NumberUpProgressBar_progressbarHeight, 0);
        mMax = max;
        mProgressBar.setMax(mMax);
        if (height > 0)
        {
            LayoutParams layoutParams = (LayoutParams) mProgressBar.getLayoutParams();
            layoutParams.height = (int) height;
            mProgressBar.setLayoutParams(layoutParams);
        }

        // 延迟100毫秒等控件都画好
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                setPercent(mPercent);
            }
        }, 100);
    }

    // 设置percent
    public void setPercent(int percent)
    {
        if (percent > mMax)
        {
            return;
        }
        mPercent = percent;
        tvPercent.setVisibility(INVISIBLE);
        // 先把改变数字位置的那个view的宽度变小
        LayoutParams ivLayoutParams = (LayoutParams) ivPercent.getLayoutParams();
        ivLayoutParams.width = 0;
        ivPercent.setLayoutParams(ivLayoutParams);
        // 再设置数字，这样假如从99变到100
        // 那时tvPercent变成100后获取宽度就不会跟99的一样
        tvPercent.setText(percent + "%");
        mProgressBar.setProgress(percent);
        // 延迟100毫秒在改变布局，因为textview.getwidth时候，setText后还没重画该控件
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                setLayout();
                tvPercent.setVisibility(VISIBLE);
            }
        }, 100);
    }

    // 改变布局
    private void setLayout()
    {
        int percentWidth = mProgressBar.getWidth() * mPercent / mMax;
        int outPercentWidth = mProgressBar.getWidth() * (mMax - mPercent) / mMax;
        int tvPercentWidth = tvPercent.getWidth();
        LayoutParams ivLayoutParams = (LayoutParams) ivPercent.getLayoutParams();
        LayoutParams tvLayoutParams = (LayoutParams) tvPercent.getLayoutParams();
        // textview的一半长度比余下的进度长时
        if (tvPercentWidth / 2 > outPercentWidth)
        {
            // textview和进度条右对齐
            ivLayoutParams.width = mProgressBar.getWidth() - tvPercentWidth;
            ivPercent.setLayoutParams(ivLayoutParams);
            tvLayoutParams.setMargins(0, 0, 0, 0);
        }
        // textview的一半长度比进度长时
        else if (tvPercentWidth / 2 > percentWidth)
        {
            // textview和进度条左对齐
            ivLayoutParams.width = 0;
            ivPercent.setLayoutParams(ivLayoutParams);
            tvLayoutParams.setMargins(0, 0, 0, 0);
        }
        else
        {
            // textview的中心与进度对齐
            ivLayoutParams.width = percentWidth;
            ivPercent.setLayoutParams(ivLayoutParams);
            tvLayoutParams.setMargins(-tvPercentWidth / 2, 0, 0, 0);
        }
    }
}
