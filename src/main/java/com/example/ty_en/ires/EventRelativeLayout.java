package com.example.ty_en.ires;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by ty_en on 2016/08/15.
 */
public class EventRelativeLayout extends RelativeLayout {

    public EventRelativeLayout(Context context) {
        super(context);
    }

    public EventRelativeLayout(Context context, AttributeSet att) {
        super(context, att);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)  {
        // タッチされたらまずonInterceptTouchEventが呼ばれる
        // ここでtrueを返せば親ViewのonTouchEvent
        // ここでfalseを返せば子ViewのonClickやらonLongClickやら

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)  {
        // ここでtrueを返すとイベントはここで終了
        // ここでfalseを返すと子ViewのonClickやらonLongClickやら

        return false;
    }
}

