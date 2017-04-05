package com.michael.notedemo.paint;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.michael.notedemo.R;

/**
 * Created by Michael on 2017/3/23.
 */

public class CircleView extends View {
    private int backgroundColor;
    private int radius;
    private int strokeColor;
    private float strokeWidth;
    private Paint mPaint;
    public CircleView(Context context) {
        this(context,null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, defStyleAttr, 0);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.CircleView_backgroundColor:
                    //背景色
                    backgroundColor = a.getColor(attr, Color.TRANSPARENT);
                    break;
                case R.styleable.CircleView_radius:
                    // 半径
                    radius = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CircleView_strokeColor:
                    strokeColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CircleView_strokeWidth:
                    strokeWidth = (float)a.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(strokeColor);
        mPaint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,radius,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(backgroundColor);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,radius,mPaint);
    }
    public void setBackgroundColor(int color){
        backgroundColor = color;
        invalidate();
    }
    public int getBackgroundColor(){
        return backgroundColor;
    }
}
