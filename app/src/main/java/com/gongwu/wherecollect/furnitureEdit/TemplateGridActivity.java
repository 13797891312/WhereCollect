package com.gongwu.wherecollect.furnitureEdit;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class TemplateGridActivity extends BaseViewActivity {
    public int downX = -1, donwY = -1, currentX = -1, currentY = -1;
    @Bind(R.id.gridlayout)
    GridLayout gridlayout;
    @Bind(R.id.submit_btn)
    Button submitBtn;
    @Bind(R.id.dragview)
    View dragview;
    MyDragListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_grid);
        ButterKnife.bind(this);
        titleLayout.setTitle("内部结构");
        titleLayout.setBack(true, null);
        listener = new MyDragListener();
        intiGrid();
    }

    private void intiGrid() {
        for (int i = 0; i < 90; i++) {
            MyView v = new MyView(this, dragview);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(BaseViewActivity.getScreenWidth(this) / 6,
                    ((int) (24 * BaseViewActivity.getScreenScale(this))));
            v.setLayoutParams(lp);
            v.x = (6 + i) % 6;
            v.y = i / 6;
            gridlayout.addView(v);
        }
    }

    /**
     * 设置选中的
     */
    private void setSelect() {
        for (int i = 0; i < gridlayout.getChildCount(); i++) {
            MyView mv = (MyView) gridlayout.getChildAt(i);
            if (mv.x < Math.min(downX, currentX)//如果不在两点练成的矩形之内
                    || mv.x > Math.max(downX, currentX)
                    || mv.y < Math.min(donwY, currentY)
                    || mv.y > Math.max(donwY, currentY)) {
                mv.setSelected(false);
            } else {
                mv.setSelected(true);
            }
        }
    }

    @OnClick(R.id.submit_btn)
    public void onClick() {
        Intent intent = new Intent();
        intent.putExtra("x", Math.abs(downX - currentX) + 1);//选择的几列
        intent.putExtra("y", Math.abs(donwY - currentY) + 1);//选中的几行
        setResult(100, intent);
        finish();
    }

    class MyView extends FrameLayout {
        public int x, y;
        View dragView;

        public MyView(Context context, View dragView) {
            super(context);
            this.dragView = dragView;
            setPadding(4, 4, 4, 4);
            View view = new View(context);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(lp);
            view.setBackgroundResource(R.drawable.select_background_grid_item);
            addView(view);
            setOnDragListener(listener);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //记录按下时候的坐标
                dragView.startDrag(null, new DragShadowBuilder(dragView), null, 0);
                TemplateGridActivity.this.submitBtn.setEnabled(true);
                downX = x;
                donwY = y;
                return false;
            }
            return super.onTouchEvent(event);
        }
    }
    /**
     * 移动监听
     */
    class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED://记录当前手势的坐标
                    if (Math.abs(((MyView) v).y - donwY) < 8) {
                        currentY = ((MyView) v).y;
                    }
                    currentX = ((MyView) v).x;
                    setSelect();
                    break;
            }
            return true;
        }
    }
}
