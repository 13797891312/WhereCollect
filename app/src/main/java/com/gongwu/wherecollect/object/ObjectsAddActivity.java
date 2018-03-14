package com.gongwu.wherecollect.object;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.volley.request.QiNiuUploadUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.NoFastClickUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.AddPhoteGridLayout;
import com.gongwu.wherecollect.view.ObjectInfoEditView;
import com.gongwu.wherecollect.view.SelectImgDialog;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
public class ObjectsAddActivity extends BaseViewActivity {
    @Bind(R.id.camare_iv)
    ImageView camareIv;
    @Bind(R.id.GridLayout)
    AddPhoteGridLayout gridLayout;
    @Bind(R.id.camare_layout)
    RelativeLayout camareLayout;
    @Bind(R.id.name_tv)
    EditText nameTv;
    @Bind(R.id.goodsInfo_view)
    ObjectInfoEditView goodsInfoView;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    String ISBN = "";
    Loading loading;
    private SelectImgDialog selectImgDialog;
    private ObjectBean tempBean = new ObjectBean();
    private int type;//1为导入物品列表跳过来的，添加完了要返回去；

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_add);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("添加物品");
        gridLayout.init();
        goodsInfoView.init(tempBean);
        type = getIntent().getIntExtra("type", 0);
        EventBus.getDefault().register(this);
        nameTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tempBean.setName(nameTv.getText().toString());
            }
        });
        showHelp();
        if (MaterialShowcaseView.showcaseView == null) {
            showSelectDialog();
        }
    }

    @OnClick({R.id.camare_iv, R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camare_iv:
                //图片选择
                showSelectDialog();
                break;
            case R.id.commit_btn:
                if (NoFastClickUtils.isFastClick())
                    return;
                if (StringUtils.isEmpty(gridLayout.mlist)) {
                    ToastUtil.show(this, "请先添加物品图片", Toast.LENGTH_LONG);
                    return;
                }
                loading = Loading.show(loading, this, "");
                upLoadImg(gridLayout.mlist);
                break;
        }
    }

    private void showSelectDialog() {
        selectImgDialog = new SelectImgDialog(this, null, 10 - gridLayout.mlist.size()) {
            @Override
            public void getResult(List<File> list) {
                super.getResult(list);
                for (int i = 0; i < StringUtils.getListSize(list); i++) {
                    gridLayout.addItem(list.get(i));
                }
                camareLayout.setVisibility(View.GONE);
                gridLayout.setVisibility(View.VISIBLE);
                selectImgDialog = null;
            }

            @Override
            protected void getBookResult(BookBean book) {
                super.getBookResult(book);
                selectImgDialog = null;
                updateBeanWithBook(book);
            }
        };
    }

    /**
     * 图书扫描后修改公共属性
     */
    public void updateBeanWithBook(BookBean book) {
        if (book == null || book.getImageFile() == null) {
            return;
        }
        ISBN = book.getIsbnCode();
        if (book.getImageFile() != null) {
            gridLayout.addItem(book.getImageFile());
            camareLayout.setVisibility(View.GONE);
            gridLayout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(book.getTitle())) {
            nameTv.setText(book.getTitle());
        }
        if (!TextUtils.isEmpty(book.getSummary())) {
            tempBean.setDetail(book.getSummary());
        }
        if (!TextUtils.isEmpty(book.getChannel())) {
            tempBean.setChannel(book.getChannel());
        }
//        if (book.getPrice() != 0) {
//            tempBean.setPrice_max((int) book.getPrice());
//        }
        if (book.getCategory() != null) {
            List<BaseBean> temp = new ArrayList<>();
            temp.add(book.getCategory());
            tempBean.setCategories(temp);
        }
        goodsInfoView.init(tempBean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gridLayout.onActivityResult(requestCode, resultCode, data);
        if (selectImgDialog != null) {
            selectImgDialog.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == 100) {
            tempBean = (ObjectBean) data.getSerializableExtra("bean");
            goodsInfoView.init(tempBean);
        }
    }

    /**
     * 上传图片
     */
    private void upLoadImg(List<File> list) {
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, list, "object/image/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                addObjects(list);
            }
        };
        uploadUtil.start();
    }

    /**
     * 添加物品
     */
    private void addObjects(List<String> list) {
        if (StringUtils.isEmpty(list)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("ISBN", ISBN);
        map.put("category_codes", StringUtils.isEmpty(tempBean.getCategories()) ? "" : tempBean.getCategories().get
                (tempBean.getCategories().size() - 1).getCode());
        map.put("channel", TextUtils.isEmpty(tempBean.getChannel()) ? "" : JsonUtils.jsonFromObject(tempBean
                .getChannel().split(">")));
        map.put("color", TextUtils.isEmpty(tempBean.getColor()) ? "" : JsonUtils.jsonFromObject(tempBean
                .getColor().split("、")));
        map.put("detail", TextUtils.isEmpty(tempBean.getDetail()) ? "" : tempBean.getDetail());
        map.put("name", nameTv.getText().toString());
        map.put("price_max", tempBean.getPrice_max() + "");
        map.put("price_min", tempBean.getPrice_min() + "");
        map.put("season", tempBean.getSeason());
        map.put("star", tempBean.getStar() + "");
        map.put("image_urls", sb.toString());
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                if (type == 0) {
                    Intent intent = new Intent(ObjectsAddActivity.this, ImportSelectFurnitureActivity.class);
                    startActivity(intent);
                } else {
                    setResult(RESULT_OK);
                }
                finish();
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                if (loading != null) {
                    loading.dismiss();
                }
            }
        };
        HttpClient.addObjects(this, map, listenner);
    }

    /**
     * 首次引导
     */
    private void showHelp() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setContentTextColor(getResources().getColor(R.color.white));
        config.setMaskColor(getResources().getColor(R.color.black_87));
        config.setDelay(200); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "addObject");
        sequence.setConfig(config);
        MaterialShowcaseView sequenceItem1 = (new MaterialShowcaseView.Builder(this)).setTarget(camareLayout)
                .setContentText("至少要添加物品图片,其他细化的属性可利用碎片化时间分步添加")
                .setTargetTouchable(false)
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTargetTouch(true)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem1);
        MaterialShowcaseView sequenceItem2 = (new MaterialShowcaseView.Builder(this))
                .setTarget(goodsInfoView)
                .setContentText("每个属性对应物品查看页的筛选项,可在查看时快速筛选找寻")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setDelay(200)
                .setShapePadding(0)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem2);
        MaterialShowcaseView sequenceItem3 = (new MaterialShowcaseView.Builder(this))
                .setTarget(nameTv).setContentText("也可添加物品关键词描述,以便搜索找寻")
                .setTargetTouchable(false)
                .setMaskColour(getResources().getColor(R.color.black_70))
                .setDismissOnTouch(true)
                .setShapePadding(0)
                .setDelay(200)
                .setDismissOnTargetTouch(false)
                .withRectangleShape(false).build();
        sequence.addSequenceItem(sequenceItem3);
        sequence.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.ImportFromBugSucces msg) {
        updateBeanWithBook(msg.bean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
