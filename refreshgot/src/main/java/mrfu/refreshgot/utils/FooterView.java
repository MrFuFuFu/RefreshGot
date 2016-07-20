package mrfu.refreshgot.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mrfu.refreshgot.R;


/**
 * Created by MrFu on 16/3/21.
 */
public class FooterView extends RelativeLayout {

    private CircularProgress circularProgress;
    private TextView textView;
    private RelativeLayout footer;

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
        footer = new RelativeLayout(context);
        footer.setBackgroundColor(Color.parseColor("#00000000"));
        footer.setPadding(dp10, dp10, dp10, dp10);
        footer.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        circularProgress = new CircularProgress(context);
        circularProgress.setBackgroundColor(Color.parseColor("#00000000"));
        LayoutParams circular_Params = new LayoutParams(dp20, dp20);
        circular_Params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        footer.addView(circularProgress, circular_Params);

        textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.footer_end_text_color));
        textView.setTextSize(12f);
        textView.setText("-- end --");
        textView.setVisibility(View.GONE);
        LayoutParams text_Params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        text_Params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        footer.addView(textView, text_Params);

        addView(footer);
    }

    public void setAttributeSet(AttributeSet attributeSet) {
        if (null != attributeSet) {
            TypedArray ta = getContext().obtainStyledAttributes(attributeSet, R.styleable.GotRefresh);
            progressAbout(ta);
            otherAbout(ta);
            ta.recycle();
        }
    }

    private void otherAbout(TypedArray typedArray) {
        int background_color = typedArray.getColor(R.styleable.GotRefresh_footer_background_color, getResources().getColor(R.color.footer_background_color));
        int end_text_color = typedArray.getColor(R.styleable.GotRefresh_footer_end_text_color, getResources().getColor(R.color.footer_end_text_color));
        String end_text = typedArray.getString(R.styleable.GotRefresh_footer_end_text);
        if (footer != null){
            footer.setBackgroundColor(background_color);
        }
        if (textView != null){
            textView.setTextColor(end_text_color);
            if (!TextUtils.isEmpty(end_text)){
                textView.setText(end_text);
            }
        }
    }

    private void progressAbout(TypedArray typedArray){
        int color = typedArray.getColor(R.styleable.GotRefresh_footer_loading_color, getResources().getColor(R.color.footer_loading_color));
        int size = typedArray.getInt(R.styleable.GotRefresh_footer_loading_size, CircularProgress.NORMAL_SIZE);
        int borderWidth = typedArray.getDimensionPixelSize(R.styleable.GotRefresh_footer_loading_border_width, getResources().getDimensionPixelSize(R.dimen.footer_loading_border_width));

        circularProgress.setCircularProgressAbout(color, size, borderWidth);
    }

    private static int dip2px(Context context, float dpValue) {
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
