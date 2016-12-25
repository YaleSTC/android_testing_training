package com.example.samplegallery;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.example.samplegallery.Utilities.VolleyRequestQueue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Set;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

// Test the initial layout of the albums fragment.
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AlbumsFragmentTest {
    // disable the volley queue before the activity is launched to make sure that we may test
    // the behaviour of the test both before and after the data has been pulled.
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){

        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();

            VolleyRequestQueue
                    .getInstance(InstrumentationRegistry.getTargetContext().getApplicationContext())
                    .getRequestQueue()
                    .stop();
        }
    };

    @Test
    public void thisFragmentFullTest() throws Exception {
        onView(withId(R.id.albums_fragment))
                .check(matches(isDisplayed()));
        // at the beginning, the spin bar is visible.
        onView(withId(R.id.loading_progress_bar))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .check(matches(isDisplayed()));
        // and the list view is not visisble.
        onView(withId(R.id.albums_list_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
                .check(matches(not(isDisplayed())));

        // register our volley request queue as an idling resource and start it.
        VolleyQueueIdlingResource vqidr = new VolleyQueueIdlingResource("Volley Queue");
        VolleyRequestQueue.getInstance(null).getRequestQueue().start();
        Espresso.registerIdlingResources(vqidr);

        // now we know that the request queue has processed all of the requests and the view
        // should have changed.

        // we only check UI. Data tests will be somewhere else.
        onView(withId(R.id.loading_progress_bar))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
                .check(matches(not(isDisplayed())));

        // the list view is not visible.
        onView(withId(R.id.albums_list_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .check(matches(isDisplayed()));

        // assert that the view is NOT empty
        onData(anything())
                .inAdapterView(withId(R.id.albums_list_view))
                .atPosition(0);

        // click the first category
        onData(anything())
                .inAdapterView(withId(R.id.albums_list_view))
                .atPosition(0)
                .perform(click());

        // remember that we are not WAITING for the volley queue to become empty! Hence, we should
        // have our thumbnail list present.
        onView(withId(R.id.albums_fragment))
                .check(doesNotExist());
        onView(withId(R.id.photos_within_album_fragment))
                .check(matches(isDisplayed()));
        onView(withId(R.id.gridview))
                .check(matches(isDisplayed()));
    }

    /*
        Class that is used to wait for the volley queue to finish executing requests.
     */
    private class VolleyQueueIdlingResource implements IdlingResource {
        private Field mCurrentRequests;
        private String resName;
        private volatile ResourceCallback resourceCallback;

        VolleyQueueIdlingResource(String resourceName) throws NoSuchFieldException {
            resName = resourceName;
            mCurrentRequests = RequestQueue.class.getDeclaredField("mCurrentRequests");
            mCurrentRequests.setAccessible(true);
        }

        @Override
        public String getName() {
            return resName;
        }

        @Override
        public boolean isIdleNow() {
            try {
                synchronized (mCurrentRequests.get(
                        VolleyRequestQueue.getInstance(null).getRequestQueue()
                )) {
                    @SuppressWarnings("unchecked")
                    Set<Request> requests = (Set<Request>) mCurrentRequests.get(
                            VolleyRequestQueue.getInstance(null).getRequestQueue()
                    );

                    if (requests.size() == 0) {
                        if(resourceCallback != null) {
                            resourceCallback.onTransitionToIdle();
                        }

                        return true;
                    }

                    return false;
                }
            } catch (IllegalAccessException e) {
                Log.e("AlbumsFragmentTest", "Cannot access request queue variable!");
                e.printStackTrace();
                System.exit(-1);
            }

            return false;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            resourceCallback = callback;
        }
    }
}
