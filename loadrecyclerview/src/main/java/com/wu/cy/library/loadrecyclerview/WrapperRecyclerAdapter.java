package com.wu.cy.library.loadrecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wcy8038 on 2016/3/23.
 */
public class WrapperRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //this can repeat with the child item type
    private static final int FOOTER_ITEM = 100001;

    private boolean needLoading = true;

    private Context mContext;

    private RecyclerView.Adapter mAdapter;

    private int[] mColors ;

    public WrapperRecyclerAdapter(Context mContext, RecyclerView.Adapter mAdapter) {
        this.mContext = mContext;
        this.mAdapter = mAdapter;
    }

    public void setNeedLoadMore(boolean loaded){
        needLoading = loaded;
    }

    public void setColorSchemeColors(int... colors){
        mColors = colors;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == FOOTER_ITEM){
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_footer_item, parent, false);
            return new FootViewHolder(view);
        }else{
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.icon.setVisibility(View.VISIBLE);
            Log.d("wcy", "onBindViewHolder");
//            if(!footViewHolder.icon.isRunning()) {
//                Log.d("wcy", "onBindViewHolder start");
//                footViewHolder.icon.start();
//            }
        } else {
            mAdapter.onBindViewHolder(holder,position);
        }
    }

    @Override
    public int getItemCount() {
        return needLoading ? mAdapter.getItemCount() + 1 : mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mAdapter.getItemCount()) {
            return FOOTER_ITEM;
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        CircleProgressView icon;
        public FootViewHolder(View itemView) {
            super(itemView);
            icon = (CircleProgressView) itemView.findViewById(R.id.foot_iv);
            icon.setColorSchemeColors(mColors);
        }
    }
}
