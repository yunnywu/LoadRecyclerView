package com.wu.cy.loadrecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by wcy8038 on 2016/3/17.
 */
public class CircleProgressView extends RelativeLayout{

    CircleImageView mCircleView;

    MaterialProgressDrawable mProgress;

    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    private static final int CIRCLE_DIAMETER = 40;

    public CircleProgressView(Context context) {
        super(context);
        initView();
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        createProgressView();
    }

    private void createProgressView() {
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER/2);
        mProgress = new MaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.VISIBLE);
        mProgress.setColorSchemeColors(Color.BLUE);
        mProgress.setAlpha(255);
        mProgress.showArrow(true);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.addRule(CENTER_IN_PARENT,TRUE);
        addView(mCircleView);
    }

    public void start(){
        if(mProgress != null){
            mProgress.start();
            mProgress.showArrow(true);
        }
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    @ColorInt
    public void setColorSchemeColors(int... colors) {
        mProgress.setColorSchemeColors(colors);
    }

    public void stop(){
        if(mProgress != null){
            mProgress.stop();
        }
    }

    public boolean isRunning() {
        if(mProgress != null){
            return mProgress.isRunning();
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }
}
