package org.xdq.floatview;

import android.app.Application;

/**
 * 文件描述: 介绍类的详细作用
 * 作者: Created by 向定权 on 2018/6/15
 * 版本号: v1.0
 * 包名: org.xdq.floatview
 * 项目名称: FloatView
 * 版权申明: 暂无
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FloatViewManager.getInstance().init(this);
    }
}
