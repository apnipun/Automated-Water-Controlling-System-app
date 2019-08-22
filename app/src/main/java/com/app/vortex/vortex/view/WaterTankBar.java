package com.app.vortex.vortex.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.app.vortex.vortex.R;

/**
 * Created by Kasun on 11/23/2016.
 */

public class WaterTankBar extends View {
    final int MAX_PROGRESS = 100;
    int mTopColor;
    int mBottomColor;
    int mProgress;// 0 - 100
    int mTankBackgroundColor;

    Bitmap mTankMask;
    Bitmap mTankOutline;
    Shader mTankColorShader;
    Rect mBounds = new Rect();
    Rect mProgressBounds = new Rect();

    Paint bgBrush = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint fgBrush = new Paint(Paint.ANTI_ALIAS_FLAG);
    Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    ValueAnimator animator;


    public WaterTankBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.WaterTankBar, 0, 0);

        try {
            mTopColor = a.getColor(R.styleable.WaterTankBar_topColor, Color.argb(100, 0, 153, 255));
            mBottomColor = a.getColor(R.styleable.WaterTankBar_bottomColor, Color.argb(100, 0, 51, 204));
            mProgress = clamp(a.getInt(R.styleable.WaterTankBar_progress, 0), 0, 100);
            mTankBackgroundColor = a.getColor(R.styleable.WaterTankBar_tankBackgroundColor, Color.WHITE);
        } finally {
            a.recycle();
        }

        init();

    }

    //setters and getters
    public int getTopColor() {
        return mTopColor;
    }

    public void setTopColor(int topColor) {
        this.mTopColor = topColor;
        setFill();
        invalidate();
        requestLayout();
    }

    public int getBottomColor() {
        return mBottomColor;
    }

    public void setBottomColor(int bottomColor) {
        this.mBottomColor = bottomColor;
        setFill();
        invalidate();
        requestLayout();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        animator = ValueAnimator.ofInt(getProgress(), progress);
        animator.setInterpolator(new DecelerateInterpolator(0.5f));
        animator.setDuration(10 * Math.abs(progress - getProgress()));

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (int) animation.getAnimatedValue();
                calcProgressBounds();
                invalidate();
                requestLayout();
            }
        });

        animator.start();
    }

    public int getTankBackgroundColor() {
        return mTankBackgroundColor;
    }

    public void setTankBackgroundColor(int tankBackgroundColor) {
        this.mTankBackgroundColor = tankBackgroundColor;
        invalidate();
        requestLayout();
    }

    //end setters and getters

    private void setFill() {
        mTankColorShader = new LinearGradient(0f, 0f,
                0f, mBounds.height(), new int[]{mTopColor, mBottomColor}, null, Shader.TileMode.CLAMP);

    }

    private void init() {
        mTankMask = BitmapFactory.decodeResource(getResources(), R.drawable.tank_mask);
        mTankOutline = BitmapFactory.decodeResource(getResources(), R.drawable.tank_outline);


        animator = ValueAnimator.ofInt(0, getProgress());

        setFill();
        calcProgressBounds();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int xpad = getPaddingLeft() + getPaddingRight();
        int ypad = getPaddingTop() + getPaddingBottom();

        int ww = w - xpad;
        int hh = h - ypad;

        mBounds = new Rect(0, 0, ww, hh);
        calcProgressBounds();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setFill();
        calcProgressBounds();
        //draw bg
        bgBrush.setColor(mTankBackgroundColor);
        canvas.drawRect(mBounds, bgBrush);

        //draw progress
        fgBrush.setShader(mTankColorShader);
        canvas.drawRect(mProgressBounds, fgBrush);

        //draw mask
        maskPaint.setXfermode(xfermode);
        canvas.drawBitmap(mTankMask, null, mBounds, maskPaint);


        //draw outline
        canvas.drawBitmap(mTankOutline, null, mBounds, null);

        maskPaint.setXfermode(null);
        setLayerType(LAYER_TYPE_HARDWARE, maskPaint);
    }

    private void calcProgressBounds() {
        int height = getProgress() * mBounds.height() / MAX_PROGRESS;
        mProgressBounds = new Rect(0, mBounds.height() - height, mBounds.width() - 10, mBounds.height() - 10);
    }

    //clamps between @min and @max
    private int clamp(int value, int min, int max) {
        if (value > max)
            value = max;
        else if (value < min)
            value = min;

        return value;
    }
}
