package com.gongwu.wherecollect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.view.ObjectInfoEditView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGoodsOtherContentActivity extends BaseViewActivity {

    @Bind(R.id.goodsInfo_other_view)
    ObjectInfoEditView goodsInfoView;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    private ObjectBean tempBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods_other_content);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_goods_other_content_text));
        initData();
    }

    private void initData() {
        tempBean = (ObjectBean) getIntent().getSerializableExtra("tempBean");
        if (tempBean != null) {
            goodsInfoView.init(tempBean);
        }
    }

    @OnClick({R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit_btn:
                Intent intent = new Intent();
                intent.putExtra("tempBean", tempBean);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            tempBean = (ObjectBean) data.getSerializableExtra("bean");
            goodsInfoView.init(tempBean);
        }
    }
}
