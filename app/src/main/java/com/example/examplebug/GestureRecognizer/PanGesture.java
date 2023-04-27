package com.example.examplebug.GestureRecognizer;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

public class PanGesture extends GestureRecognizer {
    private PointF startPosition = new PointF(0, 0);

    public PanGesture(GestureAction action, Context context) {
        super(action, context);
    }

    public PointF translation() {
        return new PointF(
                currentPosition.x - startPosition.x,
                currentPosition.y - startPosition.y
        );
    }

    @Override
    protected boolean onTouch(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            startPosition = currentPosition;
        }

        if (action == MotionEvent.ACTION_MOVE) {
            if (!isInProgress()) {
                PointF delta = translation();

                if (Math.abs(delta.x) > slop || Math.abs(delta.y) > slop) {
                    startPosition = currentPosition;
                    setState(BEGAN);
                }
            }

            if (isInProgress()) {
                setState(CHANGED);
                startPosition = currentPosition;
            }
        }

        if (
                action == MotionEvent.ACTION_UP ||
                        action == MotionEvent.ACTION_CANCEL ||
                        action == MotionEvent.ACTION_POINTER_DOWN ||
                        action == MotionEvent.ACTION_POINTER_UP
        ) {
            if (isInProgress()) {
                if (
                        action == MotionEvent.ACTION_UP ||
                                action == MotionEvent.ACTION_POINTER_DOWN ||
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
}
