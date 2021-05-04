package com.shilina.project_x;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class LogoView extends androidx.appcompat.widget.AppCompatImageView {

    int imageSource;

    public LogoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LogoView,
                0, 0);

        try {
            imageSource = a.getResourceId(R.styleable.LogoView_logoSrc, 0);
            setImageResource(imageSource);
        } finally {
            a.recycle();
        }
    }
}
