## SDMP android training - best practices, Volley and testing

Throughout this tutorial we will be working with my sample implementation of [the simple gallery app](https://github.com/yalestc/training/blob/master/android.md) that you were asked to write up over the break. Before you start following any other instructions, please fork this github repository. All of the exercises should be based on the `master` branch.

### Preliminaries

#### API keys

Before you proceed, please open the app in Android Studio and find the `APIKeys.java` file. The flickr key you will find there is not a real key -- please put your own key in place of the fake placeholder.

#### Beaurocracy

Throughout this tutorial I will be asking you a bunch of questions and I will ask you to implement different things. When you think you are done with a particular part of the tutorial, please let me know and I will ask you to tell me what you think the answers to the particular questions are. I will also review the code you write just to make sure that everything works. Hope you enjoy!

If you are doing this tutorial at other time (aka, we have no face time), please push code to your fork of the repo (to a clearly marked branch) and send me the answers to questions on email/slack. I'll review and get back to you as soon as I can.

####Solutions

I have written sample solutions for every single coding exercise that follows. Please do not look at them before you have finished implementing your own solution. The solutions may be found at this repository in corresponding branches.

### Best practices

Briefly, go through the code consituting the application and play with it in a simulator to see how it works. It's a simple app and consists of three fragments and few utility functions, nothing super fancy. That said, I have left a bunch of _poorly written code samples_ within the source. Here, we will focus on creating hard to maintain layouts - you'll go through the code to find them and correct them. 

1. Please read through 
    * `albums_fragment_listview_elt.xml`,
    * `photos_within_album_fragment.xml`, 
    * `photos_within_album_img_view.xml`,
    * `photo_blowup_fragment.xml`.
  
   Please ignore the "HUGE HACK" comment for now. We will come back to it a little bit later. 

   What is the basic flaw of the way those layouts are written? How hard would it be to change all of the font sizes and their colors to something new? How about the padding around TextViews or ImageViews? Discuss the fix and implement it. For hints and tips see [this article](https://guides.codepath.com/android/Styles-and-Themes).

   SOLUTION: Focusing on controlling the styles of progress bars, find on `InStyle` branch. 

2. While reading the source code you have likely found that I often use `.getChildAt(int)` function to get a handle to "nested" resources. Why is this a bad idea and what should we use instead? 

### Volley

Writing the Yale app this semester we will be making a ton of API requests. If you have ever written Android applications using only the most standard libraries, you probably recognize code reminiscent of [this ugly class](https://github.com/YaleSTC/YalePublic-android/blob/master/src/edu/yale/yalepublic/JSONReader.java). The class linked is very bare-bones, does not allow for requests of data of any other format than JSON, does not allow all headers to be attached etc. It also happens to be slow and does not cache the results for future use. In short, it is painfull to write and debug and even more painful to use. 

Few years ago Google (2014 I think) has introduced the [Volley](https://developer.android.com/training/volley/index.html) library to help with this exact issue.

1. Please read the linked, short description of what Volley is. 

2. Please read through the source code of Utilities package within the app. Try to understand all of its pieces using the following references as needed:
    * [Simple introduction to Volley](https://developer.android.com/training/volley/simple.html)
    * [Singleton pattern for Volley request queue](https://developer.android.com/training/volley/requestqueue.html)
    * [Getting images using Volley with custom cache](https://developer.android.com/training/volley/request.html)

    You should be able to describe how to make a Volley request, how and why we are setting up a singleton request queue, how and why to add tags to requests, how to cancel requests, how to create custom cache for images etc. For more examples of usage, please see the source code. We have most of the Volley parts set up within `Utilities` package and it is used in every single fragment within the application.

3. Now that we know how Volley works, we will put that knowledge to use. Open the app in the simulator, go to any of the albums and click on any of the photo thumbnails. 

    You'll notice that the title and the description of the photo appear before the photo is loaded and that the photo itself takes a long time to load. Look at the corresponding code within `PhotoBlowupFragment.java`. 
   * Using the [flickr api reference](https://www.flickr.com/services/api/), explain why it takes so long for the photo to download? There is a simple fix for this issue. What is it? Explain and implement. 
   * Even though the photo loads faster, the fact that the title and the description appear before the photo is rather annoying. Moreover, as you have notice before, and as the "HUGE HACK" comment within .xml files indicates, the progress bar that is visible at the beginning is never stopped. 

      In short, there is no synchronization among the element of the layouts. This is a consequence of using NetworkImageView and relying on its simplicity. This may be relatively easily changed by [creating a class extending the NetworkImageView](http://stackoverflow.com/a/31280690/7009520) or by [using a regular ImageView instead](http://stackoverflow.com/a/20292782/7009520). Choose one of the two approaches (tell me why you think this one works best!) and stop the spinners "underneath" the photo thumbnails after the photo has been loaded. 

       NOTE: You totally can synchronize the photo blowup as well! It's only a few lines if you did the former part well!

SOLUTION: can be found on `SynchronizedNetworkImageView` branch. Please note that there are two commits there. The first one corresponds to synchronization of the gridview elements and the second one corresponds to synchronization within the photo blow up.

### Testing

As explained during our earlier session, code testing is one of the most overlooked (at least during amateurish projects) and the most important parts of code development, since it makes sure that the code works and that there are no regressions caused by new code and features! Starting with the new Yale app we will be introducing Continuous Integration (the code will be built and tested for you before you are able to push / right after you push!) and rigorous guidelines for both unit and functional tests. While I will be taking care of CI, everyone must write good tests. In this part of the tutorial we will go over the basics of writing tests within the android framework. 

As with everything in Android, Google is your best friend and offers a bunch of good materials considering testing (some of which is really fresh -- 2016 or after).

#### Setup

Please follow the setup steps outlined [here](https://developer.android.com/training/testing/unit-testing/local-unit-tests.html#setup) to set up the necessary libraries for testing.

#### Unit Tests
Please read through this rather short document which outlines [how to write simple unit tests](https://developer.android.com/training/testing/unit-testing/local-unit-tests.html). We will use this knowledge to write a few simple tests for the `getCacheSize` method within `LruBitmapCache`. This should be simple and short, but will give you an idea of how such testing code looks like. 

0. Make sure that you have set up the testing environment correctly (does the project compile and the dependencies are added?). You might have to clean re sync gradle.
1. Go to Android Studio and find the `com.example.samplegallery (test)` package. Take a look at the file present in it -- it defines the most basic template for writing unit tests. 
2. Modify the file (or make a new one) named `LruBitmapCacheTest.java`. Notice the naming convention -- whenever you will be writing Java Classes (or even methods within existing classes), you will have to also write tests for what you have written! The names of files containing tests must be the same as the original file with "Test" added on. 
3. Write a test for `LruBitmapCache.getCacheSize(Context mContext)`. Notice that you will have to
   * mock the `Context` object -- please use Mockito. For a quick explanation of what mocks are and why we want to use them, see [this StackOverflow thread](http://stackoverflow.com/questions/3622455/what-is-the-purpose-of-mock-objects).
   * mock chained methods. To do this, you must enable what is known as deep stubbing. This may be done using code similar to the following:

```
		// allow deep stubbing
		ClassIWantToMock mockObj = Mockito.mock(ClassIWantToMock.class, Mockito.RETURN_DEEP_STUBS);
		// now we can deep stub like this!
		when (mockObj.firstFunction().secondFunction())
			.thenReturn(//whatever you want);
```

   * NOTE: You might have a little trouble with access specifiers on the original `LruBitmapCache` class. That is to be expected - it is defined as "package local". Please modify the structure of the test file tree. That is, please make it look exactly the same as that of the project files (make Utilities package within the test files and place your test there). In general, this is the common practice: make you test-file-tree look exactly the same as the regular file tree! This makes it much easier to locate the relevant tests quickly.
4. Run your tests by right clicking on the test package and clicking "run tests...". Your test, even if written correctly, should fail! There is a bug in the LruBitmapCache class, please fix it! Assume that the comment above the tested function is correct.

Congrats! you've just written your first Android test! If you would like to compare your solution to mine, find it in `Testing` branch.

Notice that up until now we have only tested a static, public member function* which is almost the simplest test we could have written (well, we also mocked an object so I guess they get easier, but not by much). There are many other intricacies that come up when we test, of which the most important are:

1. Testing private class members. Sometimes a class has a lot of private functions that must be tested (there seems to be a lot of conflict among Java (and not only) community as to whether private functions should be tested at all, how to structure code to avoid private methods etc., but here I just want to show you that this may be done). Testing such functions uses the most hated and loved feature of Java: reflection. Let's take a look at how this is done. 

    * First, read this to understand [what the hell is reflection and why is it useful?](http://stackoverflow.com/questions/37628/what-is-reflection-and-why-is-it-useful). NOTE: The concept itself is super important and rather meta, but it is good to have a working understanding. If you'd like to dig deeper, you totally can using the Oracle articles on [reflection](http://docs.oracle.com/javase/tutorial/reflect/index.html) and [introspection](http://web.archive.org/web/20090226224821/http://java.sun.com/docs/books/tutorial/javabeans/introspection/index.html).
    * Second, read [this stack overflow post](http://stackoverflow.com/questions/34571/how-do-i-test-a-class-that-has-private-methods-fields-or-inner-classes) explicitly showing you how to reflect to invoke (and so test) a private class function.
    * Now, change the modifier of the `getCacheSize(Context)` function within `LruBitmapCache` to `private`. Modify the tests written before and make sure that they work.
    * Make sure to change other files that are affected by this change! (Make a new constructor exists that makes use of getCacheSize "under the covers). 

    Congrats! You know how to test private (static) member functions! Throughout the discussion above I have lied to you a little bit. Reflection is in fact not the ONLY way to test private methods, but it is by far the most preferred method within Android. If you would like to read on the other existing methods in general Java settings, please see [this article](http://www.artima.com/suiterunner/privateP.html). It uses suiterunner with JUnit, but the principles are sound.
    
    Solutions: Please see the `Private_Testing` branch.

2. Testing static member functions. In general, static functions are tested in the exact same way as any other function. The problem arises when a static member function is used within a different function and we would like to mock the return value from such a member function. For instance, if we have:

``` 
class MyTestClass {
	private static int getInt() {
		return 1;	
	}

	public int functionToTest() {
		int i = getInt();
		return (i == 0);		
	}
}
```

   Of course, this example is silly but the problem is that one cannot simply mock the return value of `getInt()` without changing the code. This is one of the reasons static functions that are relevant to general logic of code are [considered harmful](https://testing.googleblog.com/2008/12/static-methods-are-death-to-testability.html), "bad taste" and in general an "anti-pattern." In general, any static function which "injects logic" into any other part of the code is bad. r

   Even so, sometimes we HAVE TO test such functions (for instance when we are dealing with legacy code that we don't want to refactor). To circumvent this problem, I will in general discourage you from writing static methods, but sometimes it is necessary (for instance when a 3rd party library uses such). We will deal with those using [PowerMockito](https://github.com/powermock/powermock/wiki/MockitoUsage) with simple code usage shown [here](http://stackoverflow.com/questions/21105403/mocking-static-methods-with-mockito). We will not exercise this now since this will come up very rarely and we will tackle these problems as they appear.

SOLUTION: `Testing` branch.

#### Instrumented Tests.

I know that there is a ton of information within this tutorial already. That is why I will not ask you to implement instrumented tests right now. You will read about them briefly in [this article](https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests.html) and understand:

  * The difference between regular unit tests and instrumented tests
  * When to use regular unit tests and when to use instrumented tests
  * What are the trade-offs between the two options?

#### Automatized UI Tests

Automatized UI tests have been around for a while, but the newest "Toy" for doing these is Espresso, released by Google in 2016. One thing that must be said about automatized UI testing is that it is rather crude and requires a lot of code to test relatively basic things. In this part, there is no exercise -- I'd like you to read [this article by Google](https://developer.android.com/training/testing/ui-testing/espresso-testing.html). Then, please visit the `UITest` branch of the project and see a sample test that I have written. The test illustrates everything the article mentions and shows how and when reflection is useful (expect for testing private methods of course).

Few notes considering the test: It is not the most basic of tests. We create our own `ActivityTestRule` to explicitly control when the web requests are fulfilled, we use reflection to see when such requests are fulfilled etc. We could have restructured the class itself (VolleyRequestQueue) to always be explicitly started/stopped and we could have added a counter for the number of requests pending, however this would make the usage of such a class annoying. The point of reflection here is that we do not have to do any of the above and the `RequestQueue` is still simple to use and it is testable!

You should notice that I am using private variables that aren't very well documented within Volley such as `mRequestQueue` for `RequestQueue`. Whenever you need any such variables, a good practice is to look within Google source code (all of android is open source!). Bear in mind that any undocumented code may be subject to changes that are not announced and such practice might lead to hardship in upgrading libraries. That said, the current variable used is the basis of the whole `RequestQueue` class and is likely safe to use. Even so, it might be a good idea to maintain such a queue explicitly within your class (here `VolleyRequestQueue`) just to make sure that the behaviour of the class will not change without notice!

### The End

I hope you enjoyed! Let me know if there are any mistakes/typos/shortcomings in the tutorial at stan.swidwinski@gmail.com or stanislaw.swidwinski@yale.edu. 