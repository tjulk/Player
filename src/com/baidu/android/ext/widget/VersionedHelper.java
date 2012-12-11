package com.baidu.android.ext.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

/**
 * �����������SDK�汾�µĺ����������⡣
 */
public abstract class VersionedHelper {

    /**TAG.*/
    static final String TAG = VersionedHelper.class.getSimpleName();
    /**DEBUG.*/
    private static final boolean DEBUG = false;
    
    /**������*/
    private static VersionedHelper sHelper;
    
    /**
     * ����SDK�汾��ȡ������
     * @return ��������
     */
    public static VersionedHelper getInstance() {
        if (sHelper == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                sHelper = new Versioned11Helper();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                sHelper = new Versioned8Helper();
            } else {
                sHelper = new VersionedHelper() {
                };
            }
        }
        
        return sHelper;
    }
    
    /**
     * ���ع��졣
     */
    private VersionedHelper() {
        
    }
    
    /**
     * view.getTranslationX()��������SDK11������getLeft()�����
     * @param v View
     * @return tranlate Xֵ��leftֵ��
     */
    public float getTranslationX(View v) {
        return v.getLeft();
    }
    
    /**
     * view.getTranslationY()��������SDK11������getTop()�����
     * @param v View
     * @return tranlate Yֵ��topֵ��
     */
    public float getTranslationY(View v) {
        return v.getTop();
    }
    
    /**
     * view.getAlpha()��������SDK11����ֱ�ӷ���1.0������ȫ��͸������Ϊû������alphaֵ����
     * @param v View
     * @return alphaֵ��1.0��
     */
    public float getAlpha(View v) {
        return 1.0f;
    }
    
    /**
     * ViewConfiguration.getScaledPagingTouchSlop��������SDK7����ֱ�ӷ���24.0��
     * @param context Context
     * @return alphaֵ��1.0��
     */
    public float getScaledPagingTouchSlop(Context context) {
        final float slop = 24.0f;
        return slop;
    }
    
    /**
     * �Ƿ���Ӳ�����٣���SDK11����ֱ�ӷ���false����Ϊû������Ӳ�����١�
     * @param v View
     * @return true or false.
     */
    public boolean isHardwareAccelerated(View v) {
        return false;
    }
    
    /**
     * layouttransition.isRunning()������
     * @param v ViewGroup
     * @return true or false.
     */
    public boolean isTransitionRunning(ViewGroup v) {
        return false;
    }
    
    /**
     * view.setTranslationX()��������SDK11������layout�����
     * @param v View
     * @param translate ����ƶ�ֵ
     */
    public void setTranslationX(View v, float translate) {
        v.layout(/*v.getLeft() + */(int) translate, v.getTop(), 
                /*v.getRight() + */(int) translate + v.getWidth(), v.getBottom());
    }
    
    /**
     * view.setTranslationY()��������SDK11������layout�����
     * @param v View
     * @param translate ����ƶ�ֵ
     */
    public void setTranslationY(View v, float translate) {
        v.layout(v.getLeft(), /*v.getTop() + */(int) translate, 
                v.getRight(), /*v.getBottom() + */(int) translate + v.getHeight());
    }
    
    /**
     * view.setAlpha()��������SDK11���²����κδ���
     * @param v View
     * @param alpha alphaֵ0.0-1.0.
     */
    public void setAlpha(View v, float alpha) {
        
    }
    
    /**
     * view.setActivated()��������SDK11���²����κδ���
     * @param v View
     * @param activated true or false.
     */
    public void setActivated(View v, boolean activated) {
        
    }
    
    /**
     * ����LayoutTranstition�Ļص�����SDK11���²����κδ���
     * @param layoutTransition LayoutTransition����. Ϊ�˼��ݣ��˴�ֻ��ʹ��Object������������
     * @param listener ������
     */
    public void setLayoutTransitionCallback(Object layoutTransition, final VersionedTransitionListener listener) {
        
    }
    
    /**
     * ����translationֵ����View��λ�ã���SDK11���²����κδ���
     * @param view View
     * @param childBounds λ������
     */
    public void invalidateGlobalRegion(View view, RectF childBounds) {
        
    }
    
    /**
     * չʾ��������SDK11����ʹ��TranslateAnimation����SDK11��������ʹ��translationֵ���ObjectAnimator����ɡ�
     * @param animView չʾ������View
     * @param duration ����ʱ��
     * @param propertyName ObjectAnimator������������SDK11���¿ɺ���
     * @param value ��SDK11���±�ʾĿ�ĵص�����ƶ�ֵ����SDK11�����ϱ�ʾObjectAnimator propertyName��Ӧ������ֵ
     * @param listener ������
     */
    public void startAnimation(final View animView, long duration,
            String propertyName, Object value,
            final VersionedAnimationListener listener) {
        float floatValue = (Float) value;
        float fromXDelta = animView.getLeft();
        animView.layout((int) floatValue, animView.getTop(), 
                (int) floatValue + animView.getWidth(), animView.getBottom());
        //<add by qumiao 2012.8.21 BEGIN
        //���û���ƶ�����ִ�ж���
        final float del = 0.1f;
        if (Math.abs(fromXDelta - floatValue) < del) {
            return;
        }
        //add by qumiao 2012.8.21 END>
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, floatValue, 0, 0);
        animation.setDuration(duration);
        animation.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
                if (listener != null) {
                    listener.onAnimationStart();
                }
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                if (listener != null) {
                    listener.onAnimationRepeat();
                }
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }
        });
        animView.startAnimation(animation);
    }
   
    /**
     * չʾ�϶�ɾ�������ʧ��������SDK11���²����κδ���
     * @param container ViewGroup
     */
    public void startDisappearAnimation(ViewGroup container) {
        
    }
    
    
    /**
     * SDK8������ʹ�á�
     * @author qumiao
     *
     */
    private static class Versioned8Helper extends VersionedHelper {
        
        @Override
        public float getScaledPagingTouchSlop(Context context) {
            return ViewConfiguration.get(context).getScaledPagingTouchSlop();
        }
        
    }
    
    
    /**
     * SDK11������ʹ�á�
     * @author qumiao
     *
     */
    private static class Versioned11Helper extends VersionedHelper {
        
        @Override
        public float getTranslationX(View v) {
            return v.getTranslationX();
        }
        
        @Override
        public float getTranslationY(View v) {
            return v.getTranslationY();
        }
        
        @Override
        public float getAlpha(View v) {
            return v.getAlpha();
        }
        
        @Override
        public boolean isHardwareAccelerated(View v) {
            return v.isHardwareAccelerated();
        }
        
        @Override
        public boolean isTransitionRunning(ViewGroup v) {
            LayoutTransition transition = v.getLayoutTransition();
            return transition != null && transition.isRunning();
        }
        
        @Override
        public void setTranslationX(View v, float translate) {
            v.setTranslationX(translate);
        }
        
        @Override
        public void setTranslationY(View v, float translate) {
            v.setTranslationY(translate);
        }
        
        @Override
        public void setAlpha(View v, float alpha) {
            v.setAlpha(alpha);
        }
        
        @Override
        public void setActivated(View v, boolean activated) {
            v.setActivated(activated);
        }
        
        @Override
        public void setLayoutTransitionCallback(Object layoutTransition,
                final VersionedTransitionListener listener) {
            if (layoutTransition instanceof LayoutTransition) {
                LayoutTransition transition = (LayoutTransition) layoutTransition;
                transition.addTransitionListener(new LayoutTransition.TransitionListener() {
                    @Override
                    public void startTransition(LayoutTransition transition,
                            ViewGroup container, View view, int transitionType) {
                        if (listener != null) {
                            listener.startTransition();
                        }
                    }

                    @Override
                    public void endTransition(LayoutTransition transition,
                            ViewGroup container, View view, int transitionType) {
                        if (listener != null) {
                            listener.endTransition();
                        }
                    }
                });
            }
        }
        
        @Override
        public void invalidateGlobalRegion(View view, RectF childBounds) {
            //childBounds.offset(view.getTranslationX(), view.getTranslationY());
            if (DEBUG) {
                Log.v(TAG, "-------------");
            }
            
            while (view.getParent() != null && view.getParent() instanceof View) {
                view = (View) view.getParent();
                view.getMatrix().mapRect(childBounds);
                view.invalidate((int) Math.floor(childBounds.left),
                                (int) Math.floor(childBounds.top),
                                (int) Math.ceil(childBounds.right),
                                (int) Math.ceil(childBounds.bottom));
                if (DEBUG) {
                    Log.v(TAG, "INVALIDATE(" + (int) Math.floor(childBounds.left)
                            + "," + (int) Math.floor(childBounds.top)
                            + "," + (int) Math.ceil(childBounds.right)
                            + "," + (int) Math.ceil(childBounds.bottom));
                }
            }
        }
        
        @Override
        public void startAnimation(final View animView, long duration,
                String propertyName, Object value, 
                final VersionedAnimationListener listener) {
            animView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ObjectAnimator anim = ObjectAnimator.ofFloat(animView, propertyName, (Float) value);
//            anim.setInterpolator(sLinearInterpolator);
            anim.setDuration(duration);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (listener != null) {
                        listener.onAnimationEnd();
                    }
                    animView.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            });
            anim.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (listener != null) {
                        listener.onAnimationUpdate();
                    }
                }
            });
            anim.start();
        }
        
        @Override
        public void startDisappearAnimation(ViewGroup container) {
            LayoutTransition transitioner = new LayoutTransition();
            container.setLayoutTransition(transitioner);
            
            final int duration = 200;
            transitioner.setDuration(duration);
            transitioner.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
            transitioner.setAnimator(LayoutTransition.DISAPPEARING, null);
        }
    }
    
  
    /**
     * ����������
     * @author qumiao
     *
     */
    public interface VersionedAnimationListener {
        /**
         * ������ʼ��
         */
        void onAnimationStart();
        
        /**
         * SDK11�����ϵ�ObjectAnimatorֵ���µĻص���
         */
        void onAnimationUpdate();
        
        /**
         * �����طš�
         */
        void onAnimationRepeat();
        
        /**
         * ����������
         */
        void onAnimationEnd();
    }
    
    /**
     * ת�������
     * @author qumiao
     *
     */
    public interface VersionedTransitionListener {
        /**
         * ��ʼת�䡣
         */
        void startTransition();
        /**
         * ����ת�䡣
         */
        void endTransition();
    }
}
