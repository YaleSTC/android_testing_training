package com.example.samplegallery.Utilities;

import android.content.Context;
import android.icu.util.RangeValueIterator;
import android.util.DisplayMetrics;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Created by parth on 1/22/2017.
 */

public class LruBitmapCacheTest {

    @Mock
    Context mMockContext;

    @Test
    public void getCacheSize_isCorrect() throws Exception {

        mMockContext = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);

        DisplayMetrics dm = Mockito.mock(DisplayMetrics.class, Mockito.RETURNS_DEEP_STUBS);

        dm.widthPixels = 50;
        dm.heightPixels = 100;

        Mockito.when(mMockContext.getResources().getDisplayMetrics()).thenReturn(dm);


        Method method = LruBitmapCache.class.getDeclaredMethod("getCacheSize", Context.class);
        method.setAccessible(true);
        //method.invoke(mMockContext, null);

        assertThat(
                (int)(method.invoke(null, mMockContext)   ),
                is(equalTo(dm.widthPixels*dm.heightPixels*4*3)));
    }
}
