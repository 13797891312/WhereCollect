package com.gongwu.wherecollect.util;
import android.app.Activity;
import android.view.View;

import com.gongwu.wherecollect.R;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
/**
 * Function:
 * Date: 2017/11/16
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ShowCaseUtil {
    /**
     * 物品点击定位
     *
     * @param context
     * @param parentView
     * @param target
     */
    public static void showCaseForObject(Activity context, View parentView, View target) {
        if (target == null) {
            return;
        }
        new MaterialShowcaseView.Builder(context)
                .setTarget(target)
                .setContentText("")
                .setContentTextColor(context.getResources().getColor(R.color.white))
                .setMaskColour(context.getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setTargetTouchable(true)
                .setParentView(parentView)
                .setDelay(0) // optional but starting animations immediately in onCreate can
                // make them choppy
                //                .singleUse("TaskCreateActivity_dirchoose") // provide a unique ID used to ensure
                // it is only shown
                // once
                .withRectangleShape(false)
                .show();
    }

    /**
     *
     * @param context
     * @param target
     * @param listener
     * @param shownListener
     * @param tag
     */
    public static void showHelp(Activity context, View target, MaterialShowcaseSequence
            .OnSequenceItemDismissedListener listener, MaterialShowcaseSequence.OnSequenceItemShownListener
            shownListener,String content,String
                                tag){
        if (target == null) {
            return;
        }
        new MaterialShowcaseView.Builder(context)
                .setTarget(target)
                .setContentText(content)
                .setContentTextColor(context.getResources().getColor(R.color.white))
                .setMaskColour(context.getResources().getColor(R.color.black_70))
                .singleUse(tag)
                .setDismissOnTouch(true)
                .setTargetTouchable(false)
                .setShapePadding(0)
                .setDelay(100) // optional but starting animations immediately in onCreate can
                .withRectangleShape(false)
                .show();
    }
}
