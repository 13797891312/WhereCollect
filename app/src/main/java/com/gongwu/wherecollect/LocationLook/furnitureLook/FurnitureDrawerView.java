package com.gongwu.wherecollect.LocationLook.furnitureLook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.furnitureEdit.ChildView;
import com.gongwu.wherecollect.furnitureEdit.CustomTableRowLayout;
import com.gongwu.wherecollect.furnitureEdit.EditTemplateActivity;
import com.gongwu.wherecollect.view.drawerLayout.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Function:
 * Date: 2017/8/28
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class FurnitureDrawerView extends LinearLayout {
    Context context;
    @Bind(R.id.close_tv)
    TextView closeTv;
    DrawerLayout drawerLayout;
    @Bind(R.id.tablelayout)
    public CustomTableRowLayout tablelayout;
    List<ObjectBean> mlist = new ArrayList<>();
    ChildView selectView;
    float shape = CustomTableRowLayout.shape_width;
    private ObjectBean bean;

    public FurnitureDrawerView(Context context) {
        this(context, null);
    }

    public FurnitureDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setBean(ObjectBean bean) {
        this.bean = bean;
    }

    public ChildView getChildView() {
        return selectView;
    }

    /**
     * @param drawerLayout
     * @param list
     * @param shape
     */
    public void init(DrawerLayout drawerLayout, List<ObjectBean> list, float shape) {
        this.shape = shape;
        if (list != null) {
            mlist.addAll(list);
        }
        this.drawerLayout = drawerLayout;
        View.inflate(context, R.layout.layout_furniture_drawer, this);
        ButterKnife.bind(this);
        initTable();
    }

    /**
     * 刷新
     *
     * @param list
     * @param shape
     */
    public void notifyData(List<ObjectBean> list, float shape) {
        this.shape = shape;
        mlist.clear();
        mlist.addAll(list);
        tablelayout.init(mlist, shape, R.drawable.shape_geceng1);
    }

    private void initTable() {
        tablelayout.init(mlist, shape, R.drawable.shape_geceng1);
        tablelayout.setOnItemClickListener(new CustomTableRowLayout.OnItemClickListener() {
            @Override
            public void itemClick(ChildView view) {
                view.setEditable(!view.isEdit());
                if (selectView != null) {
                    selectView.setEditable(false);
                    selectView = null;
                }
                if (view.isEdit()) {
                    selectView = view;
                    ((FurnitureLookActivity) context).refrushListView(view.getObjectBean());
                    if (tablelayout.findView != null && tablelayout.findView == selectView) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                ((FurnitureLookActivity) context).objectListView.findObject(tablelayout.findObject);
                            }
                        });
                    }
                    if (selectItemViewListener != null) {
                        selectItemViewListener.selectItemView(true);
                    }
                } else {
                    ((FurnitureLookActivity) context).refrushListView(null);
                    if (selectItemViewListener != null) {
                        selectItemViewListener.selectItemView(false);
                    }
                }
            }
        });
    }

    @OnClick({R.id.close_tv, R.id.dissView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_tv:
                Intent intent = new Intent(context, EditTemplateActivity.class);
                intent.putExtra("object", bean);
                intent.putExtra("position", ((FurnitureLookActivity) context).spacePosition);
                ((Activity) context).startActivityForResult(intent, 34);
                break;
            case R.id.dissView:
                drawerLayout.closeDrawer(Gravity.RIGHT);
                ((FurnitureLookActivity) context).objectListView.hide();
                break;
        }
    }

    private SelectItemViewListener selectItemViewListener;

    public void setSelectItemViewListener(SelectItemViewListener selectItemViewListener) {
        this.selectItemViewListener = selectItemViewListener;
    }

    public interface SelectItemViewListener {
        void selectItemView(boolean select);
    }
}
