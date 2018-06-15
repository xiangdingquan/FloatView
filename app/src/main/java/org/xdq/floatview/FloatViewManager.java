package org.xdq.floatview;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import org.xdq.floatview.widget.FloatView;

/**
 * 文件描述: 介绍类的详细作用
 * 作者: Created by 向定权 on 2018/6/15
 * 版本号: v1.0
 * 包名: org.xdq.floatview
 * 项目名称: FloatView
 * 版权申明: 暂无
 */
public class FloatViewManager {


    private static volatile FloatViewManager sInstance;

    private ViewHolder mViewHolder = new ViewHolder();

    private PopupWindowFactory mPopupWindowFactory;

    private FloatView.Builder mBuilder;

    private FloatViewManager() {

    }

    public static FloatViewManager getInstance() {
        if (sInstance == null) {
            synchronized (FloatViewManager.class) {
                if (sInstance == null)
                    sInstance = new FloatViewManager();
            }
        }
        return sInstance;
    }

    public void init(Application application) {
        View micView = View.inflate(application.getApplicationContext(), R.layout.layout_microphone, null);
        mPopupWindowFactory = new PopupWindowFactory(micView);
        mViewHolder.mFloatView = View.inflate(application.getApplicationContext(), R.layout.view_float, null);
        mBuilder = new FloatView.Builder()
                .setNeedNearEdge(true)
                .setView(mViewHolder.mFloatView);

    }


    public void attach(final AppCompatActivity activity) {
        ViewGroup parentViewGroup = (ViewGroup) mViewHolder.mFloatView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeView(mViewHolder.mFloatView);
        }
        FloatView floatView = mBuilder.setActivity(activity).build();
        floatView.setCallback(new FloatView.IFloatViewCallback() {
            @Override
            public void downTouch() {
                mPopupWindowFactory.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }

            @Override
            public void upTouch() {
                mPopupWindowFactory.dismiss();
            }
        });
    }

    public void show() {
        mViewHolder.mFloatView.setVisibility(View.VISIBLE);

    }

    public void hide() {
        mViewHolder.mFloatView.setVisibility(View.GONE);
    }


    static class ViewHolder {
        View mFloatView;
    }
}
