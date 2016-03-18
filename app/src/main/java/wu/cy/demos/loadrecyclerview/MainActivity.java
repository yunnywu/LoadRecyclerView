package wu.cy.demos.loadrecyclerview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView mRecyclerView;

    SwipeRefreshLayout mSwipeLayout;

    Context mContext;

    Handler mHandler = new Handler();

    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    LinearLayoutManager lm;

    int page = 0;

    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeLayout.setColorSchemeColors(Color.BLUE);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeLayout.setRefreshing(false);
                    }
                }, 3000);

            }
        });


        getAppInfo(true, 1);

        lm = new LinearLayoutManager(mContext);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                Log.d(TAG, "canChildScrollUp" + mSwipeLayout.canChildScrollUp());
                if(lm.findLastCompletelyVisibleItemPosition() == mInfos.size()){
                    getAppInfo(false, 0);
                }

            }
        });

        mRecyclerView.addItemDecoration(new SpaceDecoration());

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }



    class SpaceDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = 60;
        }
    }


    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int NORMAL_ITEM = 0;
        private static final int FOOTER_ITEM = 1;

        class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            ImageView icon;

            public NormalViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.iv);
                text = (TextView) itemView.findViewById(R.id.item_text);
            }
        }

        class FootViewHolder extends RecyclerView.ViewHolder {

            FootView icon;

            public FootViewHolder(View itemView) {
                super(itemView);
                icon = (FootView) itemView.findViewById(R.id.foot_iv);
            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == NORMAL_ITEM) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_item, parent, false);
                return new NormalViewHolder(view);
            } else {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_footer_item, parent, false);
                return new FootViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NormalViewHolder) {
                final NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                normalViewHolder.text.setText(mInfos.get(position).name);
                normalViewHolder.icon.setImageDrawable(mInfos.get(position).icon);
                normalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, normalViewHolder.text + " is clicked",
                                Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                FootViewHolder footViewHolder = (FootViewHolder) holder;
                footViewHolder.icon.setVisibility(View.VISIBLE);
                footViewHolder.icon.start();
            }


        }

        @Override
        public int getItemViewType(int position) {
            if (position == mInfos.size()) {
                return FOOTER_ITEM;
            } else {
                return NORMAL_ITEM;
            }
        }

        @Override
        public int getItemCount() {
            if (mInfos == null || mInfos.size() == 0) {
                return 0;
            } else {
                return mInfos.size() + 1;
            }

        }
    }


    class AppInfo {

        public AppInfo(CharSequence name, Drawable icon) {
            this.name = name;
            this.icon = icon;
        }

        CharSequence name;
        Drawable icon;
    }


    private boolean isRefresh;
    private List<AppInfo> mInfos = new ArrayList<>();

    private void getAppInfo(boolean refresh, int page) {
        if (refresh) {
            mInfos.clear();
        }

        rx.Observable observable = rx.Observable.create(new rx.Observable.OnSubscribe<List<AppInfo>>() {
            @Override
            public void call(Subscriber<? super List<AppInfo>> subscriber) {
                PackageManager pm = getPackageManager();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
                List<AppInfo> datas = new ArrayList<AppInfo>(infos.size());
                for (ResolveInfo info : infos) {
                    AppInfo app = new AppInfo(info.loadLabel(pm), info.loadIcon(pm));
                    datas.add(app);

                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(datas);
                subscriber.onCompleted();
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AppInfo>>() {
                    @Override
                    public void onCompleted() {
                        mSwipeLayout.setRefreshing(false);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "onCompleted", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<AppInfo> infos) {
                        mInfos.addAll(infos);
                        myAdapter.notifyDataSetChanged();
                    }

                });

    }

}
