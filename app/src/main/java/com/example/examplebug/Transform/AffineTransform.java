package com.example.examplebug.Transform;

import android.graphics.Matrix;
import android.graphics.PointF;

public class AffineTransform {
    private float m00, m10, m01, m11, m02, m12;

    public AffineTransform() {
        m00 = m11 = 1;
    }

    public AffineTransform(float m00, float m10, float m01, float m11, float m02, float m12) {
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }

    public AffineTransform(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        m00 = values[0];
        m01 = values[1];
        m02 = values[2];
        m10 = values[3];
        m11 = values[4];
        m12 = values[5];
    }

    public void scale(float sx, float sy) {
        m00 *= sx;
        m01 *= sy;
        m10 *= sx;
        m11 *= sy;
    }

    public void translate(float tx, float ty) {
        m02 += tx * m00 + ty * m01;
        m12 += tx * m10 + ty * m11;
    }

    public void rotate(float theta) {
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float n00 = m00 * c + m01 * s;
        float n01 = m00 * -s + m01 * c;
        float n10 = m10 * c + m11 * s;
        float n11 = m10 * -s + m11 * c;
        m00 = n00;
        m01 = n01;
        m10 = n10;
        m11 = n11;
    }

    public PointF applyTransform(PointF src) {
        return new PointF(
                src.x * m00 + src.y * m01 + m02,
                src.x * m10 + src.y * m11 + m12
        );
    }

    public AffineTransform createInverse() {
        float det = m00 * m11 - m01 * m10;

        return new AffineTransform(
                m11 / det,
                -m10 / det,
                -m01 / det,
                m00 / det,
                (m01 * m12 - m11 * m02) / det,
                (m10 * m02 - m00 * m12) / det
        );
    }

    public Matrix toMatrix() {
        Matrix matrix = new Matrix();

        matrix.setValues(new float[] {
                m00, m01, m02,
                m10, m11, m12,
                0f, 0f, 1f
        });

        return matrix;
    }
}
