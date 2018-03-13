package com.gongwu.wherecollect.furnitureEdit;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.JsonUtils;
import com.tencent.bugly.beta.Beta;

import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
/**
 * Function:
 * Date: 2017/8/30
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ChildView extends RelativeLayout {
    Context context;
    float x, y;
    TextView textView;
    private ObjectBean bean;
    private int resID;
    public ChildView(Context context,int resId) {
        this(context, null,resId);
    }

    public ChildView(Context context, AttributeSet attrs,int resId) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.item_geceng_child_view, null);
        textView= (TextView) view.findViewById(R.id.textView);
        this.resID=resId;
        addView(view);
        this.context = context;
    }

    /**
     * @param isEdit 是否是编辑状态
     */
    public void setEditable(boolean isEdit) {
        if(bean!=null){
            bean.setSelect(isEdit);
        }
        if (isEdit) {
            textView.setBackgroundResource(R.drawable.shape_dush_bg_white);
        } else {
            textView.setBackgroundResource(resID);
            //            setBackgroundResource(R.drawable.shape_dush_bg_white);
        }
    }

    public boolean isEdit() {
        return bean==null?false:bean.isSelect();
    }

    /**
     * 设置或刷新物品的位置或大小
     *
     * @param bean
     */
    public void setObject(ObjectBean bean, View parentView) {
        this.bean = bean;
        setX(bean.getPosition().getX() * (parentView.getWidth() / CustomTableRowLayout.MAXW));
        setY(bean.getPosition().getY() * (parentView.getHeight() / CustomTableRowLayout.MAXH));
        LayoutParams layoutParams = new LayoutParams(((int) (bean.getScale().getX() *
                parentView.getWidth() / CustomTableRowLayout.MAXW)), ((int) (bean.getScale().getY() * parentView
                .getHeight() / CustomTableRowLayout.MAXH)));
        setLayoutParams(layoutParams);
        textView.setText(bean.getName());
        setVisibility(VISIBLE);
        setEditable(bean.isSelect());
    }

    public ObjectBean getObjectBean() {
        return bean;
    }
}
