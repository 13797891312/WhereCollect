package com.gongwu.wherecollect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.util.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/10/15.
 */

public class PileAvertView extends LinearLayout {

    @Bind(R.id.pile_view)
    PileView pileView;

    private Context context = null;
    public static final int VISIBLE_COUNT = 3;//默认显示个数

    public PileAvertView(Context context) {
        this(context, null);
        this.context = context;
    }

    public PileAvertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_group_pile_avert, this);
        ButterKnife.bind(view);
    }

    public void setAvertImages(List<SharePersonBean> imageList) {
        setAvertImages(imageList, VISIBLE_COUNT);
    }

    //如果imageList>visiableCount,显示List最上面的几个
    public void setAvertImages(List<SharePersonBean> imageList, int visibleCount) {
        List<SharePersonBean> visibleList;
        if (imageList.size() > visibleCount) {
            visibleList = imageList.subList(imageList.size() - 1 - visibleCount, imageList.size() - 1);
        } else {
            visibleList = imageList;
        }
        pileView.removeAllViews();
        for (int i = 0; i < visibleList.size(); i++) {
            CircleImageView image = (CircleImageView) LayoutInflater.from(context).inflate(R.layout.item_group_round_avert, pileView, false);
            ImageLoader.load(context, image, visibleList.get(i).getAvatar());
            pileView.addView(image);
        }
    }

    public void setUserImages(List<SharePersonBean> imageList) {
        if (imageList == null) return;
        List<SharePersonBean> visibleList;
        if (imageList.size() > VISIBLE_COUNT) {
            visibleList = imageList.subList(imageList.size() - 1 - VISIBLE_COUNT, imageList.size() - 1);
        } else {
            visibleList = imageList;
        }
        pileView.removeAllViews();
        for (int i = 0; i < visibleList.size(); i++) {
            CircleImageView image = (CircleImageView) LayoutInflater.from(context).inflate(R.layout.item_group_round_share_user, pileView, false);
            ImageLoader.load(context, image, visibleList.get(i).getAvatar());
            pileView.addView(image);
        }
    }

}
