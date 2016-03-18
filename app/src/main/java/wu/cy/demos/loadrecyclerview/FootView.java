package wu.cy.demos.loadrecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by wcy8038 on 2016/3/17.
 */
public class FootView extends RelativeLayout{

    CircleImageView mCircleView;

    MaterialProgressDrawable mProgress;

    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    private static final int CIRCLE_DIAMETER = 40;

    public FootView(Context context) {
        super(context);
        createProgressView();
    }

    public FootView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        }
    }


}
