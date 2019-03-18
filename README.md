
Hotspots aims to provide you an extremely easy way to discover places in a city where you can take your laptop, when you are tired of applying wifi filter on other places.

This is the android client for hotspots.

Update:

I've taken down the app from play store and it's no longer in active development.


This was one of my side projects that I pursued primarily to get my hands dirty with Android Development. This was 2015-16 when there was **no architecture guidelines, no room, no view models**, **dagger 2** was getting popular and I wasn't aware of things like **Retrofit**, **Glide**, **Butterknife**, **Stetho** or **Timber**. So you'll see everything RAW in this project :D

- grouping of classes by Android component type rather than feature-wise or responsibility-wise.
- `HTTPUrlConnection` for networking (I am not sure if I'll again use `DataOutputStream` in my life to send data to server :))
- `BitmapFactory` and support library's `LruCache`([usage][3]) for dealing with images
- Using `AsyncTask` for network calls (look at [this][1] and [this][2])
- `Enum` instead of `StringDef`
- `LocationManager` for requesting location updates.
- `Google Analytics` for metrics
- `findViewById` everywhere since there was no butterknife.

The best thing that I like about this project is that I've written functional tests for each screen using **Espresso**.





[1]: https://github.com/yashasvigirdhar/Hotspots-Android/blob/b9a6443c6f1cf34ff58325dd77008fffb8c41937/app/src/main/java/com/valmiki/hotspots/activities/AppFeedbackActivity.java#L222
[2]: https://github.com/yashasvigirdhar/Hotspots-Android/blob/b9a6443c6f1cf34ff58325dd77008fffb8c41937/app/src/main/java/com/valmiki/hotspots/utils/CheckInternetAsyncTask.java#L11
[3]: https://github.com/yashasvigirdhar/Hotspots-Android/blob/b9a6443c6f1cf34ff58325dd77008fffb8c41937/app/src/main/java/com/valmiki/hotspots/MyApplication.java#L31

