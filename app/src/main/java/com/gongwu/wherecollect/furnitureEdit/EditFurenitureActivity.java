package com.gongwu.wherecollect.furnitureEdit;
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
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.view.EditFurnitureImgDialog;
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
public class EditFurenitureActivity extends BaseViewActivity {
    @Bind(R.id.edit_name)
    EditText editName;
    @Bind(R.id.image_bg)
    ImageView imageBg;
    @Bind(R.id.image_custom)
    ImageView imageCustom;
    @Bind(R.id.commit)
    Button commit;
    File mFile1, mFile2;
    ObjectBean furnitureObject;
    @Bind(R.id.textView3)
    TextView textView3;
    private EditFurnitureImgDialog mSelectDialog;
    private int spacePosition;
    private Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_fureniture1);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        furnitureObject = (ObjectBean) getIntent().getSerializableExtra("object");
        spacePosition = getIntent().getIntExtra("position", 0);
        titleLayout.setTitle(furnitureObject.getName());
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        textView3.setText("编辑内部结构");
        commit.setText("确定");
        imageCustom.setImageResource(R.drawable.icon_template_placeholder_active);
        editName.setText(furnitureObject.getName());
        editName.setSelection(editName.getText().length());
        ImageLoader.load(EditFurenitureActivity.this, imageBg, furnitureObject.getImage_url());
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setBtnStatu();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setBtnStatu();
    }

    @OnClick({R.id.image_bg, R.id.image_custom, R.id.commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_bg:
                mSelectDialog = new EditFurnitureImgDialog(this, null, null) {
                    @Override
                    public void getResult(File file1, File file2) {
                        super.getResult(file1, file2);
                        ImageLoader.loadFromFile(EditFurenitureActivity.this, file1, imageBg);
                        mFile1 = file1;
                        mFile2 = file2;
                        mSelectDialog = null;
                    }
                };
                break;
            case R.id.image_custom:
                Intent intent = new Intent(this, EditTemplateActivity.class);
                intent.putExtra("object", furnitureObject);
                intent.putExtra("position", spacePosition);
                startActivityForResult(intent, 34);
                break;
            case R.id.commit:
                if (mFile1 == null) {
                    editFurniture(furnitureObject.getImage_url(), furnitureObject.getBackground_url());
                } else {
                    List<File> temp = new ArrayList<>();
                    temp.add(mFile1);
                    temp.add(mFile2);
                    loading=Loading.show(loading,this,"");
                    QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, temp, "furniture/custom/") {
                        @Override
                        protected void finish(List<String> list) {
                            super.finish(list);
                            editFurniture(list.get(0).endsWith(mFile1.getName()) ? list.get(0) : list.get(1), list
                                    .get(0)
                                    .endsWith(mFile1.getName()) ? list.get(1) : list.get(0));
                        }
                    };
                    uploadUtil.start();
                }
                break;
        }
    }

    /**
     * 编辑家具
     */
    private void editFurniture(final String url1, final String url2) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("code", furnitureObject.getCode());
        map.put("name", editName.getText().toString());
        map.put("ratio", furnitureObject.getRatio() + "");
        map.put("background_url", url2);
        map.put("image_url", url1);
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(new EventBusMsg.EditLocationMsg(spacePosition));
                EventBus.getDefault().post(new EventBusMsg.editFurnitureMsg(editName.getText().toString(),
                        furnitureObject.getRatio(), url1, url2));
                finish();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                if(loading!=null){
                    loading.dismiss();
                }
            }
        };
        HttpClient.updataFurniture(this, map, listenner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSelectDialog != null) {
            mSelectDialog.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 34 && resultCode == RESULT_OK) {
            furnitureObject.getLayers().clear();
            furnitureObject.getLayers().addAll((ArrayList) data.getSerializableExtra("result"));
            furnitureObject.setRatio(data.getFloatExtra("shape", CustomTableRowLayout.shape_width));
            imageCustom.setImageResource(R.drawable.icon_template_placeholder_active);
        }
        setBtnStatu();
    }

    private void setBtnStatu() {
        if (TextUtils.isEmpty(editName.getText())) {
            commit.setEnabled(false);
        } else {
            commit.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.editFurnitureMsg msg) {
        furnitureObject.setName(msg.name);
        furnitureObject.setRatio(msg.shape);
        furnitureObject.setImage_url(msg.imageUrl);
        furnitureObject.setBackground_url(msg.background_url);
        if (msg.layers != null) {
            furnitureObject.setLayers(msg.layers);
        }
    }
}
