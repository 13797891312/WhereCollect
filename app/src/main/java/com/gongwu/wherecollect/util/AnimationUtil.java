package com.gongwu.wherecollect.util;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
/**
 * Function:
 * Date: 2017/11/16
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class AnimationUtil {
    /**
     * 左右抖动动画
     *
     * @param view
     * @param shakeFactor
     * @return
     */
    public static ObjectAnimator StartTada(View view, float shakeFactor) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );
        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -3f * shakeFactor),
                Keyframe.ofFloat(.2f, -3f * shakeFactor),
                Keyframe.ofFloat(.3f, 3f * shakeFactor),
                Keyframe.ofFloat(.4f, -3f * shakeFactor),
                Keyframe.ofFloat(.5f, 3f * shakeFactor),
                Keyframe.ofFloat(.6f, -3f * shakeFactor),
                Keyframe.ofFloat(.7f, 3f * shakeFactor),
                Keyframe.ofFloat(.8f, -3f * shakeFactor),
                Keyframe.ofFloat(.9f, 3f * shakeFactor),
                Keyframe.ofFloat(1f, 0)
        );
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate).
                setDuration(500);
        animator.start();
        return animator;
    }

    /**
     * 向上滑动
     *
     * @param view
     * @param time 动画持续时间
     */
    public static void upSlide(View view, int time) {
        if (view.getTag() != null && ((Boolean) view.getTag())) {
            return;
        }
        ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0)
                .setDuration(time).start();
        view.setTag(true);
    }

    /**
     * 向下滑动
     *
     * @param view
     * @param time 动画持续时间
     */
    public static void downSlide(View view, int time) {
        if (view.getTag() != null && (!((Boolean) view.getTag()))) {
            return;
        }
        ObjectAnimator.ofFloat(view, "translationY", 0, view.getHeight())
                .setDuration(time).start();
        view.setTag(false);
    }
}
