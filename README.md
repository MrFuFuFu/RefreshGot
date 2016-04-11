SwipeRefreshBoth
=====================


## 描述

下拉刷新

包含: ListView 和 RecyclerView 的形式!!



## How to use?


需要使用下拉刷新的xml 这样写：

```xml
<mrfu.swiperefreshboth.lib.LxRefresh
    android:id="@+id/lx_refresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <mrfu.swiperefreshboth.lib.LxListView
        android:id="@+id/lx_listview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</mrfu.swiperefreshboth.lib.LxRefresh>
```

Java 代码中如下写法，需要增加 `PullRefreshListener` 接口

```Java
LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
mLxRecyclerView.setHasFixedSize(true);
mLxRecyclerView.setLayoutManager(linearLayoutManager);

setData();
mRefreshAdapter = new RefreshAdapter(mList, this);
mLxRecyclerView.setAdapter(mRefreshAdapter);
mLxRefresh.setOnPullRefreshListener(this);
```

如果加载完成则调用如下方法:

```java
mLxRefresh.refreshReset();
```

对于 `RecyclerView` 如果加载到了最后一页,不再有上拉加载,则再多调用一次如下方法:

```java
mLxRefresh.setLoadMoreEnable(false);//false 关闭加载更多. true 可以加载更多
```

如果只需要下拉刷新,不需要上拉加载,则调用如下方法关闭上拉加载:

```java
mLxRefresh.setNoLoadMore();
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