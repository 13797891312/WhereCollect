package com.gongwu.wherecollect.LocationLook;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.AnimationUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.ErrorView;
import com.gongwu.wherecollect.view.ObjectView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * Function:
 * Date: 2017/11/14
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class LocationPage extends RelativeLayout implements View.OnDragListener,
        GestureDetector.OnGestureListener {
    private static final int MIN_OFFSET_VALUE = 20;
    public List<ObjectBean> mlist = new ArrayList<>();//家具列表；
    Context context;
    float downY, downX;
    private View v;//透明遮层
    private OnItemClickListener listener;
    private boolean isEdit = false;
    private GestureDetector mGestureDetector;
    private ErrorView mErrorView;
    private ProgressBar mProgressBar;
    private int position;

    public LocationPage(Context context) {
        this(context, null);
    }

    public LocationPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setBackgroundColor(context.getResources().getColor(R.color.white));
        setOnDragListener(this);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * @param isEdit 是否编辑状态
     */
    public void init(boolean isEdit) {
        this.isEdit = isEdit;
        v = new View(context);
        RelativeLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        v.setLayoutParams(lp);
        mGestureDetector = new GestureDetector(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 刷新数据
     */
    public void notiyData(List<ObjectBean> list) {
        cancelFind();
        initChild();
        if (StringUtils.isEmpty(list)) {
            mErrorView = new ErrorView(context);
            RelativeLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams
                    .MATCH_PARENT);
            mErrorView.setLayoutParams(lp);
            addView(mErrorView);
        }
    }

    /**
     * 删除一个家具
     *
     * @param ov
     */
    public void removeObject(ObjectView ov) {
        removeView(ov);
        for (int i = 0; i < StringUtils.getListSize(mlist); i++) {
            if (mlist.get(i) == ov.getObjectBean()) {
                mlist.remove(i);
            }
        }
    }

    /**
     * 初始化子家具
     */
    private void initChild() {
        removeAllViews();
        addView(v);
        if (mlist == null) {
            return;
        }
        for (int i = 0; i < StringUtils.getListSize(mlist); i++) {
            ObjectView ob = new ObjectView(context);
            ob.setEditable(false);
            ob.setObject(mlist.get(i));
            addView(ob);
            ob.setOnClickListener(new OnClickListener(listener));
        }
        getChildListener(position);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        LogUtil.e(event.getX() + "-----" + event.getY());
        ObjectView objectView = ((ObjectView) event.getLocalState());
        ObjectBean bean = (ObjectBean) objectView.getTag();
        switch (event.getAction()) {
            case DragEvent.ACTION_DROP://在某布局结束拖动
                bean.setPosition(context, event.getX() - bean.getWidth(context) / 2, event.getY() - bean.getHeight
                        (context) / 2);
                objectView.setObject(bean);
                objectView.bringToFront();
                moveData(objectView);
                break;
        }
        return true;
    }

    /**
     * 移动位置调用接口
     */
    private void moveData(final ObjectView view) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("location_code", view.getObjectBean().getCode());
        map.put("scale", JsonUtils.jsonFromObject(view.getObjectBean().getScale()));
        map.put("position", JsonUtils.jsonFromObject(view.getObjectBean().getPosition()));
        PostListenner listenner = new PostListenner(context) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                /********此段代码是因为位置查看和位置编辑页的家具对象不是同一个了，所以需要手动赋值********/
                String code=MainLocationFragment.mlist.get(((LocationEditActivity) context).viewPager
                        .getCurrentItem()).getCode();
                List<ObjectBean> list =MainLocationFragment.locationMap.get(code);
                if(!list.contains(view.getObjectBean())){
                    for (int i = 0; i < StringUtils.getListSize(list); i++) {
                        if(list.get(i).getCode().equals(view.getObjectBean().getCode())){
                            list.get(i).setPosition(view.getObjectBean().getPosition());
                        }
                    }
                }
                /***********************/
                EventBusMsg.EditLocationMsg msg = new EventBusMsg.EditLocationMsg(((LocationEditActivity) context)
                        .viewPager
                        .getCurrentItem());
                msg.onlyNotifyUi = true;
                EventBus.getDefault().post(msg);
            }
        };
        HttpClient.movePosition(context, map, listenner);
    }

    /**
     * 找到物品时属于哪个家具的
     *
     * @param object
     * @return
     */
    public View findViewByObject(ObjectBean object) {
        for (int i = 0; i < getChildCount(); i++) {
            if (!(getChildAt(i) instanceof ObjectView)) {
                continue;
            }
            ObjectView view = ((ObjectView) getChildAt(i));
            for (int j = 0; j < StringUtils.getListSize(object.getLocations()); j++) {
                if (view.getObjectBean().getCode().equals(object.getLocations().get(j).getCode())) {
                    return view;
                }
            }
        }
        return null;
    }

    /**
     * 高亮选中的物品所在位置
     *
     * @param objectBean
     */
    public void findView(ObjectBean objectBean) {
        View view = findViewByObject(objectBean);
        if (view == null) {
            ToastUtil.show(context, "未找到归属", Toast.LENGTH_SHORT);
            cancelFind();
            return;
        }
        v.bringToFront();
        v.setBackgroundColor(getResources().getColor(R.color.black_70));
        view.bringToFront();
        AnimationUtil.StartTada(view, 1.0f);
    }

    /**
     * 添加家具
     *
     * @param objectBean
     */
    public ObjectView addFuniture(ObjectBean objectBean) {
        mlist.add(objectBean);
        ObjectView ob = new ObjectView(context);
        ob.setEditable(false);
        ob.setObject(objectBean);
        addView(ob);
        ob.setOnClickListener(new OnClickListener(listener));
        if (mErrorView != null && mErrorView.getParent() != null) {
            ((RelativeLayout) mErrorView.getParent()).removeView(mErrorView);
        }
        return ob;
    }

    /**
     * 取消高亮
     */
    public void cancelFind() {

        v.setBackgroundColor(getResources().getColor(R.color.trans));
    }

    @Override//重写方法物品总览上滑任意位置都能呼出
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isEdit) {
            return super.onInterceptTouchEvent(ev);
        }
        cancelFind();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downY = ev.getY();
            downX = ev.getX();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            float offsetY = downY - ev.getY();//Y方向偏移量
            float offsetX = downX - ev.getX();//Y方向偏移量
            if (Math.abs(offsetX) < Math.abs(offsetY)) {
                if (downY - ev.getY() > MIN_OFFSET_VALUE) {
                    if (listener != null) {//上滑
                        listener.upSlide();
                        return true;
                    }
                } else {
                    if (listener != null) {//下滑
                        listener.downSlide();
                        return true;
                    }
                }
            } else if (Math.abs(offsetX) > MIN_OFFSET_VALUE) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 先取缓存，再从网络获取家具
     */
    public void getLocationChild(int position) {
        this.position = position;
        String code = MainLocationFragment.mlist.get(position).getCode();
        mlist = MainLocationFragment.locationMap.get(code);
        if (mlist == null) {
            final String cache = SaveDate.getInstence(context).getLocation(code);
            if (!TextUtils.isEmpty(cache)) {
                mlist = JsonUtils.listFromJson(cache, ObjectBean.class);
                MainLocationFragment.locationMap.put(code, mlist);
                notiyData(mlist);
            }
            getNetData(cache, code);
        } else {
            notiyData(mlist);
        }
    }

    /**
     * 网络获取家具
     *
     * @param cache
     * @param code
     */
    public void getNetData(final String cache, final String code) {
        if (mProgressBar == null) {
            RelativeLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
            lp.addRule(CENTER_IN_PARENT);
            mProgressBar = new ProgressBar(context);
            mProgressBar.setLayoutParams(lp);
        }
        if (StringUtils.isEmpty(mlist)) {
            addView(mProgressBar);
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("location_code", code);
        PostListenner listenner = new PostListenner(context) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                if (mProgressBar.getParent() != null) {
                    removeView(mProgressBar);
                }
                if (r.getResult().equals(cache)) {//如果和缓存一样则不做处理
                    return;
                }
                mlist = JsonUtils.listFromJson(r.getResult(), ObjectBean.class);
                SaveDate.getInstence(context).setLocation(code, r.getResult());
                MainLocationFragment.locationMap.put(code, mlist);
                notiyData(mlist);
            }
        };
        HttpClient.getFurnitureList(context, map, listenner);
    }

    /**
     * 获取到物品总览网络数据后回调
     */
    protected void getNetDataListener(List<ObjectBean> list) {
    }

    /**
     * 家具加载成功
     */
    protected void getChildListener(int position) {
    }

    //无用
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    //无用
    @Override
    public void onShowPress(MotionEvent e) {
    }

    //无用
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (listener != null) {
            listener.bgClick();
        }
        return false;
    }

    //无用
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    //无用
    @Override
    public void onLongPress(MotionEvent e) {
    }

    //无用
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1==null||e2==null){
            return false;
        }
        float offsetX = e1.getX() - e2.getX();//X方向偏移量
        float offsetY = e1.getY() - e2.getY();//Y方向偏移量
        if (Math.abs(offsetX) < Math.abs(offsetY)) {
            if (e1.getY() - e2.getY() > MIN_OFFSET_VALUE) {
                if (listener != null) {//上滑
                    listener.upSlide();
                }
            } else {
                if (listener != null) {//下滑
                    listener.downSlide();
                }
            }
        }
        return false;
    }

    /**
     * 获取物品总览
     */
    public void getObjectList() {
        GetObjectListUtil util = new GetObjectListUtil(context) {
            @Override
            protected void getObjectListDataListener(List<ObjectBean> temp) {
                super.getObjectListDataListener(temp);
                getNetDataListener(temp);
            }
        };
        util.getObjectList(MainLocationFragment.mlist.get(position).getCode());
    }

    public static interface OnItemClickListener {
        public void itemClick(ObjectView view);
        public void bgClick();
        public void upSlide();
        public void downSlide();
    }
    private static class OnClickListener implements View.OnClickListener {
        OnItemClickListener listener;

        public OnClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.itemClick(((ObjectView) v));
            }
        }
    }
}
