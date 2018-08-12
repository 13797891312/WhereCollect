package com.gongwu.wherecollect.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.MainActivity;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Function:
 * Date: 2017/9/13
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ObjectInfoLookView extends LinearLayout {
    ObjectBean bean;
    @Bind(R.id.jiage_edit)
    EditText jiageEdit;
    @Bind(R.id.yanse_flow)
    FlowViewGroup yanseFlow;
    @Bind(R.id.jijie_flow)
    FlowViewGroup jijieFlow;
    @Bind(R.id.qudao_flow)
    FlowViewGroup qudaoFlow;
    @Bind(R.id.fenlei_flow)
    FlowViewGroup fenleiFlow;
    @Bind(R.id.rating_star)
    RatingBar ratingStar;
    @Bind(R.id.location_btn)
    ImageView locationBtn;
    @Bind(R.id.star_layout)
    View starLayout;
    @Bind(R.id.fenlei_layout)
    View fenleiLayout;
    @Bind(R.id.location_layout)
    View locationLayout;
    @Bind(R.id.jiage_layout)
    View jiageLayout;
    @Bind(R.id.yanse_layout)
    View yanseLayout;
    @Bind(R.id.jijie_layout)
    View jijieLayout;
    @Bind(R.id.qita_tv)
    TextView qitaTv;
    @Bind(R.id.qita_layout)
    View qitaLayout;
    @Bind(R.id.qudao_layout)
    View qudaoLayout;
    @Bind(R.id.location_flow)
    FlowViewGroup locationFlow;
    @Bind(R.id.add_goods_count_layout)
    View addGoodsCountLaytou;
    @Bind(R.id.goods_count_edit)
    EditText goodsCountEdit;
    @Bind(R.id.purchase_time_layout)
    View purchaseTimeLayout;
    @Bind(R.id.purchase_time_tv)
    TextView purchaseTimeTv;
    @Bind(R.id.expiry_time_layout)
    View expiryTimeLayout;
    @Bind(R.id.expiry_time_tv)
    TextView expiryTimeTv;

    public ObjectInfoLookView(Context context) {
        this(context, null);
    }

    public ObjectInfoLookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_goodsinfo_look, this);
        ButterKnife.bind(this);
    }

    public void init(ObjectBean bean) {
        this.bean = bean;
        updataView();
    }

    /**
     * 刷新界面
     */
    public void updataView() {
        setFenlei();//设置分类
        setGoodsCount();//设置数量
        setPurchaseTime();//设置购买时间
        setExpirytime();//设置到期时间
        setColors();//设置颜色
        setJijie();//设置季节
        setQudao();//设置购货渠道
        setStar();//设置星级
        setQita();//其他
        setjiage();//其他
    }

    private void setPurchaseTime() {
        if (!TextUtils.isEmpty(bean.getBuy_date())) {
            purchaseTimeLayout.setVisibility(VISIBLE);
            purchaseTimeTv.setText(bean.getBuy_date());
            showView();
        } else {
            purchaseTimeTv.setText("");
            purchaseTimeLayout.setVisibility(GONE);
        }
    }

    private void setExpirytime() {
        if (!TextUtils.isEmpty(bean.getExpire_date())) {
            expiryTimeLayout.setVisibility(VISIBLE);
            expiryTimeTv.setText(bean.getExpire_date());
            showView();
        } else {
            expiryTimeTv.setText("");
            expiryTimeLayout.setVisibility(GONE);
        }
    }

    /**
     * 设置物品数量
     */
    private void setGoodsCount() {
        if (bean.getCount() > 0) {
            goodsCountEdit.setText(bean.getCount() + "");
            addGoodsCountLaytou.setVisibility(View.VISIBLE);
            showView();
        } else {
            addGoodsCountLaytou.setVisibility(View.GONE);
        }
    }

    /**
     * 设置价格
     */
    private void setjiage() {
        if (!TextUtils.isEmpty(bean.getPrice()) && !bean.getPrice().equals("0")) {
            jiageEdit.setText(bean.getPrice() + "元");
            jiageLayout.setVisibility(View.VISIBLE);
            showView();
        } else {
            jiageEdit.setText("");
            jiageLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 其他
     */
    private void setQita() {
        if (!TextUtils.isEmpty(bean.getDetail())) {
            qitaTv.setText(bean.getDetail());
            qitaLayout.setVisibility(View.VISIBLE);
            showView();
        } else {
            qitaLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置星级
     */
    private void setStar() {
        if (bean.getStar() > 0) {
            starLayout.setVisibility(View.VISIBLE);
            ratingStar.setRating(bean.getStar());
            showView();
        } else {
            ratingStar.setRating(0);
            starLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置分类
     */
    private void setFenlei() {
        fenleiFlow.removeAllViews();
        if (bean.getCategories() != null && bean.getCategories().size() > 0) {
            fenleiLayout.setVisibility(View.VISIBLE);
        } else {
            fenleiLayout.setVisibility(View.GONE);
            return;
        }
        Collections.sort(bean.getCategories(), new Comparator<BaseBean>() {
            @Override
            public int compare(BaseBean lhs, BaseBean rhs) {
                return lhs.getLevel() - rhs.getLevel();
            }
        });
        for (int i = 0; i < StringUtils.getListSize(bean.getCategories()); i++) {
            TextView text = (TextView) View.inflate(getContext(), R.layout.flow_textview, null);
            fenleiFlow.addView(text);
            MarginLayoutParams lp = (MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 5;
            lp.topMargin = 5;
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            text.setLayoutParams(lp);
            text.setText(bean.getCategories().get(i).getName());
            text.setBackgroundResource(R.drawable.shape_maingoods2_bg);
        }
        showView();
    }


    /**
     * 设置颜色
     */
    private void setColors() {
        yanseFlow.removeAllViews();
        if (TextUtils.isEmpty(bean.getColor())) {
            yanseLayout.setVisibility(View.GONE);
            return;
        } else {
            yanseLayout.setVisibility(View.VISIBLE);
        }
        String[] colors = bean.getColor().split("、");
        for (int i = 0; i < colors.length; i++) {
            TextView text = (TextView) View.inflate(getContext(), R.layout.flow_textview, null);
            yanseFlow.addView(text);
            MarginLayoutParams lp = (MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 5;
            lp.topMargin = 5;
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            text.setLayoutParams(lp);
            text.setText(colors[i]);
            text.setBackgroundResource(R.drawable.shape_maingoods2_bg);
        }
        showView();
    }

    /**
     * 设置季节
     */
    private void setJijie() {
        jijieFlow.removeAllViews();
        if (TextUtils.isEmpty(bean.getSeason())) {
            jijieLayout.setVisibility(View.GONE);
            return;
        } else {
            jijieLayout.setVisibility(View.VISIBLE);
        }
        String[] seasons = bean.getSeason().split("、");
        for (int i = 0; i < seasons.length; i++) {
            TextView text = (TextView) View.inflate(getContext(), R.layout.flow_textview, null);
            jijieFlow.addView(text);
            MarginLayoutParams lp = (MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 5;
            lp.topMargin = 5;
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            text.setLayoutParams(lp);
            text.setText(seasons[i]);
            text.setBackgroundResource(R.drawable.shape_maingoods2_bg);
        }
        showView();
    }

    /**
     * 设置购买渠道
     */
    private void setQudao() {
        qudaoFlow.removeAllViews();
        if (TextUtils.isEmpty(bean.getChannel())) {
            qudaoLayout.setVisibility(View.GONE);
            return;
        } else {
            qudaoLayout.setVisibility(View.VISIBLE);
        }
        String[] channel = bean.getChannel().split(">");
        for (int i = 0; i < channel.length; i++) {
            TextView text = (TextView) View.inflate(getContext(), R.layout.flow_textview, null);
            qudaoFlow.addView(text);
            MarginLayoutParams lp = (MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 5;
            lp.topMargin = 5;
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            text.setLayoutParams(lp);
            text.setText(channel[i]);
            text.setBackgroundResource(R.drawable.shape_maingoods2_bg);
        }
        showView();
    }


    private boolean locationIsShow = false;

    /**
     * 设置物品位置布局的显示或隐藏
     * true 隐藏  false显示
     */
    public void setLocationlayoutVisibility(boolean isShow) {
        this.locationIsShow = isShow;
        locationLayout.setVisibility(isShow ? GONE : VISIBLE);
    }

    @OnClick(R.id.location_btn)
    public void onClick() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("object", bean);
        getContext().startActivity(intent);
        ((Activity) getContext()).finish();
    }

    private void showView() {
        if (this.getVisibility() == View.GONE) {
            this.setVisibility(View.VISIBLE);
        }
    }

    public void showGoodsLayout() {
        fenleiLayout.setVisibility(View.VISIBLE);
//        addGoodsCountLaytou.setVisibility(View.VISIBLE);
        purchaseTimeLayout.setVisibility(VISIBLE);
        expiryTimeLayout.setVisibility(VISIBLE);
        yanseLayout.setVisibility(View.VISIBLE);
        jijieLayout.setVisibility(View.VISIBLE);
        starLayout.setVisibility(View.VISIBLE);
        jiageLayout.setVisibility(View.VISIBLE);
    }
}
