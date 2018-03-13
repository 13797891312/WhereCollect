package com.gongwu.wherecollect.furnitureEdit;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Function: 系统没有想要的时候点击添加
 * Date: 2016-08-20
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class FurenitureAddActivity extends BaseViewActivity {
    @Bind(R.id.btn1)
    Button btn1;
    @Bind(R.id.btn2)
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fureniture_add);
        ButterKnife.bind(this);
        titleLayout.setTitle("添加家具");
        titleLayout.setBack(true, null);
    }

    @OnClick({R.id.btn1, R.id.btn2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                break;
            case R.id.btn2:
                MyFurnitureDialog dialog = new MyFurnitureDialog(this);
                dialog.show();
                break;
        }
    }
}
