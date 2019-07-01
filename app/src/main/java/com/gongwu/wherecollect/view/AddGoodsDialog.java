package com.gongwu.wherecollect.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    @Bind(R.id.submit_tv)
    TextView submit_tv;

    private Context context;
    private ObjectBean bean;
    SelectImgDialog selectImgDialog;
    private final int imgMax = 1;
    private final int COCLOR_COUNT = 10;
    private int goodsCount;
    private int goodsMaxCount = 10;
    /**
     * 图片原文件 dialog dismiss的时候就会被初始化
     */
    private File imgOldFile;

    public AddGoodsDialog(Context context, int goodsCount) {
        super(context);
        this.context = context;
        this.goodsCount = goodsCount;
    }

    /**
     * 设置数据
     *
     * @param bean
     */
    public void setObjectBean(ObjectBean bean) {
        if (bean != null) {
            this.bean = bean;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            });
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
            } else if (bean.getObject_url().contains("#")) {
                //给了默认色
                addGoodsIv.setImageDrawable(context.getResources().getDrawable(R.drawable.select_pic));
            } else {
                File file = new File(bean.getObject_url());
                ImageLoader.loadFromFile(context, file, addGoodsIv);
                imgOldFile = file;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_goods_layout);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);
        if (goodsCount >= goodsMaxCount) {
            submit_tv.setTextColor(context.getResources().getColor(R.color.black_other_one));
        } else {
            submit_tv.setTextColor(context.getResources().getColor(R.color.maincolor));
        }
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
                if (imgOldFile != null) {
                    imgOldFile = null;
                }
                cancel();
                dismiss();
                break;
            case R.id.submit_tv:
                if (goodsCount >= goodsMaxCount) {
                    ToastUtil.show(context, "一次最多添加10个物品", Toast.LENGTH_SHORT);
                    return;
                }
                //确定
                if (!TextUtils.isEmpty(goodsNameEdit.getText().toString().trim())) {
                    bean.setName(goodsNameEdit.getText().toString().trim());
                }
                if (TextUtils.isEmpty(bean.getObjectUrl()) && TextUtils.isEmpty(goodsNameEdit.getText().toString().trim())) {
                    ToastUtil.show(context, "图片或名称至少选填一项", Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(bean.getObjectUrl())) {
                    // 随机颜色
                    Random random = new Random();
                    int randomcolor = random.nextInt(COCLOR_COUNT);
                    bean.setObject_url(StringUtils.getResCode(randomcolor));
                }
                result(bean);
                //初始化imgOldFile
                imgOldFile = null;
                dismiss();
                break;
        }
    }

    /**
     * 图片选择
     */
    private void showSelectDialog() {
        //bean url没数据的时候，编辑选择肯定是隐藏的
        if (TextUtils.isEmpty(bean.getObject_url())) {
            imgOldFile = null;
        }
        selectImgDialog = new SelectImgDialog((Activity) context, null, imgMax, imgOldFile) {
            @Override
            public void getResult(List<File> list) {
                super.getResult(list);
                if (list.size() == 1) {
                    imgOldFile = list.get(0);
                    selectImgDialog.cropBitmap(imgOldFile);
                } else {
                    setDataUrl(list);
                }
            }

            @Override
            protected void resultFile(File file) {
                super.resultFile(file);
                ImageLoader.loadFromFile(context, file, addGoodsIv);
                bean.setObject_url(file.getPath());
            }
        };
        selectImgDialog.hintLayout();
        //编辑选择是否隐藏的 根据imgOldFile来判断
        selectImgDialog.showEditIV(imgOldFile == null ? View.GONE : View.VISIBLE);
    }


    private void setDataUrl(List<File> fileList) {
        List<ObjectBean> beans = new ArrayList<>();
        for (File file : fileList) {
            ObjectBean bean = new ObjectBean();
            bean.setObject_url(file.getPath());
            beans.add(bean);
        }
        results(beans);
        dismiss();
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

    public void results(List<ObjectBean> beans) {

    }

    public void cancel() {

    }

    public void scanCode() {

    }

}
