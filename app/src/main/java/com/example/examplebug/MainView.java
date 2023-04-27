package com.example.examplebug;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.examplebug.GestureRecognizer.GestureRecognizer;
import com.example.examplebug.GestureRecognizer.GestureRecognizer.GestureAction;
import com.example.examplebug.GestureRecognizer.PanGesture;
import com.example.examplebug.GestureRecognizer.PinchGesture;
import com.example.examplebug.GestureRecognizer.RotateGesture;
import com.example.examplebug.Transform.AffineTransform;

public class MainView extends ViewGroup {
    public ContentView contentView;

    private final PanGesture panGesture;
    private final PinchGesture pinchGesture;
    private final RotateGesture rotateGesture;

    private boolean isTranslateInProgress = false;
    private boolean isScaleInProgress = false;
    private boolean isRotateInProgress = false;

    private final PointF pivot = new PointF(0, 0);
    private final PointF translate = new PointF(0, 0);
    private float scale = 1.0f;
    private float rotate = 0.0f;

    public MainView(Context context) {
        super(context);

        panGesture = new PanGesture(handlePan, context);
        pinchGesture = new PinchGesture(handlePinch, context);
        rotateGesture = new RotateGesture(handleRotate, context);
        setOnTouchListener(onTouchListener);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contentView = new ContentView(getContext());
        contentView.layout(0, 0, getWidth(), getHeight());
        addView(contentView);
    }

    private final GestureAction handlePan = new GestureAction() {
        @Override
        public void onStateChanged(GestureRecognizer r) {
            PanGesture recognizer = (PanGesture) r;

            switch (recognizer.state) {
                case GestureRecognizer.BEGAN:
                    isTranslateInProgress = true;
                    break;

                case GestureRecognizer.CHANGED:
                    PointF translation = recognizer.translation();
                    translate.x += translation.x;
                    translate.y += translation.y;
                    transformContentView();
                    break;

                case GestureRecognizer.ENDED:
                case GestureRecognizer.CANCELLED:
                    isTranslateInProgress = false;
                    break;
            }
        }
    };

    private final GestureAction handlePinch = new GestureAction() {
        float prevValue;

        @Override
        public void onStateChanged(GestureRecognizer r) {
            PinchGesture recognizer = (PinchGesture) r;

            float minScale = 0.1f;
            float maxScale = 15.0f;

            switch (recognizer.state) {
                case GestureRecognizer.BEGAN:
                    isScaleInProgress = true;
                    prevValue = scale;
                    pivotContentView(recognizer.currentPosition);
                    break;

                case GestureRecognizer.CHANGED:
                    scale = clamp(prevValue * recognizer.getScale(), minScale, maxScale);
                    transformContentView();
                    break;

                case GestureRecognizer.ENDED:
                case GestureRecognizer.CANCELLED:
                    isScaleInProgress = false;
                    break;
            }
        }
    };

    private final GestureAction handleRotate = new GestureAction() {
        float prevValue;

        @Override
        public void onStateChanged(GestureRecognizer r) {
            RotateGesture recognizer = (RotateGesture) r;

            switch (recognizer.state) {
                case GestureRecognizer.BEGAN:
                    prevValue = rotate;
                    isRotateInProgress = true;
                    pivotContentView(recognizer.currentPosition);
                    break;

                case GestureRecognizer.CHANGED:
                    rotate = prevValue + recognizer.getRotation();
                    transformContentView();
                    break;

                case GestureRecognizer.ENDED:
                case GestureRecognizer.CANCELLED:
                    isRotateInProgress = false;
                    break;
            }
        }
    };

    private final OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            panGesture.onTouch(v, event);
            pinchGesture.onTouch(v, event);
            rotateGesture.onTouch(v, event);
            return true;
        }
    };

    private void pivotContentView(PointF focus) {
        AffineTransform transform = new AffineTransform(contentView.getMatrix());
        PointF newPivot = transform.createInverse().applyTransform(focus);

        translate.x -= (pivot.x - newPivot.x) * (scale - 1);
        translate.y -= (pivot.y - newPivot.y) * (scale - 1);
        pivot.x = newPivot.x;
        pivot.y = newPivot.y;
    }

    private void transformContentView() {
        AffineTransform transform = new AffineTransform();
        transform.translate(pivot.x, pivot.y);
        transform.translate(translate.x, translate.y);
        transform.scale(scale, scale);
        transform.rotate(rotate);
        transform.translate(-pivot.x, -pivot.y);

        contentView.setMatrix(transform.toMatrix());
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
