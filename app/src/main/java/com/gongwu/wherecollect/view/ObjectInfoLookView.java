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
        setLocation();//设置位置
        setColors();//设置颜色
        setJijie();//设置季节
        setQudao();//设置购货渠道
        setStar();//设置星级
        setQita();//其他
        setjjiage();//其他
    }

    private void setPurchaseTime() {
        if (!TextUtils.isEmpty(bean.getCreated_at())) {
            purchaseTimeLayout.setVisibility(VISIBLE);
            purchaseTimeTv.setText(bean.getCreated_at());
            showView();
        } else {
            purchaseTimeLayout.setVisibility(GONE);
        }
    }

    private void setExpirytime() {
        if (!TextUtils.isEmpty(bean.getDeleted_at())) {
            expiryTimeLayout.setVisibility(VISIBLE);
            expiryTimeTv.setText(bean.getDeleted_at());
            showView();
        } else {
            expiryTimeLayout.setVisibility(GONE);
        }
    }

    /**
     * 设置物品数量
     */
    private void setGoodsCount() {
        if (bean.getObject_count() > 0) {
            goodsCountEdit.setText(bean.getObject_count() + "");
            addGoodsCountLaytou.setVisibility(View.VISIBLE);
            showView();
        } else {
            addGoodsCountLaytou.setVisibility(View.GONE);
        }
    }

    /**
     * 设置价格
     */
    private void setjjiage() {
        if (!TextUtils.isEmpty(bean.getPrice())) {
            jiageEdit.setText(bean.getPrice());
            jiageLayout.setVisibility(View.VISIBLE);
            showView();
        } else {
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
        if (bean.getStar() == 0) {
            starLayout.setVisibility(View.GONE);
        } else {
            starLayout.setVisibility(View.VISIBLE);
            ratingStar.setRating(bean.getStar());
            showView();
        }
    }

    /**
     * 设置分类
     */
    private void setFenlei() {
        if (bean.getCategories() != null && bean.getCategories().size() > 0) {
            fenleiLayout.setVisibility(View.VISIBLE);
        } else {
            fenleiLayout.setVisibility(View.GONE);
            return;
        }
        fenleiFlow.removeAllViews();
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
     * 设置位置
     */
    private void setLocation() {
        if (bean.getLocations() == null || bean.getLocations().size() == 0) {
            locationLayout.setVisibility(View.GONE);
            return;
        } else {
            locationLayout.setVisibility(View.VISIBLE);
        }
        locationFlow.removeAllViews();
        Collections.sort(bean.getLocations(), new Comparator<BaseBean>() {
            @Override
            public int compare(BaseBean lhs, BaseBean rhs) {
                return lhs.getLevel() - rhs.getLevel();
            }
        });
        for (int i = 0; i < StringUtils.getListSize(bean.getLocations()); i++) {
            TextView text = (TextView) View.inflate(getContext(), R.layout.flow_textview, null);
            locationFlow.addView(text);
            MarginLayoutParams lp = (MarginLayoutParams) text.getLayoutParams();
            lp.bottomMargin = 5;
            lp.topMargin = 5;
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            text.setLayoutParams(lp);
            text.setText(bean.getLocations().get(i).getName());
            text.setBackgroundResource(R.drawable.shape_maingoods2_bg);
        }
        if (StringUtils.isEmpty(bean.getLocations())) {
            locationBtn.setVisibility(GONE);
        } else {
            locationBtn.setVisibility(VISIBLE);
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
     * 设置季节
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
}
