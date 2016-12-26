package com.example.samplegallery.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.HashSet;
import java.util.Set;


public class CallbackNetworkImageView extends NetworkImageView {
    private String mUrl;

    /** Callback interface for completed and loaded images */
    public static interface ImageSuccessfullyLoadedListener<T> {
        /** Called when a request has finished processing. */
        public void onImageLoaded();
    }

    private Set<ImageSuccessfullyLoadedListener> successCallbacks = new HashSet<>();

    public CallbackNetworkImageView(Context context) {
        super(context);
    }

    public CallbackNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CallbackNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        super.setImageUrl(url, imageLoader);
        mUrl = url;
    }

    public String getImageUrl() {
        return mUrl;
    }

    public void addSuccessListener(ImageSuccessfullyLoadedListener cb) {
        successCallbacks.add(cb);
    }

    public void removeSuccessListener(ImageSuccessfullyLoadedListener cb) {
        successCallbacks.remove(cb);
    }

    @Override
    public synchronized void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        // run all of the registered callbacks
        for (ImageSuccessfullyLoadedListener cb : successCallbacks) {
            cb.onImageLoaded();
        }
    }
}
