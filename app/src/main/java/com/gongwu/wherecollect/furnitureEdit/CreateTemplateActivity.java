package com.gongwu.wherecollect.furnitureEdit;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ShowCaseUtil;
import com.gongwu.wherecollect.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class CreateTemplateActivity extends BaseViewActivity implements View.OnClickListener {
    @Bind(R.id.tablelayout)
    CustomTableRowLayout tablelayout;
    @Bind(R.id.ckb_tv)
    TextView ckbTv;
    @Bind(R.id.nbjg_tv)
    TextView nbjgTv;
    @Bind(R.id.gcmc)
    EditText gcmc;
    @Bind(R.id.sxcf)
    TextView sxcf;
    @Bind(R.id.zycf)
    TextView zycf;
    @Bind(R.id.hbgc)
    TextView hbgc;
    @Bind(R.id.last_btn)
    TextView lastBtn;
    @Bind(R.id.next_btn)
    TextView nextBtn;
    @Bind(R.id.activity_custom_template)
    LinearLayout activityCustomTemplate;
    PopupWindow popupWindow;
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tablelayout.getSelectBeans().get(0).setName(s.toString());
            tablelayout.initChild();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_template);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("构建结构");
        titleLayout.textBtn.setText("确定");
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", (Serializable) tablelayout.getChildBeans());
                intent.putExtra("shape", tablelayout.getShape());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        initView();
        List<ObjectBean> list = (List<ObjectBean>) getIntent().getSerializableExtra("list");
        if (!StringUtils.isEmpty(list)) {
            tablelayout.init(list, getIntent().getFloatExtra("shape", CustomTableRowLayout.shape_width),R.drawable
                    .shape_geceng);//刷新家具g隔层
        }else {
            initTable(2, 4);
        }
        showHelp();
    }

    private void initView() {
        gcmc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!gcmc.getText().toString().equals(tablelayout.getSelectBeans().get(0).getName())) {
                        tablelayout.getSelectBeans().get(0).setName(gcmc.getText().toString());
                    }
                    tablelayout.getSelectBeans().get(0).setSelect(false);
                    tablelayout.getSelectBeans().clear();
                    tablelayout.initChild();
                    setUiStatus(tablelayout.getSelectState());
                    return false;
                }
                return false;
            }
        });
        tablelayout.setOnItemClickListener(new CustomTableRowLayout.OnItemClickListener() {
            @Override
            public void itemClick(ChildView view) {
                view.setEditable(!view.isEdit());
                boolean[] status = tablelayout.getSelectState();
                setUiStatus(status);
            }
        });
    }

    /**
     * 设置个按钮状态
     *
     * @param status
     */
    private void setUiStatus(boolean[] status) {
        sxcf.setEnabled(status[0]);
        zycf.setEnabled(status[1]);
        hbgc.setEnabled(status[2]);
        gcmc.setEnabled(status[3]);
        if (!status[3]) {
            gcmc.removeTextChangedListener(mTextWatcher);
            gcmc.setText("隔层名称");
        } else {
            gcmc.setText(tablelayout.getSelectBeans().get(0).getName());
            gcmc.setSelection(gcmc.length());//将光标移至文字末尾
            gcmc.addTextChangedListener(mTextWatcher);
        }
    }

    @OnClick({R.id.ckb_tv, R.id.nbjg_tv, R.id.gcmc, R.id.sxcf, R.id.zycf, R.id.hbgc, R.id.last_btn, R.id.next_btn})
    public void bindClick(View view) {
        switch (view.getId()) {
            case R.id.ckb_tv:
                showShapePopWindow();
                break;
            case R.id.nbjg_tv:
                Intent intent = new Intent(this, TemplateGridActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.gcmc:
                break;
            case R.id.sxcf:
                tablelayout.splitUpAndDown();
                setUiStatus(tablelayout.getSelectState());
                break;
            case R.id.zycf:
                tablelayout.splitLeftAndRight();
                setUiStatus(tablelayout.getSelectState());
                break;
            case R.id.hbgc:
                tablelayout.merge();
                setUiStatus(tablelayout.getSelectState());
                break;
            case R.id.last_btn:
                tablelayout.last();
                break;
            case R.id.next_btn:
                tablelayout.next();
                break;
        }
        lastBtn.setEnabled(tablelayout.isCanLast());
        nextBtn.setEnabled(tablelayout.isCanNext());
    }

    /**
     * 长宽比
     */
    private void showShapePopWindow() {
        if (popupWindow != null) {
            popupWindow.showAtLocation(activityCustomTemplate, Gravity.CENTER, 0, 0);
            return;
        }
        View view = View.inflate(this, R.layout.layout_shape_popwindow, null);
        View shape1 = view.findViewById(R.id.view1);
        View shape2 = view.findViewById(R.id.view2);
        View shape3 = view.findViewById(R.id.view3);
        shape1.setOnClickListener(this);
        shape2.setOnClickListener(this);
        shape3.setOnClickListener(this);
        popupWindow = new PopupWindow(view, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams
                .WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(activityCustomTemplate, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view1:
                tablelayout.setShape(CustomTableRowLayout.shape_rect);
                popupWindow.dismiss();
                break;
            case R.id.view2:
                tablelayout.setShape(CustomTableRowLayout.shape_height);
                popupWindow.dismiss();
                break;
            case R.id.view3:
                tablelayout.setShape(CustomTableRowLayout.shape_width);
                popupWindow.dismiss();
                break;
        }
    }

    /**
     * @param x 几列
     * @param y 几行
     */
    private void initTable(int x, int y) {
        List<ObjectBean> list = new ArrayList<>();
        for (int i = 0; i < x * y; i++) {
            ObjectBean temp = new ObjectBean();
            temp.setScale(new ObjectBean.Point());
            temp.setName("隔层" + (i + 1));
            temp.setPosition(new ObjectBean.Point());
            temp.getScale().setX(CustomTableRowLayout.MAXW / x);
            temp.getScale().setY(CustomTableRowLayout.MAXH / y);
            temp.getPosition().setX((i % x) * temp.getScale().getX());
            temp.getPosition().setY(i / x * temp.getScale().getY());
            list.add(temp);
        }
        tablelayout.init(list, -1,R.drawable.shape_geceng);//刷新家具g隔层
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            tablelayout.nextActions.clear();
            tablelayout.addAction();//记录动作到上一步
            initTable(data.getIntExtra("x", 0), data.getIntExtra("y", 0));//多到几行几列
            lastBtn.setEnabled(tablelayout.isCanLast());
            nextBtn.setEnabled(tablelayout.isCanNext());
            setUiStatus(tablelayout.getSelectState());//刷新界面按钮状态
        }
    }

    /**
     * 提示信息
     */
    private void showHelp(){
        ShowCaseUtil.showHelp(this,findViewById(R.id.help_layout),null,null,"可根据实际的家具结构进行编辑设置,并调整外形高宽比",
                "edittemplate");
    }
}
