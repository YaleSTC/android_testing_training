package com.example.samplegallery;

/**
 * Created by Kadiyala on 1/16/17.
 */

import android.content.Context;
import android.util.DisplayMetrics;

import com.example.samplegallery.Utilities.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@RunWith(MockitoJUnitRunner.class)
public class LruBitmapCacheTest {

    @Mock
    Context mockContext;

    // Solution when LruBitmapCache.getCacheSize is public
//    @Test
//    public void test()
//    {
//        Context mockContext = Mockito.mock(Context.class,Mockito.RETURNS_DEEP_STUBS);
//
//
//        DisplayMetrics mockedDisplaymetrics = new DisplayMetrics();
//
//        mockedDisplaymetrics.widthPixels = 4;
//        mockedDisplaymetrics.heightPixels = 5;
//
//        when(mockContext.getResources().getDisplayMetrics()).thenReturn(mockedDisplaymetrics);
//
//        int cachesize = LruBitmapCache.getCacheSize(mockContext);
//
//
//        assertThat(cachesize,is(240));
//
//    }

    // For private getCacheSize method
    @Test
    public void test() throws NoSuchMethodException {
        Context mockContext = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);


        DisplayMetrics mockedDisplaymetrics = new DisplayMetrics();

        mockedDisplaymetrics.widthPixels = 4;
        mockedDisplaymetrics.heightPixels = 5;

        when(mockContext.getResources().getDisplayMetrics()).thenReturn(mockedDisplaymetrics);

        Method getCacheSize = null;
        try {
            getCacheSize = LruBitmapCache.class.getDeclaredMethod("getCacheSize", Context.class);
            getCacheSize.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        int cachesize = 0;
        try {
            cachesize = (int) getCacheSize.invoke(LruBitmapCache.class, mockContext);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        assertThat(cachesize, is(240));

    }
}
