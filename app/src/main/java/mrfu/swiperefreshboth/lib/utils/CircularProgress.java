package mrfu.swiperefreshboth.lib.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.lang.ref.WeakReference;

import mrfu.swiperefreshboth.R;

/**
 * https://github.com/castorflex/SmoothProgressBar
 * <p/>
 * Copyright 2014 Antoine Merle
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CircularProgress extends View {
    public static final int SMALL_SIZE = 0;
    public static final int NORMAL_SIZE = 1;
    public static final int LARGE_SIZE = 2;

    private int mSize;

    private IndeterminateProgressDrawable mIndeterminateProgressDrawable;

    public CircularProgress(Context context) {
        this(context, null);
    }

    public CircularProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //set a default attrs
        setCircularProgressAbout(context.getResources().getColor(R.color.footer_loading_color), NORMAL_SIZE, context.getResources().getDimensionPixelOffset(R.dimen.footer_loading_border_width));
    }

    public void setCircularProgressAbout(int color, int size, int borderwidth){
        mSize = size;
        mIndeterminateProgressDrawable = new IndeterminateProgressDrawable(color, borderwidth);
        mIndeterminateProgressDrawable.setCallback(this);
    }

    public void startAnimation() {
        if (getVisibility() != VISIBLE) {
            return;
        }
        mIndeterminateProgressDrawable.start();
    }

    public void stopAnimation() {
        mIndeterminateProgressDrawable.stop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            int size = 0;
            switch (mSize) {
                case SMALL_SIZE:
                    size = getResources().getDimensionPixelSize(R.dimen.footer_loading_small_size);
                    break;
                case NORMAL_SIZE:
                    size = getResources().getDimensionPixelSize(R.dimen.footer_loading_normal_size);
                    break;
                case LARGE_SIZE:
                    size = getResources().getDimensionPixelSize(R.dimen.footer_loading_large_size);
                    break;
            }
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        mIndeterminateProgressDrawable.draw(canvas);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            mIndeterminateProgressDrawable.start();
        } else {
            mIndeterminateProgressDrawable.stop();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == mIndeterminateProgressDrawable || super.verifyDrawable(drawable);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mIndeterminateProgressDrawable.setBounds(0, 0, width, height);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    private final AngleProperty mAngleProperty = new AngleProperty(CircularProgress.this, Float.class, "angle");

    private static class AngleProperty extends Property<IndeterminateProgressDrawable, Float> {

        private final WeakReference<CircularProgress> mWeakReference;
        public AngleProperty(CircularProgress circularProgress, Class<Float> type, String name) {
            super(type, name);
            mWeakReference = new WeakReference<>(circularProgress);
        }

        @Override
        public Float get(IndeterminateProgressDrawable object) {
            return object.getCurrentGlobalAngle();
        }

        @Override
        public void set(IndeterminateProgressDrawable object, Float value) {
            object.setCurrentGlobalAngle(value);
        }
    }

    private final SweepProperty mSweepProperty = new SweepProperty(CircularProgress.this, Float.class, "arc");

    private static class SweepProperty extends Property<IndeterminateProgressDrawable, Float> {

        private final WeakReference<CircularProgress> mWeakReference;
        public SweepProperty(CircularProgress circularProgress, Class<Float> type, String name) {
            super(type, name);
            mWeakReference = new WeakReference<>(circularProgress);
        }

        @Override
        public Float get(IndeterminateProgressDrawable object) {
            return object.getCurrentSweepAngle();
        }

        @Override
        public void set(IndeterminateProgressDrawable object, Float value) {
            object.setCurrentSweepAngle(value);
        }
    }

    private static class SweepAnimator implements Animator.AnimatorListener {

        private final WeakReference<IndeterminateProgressDrawable> mWeakReference;
        private SweepAnimator(IndeterminateProgressDrawable indeterminateProgressDrawable){
            mWeakReference = new WeakReference<>(indeterminateProgressDrawable);
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            IndeterminateProgressDrawable indeterminateProgressDrawable = mWeakReference.get();
            if (indeterminateProgressDrawable != null){
                indeterminateProgressDrawable.toggleAppearingMode();
            }
        }
    }

    /**
     * https://gist.github.com/castorflex/4e46a9dc2c3a4245a28e
     */
    private class IndeterminateProgressDrawable extends Drawable implements Animatable {
        private final SweepAnimator mSweepAnimator = new SweepAnimator(IndeterminateProgressDrawable.this);

        private final Interpolator ANGLE_INTERPOLATOR = new LinearInterpolator();
        private final Interpolator SWEEP_INTERPOLATOR = new DecelerateInterpolator();
        private static final int ANGLE_ANIMATOR_DURATION = 2000;
        private static final int SWEEP_ANIMATOR_DURATION = 600;
        private static final int MIN_SWEEP_ANGLE = 30;
        private final RectF mDrawableBounds = new RectF();

        private ObjectAnimator mObjectAnimatorSweep;
        private ObjectAnimator mObjectAnimatorAngle;
        private boolean mModeAppearing;
        private Paint mPaint;
        private float mCurrentGlobalAngleOffset;
        private float mCurrentGlobalAngle;
        private float mCurrentSweepAngle;
        private float mBorderWidth;
        private boolean mRunning;
        private boolean mIsFirst = true;

        public IndeterminateProgressDrawable(int color, float borderWidth) {
            mBorderWidth = borderWidth;

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(borderWidth);
            mPaint.setColor(color);

            setupAnimations();
        }

        public void setColor(int color){
            if (mPaint != null){
                mPaint.setColor(color);
            }
        }

        @Override
        public void draw(Canvas canvas) {
            float startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset;
            float sweepAngle = mCurrentSweepAngle;
            if (!mModeAppearing) {
                startAngle = startAngle + sweepAngle;
                sweepAngle = 360 - sweepAngle - MIN_SWEEP_ANGLE;
            } else {
                sweepAngle += MIN_SWEEP_ANGLE;
            }
            canvas.drawArc(mDrawableBounds, startAngle, sweepAngle, false, mPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

        private void toggleAppearingMode() {
            mModeAppearing = !mModeAppearing;
            if (mModeAppearing) {
                mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360;
            }
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mDrawableBounds.left = bounds.left + mBorderWidth / 2f + .5f;
            mDrawableBounds.right = bounds.right - mBorderWidth / 2f - .5f;
            mDrawableBounds.top = bounds.top + mBorderWidth / 2f + .5f;
            mDrawableBounds.bottom = bounds.bottom - mBorderWidth / 2f - .5f;
        }

        private void setupAnimations() {
            mObjectAnimatorAngle = ObjectAnimator.ofFloat(this, mAngleProperty, 360f);
            mObjectAnimatorAngle.setInterpolator(ANGLE_INTERPOLATOR);
            mObjectAnimatorAngle.setDuration(ANGLE_ANIMATOR_DURATION);
            mObjectAnimatorAngle.setRepeatMode(ValueAnimator.RESTART);
            mObjectAnimatorAngle.setRepeatCount(ValueAnimator.INFINITE);

            mObjectAnimatorSweep = ObjectAnimator.ofFloat(this, mSweepProperty, 360f - MIN_SWEEP_ANGLE * 2);
            mObjectAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
            mObjectAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);
            mObjectAnimatorSweep.setRepeatMode(ValueAnimator.RESTART);
            mObjectAnimatorSweep.setRepeatCount(ValueAnimator.INFINITE);
            mObjectAnimatorSweep.addListener(mSweepAnimator);
        }

        @Override
        public void start() {
            if (isRunning()) {
                return;
            }
            if (mIsFirst){
                startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_visibilty));
                mIsFirst = false;
            }
            mRunning = true;
            mObjectAnimatorAngle.start();
            mObjectAnimatorSweep.start();
            invalidateSelf();
        }

        @Override
        public void stop() {
            if (!isRunning()) {
                return;
            }
            mRunning = false;
            mObjectAnimatorAngle.cancel();
            mObjectAnimatorSweep.cancel();
            invalidateSelf();
        }

        @Override
        public boolean isRunning() {
            return mRunning;
        }

        public void setCurrentGlobalAngle(float currentGlobalAngle) {
            mCurrentGlobalAngle = currentGlobalAngle;
            invalidateSelf();
        }

        public float getCurrentGlobalAngle() {
            return mCurrentGlobalAngle;
        }

        public void setCurrentSweepAngle(float currentSweepAngle) {
            mCurrentSweepAngle = currentSweepAngle;
            invalidateSelf();
        }

        public float getCurrentSweepAngle() {
            return mCurrentSweepAngle;
        }

    }
}
