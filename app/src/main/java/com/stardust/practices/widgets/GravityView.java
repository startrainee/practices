package com.stardust.practices.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


//引力弹弓
public class GravityView extends View {
    public GravityView(Context context) {
        super(context);
        initView();
    }

    public GravityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GravityView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){

    }

}
