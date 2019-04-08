package com.example.measureit.Part_NEW.DataSession;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;


public class BouncyListView extends ListView {

    private Context context;
    private boolean outbound;
    private int distance;
    private int firstOut;

    public BouncyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BouncyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    GestureDetector gestureDetector = new GestureDetector(context,
            new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    int firstPosition = getFirstVisiblePosition();
                    int lastPosition = getLastVisiblePosition();
                    int itemCount = getCount();
                    if (outbound && firstPosition !=0 && lastPosition != (itemCount - 1)) {
                        scrollTo(0, 0);
                        return false;
                    }
                    View firstView = getChildAt(firstPosition);
                    if (!outbound) {
                        firstOut = (int) e2.getRawY();
                    }
                    if (firstView != null && (outbound || (firstPosition == 0 && firstView.getTop()==0
                    && distanceY <= 0))) {
                        distance = firstOut - (int) e2.getRawY();
                        scrollTo(0, distance/2);
                        return true;
                    }
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int act = event.getAction();
        if ((act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_CANCEL) && outbound) {
            outbound = false;
        }
        if (!gestureDetector.onTouchEvent(event)) {
            outbound = false;
        }
        else {
            outbound = true;
        }
        return super.dispatchTouchEvent(event);
    }
}
