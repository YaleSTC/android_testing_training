package com.example.samplegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by parth on 2/5/2017.
 */

public class ManagedNetworkImageView extends NetworkImageView {
    private int mErrorResId;

    private ProgressBar removableSpinner;

    public ManagedNetworkImageView(Context context) {
        super(context);
    }

    public ManagedNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManagedNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setErrorImageResId(int errorImage) {
        mErrorResId = errorImage;
        super.setErrorImageResId(errorImage);
    }

    @Override
    public void setImageResource(int resId) {
        if (resId == mErrorResId) {
        }
        super.setImageResource(resId);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        removableSpinner.setVisibility(GONE);
    }

    public void setProgressBar(ProgressBar spinner) {
        removableSpinner = spinner;
    }
}