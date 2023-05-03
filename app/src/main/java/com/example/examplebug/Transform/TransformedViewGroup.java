package com.example.examplebug.Transform;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;

public abstract class TransformedViewGroup extends ViewGroup {
    private Matrix matrix = new Matrix();

    public TransformedViewGroup(Context context) {
        super(context);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.setMatrix(matrix);
        super.dispatchDraw(canvas);
    }

    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;

        if (getParent() instanceof View) {
            ((View) getParent()).invalidate();
        }

        invalidate();
    }
}
