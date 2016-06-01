package mrfu.swiperefreshboth.lib.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by MrFu on 16/3/21.
 */
public class FooterView extends RelativeLayout {

    private CircularProgress circularProgress;
    private TextView textView;

    public FooterView(Context context) {
        this(context, null);
    }

    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFooterView(context);
    }

    private void initFooterView(Context context){
        int dp10 = dip2px(context, 10);
        int dp20 = dip2px(context, 20);
        RelativeLayout footer = new RelativeLayout(context);
        footer.setBackgroundColor(Color.parseColor("#fff3f3f3"));
        footer.setPadding(dp10, dp10, dp10, dp10);
        footer.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        circularProgress = new CircularProgress(context);
        circularProgress.setBackgroundColor(Color.parseColor("#00000000"));//android.R.color.transparent
        LayoutParams circular_Params = new LayoutParams(dp20, dp20);
        circular_Params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        footer.addView(circularProgress, circular_Params);

        textView = new TextView(context);
        textView.setTextColor(Color.parseColor("#FFb0b0b0"));
        textView.setTextSize(12f);
        textView.setText("-- end --");
        textView.setVisibility(View.GONE);
        LayoutParams text_Params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        text_Params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        footer.addView(textView, text_Params);

//        return footer;
        addView(footer);

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void theEnd(boolean isEnd){
        if (isEnd){
            circularProgress.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }else {
            circularProgress.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
    }
}
