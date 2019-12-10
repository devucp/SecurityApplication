package com.example.securityapplication.model;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.securityapplication.R;

public class CircularProgressButton extends android.support.v7.widget.AppCompatButton {
    public CircularProgressButton(Context context) {
        super(context);
    }

    private enum State {
        PROGRESS, IDLE
    }

    public class LoadingButton extends AppCompatButton {
        public LoadingButton(Context context) {
            super(context);
            init(context);
        }

        public LoadingButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            GradientDrawable mGradientDrawable = (GradientDrawable)
                    ContextCompat.getDrawable(context, R.drawable.loading_bttn_shape);

            setBackground(mGradientDrawable);
        }

    }
}