package com.aj2014.scalableptrview;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.os.Handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2014/6/27.
 */
public class SmoothScroller {

    private static final long NORMAL_DURATION = 200l;
    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(2, 5, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5));
    private static SmoothScroller mInstance;
    private SmoothScroller() {

    }
    public static SmoothScroller getInstance() {
        if (null == mInstance) {
            mInstance = new SmoothScroller();
        }
        return mInstance;
    }

    public void smoothScroll(Handler handler, int from, int to, IScrollAction action, OnSmoothScrollFinishedListener listener) {
        SmoothScrollRunnable scroller = new SmoothScrollRunnable(handler, from, to, NORMAL_DURATION, listener, action);
        mExecutor.execute(scroller);
    }

    public class SmoothScrollRunnable implements Runnable {

        private Handler mHandler;
        private IScrollAction mAction;
        private final Interpolator mInterpolator;
        private final int mScrollToY;
        private final int mScrollFromY;
        private final long mDuration;
        private OnSmoothScrollFinishedListener mListener;

        private boolean mContinueRunning = true;
        private long mStartTime = -1;
        private int mCurrentY = -1;

        private SmoothScrollRunnable(Handler handler, int fromY, int toY, long duration,
                                     OnSmoothScrollFinishedListener listener, IScrollAction action) {
            mHandler = handler;
            mScrollFromY = fromY;
            mScrollToY = toY;
            mInterpolator = new DecelerateInterpolator();
            mDuration = duration;
            mListener = listener;
            mAction = action;
        }

        @Override
        public void run() {

            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime / 1000f));
                mCurrentY = mScrollFromY - deltaY;
                if (null != mAction) {
                    mAction.actScroll(mCurrentY);
                }
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                mHandler.post(this);
            } else {
                if (null != mListener) {
                    mListener.onSmoothScrollFinished();
                }
                stop();
            }
        }

        public void stop() {
            mContinueRunning = false;
            mHandler.removeCallbacks(this);
        }

    }

    public static interface OnSmoothScrollFinishedListener {
        void onSmoothScrollFinished();
    }

    public static interface IScrollAction {
        void actScroll(int nextVal);
    }

}
