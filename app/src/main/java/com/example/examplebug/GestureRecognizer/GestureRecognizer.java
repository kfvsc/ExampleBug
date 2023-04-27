package com.example.examplebug.GestureRecognizer;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public abstract class GestureRecognizer implements View.OnTouchListener {
    public static final int BEGAN = 0;
    public static final int CHANGED = 1;
    public static final int ENDED = 2;
    public static final int CANCELLED = 3;
    public int state = ENDED;
    public PointF currentPosition = new PointF(0, 0);
    protected float slop;

    public interface GestureAction {
        void onStateChanged(GestureRecognizer recognizer);
    }

    GestureAction action;

    public GestureRecognizer(GestureAction action, Context context) {
        this.action = action;
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    protected void setState(int state) {
        this.state = state;
        action.onStateChanged(this);
    }

    protected boolean isInProgress() {
        return state == BEGAN || state == CHANGED;
    }

    @Override
    public final boolean onTouch(View view, MotionEvent event) {
        if (event.getPointerCount() > 2) {
            return false;
        }

        currentPosition = getLocation(event);

        return onTouch(event);
    }

    protected boolean onTouch(MotionEvent event) {
        return false;
    }

    protected PointF getLocation(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int index = event.getActionIndex();

        float sumX = 0;
        float sumY = 0;
        int num = 0;
        for (int i = 0; i < event.getPointerCount(); i++) {
            if (action == MotionEvent.ACTION_POINTER_UP && index == i) {
                continue;
            }

            sumX += event.getX(i);
            sumY += event.getY(i);
            num++;
        }

        return new PointF(sumX / num, sumY / num);
    }
}
