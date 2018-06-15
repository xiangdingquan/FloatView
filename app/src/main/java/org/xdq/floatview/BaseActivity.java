package org.xdq.floatview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 文件描述: 介绍类的详细作用
 * 作者: Created by 向定权 on 2018/6/15
 * 版本号: v1.0
 * 包名: org.xdq.floatview
 * 项目名称: FloatView
 * 版权申明: 暂无
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewManager.getInstance().attach(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //        FloatViewManager.getInstance().dettach();
    }
}
