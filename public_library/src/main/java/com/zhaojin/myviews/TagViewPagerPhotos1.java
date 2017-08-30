package com.zhaojin.myviews;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
/**
 * @author 赵进
 * @步骤1 直接在XML中添加此控件（自定义相对布局），然后再代码中找到控件先调用init方法，和setId方法（随便给个int型ID,多个地方使用此控件，id不能重复）
 * @步骤2 选择调用setAutoNext方法，设置是否自动滚动，默认不滚动
 * @步骤3 必须调用setOnGetView方法，设置ViewPager的Item视图,也就是重写PagerAdapter的instantiateItem方法
 * @步骤4 必须调用setAdapter();
 */
public class TagViewPagerPhotos1 extends RelativeLayout implements
        OnPageChangeListener, Runnable {
    public MyViewPager viewPager;
    OnGetView onGetView;
    private boolean isAutoNext = false;
    private int autoNextTime = 5000;
    private Handler hd = new Handler();
    private Context context;
    private int count;
    private int tagImageId_seleced = 0, tagImageId_nomorl = 0;
    private int size = 16;
    private int margin = 5;
    private LinearLayout tagImageLayout;
    private List<ImageView> imageList = new ArrayList<ImageView>();
    private int marginButtom = 20;
    /**
     * 当前viewpager显示第几�?
     ***/
    private int currentItem = 0;
    private OnSelectedListoner selectedListoner;

    public TagViewPagerPhotos1(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public TagViewPagerPhotos1(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void setOnSelectedListoner(OnSelectedListoner listoner) {
        this.selectedListoner = listoner;
    }

    /**
     * @param id1 小圆选中后图标资源ID
     * @param id2 小圆没选中图标资源ID
     */
    public void init(int id1, int id2) {
        this.tagImageId_seleced = id1;
        this.tagImageId_nomorl = id2;
        tagImageLayout = new LinearLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(0, 0, 0, marginButtom);
        tagImageLayout.setLayoutParams(params);
        this.addView(tagImageLayout);
        tagImageLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        tagImageLayout.setBottom(marginButtom);
        viewPager = new MyViewPager(context);
        viewPager.setOnPageChangeListener(this);
        viewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        this.addView(viewPager);
    }

    /**
     * @param id1          小圆选中后图标资源ID
     * @param id2          小圆没选中中图标资源ID
     * @param size         小图标大小
     * @param imageMargin  小图标间距
     * @param gravity      小图标位置 1代表viewPager上面，2代表viewPager下面;
     * @param layoutMargin 小图标距离父控件margin,如果在上面则代表距离上边框距离，如果在下面则代表距离下边框的距离
     */
    public void init(int id1, int id2, int size, int imageMargin, int gravity,
                     int layoutMargin) {
        this.tagImageId_seleced = id1;
        this.tagImageId_nomorl = id2;
        this.margin = imageMargin;
        this.size = size;
        this.marginButtom = layoutMargin;
        tagImageLayout = new LinearLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if (gravity == 2) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0, 0, 0, marginButtom);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.setMargins(0, marginButtom, 0, 0);
        }
        tagImageLayout.setLayoutParams(params);
        this.addView(tagImageLayout);
        viewPager = new MyViewPager(context);
        viewPager.setOnPageChangeListener(this);
        viewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        this.addView(viewPager);
    }

    /**
     * @param adapter viewpager适配器
     * @param count   viewpager实际item数量,一般是list.size();
     */
    public void setAdapter(int count, int currentPositon) {
        this.count = count;
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setCurrentItem(currentPositon);
        initTagImage(this.count, currentPositon);
        if (isAutoNext && count > 1) {
            hd.postDelayed(this, autoNextTime);
        }
    }

    /**
     * @param postion 返回指定页面的视图
     * @return
     */
    public View getView(int postion) {
        return viewPager.getChildAt(postion);
    }

    /**
     * @param adapter viewpager适配器
     * @param count   viewpager实际item数量,一般是list.size();
     */
    public void setAdapter(int coun) {
        this.count = count;
        viewPager.setAdapter(new MyPagerAdapter());
        initTagImage(this.count, 0);
        if (isAutoNext && count > 1) {
            hd.postDelayed(this, autoNextTime);
        }
    }

    /**
     * @param count viewpager实际数据数量
     */
    public void notifyChanged(int count) {
        viewPager.getAdapter().notifyDataSetChanged();
        initTagImage(count, 0);
        if (isAutoNext && count > 1) {
            hd.postDelayed(this, autoNextTime);
        }
    }

    public int getCurrentItem() {
        return currentItem;
    }

    ;

    public void setCurrentItem(int position) {
        viewPager.setCurrentItem(position);
    }

    ;

    /**
     * @param count2 viewpager实际数据数量
     */
    private void initTagImage(int count2, int position) {
        // TODO Auto-generated method stub
        tagImageLayout.removeAllViews();
        imageList.clear();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,
                size);
        params.setMargins(margin, 0, margin, 0);
        for (int i = 0; i < count2; i++) {
            ImageView view1 = new ImageView(context);
            view1.setLayoutParams(params);
            if (tagImageId_seleced != 0) {
                if (i == position) {
                    view1.setImageResource(tagImageId_seleced);
                } else {
                    view1.setImageResource(tagImageId_nomorl);
                }
            }
            view1.setScaleType(ScaleType.FIT_XY);
            imageList.add(view1);
            tagImageLayout.addView(view1);
        }
        tagImageLayout.bringToFront();
    }

    /**
     * @param isAutoNext 是否自动循环,默认为否
     * @param time       间隔时间 ，默认5000毫秒
     */
    public void setAutoNext(boolean isAutoNext, int time) {
        this.isAutoNext = isAutoNext;
        this.autoNextTime = time;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        if (imageList.isEmpty()) {
            return;
        }
        if (!isAutoNext) {
            currentItem = arg0;
            for (int i = 0; i < imageList.size(); i++) {
                if (tagImageId_seleced != 0) {
                    if (i == arg0) {
                        imageList.get(arg0).setImageResource(tagImageId_seleced);
                    } else
                        imageList.get(i).setImageResource(tagImageId_nomorl);
                }
            }
        } else {
            if (tagImageId_nomorl != 0) {
                imageList.get(currentItem).setImageResource(tagImageId_nomorl);
            }
            currentItem = arg0 % this.count;
            if (tagImageId_seleced != 0) {
                imageList.get(currentItem).setImageResource(tagImageId_seleced);
            }
            hd.removeCallbacks(this);
            hd.postDelayed(this, autoNextTime);
        }
        if (selectedListoner != null) {
            selectedListoner.onSelected(arg0);
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        if (count <= 1) {
            return;
        }
        int count = viewPager.getCurrentItem();
        if (tagImageId_nomorl != 0) {
            imageList.get(currentItem).setImageResource(tagImageId_nomorl);
        }
        viewPager.setCurrentItem(++count);
        currentItem = viewPager.getCurrentItem() % this.count;
        if (tagImageId_seleced != 0) {
            imageList.get(currentItem).setImageResource(tagImageId_seleced);
        }
        hd.postDelayed(this, autoNextTime);
    }

    /**
     * @param onGetView 重写PagerAdapter里的instantiateItem方法，用于实现自己的视图，可以直接填new OnGetView接口对象
     */
    public void setOnGetView(OnGetView onGetView) {
        this.onGetView = onGetView;
    }

    public interface OnSelectedListoner {
        /**
         * @param position 当前切换到哪项
         */
        public void onSelected(int position);
    }
    public interface OnGetView {
        /**
         * @param container
         * @param position  注意position是代表预加载的项，也就是当前显示的下一项
         * @return
         */
        public View getView(ViewGroup container, int position);
    }
    public class MyPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            return onGetView.getView(container, position % count);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (isAutoNext && count > 1) {
                return Integer.MAX_VALUE;
            } else
                return count;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView((View) object);
            object = null;
        }
    }
}
