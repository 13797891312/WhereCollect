package com.gongwu.wherecollect.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.ToastUtil;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mucll on 2018/3/15.
 */

public class AddGoodsDialog extends Dialog {

    @Bind(R.id.add_goods_iv)
    ImageView addGoodsIv;
    @Bind(R.id.goods_name_et)
    EditText goodsNameEdit;

    private Context context;
    private ObjectBean bean;

    public AddGoodsDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 设置数据
     *
     * @param bean
     */
    public void setObjectBean(ObjectBean bean) {
        if (bean != null) {
            this.bean = bean;
            initData();
        } else {
            this.bean = new ObjectBean();
        }
    }

    private void initData() {
        if (!TextUtils.isEmpty(bean.getName())) {
            goodsNameEdit.setText(bean.getName());
            goodsNameEdit.setSelection(bean.getName().length());
        }
        if (!TextUtils.isEmpty(bean.getObject_url())) {
            if (bean.getObject_url().contains("http")) {
                ImageLoader.load(context, addGoodsIv, bean.getObject_url());
            } else {
                File file = new File(bean.getObject_url());
                ImageLoader.loadFromFile(context, file, addGoodsIv);
                imgOldFile = file;
            }
        } else {
            addGoodsIv.setImageDrawable(context.getResources().getDrawable(R.drawable.select_pic));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_goods_layout);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.add_goods_iv, R.id.code_layout, R.id.cancel_tv, R.id.submit_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_goods_iv:
                showSelectDialog();
                break;
            case R.id.code_layout:
                scanCode();
                break;
            case R.id.cancel_tv:
                cancel();
                dismiss();
                break;
            case R.id.submit_tv:
                if (!TextUtils.isEmpty(goodsNameEdit.getText().toString().trim())) {
                    bean.setName(goodsNameEdit.getText().toString().trim());
                }
                if (TextUtils.isEmpty(bean.getObject_url()) && TextUtils.isEmpty(bean.getName())) {
                    ToastUtil.show(context, "图片或名称至少选填一项", Toast.LENGTH_SHORT);
                    return;
                }
                result(bean);
                dismiss();
                break;
        }
    }

    SelectImgDialog selectImgDialog;
    private final int imgMax = 1;
    private File imgOldFile;

    /**
     * 图片选择
     */
    private void showSelectDialog() {
        selectImgDialog = new SelectImgDialog((Activity) context, null, imgMax, imgOldFile) {
            @Override
            public void getResult(List<File> list) {
                super.getResult(list);
                imgOldFile = list.get(0);
                selectImgDialog.cropBitmap(imgOldFile);
            }

            @Override
            protected void resultFile(File file) {
                super.resultFile(file);
                ImageLoader.loadFromFile(context, file, addGoodsIv);
                bean.setObject_url(file.getPath());
            }
        };
        selectImgDialog.hintLayout();
        selectImgDialog.showEditIV(imgOldFile == null ? View.GONE : View.VISIBLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (selectImgDialog != null) {
            selectImgDialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void show() {
        super.show();
    }

    public void result(ObjectBean bean) {

    }

    public void cancel() {

    }

    public void scanCode() {

    }
}
