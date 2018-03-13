package com.gongwu.wherecollect.view;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.zhaojin.myviews.HackyViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Function:
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ObjectView extends LinearLayout implements View.OnLongClickListener {
    public final int MINSIZE = 150;
    Context context;
    @Bind(R.id.goods_iv)
    ImageView goodsIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    float x, y;
    boolean isEdit = false;
    @Bind(R.id.zoom_iv_4)
    ImageView zoomIv4;
    @Bind(R.id.edit_layout)
    RelativeLayout editLayout;
    private ObjectBean bean;

    public ObjectView(Context context) {
        this(context, null);
    }

    public ObjectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = View.inflate(getContext(), R.layout.item_goods_view, this);
        ButterKnife.bind(this, view);
        initZoom();
        setEditable(isEdit);
    }

    /**
     * @param isEdit 是否是编辑状态
     */
    public void setEditable(boolean isEdit) {
        this.isEdit = isEdit;
        if (isEdit) {
            editLayout.setVisibility(VISIBLE);
            goodsIv.setBackgroundColor(Color.TRANSPARENT);
        } else {
            editLayout.setVisibility(GONE);
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    /**
     * 设置或刷新物品的位置或大小
     *
     * @param bean
     */
    public void setObject(ObjectBean bean) {
        this.bean = bean;
        setX(bean.getX(context));
        setY(bean.getY(context));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(((int) bean.getWidth(context)), (
                (int) bean.getHeight(context)));
        setLayoutParams(layoutParams);
        if (!TextUtils.isEmpty(bean.getImage_url())) {
            ImageLoader.load(context, goodsIv, bean.getImage_url(), R.drawable.icon_placeholder);
        }
        nameTv.setText(bean.getName());
        setVisibility(VISIBLE);
    }

    public ObjectBean getObjectBean() {
        return bean;
    }

    private void initZoom() {
        OnTouchListener listener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.LayoutParams lp = getLayoutParams();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    HackyViewPager.isItercept = true;
                    x = event.getX();
                    y = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    HackyViewPager.isItercept = true;
                    float tempX;
                    float tempY;
                    tempX = lp.width + (event.getX() - x);
                    tempY = lp.height + (event.getY() - y);
                    if (tempX < MINSIZE)
                        tempX = MINSIZE;
                    if (tempY < MINSIZE + 50)
                        tempY = MINSIZE + 50;
                    lp.width = (int) tempX;
                    lp.height = (int) tempY;
                    x = event.getX();
                    x = event.getY();
                }
                if (lp.height > ((ViewGroup) getParent()).getHeight())
                    lp.height = ((ViewGroup) getParent()).getHeight();
                if (lp.width > ((ViewGroup) getParent()).getWidth())
                    lp.width = ((ViewGroup) getParent()).getWidth();
                setLayoutParams(lp);
                bean.setHeight(context, lp.height);
                bean.setWidth(context, lp.width);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    HackyViewPager.isItercept = false;
                    postWidthAndHeight();
                }
                return true;
            }
        };
        setOnLongClickListener(this);
        zoomIv4.setOnTouchListener(listener);
    }

    /**
     * 调整大小调用接口
     */
    private void postWidthAndHeight() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("location_code", getObjectBean().getCode());
        map.put("scale", JsonUtils.jsonFromObject(getObjectBean().getScale()));
        map.put("position", JsonUtils.jsonFromObject(getObjectBean().getPosition()));
        PostListenner listenner = new PostListenner(context) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                /********此段代码是因为位置查看和位置编辑页的家具对象不是同一个了，所以需要手动赋值********/
                String code=MainLocationFragment.mlist.get(((LocationEditActivity) context).viewPager
                                .getCurrentItem()).getCode();
                List<ObjectBean> list =MainLocationFragment.locationMap.get(code);
                if(!list.contains(bean)){
                    for (int i = 0; i < StringUtils.getListSize(list); i++) {
                        if(list.get(i).getCode().equals(bean.getCode())){
                            list.get(i).setScale(bean.getScale());
                        }
                    }
                }
                /***********************/
                EventBusMsg.EditLocationMsg msg=new EventBusMsg.EditLocationMsg(((LocationEditActivity) context).viewPager
                        .getCurrentItem());
                msg.onlyNotifyUi=true;
                EventBus.getDefault().post(msg);
                if (!TextUtils.isEmpty(bean.getImage_url())) {
                    ImageLoader.load(context, goodsIv, bean.getImage_url(), R.drawable.icon_placeholder);
                }
            }
        };
        HttpClient.movePosition(context, map, listenner);
    }

    @Override
    public boolean onLongClick(View view) {
        if (context instanceof LocationEditActivity) {
            this.setTag(bean);
            startDrag(null, new DragShadowBuilder(this), this, 0);
        }
        return true;
    }
}
