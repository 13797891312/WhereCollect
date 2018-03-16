package com.gongwu.wherecollect.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gongwu.wherecollect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Date: 2016/9/15
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class GoodsImageView extends FrameLayout {
    @Bind(R.id.head)
    public ImageView head;
    @Bind(R.id.name)
    public TextView name;
    Context context;
    private GlideListener listener;
    private final int substringlength = 4;

    public GoodsImageView(Context context) {
        this(context, null);
    }

    public GoodsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View v = View.inflate(context, R.layout.layout_goods_image_view, null);
        this.addView(v);
        ButterKnife.bind(this, v);
    }

    public void setTextSize(int size) {
        name.setTextSize(size);
    }

    public void setImageResource(int id) {
        name.setVisibility(GONE);
        head.setImageResource(id);
        Glide.with(context)
                .load(id)
                .asBitmap()
                .into(new BitmapImageViewTarget(head) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(),
                                        resource);
                        circularBitmapDrawable.setCircular(true);
                        head.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }


    /**
     * 设置用户
     *
     * @param userId
     * @param nickName
     * @param headUrl
     */
    public void setHead(final String userId, String nickName, String headUrl) {
        name.setVisibility(VISIBLE);
        nickName = TextUtils.isEmpty(nickName) ? userId : nickName;
        name.setText(getEndNick(nickName));
        name.setTag(userId);
        head.setImageResource(getResId(userId));
        Glide.with(context)
                .load(headUrl)
                .asBitmap()
                .placeholder(getResId(userId))
                .dontAnimate()
                .diskCacheStrategy
                        (DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(new BitmapImageViewTarget(head) {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        try {
                            if (name.getTag().toString().equals(userId)) {
                                head.setImageResource(getResId(userId));
                                name.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (listener != null) {
                            listener.onError();
                        }
                    }
                });
    }

    private int getResId(String userId) {
        int i = 0;
        try {
            i = Integer.parseInt(userId.substring(userId.length() - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (i) {
            case 0:
                return R.color.blue;
            case 1:
                return R.color.red;
            case 2:
                return R.color.red_light;
            case 3:
                return R.color.maincolor;
            case 4:
                return R.color.possible_result_points;
            case 5:
                return R.color.holo_purple;
            case 6:
                return R.color.pieChart1;
            case 7:
                return R.color.pieChart12;
            case 8:
                return R.color.pieChart5;
            case 9:
                return R.color.pieChart11;
            default:
                return R.color.pieChart9;
        }
    }

    /**
     * 获取昵称最后两位
     *
     * @param nick
     * @return
     */
    private String getEndNick(String nick) {
        if (TextUtils.isEmpty(nick)) {
            return "";
        }
        if (nick.length() >= 5) {
            return nick.substring(0, substringlength);
        } else {
            return nick;
        }
    }

    public void setGlideListener(GlideListener listener) {
        this.listener = listener;
    }


    public static interface GlideListener {
        public void onSucces(Bitmap resource);

        public void onError();
    }
}
