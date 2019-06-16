package com.gongwu.wherecollect.furnitureEdit;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.AnimationUtil;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
/**
 * Function:自定义模板控件
 * 默认背景分隔成6列8行；
 * Date: 2018/1/2
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class CustomTableRowLayout extends RelativeLayout {
    public static final int action_splip_merge = 108;//合并拆分动作
    public static final int action_rename = 109;//重命名动作
    public static final int action_shape = 110;//长宽比设置动作
    public static final int action_struct = 111;//调整内部结构动作
    public static final float shape_width = 1.33f;//长宽比，宽比较长
    public static final float shape_height = 0.75f;//长宽比，高比较长
    public static final float shape_rect = 1f;//长宽比，正方形
    public final static float MAXW = 8f;//横向最多排几个
    public final static float MAXH = 8f;//竖向最多排几个
    public Stack<Action> nextActions = new Stack<>();
    public Action currentAction;
    Context context;
    ArrayList<ObjectBean> selectBeans = new ArrayList<>();//已选择的列表
    private float shape = shape_width;
    private List<ObjectBean> childBeans = new ArrayList<>();
    private Stack<Action> lastActions = new Stack<>();
    private OnItemClickListener listener;
    private int resId;
    private View v;//透明遮层
    public  ObjectBean findObject;
    public  ChildView findView;
    public Handler hd=new Handler();
    public Runnable run=new Runnable() {
        @Override
        public void run() {
            findView=null;
            findObject=null;
        }
    };
    private int num;
    public CustomTableRowLayout(Context context) {
        this(context, null);
    }

    public CustomTableRowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public List<ObjectBean> getChildBeans() {
        return childBeans;
    }

    /**
     * @param childBeans
     * @param s          长宽比  传-1表示不设置
     * @param resId      隔层的背景ID，查看结构白色和编辑结构不一样
     */
    public void init(List<ObjectBean> childBeans, float s, int resId) {
        v = new View(context);
        RelativeLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        v.setLayoutParams(lp);
        this.resId = resId;
        if (shape != -1) {
            shape = s;
        }
        this.childBeans = childBeans;
        num = childBeans.size();
        selectBeans.clear();
        initChild();
        setShape(shape);
    }

    private void changeShapeAndInit() {
        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initChild();
            }
        });
    }

    /**
     * 初始化子家具
     */
    public void initChild() {
        removeAllViews();
        addView(v);
        for (int i = 0; i < StringUtils.getListSize(childBeans); i++) {
            ChildView ob = new ChildView(context, resId);
            ob.setEditable(false);
            ob.setObject(childBeans.get(i), this);
            addView(ob);
            ob.setOnClickListener(new OnClickListener(listener));
        }
    }

    public ArrayList<ObjectBean> getSelectBeans() {
        return selectBeans;
    }

    public void setSelectBeans(ArrayList<ObjectBean> selectBeans) {
        this.selectBeans = selectBeans;
    }

    /**
     * 上下拆分
     */
    public List<ObjectBean> splitUpAndDown() {
        num++;
        List<ObjectBean> resultList = new ArrayList<>();
        nextActions.clear();
        addAction();
        ObjectBean bean = selectBeans.get(0);
        /**
         * ====================上面的隔层
         */
        ObjectBean tempBeanUp = new ObjectBean();
        tempBeanUp.setPosition(bean.getPosition());
        ObjectBean.Point scaleUp = new ObjectBean.Point();
        scaleUp.setX(bean.getScale().getX());
        scaleUp.setY(bean.getScale().getY() / 2);
        tempBeanUp.setScale(scaleUp);
        tempBeanUp.setName(bean.getName());
        tempBeanUp.setCode(bean.getCode());
        /**
         * ====================下面的隔层
         */
        ObjectBean tempBeanBottom = new ObjectBean();
        ObjectBean.Point positionBottom = new ObjectBean.Point();
        positionBottom.setX(bean.getPosition().getX());
        positionBottom.setY(bean.getPosition().getY() + bean.getScale().getY() / 2);
        tempBeanBottom.setPosition(positionBottom);
        ObjectBean.Point scaleBottom = new ObjectBean.Point();
        scaleBottom.setX(bean.getScale().getX());
        scaleBottom.setY(bean.getScale().getY() / 2);
        tempBeanBottom.setScale(scaleBottom);
        childBeans.removeAll(selectBeans);
        childBeans.add(tempBeanUp);
        childBeans.add(tempBeanBottom);
        selectBeans.get(0).setSelect(false);
        selectBeans.clear();
        tempBeanBottom.setName("隔层" + num);
        initChild();
        resultList.add(tempBeanBottom);
        resultList.add(tempBeanUp);
        return resultList;
    }

    /**
     * 左右拆分
     */
    public List<ObjectBean> splitLeftAndRight() {
        num++;
        List<ObjectBean> resultList = new ArrayList<>();
        nextActions.clear();
        addAction();
        ObjectBean bean = selectBeans.get(0);
        /**
         * ====================左面的隔层
         */
        ObjectBean tempBeanLeft = new ObjectBean();
        tempBeanLeft.setPosition(bean.getPosition());
        ObjectBean.Point scaleLeft = new ObjectBean.Point();
        scaleLeft.setX(bean.getScale().getX() / 2);
        scaleLeft.setY(bean.getScale().getY());
        tempBeanLeft.setScale(scaleLeft);
        tempBeanLeft.setName(bean.getName());
        tempBeanLeft.setCode(bean.getCode());
        /**
         * ====================右面的隔层
         */
        ObjectBean tempBeanRight = new ObjectBean();
        ObjectBean.Point positionRight = new ObjectBean.Point();
        positionRight.setX(bean.getPosition().getX() + bean.getScale().getX() / 2);
        positionRight.setY(bean.getPosition().getY());
        tempBeanRight.setPosition(positionRight);
        ObjectBean.Point scaleRight = new ObjectBean.Point();
        scaleRight.setX(bean.getScale().getX() / 2);
        scaleRight.setY(bean.getScale().getY());
        tempBeanRight.setScale(scaleRight);
        tempBeanRight.setName("隔层" + num);
        childBeans.removeAll(selectBeans);
        childBeans.add(tempBeanLeft);
        childBeans.add(tempBeanRight);
        selectBeans.get(0).setSelect(false);
        selectBeans.clear();
        initChild();
        resultList.add(tempBeanLeft);
        resultList.add(tempBeanRight);
        return resultList;
    }

    /**
     * 合并
     */
    public List<ObjectBean> merge() {
        List<ObjectBean> resultList = new ArrayList<>();
        nextActions.clear();//清空前进栈
        addAction();//记录当前到上一步栈
        ObjectBean.Point[] points = getLT_RT_LB_RB();//获取已选中的矩形4个顶点
        ObjectBean tempBean = new ObjectBean();//创建新的对象
        tempBean.setPosition(points[0]);//设置坐标
        tempBean.setName(selectBeans.get(0).getName());
        ObjectBean.Point scale = new ObjectBean.Point();
        scale.setX(points[1].getX() - points[0].getX() + 1);
        scale.setY(points[3].getY() - points[0].getY() + 1);
        tempBean.setScale(scale);//设置大小
        childBeans.removeAll(selectBeans);//移除选中的对象
        childBeans.add(tempBean);//添加新对象
        StringBuilder sb = new StringBuilder();
        for (ObjectBean bean : selectBeans) {//取消选中状态
            bean.setSelect(false);
            if (!TextUtils.isEmpty(bean.getCode())) {
                sb.append(bean.getCode()).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        tempBean.setCode(sb.toString());
        selectBeans.clear();
        initChild();//刷新界面
        resultList.add(tempBean);
        return resultList;
    }

    /**
     * 保留当前动作到上一步栈
     */
    public void addAction() {
        if (currentAction == null) {
            currentAction = new Action(action_splip_merge);
            List<ObjectBean> list = new ArrayList<>();
            list.addAll(childBeans);
            currentAction.setBeans(list);
        }
        lastActions.add(currentAction);
        currentAction = null;
    }

    /**
     * 上一步
     */
    public void last() {
        if (currentAction == null) {
            currentAction = new Action(action_splip_merge);
            List<ObjectBean> list = new ArrayList<>();
            list.addAll(childBeans);
            currentAction.setBeans(list);
            nextActions.push(currentAction);
        } else {
            nextActions.push(currentAction);
        }
        currentAction = lastActions.pop();
        selectBeans.clear();
        childBeans.clear();
        childBeans.addAll(currentAction.beans);
        initChild();
    }

    /**
     * 下一步
     */
    public void next() {
        selectBeans.clear();
        lastActions.push(currentAction);
        currentAction = nextActions.pop();
        childBeans.clear();
        childBeans.addAll(currentAction.beans);
        initChild();
    }

    /**
     * 是否可以撤销
     *
     * @return
     */
    public boolean isCanLast() {
        return !StringUtils.isEmpty(lastActions);
    }

    /**
     * 是否可以下一步
     *
     * @return
     */
    public boolean isCanNext() {
        return !StringUtils.isEmpty(nextActions);
    }

    /**
     * @return 是否可以合并拆分，左右拆分，合并隔层
     */
    public boolean[] getSelectState() {
        boolean sx = true, zy = true, hb = true, name = true;
        boolean[] status = new boolean[4];
        selectBeans.clear();
        ArrayList<ChildView> unSelectBeans = new ArrayList<>();//没选择的列表
        for (int i = 0; i < getChildCount(); i++) {//选中和没选中分类
            if (getChildAt(i) instanceof ChildView) {
                ChildView ch = ((ChildView) getChildAt(i));
                if (ch.isEdit()) {
                    selectBeans.add(ch.getObjectBean());
                } else {
                    unSelectBeans.add(ch);
                }
            }
        }
        if (selectBeans.isEmpty()) {//如果一个都没有选中
            sx = false;//是否可以上下拆分
            zy = false;//是否可以左右拆分
            hb = false;//是否可以合并
            name = false;//是否可以改名字
        } else if (selectBeans.size() == 1) {//如果只选择了一个
            hb = false;//不能合并
            if (selectBeans.get(0).getScale().getX() <= 1) {
                zy = false;//不能左右拆
            }
            if (selectBeans.get(0).getScale().getY() <= 1) {
                sx = false;//不能上下拆
            }
        } else {//如果选择了多个
            name = false;
            zy = false;
            sx = false;
            ObjectBean.Point[] points = getLT_RT_LB_RB();
            ObjectBean.Point leftTop = points[0];
            ObjectBean.Point rightTop = points[1];
            ObjectBean.Point leftBottom = points[2];
            ObjectBean.Point rightBottom = points[3];
            //如果选中的对角线不相等不能合并
            if (leftTop.getX() + leftTop.getY() + rightBottom.getX() + rightBottom.getY() != rightTop.getX() +
                    rightTop.getY()
                    + leftBottom.getX() + leftBottom.getY()) {
                hb = false;
            } else {//如果选中的对角线相等，需要验证是不是连续的
                //循环没有选中的隔层是不是有夹在选中的中间的，如果是，不能合并，需要连续选中才能合并
                for (int i = 0; i < unSelectBeans.size(); i++) {
                    ChildView ch = (unSelectBeans.get(i));
                    ObjectBean.Point chLeftTop = ch.getObjectBean().getLeftTopBasePoint();
                    ObjectBean.Point chRightTop = ch.getObjectBean().getRightTopBasePoint();
                    ObjectBean.Point chLeftBottom = ch.getObjectBean().getLeftBottomBasePoint();
                    ObjectBean.Point chRightBottom = ch.getObjectBean().getRightBottomBasePoint();
                    //                    if (chLeftTop.getX() >= leftTop.getX() && chLeftTop.getY() >= leftTop.getY
                    // ()//完全被包裹
                    //                            && chRightTop.getX() <= rightTop.getX() && chRightTop.getY() >=
                    // rightTop.getY()
                    //                            && chLeftBottom.getX() >= leftBottom.getX() && chLeftBottom.getY()
                    // <= leftBottom.getY()
                    //                            && chRightBottom.getX() <= rightBottom.getX() && chRightBottom.getY
                    // () <= rightBottom.getY
                    //                            ()) {
                    //                        hb = false;
                    //                        break;
                    //                    }
                    if (!(chLeftBottom.getY() < leftTop.getY() || chLeftTop.getX() > rightTop.getX()
                            || chLeftTop.getY() > leftBottom.getY() || chRightBottom.getX() < leftBottom.getX())) {
                        hb = false;
                        break;
                    }
                }
            }
        }
        status[0] = sx;
        status[1] = zy;
        status[2] = hb;
        status[3] = name;
        return status;
    }

    public float getShape() {
        return shape;
    }

    /**
     * @param shape
     */
    public void setShape(float shape) {
        this.shape = shape;
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (shape == shape_height) {
            lp.width = ((int) (200 * BaseViewActivity.getScreenScale(((Activity) context))));
            lp.height = FrameLayout.LayoutParams.MATCH_PARENT;
        } else if (shape == shape_rect) {
            lp.width = ((int) (300 * BaseViewActivity.getScreenScale(((Activity) context))));
            lp.height = ((int) (300 * BaseViewActivity.getScreenScale(((Activity) context))));
        } else if (shape == shape_width) {
            lp.width = FrameLayout.LayoutParams.MATCH_PARENT;
            lp.height = FrameLayout.LayoutParams.MATCH_PARENT;
        }
        setLayoutParams(lp);
        changeShapeAndInit();
    }

    /**
     * 获取选取的隔层的4个顶点
     *
     * @return
     */
    private ObjectBean.Point[] getLT_RT_LB_RB() {
        ObjectBean.Point leftTop = null;
        ObjectBean.Point rightTop = null;
        ObjectBean.Point leftBottom = null;
        ObjectBean.Point rightBottom = null;
        for (int i = 0; i < selectBeans.size(); i++) {
            ObjectBean bean = selectBeans.get(i);
            if (leftTop == null) {
                leftTop = bean.getLeftTopBasePoint();
            } else if (bean.getLeftTopBasePoint().getX() <= leftTop.getX() && bean.getLeftTopBasePoint().getY() <=
                    leftTop.getY()) {
                leftTop = bean.getLeftTopBasePoint();
            }
            if (rightTop == null) {
                rightTop = bean.getRightTopBasePoint();
            } else if (bean.getRightTopBasePoint().getX() >= rightTop.getX() && bean.getRightTopBasePoint().getY()
                    <= rightTop
                    .getY()) {
                rightTop = bean.getRightTopBasePoint();
            }
            if (leftBottom == null) {
                leftBottom = bean.getLeftBottomBasePoint();
            } else if (bean.getLeftBottomBasePoint().getX() <= leftBottom.getX() && bean.getLeftBottomBasePoint()
                    .getY() >= leftBottom.getY()) {
                leftBottom = bean.getLeftBottomBasePoint();
            }
            if (rightBottom == null) {
                rightBottom = bean.getRightBottomBasePoint();
            } else if (bean.getRightBottomBasePoint().getX() >= rightBottom.getX() && bean.getRightBottomBasePoint()
                    .getY() >= rightBottom.getY()) {
                rightBottom = bean.getRightBottomBasePoint();
            }
        }
        return new ObjectBean.Point[]{leftTop, rightTop, leftBottom, rightBottom};
    }

    /**
     * 高亮选中的物品所在位置
     *
     * @param objectBean
     */
    public void findView(ObjectBean objectBean,boolean isClick) {
        ChildView view = (ChildView) findViewByObject(objectBean);
        if (view == null) {
            ToastUtil.show(context, "未找到归属", Toast.LENGTH_SHORT);
            cancelFind();
            return;
        } else if (view.isEdit()) {
            return;
        }
        if(listener!=null&&isClick){
            listener.itemClick(view);
            return;
        }
        v.bringToFront();
        v.setBackgroundColor(getResources().getColor(R.color.black_70));
        view.bringToFront();
        AnimationUtil.StartTada(view, 1.0f);
        hd.removeCallbacks(run);
        findObject=objectBean;
        findView=view;
    }

    /**
     * 取消高亮
     */
    public void cancelFind() {
        v.setBackgroundColor(getResources().getColor(R.color.trans));
        if(findView!=null){//
            hd.postDelayed(run,500);
        }
    }

    /**
     * 找到物品时属于哪个家具的
     *
     * @param object
     * @return
     */
    public View findViewByObject(ObjectBean object) {
        for (int i = 0; i < getChildCount(); i++) {
            if (!(getChildAt(i) instanceof ChildView)) {
                continue;
            }
            ChildView view = ((ChildView) getChildAt(i));
            for (int j = 0; j < StringUtils.getListSize(object.getLocations()); j++) {
                if (view.getObjectBean().getCode().equals(object.getLocations().get(j).getCode())) {
                    return view;
                }
            }
        }
        return null;
    }

    public static interface OnItemClickListener {
        public void itemClick(ChildView view);
    }
    private static class OnClickListener implements View.OnClickListener {
        OnItemClickListener listener;

        public OnClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.itemClick((ChildView) v);
        }
    }
    private static class Action {
        private int type;
        private int shape;
        private List<ObjectBean> beans;

        public Action(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getShape() {
            return shape;
        }

        public void setShape(int shape) {
            this.shape = shape;
        }

        public List<ObjectBean> getBeans() {
            return beans;
        }

        public void setBeans(List<ObjectBean> beans) {
            this.beans = beans;
        }
    }
}
