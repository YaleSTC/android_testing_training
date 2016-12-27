package com.example.samplegallery;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.samplegallery.Utilities.VolleyErrorListener;
import com.example.samplegallery.Utilities.VolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlbumsFragment extends Fragment {
    public static final String RequestTag = "AlbumsQuery";
    // first: photosetID,
    // second: photosetTitle
    private ArrayList<Pair<String, String>> photosets = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate and get the views we will be operating on.
        RelativeLayout rootView =
                (RelativeLayout) inflater.inflate(R.layout.albums_fragment_layout, container, false);
        final ListView lv = (ListView) rootView.getChildAt(0);
        final RelativeLayout progress = (RelativeLayout) rootView.getChildAt(1);

        // create the request for album names
        JsonObjectRequest albumNamesRequest = new JsonObjectRequest(
                Request.Method.GET,
                getAlbumsAPIRequestString(),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // pss - photo sets set (a single set within photosets)
                        JSONArray pss;
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getContext(),
                                R.layout.albums_fragment_listview_elt,
                                R.id.albums_fragment_listview_elt
                        );

                        try {
                            pss = response.getJSONObject("photosets").getJSONArray("photoset");
                            for (int i = 0; i < pss.length(); i++) {
                                // cp -- current photoset
                                JSONObject cp = pss.getJSONObject(i);
                                adapter.add(cp.getJSONObject("title").getString("_content"));
                                photosets.add(
                                        new Pair<>(
                                                cp.getString("id"),
                                                cp.getJSONObject("title").getString("_content")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // give the listview the correct data set to display
                        lv.setAdapter(adapter);

                        // reveal the layouts.
                        lv.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.INVISIBLE);
                    }
                },
                new VolleyErrorListener(getContext())
        );
        // give the request a tag for easy cancellation. See onStop for details.
        albumNamesRequest.setTag(RequestTag);
        VolleyRequestQueue.getInstance(getContext()).addToRequestQueue(albumNamesRequest);

        // set the onClickListener that will enable us to view photos within an albums
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // pass on the title and id of the album
                Bundle b = new Bundle();
                b.putString("albumID", photosets.get(position).first);
                b.putString("albumTitle", photosets.get(position).second);

                PhotosWithinAlbumFragment pwaf = new PhotosWithinAlbumFragment();
                pwaf.setArguments(b);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activity_fragment, pwaf)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Albums");
    }

    @Override
    public void onStop() {
        super.onStop();

        // cancel any requests that are pending.
        if (VolleyRequestQueue.getInstance(getContext()) != null) {
            VolleyRequestQueue.getInstance(getContext()).cancelAll(RequestTag);
        }
    }

    private String getAlbumsAPIRequestString() {
        String baseUri = getContext().getResources().getString(R.string.flickr_base_url);
        String yaleID = getContext().getResources().getString(R.string.yale_album_id);
        Uri u = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("method", "flickr.photosets.getList")
                .appendQueryParameter("api_key", APIKeys.FLICKR_API_KEY)
                .appendQueryParameter("user_id", yaleID)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build();

        return u.toString();
    }
}