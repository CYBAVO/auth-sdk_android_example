package com.cybavo.example.auth.security;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybavo.example.auth.R;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.ImageViewCompat;

public class SecurityItemView extends LinearLayout {

    private TextView mTitle;
    private ImageView mIcon;
    private ContentLoadingProgressBar mProgress;
    private SecurityItem.State mState;

    public SecurityItemView(Context context) {
        super(context);
    }

    public SecurityItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SecurityItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitle = findViewById(R.id.title);
        mIcon = findViewById(R.id.icon);
        mProgress = findViewById(R.id.progress);
    }

    void setTitle(@StringRes int titleRes) {
        mTitle.setText(titleRes);
    }

    void setState(SecurityItem.State state) {
        if (mState != null) {
            return;
        }

        int colorRes;
        int iconRes;
        switch (state) {
            case FATAL:
                iconRes = R.drawable.ic_secure_fail;
                colorRes = R.color.colorReject;
                break;
            case WARNING:
                iconRes = R.drawable.ic_secure_waning;
                colorRes = R.color.colorWarning;
                break;
            case PASS:
            default:
                iconRes = R.drawable.ic_secure_pass;
                colorRes = android.R.color.white;
                break;
        }
        mIcon.setImageResource(iconRes);
        ImageViewCompat.setImageTintList(mIcon, ColorStateList.valueOf(ContextCompat.getColor(getContext(), colorRes)));

        mProgress.animate().scaleX(0.5f).scaleY(0.5f).alpha(0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgress.setVisibility(INVISIBLE);
            }
        }).start();

        mIcon.setScaleX(0.5f);
        mIcon.setScaleY(0.5f);
        mIcon.setAlpha(0f);
        mIcon.animate().scaleX(1).scaleY(1).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIcon.setVisibility(VISIBLE);
            }
        }).start();
        mState = state;
    }
}
