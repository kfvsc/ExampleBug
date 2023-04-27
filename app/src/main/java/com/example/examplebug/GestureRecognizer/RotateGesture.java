package com.example.examplebug.GestureRecognizer;

import android.content.Context;
import android.view.MotionEvent;

public class RotateGesture extends GestureRecognizer {
    public float startAngle;
    public float currentAngle;

    public RotateGesture(GestureAction action, Context context) {
        super(action, context);
        slop = (float) (Math.PI / 180);
    }

    public float getRotation() {
        return currentAngle - startAngle;
    }

    @Override
    protected boolean onTouch(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        float angle = calculateAngle(event);
        int pointerCount = event.getPointerCount();

        if (action == MotionEvent.ACTION_POINTER_DOWN && pointerCount == 2) {
            startAngle = angle;
            currentAngle = angle;
        }

        if (action == MotionEvent.ACTION_MOVE && pointerCount >= 2) {
            if (!isInProgress()) {
                float deltaAngle = angle - startAngle;
                if (Math.abs(deltaAngle) > slop) {
                    float adjustAngle = Math.signum(deltaAngle) * slop;

                    startAngle += adjustAngle;
                    currentAngle += adjustAngle;

                    setState(BEGAN);
                }
            }

            if (isInProgress()) {
                currentAngle = angle;
                setState(CHANGED);
            }
        }

        if (
                action == MotionEvent.ACTION_POINTER_UP && pointerCount == 2 ||
                        action == MotionEvent.ACTION_CANCEL && pointerCount >= 2
        ) {

            startAngle = 0;
            currentAngle = 0;

            if (isInProgress()) {
                if (action == MotionEvent.ACTION_POINTER_UP) {
                    setState(ENDED);
                } else {
                    setState(CANCELLED);
                }
            }
        }

        return false;
    }

    private float calculateAngle(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = event.getActionIndex();
        int pointerCount = event.getPointerCount();

        if (pointerCount < 2) {
            return 0;
        }
        if (action == MotionEvent.ACTION_POINTER_UP && pointerCount == 2) {
            return 0;
        }

        int i0 = 0;
        int i1 = 1;
        if (action == MotionEvent.ACTION_POINTER_UP) {
            if (pointerIndex == 0) {
                i0++;
                i1++;
            } else if (pointerIndex == 1) {
                i1++;
            }
        }

        float x0 = event.getX(i0);
        float y0 = event.getY(i0);
        float x1 = event.getX(i1);
        float y1 = event.getY(i1);

        return (float) Math.atan2(y1 - y0, x1 - x0);
    }
}
