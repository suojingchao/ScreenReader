package com.catsuo.screenreader.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.catsuo.screenreader.R;


/**
 * Created by cat on 2017/7/6.
 */

public class ToastWindowControler {


    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private WindowManager mWindowManager = null;
    private View mView = null;
    private TextView mTextView = null;
    private static volatile ToastWindowControler mInstance = null;

    private ToastWindowControler(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mView = mInflater.inflate(R.layout.toast_window_layout, null);

        mTextView = (TextView) mView.findViewById(R.id.click_content_text);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public static ToastWindowControler getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ToastWindowControler.class) {
                if (mInstance == null) {
                    return new ToastWindowControler(context);
                }
            }
        }
        return mInstance;
    }

    public void show() {

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.height = 500;
        params.width = 500;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.addView(mView, params);

    }

    public void invalidate(String content) {
        mTextView.setText(content);
    }

}
