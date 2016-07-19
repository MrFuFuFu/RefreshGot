package mrfu.swiperefreshboth.lib.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

/**
 * Created by MrFu on 16/3/21.
 */
public class ViewUtils {

    public static View initFooterView(Context context){
        int dp10 = dip2px(context, 10);
        int dp20 = dip2px(context, 20);
        RelativeLayout footer = new RelativeLayout(context);
        footer.setBackgroundColor(Color.parseColor("#00000000"));
        footer.setPadding(dp10, dp10, dp10, dp10);
        footer.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        CircularProgress circularProgress = new CircularProgress(context);
        circularProgress.setBackgroundColor(Color.parseColor("#00000000"));//android.R.color.transparent
        RelativeLayout.LayoutParams footParams = new RelativeLayout.LayoutParams(dp20, dp20);
        footParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        footer.addView(circularProgress, footParams);

        return footer;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
