package com.example.examplebug.GestureRecognizer;

import android.content.Context;
import android.view.MotionEvent;

public class PinchGesture extends GestureRecognizer {
    public float startSpan;
    public float currentSpan;

    public PinchGesture(GestureAction action, Context context) {
        super(action, context);
    }

    public float getScale() {
        return startSpan > 0 ? currentSpan / startSpan : 1;
    }

    @Override
    protected boolean onTouch(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerCount = event.getPointerCount();

        float span = calculateAverageSpan(event, currentPosition.x, currentPosition.y);

        if (action == MotionEvent.ACTION_POINTER_DOWN && pointerCount == 2) {
            startSpan = span;
            currentSpan = span;
        }

        if (action == MotionEvent.ACTION_MOVE && pointerCount >= 2) {
            if (!isInProgress()) {
                float deltaSpan = span - startSpan;
                if (Math.abs(deltaSpan) > slop) {
                    float adjustSpan = 1 + Math.signum(deltaSpan) * (slop / startSpan);

                    startSpan *= adjustSpan;
                    currentSpan *= adjustSpan;

                    setState(BEGAN);
                }
            }

            if (isInProgress()) {
                currentSpan = span;
                setState(CHANGED);
            }
        }

        if (
                action == MotionEvent.ACTION_POINTER_UP && pointerCount == 2 ||
                        action == MotionEvent.ACTION_CANCEL && pointerCount >= 2 ||
                        action == MotionEvent.ACTION_POINTER_DOWN && pointerCount > 2 ||
                        action == MotionEvent.ACTION_POINTER_UP && pointerCount > 2
        ) {
            startSpan = 0;
            currentSpan = 0;

            if (isInProgress()) {
                if (
                        action == MotionEvent.ACTION_POINTER_UP
                ) {
                    setState(ENDED);
                } else {
                    setState(CANCELLED);
                }
            }
        }

        return false;
    }

    private float calculateAverageSpan(MotionEvent event, float centroidX, float centroidY) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int index = event.getActionIndex();

        float sum = 0;
        int num = 0;

        for (int i = 0, count = event.getPointerCount(); i < count; i++) {
            if (action == MotionEvent.ACTION_POINTER_UP && index == i) {
                continue;
            }

            sum += calculateDistance(event, i, centroidX, centroidY);
            num++;
        }

        float averageDistance = sum / num;
        return averageDistance * 2;
    }

    private float calculateDistance(MotionEvent event, int pointerIndex, float centroidX, float centroidY) {
        float dx = event.getX(pointerIndex) - centroidX;
        float dy = event.getY(pointerIndex) - centroidY;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
