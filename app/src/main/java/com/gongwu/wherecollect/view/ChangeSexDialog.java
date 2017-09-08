package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.gongwu.wherecollect.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Function:
 * Date: 2017/9/7
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ChangeSexDialog {
    Context context;
    Dialog dialog;

    public ChangeSexDialog(Activity context) {
        this.context = context;
        dialog = new Dialog(context,
                R.style.Transparent2);
        dialog.setCanceledOnTouchOutside(true);
        View view = View.inflate(context,
                R.layout.layout_change_sex, null);
        ButterKnife.bind(this, view);
        dialog.setContentView(view);
        Animation ani = AnimationUtils.loadAnimation(context, R.anim.push_bottom_in);
        view.findViewById(R.id.linearLayout).startAnimation(ani);
        dialog.show();
    }

    @OnClick({R.id.male, R.id.female, R.id.cancel, R.id.linearLayout, R.id.root})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.male:
                dialog.dismiss();
                getResult("男");
                break;
            case R.id.female:
                dialog.dismiss();
                getResult("女");
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.linearLayout:
                dialog.dismiss();
                break;
            case R.id.root:
                dialog.dismiss();
                break;
        }
    }

    /**
     * 结果
     */
    public void getResult(String str) {
    }
}
