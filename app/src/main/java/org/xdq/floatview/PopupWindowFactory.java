package org.xdq.floatview;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;


public class PopupWindowFactory {

    private PopupWindow mPop;

    public PopupWindowFactory(View view) {
        this(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private PopupWindowFactory(View view, int width, int heigth) {
        init(view, width, heigth);
    }

    private void init(View view, int width, int heigth) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        mPop = new PopupWindow(view, width, heigth, true);
        mPop.setOutsideTouchable(false);
        mPop.setFocusable(true);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mPop.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    public PopupWindow getPopupWindow() {
        return mPop;
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (mPop.isShowing()) {
            return;
        }
        mPop.showAtLocation(parent, gravity, x, y);
    }

    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (mPop.isShowing()) {
            return;
        }
        mPop.showAsDropDown(anchor, xoff, yoff);
    }

    public void dismiss() {
        if (mPop.isShowing()) {
            mPop.dismiss();
        }
    }

}