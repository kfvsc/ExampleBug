package com.example.examplebug;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.example.examplebug.Transform.TransformedViewGroup;

public class ContentView extends TransformedViewGroup {
    public ContentView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        TextView text = new TextView(getContext());
        text.layout(0, 0, 400, 400);
        text.setBackgroundColor(Color.GRAY);
        text.setTextColor(Color.WHITE);
        text.setTextSize(30);
        text.setText("Hello, world!");
        addView(text);
    }
}
