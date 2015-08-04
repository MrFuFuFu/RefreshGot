SwipeRefreshBoth
=====================


## 描述

下拉刷新，取出了 Android support v4 包里的 SwipeRefreshLayout, 对其进行了扩展，使其增加上拉加载的功能，只增加了 ListView 的扩展，不对 ScrollView 进行扩展，主要是 ScrollView 扩展没有实际意义，如果需要，可以邮件我，立马加上（之前加上了，为了少一个类，被我干掉了）。


## describe

Pull to refresh, get from Android support v4 package SwipeRefreshLayout, it was extended to the pull from bottom, only adds ListView extension. does not extend ScrollView. mainly ScrollView extended moot, if necessary you can e-mail me, immediately add it (before adding, in order to reduce a class, I had take it removed).


## How to use?


需要使用下拉刷新的xml 这样写：

```xml
<mrfu.swiperefreshboth.lib.SwipeRefreshBothPull
    android:id="@+id/swipe_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <ListView
        android:id="@+id/listview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</mrfu.swiperefreshboth.lib.SwipeRefreshBothPull>
```

Java 代码中如下写法，需要增加 `PullRefreshListener` 接口

```Java
···
mSwipeLayout = (SwipeRefreshBothPull) findViewById(R.id.swipe_container);
mSwipeLayout.setOnPullRefreshListener(this);
···
@Override
public void onPullDownRefresh() {
   //TODO 下拉刷新时的操作
}
@Override
public void onPullUpRefresh() {
   //TODO 上拉加载时的操作
}
···
//刷新完成后调用该方法结束刷新动画
mSwipeLayout.refreshReset();
···
```


## Preview

![preview1](https://raw.githubusercontent.com/MrFuFuFu/SwipeRefreshBoth/master/images/pulldown.png)
![preview2](https://raw.githubusercontent.com/MrFuFuFu/SwipeRefreshBoth/master/images/pullup.png)

## More about me

* [MrFu-傅圆的个人博客](http://mrfufufu.github.io/)

License
============

    Copyright 2015 MrFu

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.