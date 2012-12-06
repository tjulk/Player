package com.baidu.player.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

import com.baidu.player.R;

/**
 * 
 * ��ʾ��ٽ��ȵĽ����� ��
 * �������ع��̷�Ϊ���Σ���һ�׶ο��������ȵ�ĳһ��ֵ���ڶ��׶�������䣬ֱ���ﵽ�趨����ٽ������ֵ�����ڼ������ϣ�����ٽ��������������Ȼ���������ʧ��
 * 
 * �����������������ý���ֵ��Χ��Ϊ�˶��������ȣ��������ֵ���̶�Ϊ10000
 * @author chengyifan
 * @since 2012-10-28
 */
public class FakeProgressBar extends ProgressBar {
    /** Ĭ�ϵ�һ�׶μ����ٶ� */
    public static final int FAST_VELOCITY = 40;
    /** Ĭ�ϵڶ��׶μ����ٶ� */
    public static final int SLOW_VELOCITY = 3;
    /** Ĭ�ϵ����׶ζ�������ʱ�� */
    public static final int HIDE_DURATION = 120;
    /** Ĭ�ϵ�һ�׶�������ֵ */ 
    public static final int FIRST_SECTION_MAX = 10;
    /** Ĭ�ϵڶ��׶�������ֵ */
    public static final int FAKE_PROGRESS_MAX = 90;
    /** ������ֵ */
    private static final int MAX_PROGRESS = 10000;
    /** ˢ�¼�� */
    private static final int REFRESH_DELAY = 30;
    /** ��������{@link #mLastUpdateTime},��ʶδ�������� */
    private static final long UNINITIALED = -1;
    /** ���õ���ʵ���� */
    private int mRealProgress;
    /** ������ʾ�Ľ���ֵ����ٵĽ���*/
    private int mFakeProgress;
    /** �����׶ζ�������ʱ�� */
    private int mHideDuration = HIDE_DURATION;
    /** �ڶ��׶�������ֵ */
    private int mFakeProgressMax = resolveProgress(FAKE_PROGRESS_MAX);
    /** ��һ�׶�������ֵ */
    private int mFirstSectionMax = resolveProgress(FIRST_SECTION_MAX);
    /** �ڶ��׶μ����ٶ� */
    private float mSlowVelocity = resolveVelocity(SLOW_VELOCITY);
    /** ��һ�׶μ����ٶ� */
    private float mFastVelocity = resolveVelocity(FAST_VELOCITY);
    /** ��һ��ˢ��ʱ�� */
    private long mLastUpdateTime = UNINITIALED;
    /** ���ڼ�������׶εĽ���ֵ */
    private Transformation mTrans = new Transformation();
    /** �����׶ζ���Interpolator */
    private Interpolator mInterpolator = new AccelerateInterpolator(2.0f);
    /** �����׶ζ��� */
    private AnimationSet mAnimation;
    
    /**
     * ������ͷ��ͼ��
     */
    private Drawable mThumb;
    /** thumb offset */
    private int mThumbOffset;
//    private Random mRandom = new Random();
    
    /**
     * ����ֵ��Χ�̶���0-10000,��ʼֵΪ0.
     * @param context context
     */
    public FakeProgressBar(Context context) {
        super(context);
        init();
    }
    
    /**
     * ����ֵ��Χ�̶���0-10000,��ʼֵΪ0.
     * @param context context
     * @param attrs attrs
     */
    public FakeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    /**
     * ����ֵ��Χ�̶���0-10000,��ʼֵΪ0.
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public FakeProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    /**
     * ����ֵ��Χ���̶���0-10000
     */
    private void init() {
        super.setMax(MAX_PROGRESS);
        mThumb = getResources().getDrawable(R.drawable.progress_thumb);
        mThumbOffset = getResources().getDimensionPixelSize(R.dimen.browser_progressbar_offset);
    }
    
    /**
     * 
     * �˷���������
     * ���������ý���������ֵ��Χ��Ϊ�˶��������ȣ��������ֵ���̶�Ϊ10000
     * @param max max
     */
    @Override
    public synchronized void setMax(int max) {
        //do nothing
    }
    
    /**
     * ������ϣ���ʾ������䶯����ʹ��������ʧ��
     */
    public void hide() {
        if (getVisibility() == View.VISIBLE) {
            float start = getProgress() / (float) getMax();
            // mAnimation = new AlphaAnimation(start, 1.0f);
            // mAnimation.setDuration(mHideDuration);
            // mAnimation.setInterpolator(mInterpolator);
            mAnimation = new AnimationSet(false);
            AlphaAnimation a = new AlphaAnimation(start, 1.0f);
            a.setDuration(mHideDuration);
            a.setInterpolator(mInterpolator);
            AlphaAnimation b = new AlphaAnimation(1.0f, 1.0f);
            b.setDuration(mHideDuration);
            b.setStartOffset(mHideDuration);
            mAnimation.addAnimation(a);
            mAnimation.addAnimation(b);

            mTrans.clear();
            mAnimation.start();
            invalidate();
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mThumb != null) {
            canvas.save();
            int progress = getProgress();
            float percent = progress / (float) getMax();
            int x = ((int) ((getWidth() - getPaddingLeft() - getPaddingRight()) * percent))
                    + mThumbOffset;
            canvas.translate(-mThumb.getIntrinsicWidth(), 0);
            mThumb.setBounds(x, 0, x + mThumb.getIntrinsicWidth(),
                    mThumb.getIntrinsicHeight());
            mThumb.draw(canvas);
            canvas.restore();
        }
        if (mAnimation != null) {
            if (mAnimation.getTransformation(System.currentTimeMillis(), mTrans)) {
                setProgressInAnimation((int) (mTrans.getAlpha() * getMax()));
                invalidate();
            } else {
                mAnimation = null;
                reset();
            }
            return;
        }

        if (mLastUpdateTime != UNINITIALED && mFakeProgress < mFakeProgressMax) {
            long current = System.currentTimeMillis();
            int increasment = 0;
            int resovledProgress = mRealProgress > mFirstSectionMax ? mRealProgress : mFirstSectionMax;
            if (resovledProgress > mFakeProgress) {
                increasment = (int) (mFastVelocity * 2 * (current - mLastUpdateTime));
                mFakeProgress += increasment;
            } else {
                increasment = (int) (mSlowVelocity * (current - mLastUpdateTime));
                mFakeProgress += increasment;
            }

            if (increasment != 0) {
                mLastUpdateTime = current;
                setProgressInAnimation(mFakeProgress);
            }
            postInvalidateDelayed(REFRESH_DELAY);
        }
    }
    
    /**
     * ���ý�������ʾ��ֵ�����ڻ��ƽ������Ľ��ȡ�
     * @param progress ����ֵ��0-10000
     */
    private void setProgressInAnimation(int progress) {
        super.setProgress(progress);
    }

    @Override
    public synchronized void setProgress(int progress) {
        int resolved = resolveProgress(progress);
        mRealProgress = resolved;
        if (mFakeProgress < resolved) {
            mFakeProgress = resolved;
        }
        postInvalidate();
    }
    
    /**
     * �����ʵ����
     * @return ��ʵ����
     */
    public int getRealProgress() {
        return mRealProgress / 100; // SUPPRESS CHECKSTYLE
    }
    
    /**
     * ��ʼ���ؽ���������ʾ�ٽ��ȡ�
     */
    public void start() {
        mFakeProgress = 0;
        mRealProgress = 0;
        mLastUpdateTime = System.currentTimeMillis();
        mAnimation = null;
        setProgressInAnimation(mFakeProgress);
        setVisibility(View.VISIBLE);
        invalidate();
    }
    
    /**
     * ���ý�����״̬
     */
    public void reset() {
        mFakeProgress = 0;
        mRealProgress = 0;
        mLastUpdateTime = System.currentTimeMillis();
        mAnimation = null;
        setProgressInAnimation(0);
        setVisibility(View.INVISIBLE);
        invalidate();
    }
    
    /**
     * ���üٽ��ȵ����ֵ��
     * 
     * ��ʵ�ʽ���һֱδ��ɣ������������ʾ�ٽ��ȵ����ֵ�󣬱��ֹͣ������
     * @param progress ����ֵ��Ӧ��0-100֮��
     */
    public void setFakeProgressMax(int progress) {
        mFakeProgressMax = resolveProgress(progress);
    }
    
    /**
     * ���õ�һ�׶ε�������ֵ��
     * �ڴ�������ֵ��ǰ������������{@link FakeProgressBar#FAST_VELOCITY}���ٶȿ��ټ��ء�
     * ���ӵ��˽��Ⱥ󣬼��ؽ���ڶ��׶�
     * @param progress ����ֵ��Ӧ��0-100֮��
     */
    public void setFirstSectionMax(int progress) {
        mFirstSectionMax = resolveProgress(progress);
    }
    
    /**
     * ���õڶ��׶εĽ����������ٶȡ�
     * @param velocity �ٶȣ���λΪ ����/��
     */
    public void setSlowVelocity(int velocity) {
        mSlowVelocity = resolveVelocity(velocity);
    }
    
    /**
     * ���õ�һ�׶εĽ����������ٶȡ�
     * @param velocity �ٶȣ���λΪ ����/��
     */
    public void setFastVelocity(int velocity) {
        mFastVelocity = resolveVelocity(velocity);
    }
    
    /**
     * ���õ����׶εĶ�������ʱ��
     * @param duration ��������ʱ��
     */
    public void setHideAnimationDuration(int duration) {
        mHideDuration = duration;
    }
   
    /**
     * �����õ��ٶȶ�ֵ����ת��
     * @param velocityInProgress �ٶȣ���λΪ ����/�룬���н������ֵΪ100
     * @return ת������ٶȣ���λΪ ����/���룬�������ֵΪ10000
     */
    private static float resolveVelocity(int velocityInProgress) {
        return velocityInProgress * 100 / (float) 1000; // SUPPRESS CHECKSTYLE
    }

    /**
     * �����õĽ���ֵ����ת��
     * @param progress ���ȣ����ֵΪ100
     * @return ת����Ľ��ȣ����ֵΪ10000
     */
    private static int resolveProgress(int progress) {
        return progress * 100; // SUPPRESS CHECKSTYLE
    }
}
