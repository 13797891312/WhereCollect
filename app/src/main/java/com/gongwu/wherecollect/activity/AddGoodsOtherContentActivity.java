package com.gongwu.wherecollect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.object.AddGoodsActivity;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.view.ObjectInfoEditView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置其他属性
 */
public class AddGoodsOtherContentActivity extends BaseViewActivity {

    @Bind(R.id.goodsInfo_other_view)
    ObjectInfoEditView goodsInfoView;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    private ObjectBean tempBean;
    private String startType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods_other_content);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_goods_other_content_text));
        EventBus.getDefault().register(this);
        initData();
        initEvent();
    }

    private void initData() {
        tempBean = (ObjectBean) getIntent().getSerializableExtra("tempBean");
        startType = getIntent().getStringExtra("type");
        if (tempBean != null) {
            goodsInfoView.init(tempBean);
        }
        if (AddGoodsActivity.MORE_TYPE.equals(startType)) {
            commitBtn.setText("跳过");
            titleLayout.setTitle(getResources().getString(R.string.add_goods_other_content_text_2));
            goodsInfoView.hintMoreGoodsLayout();
        }
    }


    private void initEvent() {
        goodsInfoView.setChangeListener(new ObjectInfoEditView.ChangeListener() {
            @Override
            public void change() {
                setViewText();
            }
        });

    }

    @OnClick({R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit_btn:
                if (AddGoodsActivity.MORE_TYPE.equals(startType)) {
                    AddMoreGoodsActivity.start(context, tempBean);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("tempBean", tempBean);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            tempBean = (ObjectBean) data.getSerializableExtra("bean");
            goodsInfoView.init(tempBean);
            if (!tempBean.isEmpty()) {
                setViewText();
            }
        }
    }

    private void setViewText() {
        if (AddGoodsActivity.MORE_TYPE.equals(startType)) {
            commitBtn.setText("下一步");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        if (EventBusMsg.ACTIVITY_FINISH.contains(str)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
