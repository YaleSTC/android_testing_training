package com.example.samplegallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // attempting to add another fragment when we are restoring might result in multiple
        // fragments on the stack or overlapping fragments...
        if (savedInstanceState != null) {
            return;
        }

        // add the albums fragment. This is the basis of all user interaction.
        AlbumsFragment af = new AlbumsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity_fragment, af)
                .commit();
    }
}