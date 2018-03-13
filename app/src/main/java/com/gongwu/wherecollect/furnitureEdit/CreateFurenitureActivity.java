package com.gongwu.wherecollect.furnitureEdit;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.view.EditFurnitureImgDialog;
import com.umeng.analytics.MobclickAgent;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 自定义家具
 */
public class CreateFurenitureActivity extends BaseViewActivity {
    public int spacePosition;//哪个空间桌布
    @Bind(R.id.edit_name)
    EditText editName;
    @Bind(R.id.image_bg)
    ImageView imageBg;
    @Bind(R.id.image_custom)
    ImageView imageCustom;
    @Bind(R.id.commit)
    Button commit;
    File mFile1;
    File mFile2;
    Loading loading;
    private EditFurnitureImgDialog selectDialog;
    private List<ObjectBean> mlist = new ArrayList<>();
    private float shape = CustomTableRowLayout.shape_width;
    private String code;

    /**
     * 获取图片的宽高
     *
     * @param path
     * @return
     */
    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        return new int[]{options.outWidth, options.outHeight};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_fureniture1);
        ButterKnife.bind(this);
        titleLayout.setTitle("添加家具");
        titleLayout.setBack(true, null);
        code = getIntent().getStringExtra("code");
        spacePosition = getIntent().getIntExtra("position", 0);
        initView();
    }

    private void initView() {
        if (mlist.isEmpty()) {
            imageCustom.setImageResource(R.drawable.icon_templatate_placeholder);
        } else {
            imageCustom.setImageResource(R.drawable.icon_template_placeholder_active);
        }
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
                selectDialog = new EditFurnitureImgDialog(this, null, null) {
                    @Override
                    public void getResult(File file, File file2) {
                        super.getResult(file, file2);
                        ImageLoader.loadFromFile(CreateFurenitureActivity.this, file2, imageBg);
                        mFile1 = file;
                        mFile2 = file2;
                        selectDialog = null;
                    }
                };
                break;
            case R.id.image_custom:
                Intent intent = new Intent(this, CreateTemplateActivity.class);
                if (!mlist.isEmpty()) {
                    intent.putExtra("list", (ArrayList) mlist);
                    intent.putExtra("shape", shape);
                }
                startActivityForResult(intent, 34);
                break;
            case R.id.commit:
                List<File> temp = new ArrayList<>();
                temp.add(mFile1);
                temp.add(mFile2);
                loading = Loading.show(loading, this, "");
                QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, temp, "furniture/custom/") {
                    @Override
                    protected void finish(List<String> list) {
                        super.finish(list);
                        creatFurniture(list.get(0).endsWith(mFile1.getName()) ? list.get(0) : list.get(1), list.get(0)
                                .endsWith(mFile1.getName()) ? list.get(1) : list.get(0));
                    }
                };
                uploadUtil.start();
                MobclickAgent.onEvent(this, "060202");
                break;
        }
    }

    /**
     * 创建家具
     */
    private void creatFurniture(String url, String url2) {
        List<Map> layers = new ArrayList<>();
        for (int i = 0; i < mlist.size(); i++) {
            Map<String, Object> layer = new HashMap<>();
            layer.put("name", mlist.get(i).getName());
            layer.put("position", mlist.get(i).getPosition());
            layer.put("scale", mlist.get(i).getScale());
            layers.add(layer);
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("location_code", code);
        map.put("name", editName.getText().toString());
        ObjectBean.Point scale = new ObjectBean.Point();
        int sizes[] = getImageWidthHeight(mFile1.getPath());
        float width=sizes[0];
        float height=sizes[1];
               // +15*BaseViewActivity.getScreenScale(this);
        if(sizes[1]>width){
            scale.setX(1);
            scale.setY((height-15*BaseViewActivity.getScreenScale(this))/ width);
        }else{
            scale.setY(1);
            scale.setX(width / (height+15*BaseViewActivity.getScreenScale(this)));
        }
        map.put("scale", JsonUtils.jsonFromObject(scale));
        map.put("position", JsonUtils.jsonFromObject(getPostion()));
        map.put("ratio", shape + "");
        map.put("background_url", url2);
        map.put("image_url", url);
        map.put("layers", JsonUtils.jsonFromObject(layers));
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                ObjectBean temp = JsonUtils.objectFromJson(r.getResult(), ObjectBean.class);
                EventBusMsg.AddFurnitureMsg msg = new EventBusMsg.AddFurnitureMsg();
                msg.objectBean = temp;
                EventBus.getDefault().post(msg);
                EventBus.getDefault().post(new EventBusMsg.EditLocationMsg(spacePosition));
                setResult(RESULT_OK, new Intent());
                finish();
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                if (loading != null) {
                    loading.dismiss();
                }
            }
        };
        HttpClient.createFurniture(this, map, listenner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (selectDialog != null) {
            selectDialog.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 34 && resultCode == RESULT_OK) {
            mlist.clear();
            mlist.addAll((ArrayList) data.getSerializableExtra("result"));
            shape = data.getFloatExtra("shape", CustomTableRowLayout.shape_width);
            imageCustom.setImageResource(R.drawable.icon_template_placeholder_active);
        }
        setBtnStatu();
    }

    private void setBtnStatu() {
        if (TextUtils.isEmpty(editName.getText()) || mlist.isEmpty() || mFile1 == null) {
            commit.setEnabled(false);
        } else {
            commit.setEnabled(true);
        }
    }

    /**
     * 获取最好的添加坐标
     *
     * @return
     */
    private ObjectBean.Point getPostion() {
        ObjectBean.Point point = new ObjectBean.Point();
        try {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    boolean isHas = false;
                    for (int k = 0; k < LocationEditActivity.addPage.mlist.size(); k++) {
                        ObjectBean.Point temp = LocationEditActivity.addPage.mlist.get(k).getPosition();
                        if ((temp.getX() > j - 0.5f && temp.getX() < j + 0.5f) && (temp.getY() > i - 0.5 && temp.getY
                                () < i + 0.5)) {
                            isHas = true;
                            break;
                        }
                    }
                    if (!isHas) {
                        point.setX(j);
                        point.setY(i);
                        return point;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return point;
    }
}
