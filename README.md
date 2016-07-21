# RefreshGot


## Description

An pull down refresh and pull up refresh for `ListView` and `RecyclerView`.

RefreshGot means Refresh ***Game Of Thrones***.

## Dependency

use Gradle:

```xml
compile 'mrfu.refreshgot:refreshgot:0.1'
```

or use maven:

```xml
<dependency>
  <groupId>mrfu.refreshgot</groupId>
  <artifactId>refreshgot</artifactId>
  <version>0.1</version>
  <type>pom</type>
</dependency>
```

## How do I use RefreshGot?

just like this below code：

```xml
<mrfu.refreshgot.GotRefresh
    android:id="@+id/swipe_refresh_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <mrfu.refreshgot.GotRecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</mrfu.refreshgot.GotRefresh>
```

or

```xml
<mrfu.refreshgot.GotRefresh
    android:id="@+id/lx_refresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <mrfu.refreshgot.GotListView
        android:id="@+id/lx_listview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</mrfu.refreshgot.GotRefresh>
```



In java code，add `PullRefreshListener` implements

RecyclerView:

```Java
mGotRecyclerView.setAdapter(mRefreshAdapter);
mGotRefresh.setOnPullRefreshListener(this);
```

When Loading finish:

```java
mGotRefresh.refreshReset();
```

When No more data, and load ended:

```java
//Only RecyclerView needs to call this method, ListView doesn't needs it.
mGotRefresh.setLoadMoreEnable(false);//close pull up refresh
```


If you only need pull down refresh, don't need pull up refresh, just call this method to close it:

```java
mGotRefresh.setNoLoadMore();
```



## Preview

![preview1](https://raw.githubusercontent.com/MrFuFuFu/SwipeRefreshBoth/master/images/pulldown.png)
![preview2](https://raw.githubusercontent.com/MrFuFuFu/SwipeRefreshBoth/master/images/pullup.png)

## About me

**[MrFu Blog](http://mrfu.me/)**

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