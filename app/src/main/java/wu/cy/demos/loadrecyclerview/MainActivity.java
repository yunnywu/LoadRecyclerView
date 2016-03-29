package wu.cy.demos.loadrecyclerview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    LoadRecyclerView mRecyclerView;

    SwipeRefreshLayout mSwipeLayout;

    Context mContext;

    LinearLayoutManager lm;

    int currentPage = 0;

    int totalPage = 4;

    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        mRecyclerView = (LoadRecyclerView) findViewById(R.id.recycler_view);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeLayout.setColorSchemeColors(Color.BLUE);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if(!mSwipeLayout.isRefreshing()) {
                loadData(0);
//                }
            }
        });
        mSwipeLayout.setRefreshing(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(0);
            }
        }, 500);


        lm = new LinearLayoutManager(mContext);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);

        mRecyclerView.addItemDecoration(new SpaceDecoration());

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setOnLoadNextListener(new LoadRecyclerView.OnLoadNextListener() {
            @Override
            public void onLoadNext() {
                loadData(currentPage);
            }
        });
    }

    class SpaceDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = 60;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            ImageView icon;

            public NormalViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.iv);
                text = (TextView) itemView.findViewById(R.id.item_text);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_item, parent, false);
            return new NormalViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            normalViewHolder.text.setText(mInfos.get(position).name);
            normalViewHolder.icon.setImageDrawable(mInfos.get(position).icon);
            normalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, normalViewHolder.text.getText() + " is clicked",
                            Toast.LENGTH_LONG).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mInfos.size();
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

    private List<AppInfo> mInfos = new ArrayList<>();

    private void loadData(int pageIndex) {
        final boolean isRefresh = pageIndex == 0;
        if (pageIndex == 0) {
            mSwipeLayout.setRefreshing(true);
            currentPage = 0;
        }else{
            mRecyclerView.setState(LoadRecyclerView.STATE_LOADING);
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
                        if (isRefresh) {
                            mSwipeLayout.setRefreshing(false);
                        }else{
                            mRecyclerView.setState(LoadRecyclerView.STATE_IDEL);
                        }

                        if (currentPage == totalPage - 1) {
                            mRecyclerView.setShowTheFooterView(false);
                        }else{
                            mRecyclerView.setShowTheFooterView(true);
                            currentPage++;
                        }
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "onCompleted", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_LONG).show();
                        if (isRefresh) {
                            mSwipeLayout.setRefreshing(false);
                        }else{
                            mRecyclerView.setState(LoadRecyclerView.STATE_IDEL);
                        }
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<AppInfo> infos) {
                        if (isRefresh) {
                            mInfos.clear();
                        }
                        mInfos.addAll(infos);
                    }
                });
    }

}
