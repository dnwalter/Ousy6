package com.huaweisoft.ousy.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形imageview
 * Created by ousy on 2016/4/11.
 */
public class CircularImageView extends ImageView {


    public CircularImageView(Context context) {
        super(context);
    }
 
    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
 
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (null != bitmap) {
                Bitmap b = toRoundCorner(bitmap);
 
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
 
                canvas.drawBitmap(b, new Rect(0, 0, b.getWidth(), b.getHeight()),
                        new Rect(0, 0, this.getWidth(), this.getHeight()), paint);
            }
        } else {
            super.onDraw(canvas);
        }
    }
 
    private Bitmap toRoundCorner(Bitmap bitmap) {

        // 根据bitmap最短的边截取正方形
        final int bitmapSize=bitmap.getWidth()< bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
        final int left=bitmap.getWidth()< bitmap.getHeight() ? 0 : (bitmap.getWidth()-bitmap.getHeight())/2;
        final int top=bitmap.getWidth()> bitmap.getHeight() ? 0 : (bitmap.getHeight()-bitmap.getWidth())/2;
        bitmap = Bitmap.createBitmap(bitmap, left,top,bitmapSize,bitmapSize);
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
 
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setColor(0xffffffff);
 
        final float roundPX = bitmap.getWidth() / 2 < bitmap.getHeight() / 2 ? bitmap.getWidth() : bitmap.getHeight();
        final Rect rect = new Rect(0, 0, bitmap.getHeight(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        Canvas canvas = new Canvas(outBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
 
        return outBitmap;
    }
}
