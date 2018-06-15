package org.xdq.floatview.widget;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * 文件描述: 介绍类的详细作用
 * 作者: Created by 向定权 on 2018/6/15
 * 版本号: v1.0
 * 包名: org.xdq.floatview.widget
 * 项目名称: FloatView
 * 版权申明: 暂无
 */
public class FloatView implements View.OnTouchListener {

    private Builder mBuilder;

    private int mStatusBarHeight, mScreenWidth, mScreenHeight;

    //手指按下位置
    private int mStartX, mStartY, mLastX, mLastY;

    private static int mPrevPositionX, mPrevPositionY = 1000;

    //长按时长
    private static final int LONG_PRESS_TIME = 200;

    //移动阀值
    private static final int MOVE_RANGE = 50;

    private boolean mTouchResult = false;

    //是否是长按事件
    private boolean mLongPress = false;

    //处理长按事件
    private Handler mHandler = new Handler();

    private IFloatViewCallback mCallback;


    private FloatView(Builder builder) {
        mBuilder = builder;
        initFloatView();
    }

    public View getFloatView() {
        return mBuilder.view;
    }

    public Activity getActivity() {
        return mBuilder.activity;
    }

    public boolean getNeedNearEdge() {
        return mBuilder.needNearEdge;
    }

    public void setNeedNearEdge(boolean needNearEdge) {
        mBuilder.needNearEdge = needNearEdge;
        if (mBuilder.needNearEdge) {
            moveToEdge();
        }
    }

    private void initFloatView() {
        if (null == getActivity()) {
            throw new NullPointerException("activity为空");
        }
        if (null == mBuilder.view) {
            throw new NullPointerException("view为空");
        }
        if (mBuilder.activity.isDestroyed()) {
            return;
        }

        //屏幕宽高
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (null != windowManager) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight = displayMetrics.heightPixels;
        }

        //状态栏高度
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusBarHeight = frame.top;
        if (mStatusBarHeight <= 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                mStatusBarHeight = getActivity().getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        int left = mPrevPositionX;
        FrameLayout.LayoutParams layoutParams = createLayoutParams(left, mStatusBarHeight + mPrevPositionY, 0, 0);
        FrameLayout rootLayout = (FrameLayout) getActivity().getWindow().getDecorView();
        rootLayout.addView(getFloatView(), layoutParams);
        getFloatView().setOnTouchListener(this);
    }

    private static FloatView createFloatView(Builder builder) {
        if (null == builder) {
            throw new NullPointerException(" builder为空");
        }
        if (null == builder.activity) {
            throw new NullPointerException("activity为空");
        }
        if (null == builder.view) {
            throw new NullPointerException("view为空");
        }
        return new FloatView(builder);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchResult = false;
                mStartX = mLastX = (int) event.getRawX();
                mStartY = mLastY = (int) event.getRawY();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLongPress = true;
                        if (mCallback != null)
                            mCallback.downTouch();
                    }
                }, LONG_PRESS_TIME);
                break;
            case MotionEvent.ACTION_MOVE:
                int left, top, right, bottom;
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                left = v.getLeft() + dx;
                if (left < 0) {
                    left = 0;
                }
                right = left + v.getWidth();
                if (right > mScreenWidth) {
                    right = mScreenWidth;
                    left = right - v.getWidth();
                }
                top = v.getTop() + dy;
                if (top < mStatusBarHeight + 2) {
                    top = mStatusBarHeight + 2;
                }
                bottom = top + v.getHeight();
                if (bottom > mScreenHeight) {
                    bottom = mScreenHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();

                if (Math.abs(mLastX - mStartX) > MOVE_RANGE || Math.abs(mLastY - mStartY) > MOVE_RANGE) {
                    this.mHandler.removeCallbacksAndMessages(null);
                }

                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacksAndMessages(null);
                v.setLayoutParams(createLayoutParams(v.getLeft(), v.getTop(), 0, 0));
                float endX = event.getRawX();
                float endY = event.getRawY();
                if (Math.abs(endX - mStartX) > MOVE_RANGE || Math.abs(endY - mStartY) > MOVE_RANGE) {
                    mTouchResult = true;
                }
                if (mTouchResult && mBuilder.needNearEdge) {
                    moveToEdge();
                }
                if (mLongPress && mCallback != null) {
                    mCallback.upTouch();
                    mLongPress = false;
                }
                break;
        }
        return mTouchResult;
    }

    /**
     * 移至最近的边沿
     */
    private void moveToEdge() {
        int left = getFloatView().getLeft();
        int lastX;
        if (left + getFloatView().getWidth() / 2 <= mScreenWidth / 2) {
            lastX = 0;
        } else {
            lastX = mScreenWidth - getFloatView().getWidth();
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(left, lastX);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(0);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) animation.getAnimatedValue();
                getFloatView().setLayoutParams(createLayoutParams(left, getFloatView().getTop(), 0, 0));
                mPrevPositionX = left;
                mPrevPositionY = getFloatView().getTop() - mStatusBarHeight;
            }

        });
        valueAnimator.start();
    }

    private FrameLayout.LayoutParams createLayoutParams(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mBuilder.size, mBuilder.size);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }


    public void setCallback(IFloatViewCallback callback) {
        mCallback = callback;
    }

    public interface IFloatViewCallback {
        void downTouch();

        void upTouch();
    }

    public static class Builder {
        private Activity activity;
        private int size = FrameLayout.LayoutParams.WRAP_CONTENT;
        private boolean needNearEdge = false;
        private View view;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setNeedNearEdge(boolean needNearEdge) {
            this.needNearEdge = needNearEdge;
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public FloatView build() {
            return createFloatView(this);
        }
    }
}