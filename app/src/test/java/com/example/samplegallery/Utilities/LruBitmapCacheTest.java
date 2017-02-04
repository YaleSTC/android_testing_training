package com.example.samplegallery.Utilities;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import android.content.Context;
import android.util.DisplayMetrics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LruBitmapCacheTest {

    @Mock
    Context mMockContext;

    @Test
    public void getCacheSizeTest() throws Exception {
        //given a mocked object injected into the object under test
        mMockContext = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);

        //create a mock display metrics object
        DisplayMetrics mMockDisplayMetrics = new DisplayMetrics();
        mMockDisplayMetrics.widthPixels = 6;
        mMockDisplayMetrics.heightPixels = 10;

        //mock the chained methods
        when(mMockContext.getResources().getDisplayMetrics())
                .thenReturn(mMockDisplayMetrics);

        assertEquals(LruBitmapCache.getCacheSize(mMockContext), 6 * 10 * 3 * 4);
    }
}
