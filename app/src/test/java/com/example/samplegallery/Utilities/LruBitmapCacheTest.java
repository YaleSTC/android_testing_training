package com.example.samplegallery.Utilities;

import android.content.Context;
import android.util.DisplayMetrics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class LruBitmapCacheTest {
    @Mock
    Context mContextMock;

    @Test
    public void getCacheSizeTest() throws Exception {
        // make a mock of the display metrics to control what is returned.
        DisplayMetrics mockDisplayMetrics = new DisplayMetrics();
        mockDisplayMetrics.widthPixels = 4;
        mockDisplayMetrics.heightPixels = 5;

        // allow deep stubbing
        mContextMock = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);
        when (mContextMock.getResources().getDisplayMetrics())
                .thenReturn(mockDisplayMetrics);

        // finally check the assertion.
        assertThat(LruBitmapCache.getCacheSize(mContextMock), is(4 * 5 * 3 * 4));
    }
}