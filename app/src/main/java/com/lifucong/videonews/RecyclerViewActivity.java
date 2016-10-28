package com.lifucong.videonews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/10/28.
 */

public class RecyclerViewActivity extends AppCompatActivity {

    @BindView(R.id.recycle_rcv)
    RecyclerView recycleRcv;
    private Unbinder unbinder;
    private RecyclerViewAdapter adapter;
    private List<String> mData;
//    private

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);
        unbinder = ButterKnife.bind(this);
        mData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mData.add("第 " + i + " item");
        }
        adapter = new RecyclerViewAdapter(this, mData);
        //设置布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        GridLayoutManager layoutManager=new GridLayoutManager(this,3);
        recycleRcv.setLayoutManager(layoutManager);
        //设置adapter
        recycleRcv.setAdapter(adapter);
        //设置分割线
        recycleRcv.addItemDecoration(new AAADivider(this, OrientationHelper.VERTICAL));
        //设置Item动画
        recycleRcv.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(RecyclerViewActivity.this, "点击了==" + position + "==item", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int position) {
                Toast.makeText(RecyclerViewActivity.this, "长按了 ==" + position + "==item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.recycle_add, R.id.recycle_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recycle_add:
                adapter.add(1);
                break;
            case R.id.recycle_delete:
                adapter.delete(1);
                break;
        }
    }
}
