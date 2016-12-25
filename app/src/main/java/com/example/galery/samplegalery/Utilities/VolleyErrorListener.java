package com.example.galery.samplegalery.Utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class VolleyErrorListener implements Response.ErrorListener {
    private Context mCtx;

    public VolleyErrorListener(Context ctx) {
        super();

        mCtx = ctx;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // inform the user of the failure and log the error for debugging.
        if (error instanceof NetworkError) {
            Toast.makeText(
                    mCtx,
                    "Network failure. Please make sure you are connected " +
                            "to the Internet.",
                    Toast.LENGTH_LONG)
                    .show();
        } else if (error instanceof TimeoutError) {
            Toast.makeText(
                    mCtx,
                    "Timeout. Your connection might be slow or Flickr is down. " +
                            "Please try again later.",
                    Toast.LENGTH_LONG)
                    .show();
        } else if (error instanceof AuthFailureError) {
            Toast.makeText(
                    mCtx,
                    "Auth failure. Please make sure you are using the correct " +
                            "API key for flickr",
                    Toast.LENGTH_LONG)
                    .show();
        } else {
            error.printStackTrace();
        }

        Log.e("ConnError", error.getMessage());
    }
}
