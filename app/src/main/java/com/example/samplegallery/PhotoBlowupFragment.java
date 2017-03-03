package com.example.samplegallery;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.samplegallery.Utilities.VolleyErrorListener;
import com.example.samplegallery.Utilities.VolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhotoBlowupFragment extends Fragment {
    String photoID = null;
    private static final String RequestTag = "PhotoBlowupString";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getArguments();
        if (b == null) {
            throw new NullPointerException(
                    "PhotoBlowupFragment may not be invoked without photo information.");
        }

        photoID = b.getString("photoID");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootView = (RelativeLayout) inflater.
                inflate(R.layout.photo_blowup_fragment, container, false);
        final ImageView img = (ImageView) rootView.findViewById(R.id.photo_blowup);
        final TextView photoTitleView = (TextView) rootView.findViewById(R.id.photo_title);
        final TextView photoDescriptionView = (TextView) rootView.findViewById(R.id.photo_description);
        final ProgressBar progressSpinner = (ProgressBar) rootView.findViewById(R.id.blowup_progress_bar);
        // / create a request for photo title
        JsonObjectRequest photoInfoRequest = new JsonObjectRequest(
                Request.Method.GET,
                getPhotosInfoUrl(),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // get the biggest size available
                        try {
                            JSONObject photoInfo = response.getJSONObject("photo");
                            photoTitleView.setText(
                                    photoInfo.getJSONObject("title").getString("_content"));
                            photoDescriptionView.setText(
                                    photoInfo.getJSONObject("description").getString("_content"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new VolleyErrorListener(getContext())
        );

        // create the request for photo links and their sizes...
        JsonObjectRequest photoSizesRequest = new JsonObjectRequest(
                Request.Method.GET,
                getPhotoSizesUrl(),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // get the biggest size available
                        JSONArray sizes;
                        String photoUrl = null;
                        try {
                            sizes = response.getJSONObject("sizes").getJSONArray("size");
                            photoUrl = sizes
                                    .getJSONObject(sizes.length() - 1) //finding a smaller size will speed up load times
                                    .getString("source");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //isnt onResponse on the uithread? would moving it off the main thread speed things up? would it be possible to?


                        Response.Listener<Bitmap> imgListener = new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                img.setImageBitmap(response);
                                photoDescriptionView.setVisibility(View.VISIBLE);
                                photoTitleView.setVisibility(View.VISIBLE);
                                progressSpinner.setVisibility(View.GONE); //removes the progress spinner from the background
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        };
                        //solution is deprecated: better, worse or equivalent to hacky?
                        ImageRequest getImgReq = new ImageRequest(photoUrl, imgListener, 0,0,null,errorListener);
                        VolleyRequestQueue.getInstance(getContext()).addToRequestQueue(getImgReq);
                    }
                },
                new VolleyErrorListener(getContext())
        );

        photoSizesRequest.setTag(RequestTag);
        photoInfoRequest.setTag(RequestTag);
        VolleyRequestQueue.getInstance(getContext()).addToRequestQueue(photoInfoRequest);
        VolleyRequestQueue.getInstance(getContext()).addToRequestQueue(photoSizesRequest);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        // cancel any requests that are pending.
        if (VolleyRequestQueue.getInstance(getContext()) != null) {
            VolleyRequestQueue.getInstance(getContext()).cancelAll(RequestTag);
        }
    }

    private String getPhotoSizesUrl() {
        String baseUri = getContext().getResources().getString(R.string.flickr_base_url);
        Uri u = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("method", "flickr.photos.getSizes")
                .appendQueryParameter("api_key", APIKeys.FLICKR_API_KEY)
                .appendQueryParameter("photo_id", photoID)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build();

        return u.toString();
    }

    private String getPhotosInfoUrl() {
        String baseUri = getContext().getResources().getString(R.string.flickr_base_url);
        Uri u = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("method", "flickr.photos.getInfo")
                .appendQueryParameter("api_key", APIKeys.FLICKR_API_KEY)
                .appendQueryParameter("photo_id", photoID)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build();

        return u.toString();
    }
}
