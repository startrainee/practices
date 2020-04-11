package com.stardust.practices.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.stardust.practices.R;

/**
 * Created on 2020/4/10.
 *
 * @author siasun-wangchongyang
 */
public class JikeView extends View {
    
    private static final String TAG = JikeView.class.getSimpleName();

    int centerX;
    int centerY;

    int centerCircleX;
    int centerCircleY;

    Paint defaultPaint = new Paint();
    Paint circlePaint  = new Paint();
    Paint textPaint    = new Paint();

    Bitmap selectBitMap;
    Bitmap shinningBitMap;
    Bitmap unselectedBitMap;
    boolean isSelected = false;

    float circleRadius = 0;   // 动画圆圈半径
    float deCircleRadius = 0; // 图片圆圈半径
    float bitmapScale = 1f;   // 图片动画缩放参数

    int textValue = 999;      // 显示的数字
    float textSize = 60f;     // 显示的数字的字体大小；
    float textTranslation = 0;// 上下移动的文字距离
    float char_width = 30f;   // 一个字符的宽度
    boolean isSwitchText = false; // 是否进行数字切换动画；

    float textX;              // 显示的数字坐标x
    float textY;              // 显示的数字坐标y


    ValueAnimator bitMapAnimator = ValueAnimator.ofFloat(0.8f,1.2f);
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);


    public JikeView(Context context) {
        super(context);
        initView();
    }

    public JikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public JikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public int getTextValue(){
        return textValue;
    }

    public void setTextValue(int textValue){
        this.textValue = textValue;
    }

    public boolean isSelected(){
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        this.isSwitchText = true;
        if(isSelected){
            bitMapAnimator.start();
            setTextValue(getTextValue() + 1);
            valueAnimator.start();
        } else {
            setTextValue(getTextValue() - 1);
            valueAnimator.start();
        }
        invalidate();
    }

    private void initView() {
        // 初始化三种图片
        selectBitMap     = BitmapFactory.decodeResource(getResources(), R.mipmap.jike_like_selected);
        shinningBitMap   = BitmapFactory.decodeResource(getResources(), R.mipmap.jike_like_selected_shining);
        unselectedBitMap = BitmapFactory.decodeResource(getResources(), R.mipmap.jike_like_unselected);

        //原图片太小了，所以这里进行了放大处理
        Matrix matrix = new Matrix();
        matrix.postScale(2f,2f); //长和宽放大缩小的比例
        selectBitMap     = Bitmap.createBitmap(selectBitMap,0,0,selectBitMap.getWidth(),selectBitMap.getHeight(),matrix,true);
        shinningBitMap   = Bitmap.createBitmap(shinningBitMap,0,0,shinningBitMap.getWidth(),shinningBitMap.getHeight(),matrix,true);
        unselectedBitMap = Bitmap.createBitmap(unselectedBitMap,0,0,unselectedBitMap.getWidth(),unselectedBitMap.getHeight(),matrix,true);

        // 初始化画笔
        circlePaint.setColor(Color.parseColor("#E6644D"));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.STROKE);

        textPaint.setColor(getResources().getColor(R.color.colorPrimary));
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.STROKE);

        //初始化一个字符占用的宽度
        char_width = textPaint.measureText("9",0,1);

        bitMapAnimator.setDuration(200);
        bitMapAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Log.d(TAG,"bitMapAnimator.value:" + value);
                if(value >= 1.2f){
                    circleRadius = 0;
                    bitmapScale = 1f;
                } else {
                    circleRadius = deCircleRadius * value;
                    bitmapScale = 1f * (value/1.2f);
                }
                invalidate();
            }
        });
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Log.d(TAG,"valueAnimator.value:" + value);
                if(value == 0){
                    textTranslation = 0;
                }
                if(isSelected){
                    textTranslation = -40 * value;
                }else{
                    textTranslation = 40 * value;
                }

                if(value >= 1){
                    isSwitchText = !isSwitchText;
                }

                invalidate();
            }
        });
        
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"");
                setSelected(!isSelected());
                animate().alpha(0.75f).setDuration(150).start();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw()");
        //画出进行动画切换时应该显示的图片
        drawBitMap(canvas,isSelected);
        Log.d(TAG, "getTextValue():" + getTextValue());


        if(isSwitchText){
            //显示动画过程中应显示的数字
            switchTextValue(canvas,isSelected);
        }else{
            //动画切换结束后最中显示结果
            canvas.save();
            char[] curChars  = String.valueOf(textValue).toCharArray();
            canvas.drawText(curChars,0,curChars.length,textX,textY,textPaint);
            canvas.restore();
        }
    }



    private void switchTextValue(Canvas canvas, boolean isSelected) {

        char[] preChars  = String.valueOf(textValue - 1).toCharArray();
        char[] curChars  = String.valueOf(textValue).toCharArray();
        char[] nextChars = String.valueOf(textValue + 1).toCharArray();

        if(isSelected){
            int index = findFirstDifferentIndex(preChars,curChars);
            if(index == -1){
                isSwitchText = false;
                return;
            }
            if(index == 0){
                canvas.save();
                canvas.drawText(preChars,0,preChars.length,textX,textY + textTranslation,textPaint);
                canvas.drawText(curChars,0,curChars.length,textX,textY + deCircleRadius + textTranslation,textPaint);
                canvas.restore();
            } else {
                canvas.save();
                canvas.drawText(curChars,0,index,textX,textY,textPaint);
                canvas.drawText(curChars,index,curChars.length - index,textX + char_width * (index),textY + textSize + textTranslation,textPaint);
                canvas.drawText(preChars,index,preChars.length - index,textX + char_width * (index),textY + textTranslation,textPaint);
                canvas.restore();
            }

        } else {
            int index = findFirstDifferentIndex(nextChars,curChars);
            if(index == -1){
                isSwitchText = false;
                return;
            }
            if(index == 0){
                canvas.save();
                canvas.drawText(curChars,0,curChars.length,textX,textY - deCircleRadius + textTranslation,textPaint);
                canvas.drawText(nextChars,0,nextChars.length,textX,textY + textTranslation,textPaint);
                canvas.restore();
            }else {
                canvas.save();
                canvas.drawText(curChars, 0,index, textX, textY, textPaint);
                canvas.drawText(nextChars, index, nextChars.length - index, textX + char_width * (index), textY + textTranslation, textPaint);
                canvas.drawText(curChars, index, curChars.length - index, textX + char_width * (index), textY - textSize + textTranslation, textPaint);
                canvas.restore();
            }
        }
    }

    private int findFirstDifferentIndex(char[] targets, char[] sources) {

        int index = -1;

        int length = Math.min(targets.length,sources.length);

        for(int i = length - 1;i >= 0;i--){

            if(targets[i] != sources[i]){
                index = i;
            } else {
                break;
            }

        }

        return index;

    }

    private void drawBitMap(Canvas canvas, boolean selected) {
        canvas.save();
        if(selected){
            drawShineCircle(canvas);
            canvas.scale(bitmapScale, bitmapScale,centerX + circleRadius,centerY + circleRadius);
            canvas.drawBitmap(selectBitMap,centerX,centerY,defaultPaint);
            canvas.drawBitmap(shinningBitMap,centerX + 3,centerY - shinningBitMap.getWidth()/2f,defaultPaint);
        } else {
            canvas.scale(bitmapScale, bitmapScale,centerX + circleRadius,centerY + circleRadius);
            canvas.drawBitmap(unselectedBitMap,centerX,centerY,defaultPaint);
        }
        canvas.restore();
    }

    private void drawShineCircle(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(centerCircleX,centerCircleY, circleRadius,circlePaint);
        canvas.restore();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout()");
        Log.d(TAG, "onLayout() change: " + changed);
        Log.d(TAG, "onLayout() left: " + left);
        Log.d(TAG, "onLayout() top: " + top);
        Log.d(TAG, "onLayout() right: " + right);
        Log.d(TAG, "onLayout() bottom: " + bottom);

        centerX = getWidth() / 2 - selectBitMap.getWidth();
        centerY = getHeight() / 2;
        centerCircleX  = centerX + selectBitMap.getWidth() / 2;
        centerCircleY  = centerY + selectBitMap.getHeight() / 2 - 3;
        deCircleRadius = (shinningBitMap.getHeight() + selectBitMap.getHeight() - 10)/2;
        circleRadius   = deCircleRadius;

        textX = centerX + selectBitMap.getWidth() + selectBitMap.getWidth()/3f;
        textY = centerY + deCircleRadius;

        textSize = selectBitMap.getHeight()/3f * 2;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure()");
    }
}

