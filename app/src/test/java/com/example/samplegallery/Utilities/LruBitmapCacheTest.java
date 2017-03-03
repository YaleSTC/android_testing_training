package com.example.samplegallery.Utilities;

import android.content.Context;
import android.util.DisplayMetrics;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by cr554 on 3/3/2017.
 *
 */

public class LruBitmapCacheTest{
    @Mock
    Context mCtx;

    @Test
    public void getCacheSizeTest() throws Exception{
        //set up an environment
        DisplayMetrics dm = new DisplayMetrics();
        dm.heightPixels = 4;
        dm.widthPixels = 2;

        //deep
        //I want to mock context; but i declared it as a member var so, its not necessary to say that
        mCtx = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);
        //DEEP STUBBIN'
        when(mCtx.getResources().getDisplayMetrics()).thenReturn(dm); // plain english: When we get the display metric from the rsource of the context, give it the mock dm we made (this is the  hot dog supplied to the cook)

        //now we assert that what we're testing is what we expect it to be.
        assertThat(LruBitmapCache.getCacheSize(mCtx),is(4*2*4*4)); //4h * 2w * 4pages * 4bytes

        //does including these #'s in the test necessitate putting that shit into an asset? ALso why x4 for 3 pages???
        //"any constant that is used 2+ should be an asset"
    }

}
