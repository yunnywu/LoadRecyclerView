package wu.cy.demos.loadrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
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
    }

    public FootView(Context context, AttributeSet attrs) {
        super(context, attrs);

        createProgressView();
    }

    private void createProgressView() {
        mCircleView = CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER/2);
        mProgress = MaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.GONE);
        addView(mCircleView);
    }


}
