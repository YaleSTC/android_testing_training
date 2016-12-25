package com.example.galery.samplegalery;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.galery.samplegalery.Utilities.VolleyErrorListener;
import com.example.galery.samplegalery.Utilities.VolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotosWithinAlbumFragment extends Fragment {
    private String mAlbumID;
    private String mAlbumTitle;
    // first: photo_url -- this is used to get the thumbnail,
    // second: photo_id -- this is used to pass to the "blowup" fragment
    private ArrayList<Pair<String, String>> photosInfo = new ArrayList<>();
    private static final String RequestTag = "AlbumThumbnailsRequest";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getArguments();
        if (b != null) {
            mAlbumID = b.getString("albumID");
            mAlbumTitle = b.getString("albumTitle");
            return;
        }

        throw new NullPointerException(
                "Cannot invoke PhotosWithinAlbumFragment without albums information.");
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        // inflate and get the views we will be operating on.
        LinearLayout rootView =
                (LinearLayout) inflater.inflate(
                        R.layout.photos_within_album_fragment, container, false);
        final GridView gv = (GridView) rootView.getChildAt(0);
        final RelativeLayout progressBar = (RelativeLayout)  rootView.getChildAt(1);
        progressBar.setVisibility(View.VISIBLE);

        // create the request for photo links...
        JsonObjectRequest thumbnailsRequest = new JsonObjectRequest(
                Request.Method.GET,
                getAlbumThumbnailInfoUrl(),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // psp = photo set photo (photo array within photoset)
                        JSONArray psp;
                        try {
                            psp = response.getJSONObject("photoset").getJSONArray("photo");
                            for (int i = 0; i < psp.length(); i++) {
                                // cp -- current photoset
                                JSONObject cp = psp.getJSONObject(i);
                                photosInfo.add(
                                        new Pair<>(cp.getString("url_sq"), cp.getString("id")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // set the adapter for our grid view.
                        gv.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return photosInfo.size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return null;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                RelativeLayout rootView;
                                if (convertView == null) {
                                    rootView = (RelativeLayout) inflater
                                            .inflate(
                                                    R.layout.photos_within_album_img_view,
                                                    parent,
                                                    false);

                                } else {
                                    rootView = (RelativeLayout) convertView;
                                }

                                NetworkImageView img = (NetworkImageView) rootView.getChildAt(1);
                                img.setImageDrawable(null);
                                img.setImageUrl(
                                        photosInfo.get(position).first,
                                        VolleyRequestQueue
                                                .getInstance(getContext())
                                                .getImgLoader());

                                return rootView;
                            }
                        });

                        // set listeners so that images may be "blown up"
                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // pass on the title and id of the album
                                Bundle b = new Bundle();
                                b.putString("photoID", photosInfo.get(position).second);

                                PhotoBlowupFragment bwf = new PhotoBlowupFragment();
                                bwf.setArguments(b);

                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_activity_fragment, bwf)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });

                        // set the correct visibility
                        gv.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new VolleyErrorListener(getContext())
        );

        thumbnailsRequest.setTag(RequestTag);
        VolleyRequestQueue.getInstance(getContext()).addToRequestQueue(thumbnailsRequest);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(mAlbumTitle);
    }

    @Override
    public void onStop() {
        super.onStop();

        // cancel any requests that are pending.
        if (VolleyRequestQueue.getInstance(getContext()) != null) {
            VolleyRequestQueue.getInstance(getContext()).cancelAll(RequestTag);
        }
    }

    private String getAlbumThumbnailInfoUrl() {
        String baseUri = getContext().getResources().getString(R.string.flickr_base_url);
        Uri u = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("method", "flickr.photosets.getPhotos")
                .appendQueryParameter("api_key", APIKeys.FLICKR_API_KEY)
                .appendQueryParameter("photoset_id", mAlbumID)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("extras", "url_sq")
                .appendQueryParameter("nojsoncallback", "1")
                .build();

        return u.toString();
    }
}
