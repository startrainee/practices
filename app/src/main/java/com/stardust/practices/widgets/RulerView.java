package com.stardust.practices.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * Created on 2020/4/10.
 *
 * @author siasun-wangchongyang
 */
public class RulerView extends View {

    private static final String TAG = RulerView.class.getSimpleName();

    //尺子高度,最大值和最小值
    int rulerHeight = 400;
    int minWeight = 0;
    int maxWeight = 200;
    int minTagWidth = 20;
    int rulerBackground = Color.parseColor("#F6F9F6");

    //中间指针颜色，高度，宽度
    int targetColor  = Color.parseColor("#4CBB75");
    int targetHeight = rulerHeight/4;

    //大小刻度的颜色宽高
    int tagColor = Color.parseColor("#DADEDA");
    int shortTagHeight = 10;
    int longTagHeight  = 20;

    //重量文字大小颜色内容
    float textWeight = 0.0f;
    int textValueColor  = Color.parseColor("#4CBB75");
    int textWeightColor = Color.parseColor("#4CBB75");
    int textValueSize  = 100;
    int textWeightSize = 40;

    //尺子刻度文字
    int textRulerTagColor  = Color.parseColor("#66000000");
    int textRulerTagSize  = 50;

    Paint rulerPaint;
    Paint targetPaint;
    Paint tagPaint;
    Paint textValuePaint;
    Paint textKgPaint;

    Paint rulerTextPaint;

    int centerX = 0;
    int centerY = 0;

    float rulerBaseX = 0; //尺子0刻度位置
    int offsetX = 0; // 滑动尺子产生的位移长度
    int minOffsetX = 20; //有效的最小滑动距离

    boolean isPush = false;

    int lastX = 0;
    int lastY = 0;
    long lastTime = 0;


    public RulerView(Context context) {
        super(context);
        initView();
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        Log.d(TAG,"initView()");
        rulerPaint = new Paint();
        rulerPaint.setColor(rulerBackground);
        rulerPaint.setStyle(Paint.Style.FILL);
        rulerPaint.setAntiAlias(true);
        rulerPaint.setStrokeWidth(2);

        targetPaint     = new Paint();
        targetPaint.setColor(targetColor);
        targetPaint.setStyle(Paint.Style.FILL);
        targetPaint.setAntiAlias(true);
        targetPaint.setDither(true);
        targetPaint.setStrokeWidth(8);

        tagPaint        = new Paint();
        tagPaint.setColor(tagColor);
        tagPaint.setStyle(Paint.Style.STROKE);
        tagPaint.setAntiAlias(true);
        tagPaint.setStrokeWidth(2);

        textValuePaint  = new Paint();
        textValuePaint.setColor(textValueColor);
        textValuePaint.setStyle(Paint.Style.STROKE);
        textValuePaint.setAntiAlias(true);
        textValuePaint.setTextSize(textValueSize);

        textKgPaint = new Paint();
        textKgPaint.setColor(textWeightColor);
        textKgPaint.setStyle(Paint.Style.STROKE);
        textKgPaint.setAntiAlias(true);
        textKgPaint.setTextSize(textWeightSize);

        rulerTextPaint = new Paint();
        rulerTextPaint.setColor(textRulerTagColor);
        rulerTextPaint.setStyle(Paint.Style.STROKE);
        rulerTextPaint.setAntiAlias(true);
        rulerTextPaint.setTextSize(textRulerTagSize);

        setTextWeight(80);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG,"onDraw()");

        drawRuler(canvas);

        drawWeightText(canvas);

        drawRulerTarget(canvas);

    }

    public void setTextWeight(float textWeight) {

        if(textWeight <= 0){
            textWeight = minWeight;
        }

        this.textWeight = textWeight;

        rulerBaseX = centerX - this.textWeight * minTagWidth / 0.1f;

        invalidate();

    }

    private void drawRulerTarget(Canvas canvas) {
        float targetX   = centerX;
        float targetY   = centerY - textValueSize;
        canvas.drawLine(targetX,targetY,targetX,targetY + targetHeight,targetPaint);
    }


    private void drawRuler(Canvas canvas) {
        rulerBaseX = rulerBaseX + offsetX * 1f / minOffsetX;
        Log.d(TAG,"drawRuler() rulerBaseX:" + rulerBaseX);
        Log.d(TAG,"onTouchEvent() offsetX:" + offsetX);
        if(rulerBaseX >= centerX){
            rulerBaseX = centerX;
        }
        float rulerLeft   = rulerBaseX;
        float rulerTop    = centerY - textValueSize;
        float rulerBottom = rulerTop + rulerHeight;
        float rulerRight  = rulerLeft + maxWeight/0.1f/minTagWidth;

        RectF rectF = new RectF();
        rectF.set(rulerLeft,rulerTop,rulerRight,rulerBottom);
        rulerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(rectF,rulerPaint);

        for (int i = 0; i <= maxWeight / 0.1; i += 1) {

            int dx = i * minTagWidth;
            if (i % 10 != 0) {
                canvas.drawLine(rulerLeft + dx, rulerTop, rulerLeft + dx, rulerTop + shortTagHeight, tagPaint);
            } else {
                canvas.drawLine(rulerLeft + dx, rulerTop, rulerLeft + dx, rulerTop + longTagHeight, tagPaint);
                if (i == 0) {
                    continue;
                }
                String text = String.format(Locale.CHINA, "%.0f", i / 10f);
                float charWidth = rulerTextPaint.measureText("0");
                canvas.drawText(text, rulerLeft + dx - charWidth, rulerTop + longTagHeight + charWidth * 2, rulerTextPaint);
            }
        }
    }

    private void drawWeightText(Canvas canvas) {

        textWeight = (centerX - rulerBaseX) / (minTagWidth / 0.1f);

        String textWeightStr = String.format(Locale.CHINA, "%.1f", textWeight);

        float charWidth = textValuePaint.measureText(textWeightStr, 0, textWeightStr.length() / 2);

        float textX = centerX - charWidth;
        float textY = centerY - textValueSize * 2;

        float kgTextX = textX + textValuePaint.measureText(textWeightStr) + 20;
        float kgTextY = textY - 10;

        canvas.drawText(textWeightStr, textX, textY, textValuePaint);
        canvas.drawText("kg", kgTextX, kgTextY, textKgPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout()");

        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        longTagHeight  = rulerHeight / 3;
        shortTagHeight = longTagHeight / 2;

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getY() >= centerY && ev.getY() <= centerY + rulerHeight){
            getParent().requestDisallowInterceptTouchEvent(true);
        }else{
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"onTouchEvent()");
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // 记录触摸点坐标
                isPush = true;
                lastX = x;
                lastY = y;
                lastTime = System.currentTimeMillis();
                Log.d(TAG,"onTouchEvent() ACTION_DOWN lastX: " + lastX);
                Log.d(TAG,"onTouchEvent() ACTION_DOWN lastY: " + lastY);
                return true;
            case MotionEvent.ACTION_MOVE:
                // 计算偏移量
                Log.d(TAG,"onTouchEvent() ACTION_MOVE offsetX: " + offsetX);
                if(Math.max(lastX,x) - Math.min(lastX,x) < minOffsetX){
                    return true;
                }
                offsetX = x - lastX;
                invalidate();
                // 在当前left、top、right、bottom的基础上加上偏移量
                /*
                int offsetY = y - lastY;
                layout(getLeft() + offsetX,
                        getTop() + offsetY,
                        getRight() + offsetX,
                        getBottom() + offsetY);*/

                return true;
            case MotionEvent.ACTION_UP:
                isPush = false;
                Log.d(TAG,"onTouchEvent() ACTION_UP offsetX: " + offsetX);
                return true;
        }

        return super.onTouchEvent(event);
    }
}
