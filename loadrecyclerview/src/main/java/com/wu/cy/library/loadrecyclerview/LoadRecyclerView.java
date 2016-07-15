package com.wu.cy.library.loadrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by wcy8038 on 2016/3/29.
 */
public class LoadRecyclerView extends RecyclerView{

    public static final int STATE_IDEL = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADING = 2;

    private int mCurrentState = STATE_IDEL;

    CustomOnScrollListener mCustomOnScrollListener;

    OnLoadNextListener mOnLoadNextListener;

    private WrapperRecyclerAdapter mWrapperAdapter;

    private boolean canLoadMore;

    public LoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public LoadRecyclerView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mCustomOnScrollListener = new CustomOnScrollListener();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapperAdapter = new WrapperRecyclerAdapter(getContext(),adapter);
        setCanLoadMore(false);
        super.setAdapter(mWrapperAdapter);
    }

    public WrapperRecyclerAdapter getAdapter(){
        return mWrapperAdapter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnScrollListener(mCustomOnScrollListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnScrollListener(mCustomOnScrollListener);
    }

    private class CustomOnScrollListener extends OnScrollListener{
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(mCurrentState != STATE_IDEL || !canLoadMore){
                return;
            }

            //如果最后一项是FOOTVIEW 说明还可以加载更多
                if(getLayoutManager() instanceof LinearLayoutManager){
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
                    if(getAdapter() != null && getAdapter().getItemCount() != 0 &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition()
                            == getAdapter().getItemCount() - 1){
                        if(mOnLoadNextListener != null){
                            mOnLoadNextListener.onLoadNext();
                            setState(STATE_LOADING);
                        }
                    }
                }
        }
    }

    public void setState(int state){
        if(mCurrentState == state){
            return ;
        }else{
            mCurrentState = state;
        }
    }

    public void setCanLoadMore(boolean show){
        canLoadMore = show;
        mWrapperAdapter.setNeedLoadMore(show);
    }

    public interface OnLoadNextListener {
         void onLoadNext();
    }

    public void setOnLoadNextListener(OnLoadNextListener listener){
        mOnLoadNextListener = listener;
    }
}
