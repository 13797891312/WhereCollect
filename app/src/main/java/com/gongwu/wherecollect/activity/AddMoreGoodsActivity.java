package com.gongwu.wherecollect.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.volley.request.QiNiuUploadUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.AddMoreGoodsListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BaseBean;
import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.AddGoodsDialog;
import com.zhaojin.myviews.Loading;
import com.zsitech.oncon.barcode.core.CaptureActivity;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 批量添加
 */
public class AddMoreGoodsActivity extends BaseViewActivity {

    private final int BOOK_CODE = 0x132;

    @Bind(R.id.textBtn)
    TextView addGoodsTv;
    @Bind(R.id.more_goods_list_view)
    ListView mListView;
    @Bind(R.id.more_commit_btn)
    Button commitBtn;
    private ObjectBean tempBean;
    private AddGoodsDialog mDialog;
    private List<ObjectBean> mDatas;
    private AddMoreGoodsListAdapter mAdapter;
    /**
     * 记录点击的item
     */
    private int currentItem = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_goods);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle(getResources().getString(R.string.add_more_goods_text));
        mDatas = new ArrayList<>();
        initView();
        initData();
        initEvent();
        startDialog();
    }

    private void initView() {
        addGoodsTv.setText("添加");
        addGoodsTv.setVisibility(View.VISIBLE);
        mAdapter = new AddMoreGoodsListAdapter(context, mDatas);
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        tempBean = (ObjectBean) getIntent().getSerializableExtra("bean");
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentItem = position;
                mDialog.setObjectBean(mDatas.get(position));
                mDialog.show();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
    }

    @OnClick({R.id.textBtn, R.id.more_commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textBtn:
                startDialog();
                break;
            case R.id.more_commit_btn:
                if (mDatas.size() > 0) {
                    initImgUrls();
                } else {
                    Toast.makeText(context, "请先添加物品", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initImgUrls() {
        List<String> name = new ArrayList<>();
        List<String> files = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            ObjectBean bean = mDatas.get(i);
            name.add(bean.getName());
            files.add(TextUtils.isEmpty(bean.getObject_url()) ? getResId(i) + "" : bean.getObject_url());
        }
        addObjects(name, files);
    }

    /**
     * 添加物品
     */
    private void addObjects(List<String> name, List<String> files) {
        Map<String, String> map = new TreeMap<>();
        map.put("name", JsonUtils.jsonFromObject(name));
        map.put("image_urls", JsonUtils.jsonFromObject(files));
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("user_id", MyApplication.getUser(this).getId());
        map.put("ISBN", ISBN);
        map.put("category_codes", StringUtils.isEmpty(tempBean.getCategories()) ? "" : tempBean.getCategories().get
                (tempBean.getCategories().size() - 1).getCode());
        map.put("channel", TextUtils.isEmpty(tempBean.getChannel()) ? "" : JsonUtils.jsonFromObject(tempBean
                .getChannel().split(">")));
        map.put("color", TextUtils.isEmpty(tempBean.getColor()) ? "" : JsonUtils.jsonFromObject(tempBean
                .getColor().split("、")));
        map.put("detail", TextUtils.isEmpty(tempBean.getDetail()) ? "" : tempBean.getDetail());
        map.put("price_max", tempBean.getPrice() + "");
        map.put("price_min", tempBean.getPrice() + "");
        map.put("season", tempBean.getSeason());
        map.put("star", tempBean.getStar() + "");
        map.put("count", tempBean.getObject_count() + "");
        PostListenner listenner = new PostListenner(this, Loading.show(null, context,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                Intent intent = new Intent(context, ImportSelectFurnitureActivity.class);
                startActivity(intent);
                finish();
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                EventBus.getDefault().post(EventBusMsg.ACTIVITY_FINISH);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.addMoreGoods(this, map, listenner);
    }

    /**
     * 添加物品的dialog
     */
    private void startDialog() {
        //添加
        mDialog = new AddGoodsDialog(context) {
            @Override
            public void result(ObjectBean bean) {
                //上传
                if (!TextUtils.isEmpty(bean.getObject_url()) && !bean.getObject_url().contains("7xroa4")) {
                    upLoadImg(bean);
                    return;
                }
                //currentItem不为默认值时，修改记录的item的值
                if (currentItem != -1) {
                    mDatas.set(currentItem, bean);
                    currentItem = -1;
                } else {
                    //为默认值，就是新添加的
                    mDatas.add(bean);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void results(List<ObjectBean> beans) {
                upLoadImgs(beans);
            }

            @Override
            public void cancel() {
                currentItem = -1;
            }

            @Override
            public void scanCode() {
                qrBook();
            }
        };
        mDialog.setObjectBean(null);
        mDialog.show();
    }

    /**
     * 上传图片
     */
    private void upLoadImg(final ObjectBean objectBean) {
        List<File> list = new ArrayList<>();
        list.add(new File(objectBean.getObject_url()));
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, list, "object/image/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                //currentItem不为默认值时，修改记录的item的值
                objectBean.setObject_url(list.get(0));
                if (currentItem != -1) {
                    mDatas.set(currentItem, objectBean);
                    currentItem = -1;
                } else {
                    //为默认值，就是新添加的
                    mDatas.add(objectBean);
                }
                mAdapter.notifyDataSetChanged();
            }
        };
        uploadUtil.start();
    }

    /**
     * 上传图片
     */
    private Loading loading;

    private void upLoadImgs(final List<ObjectBean> objectBeans) {
        List<File> list = new ArrayList<>();
        for (ObjectBean bean : objectBeans) {
            list.add(new File(bean.getObject_url()));
        }
        loading = Loading.show(loading, this, "上传中...");
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, list, "object/image/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                for (int i = 0; i < list.size(); i++) {
                    objectBeans.get(i).setObject_url(list.get(i));
                }
                mDatas.addAll(objectBeans);
                mAdapter.notifyDataSetChanged();
                if (loading != null) {
                    loading.dismiss();
                }
            }
        };
        uploadUtil.start();
    }

    /**
     * 图书扫描
     */
    private void qrBook() {
        startActivityForResult(new Intent(context, CaptureActivity.class), BOOK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mDialog != null) {
            mDialog.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == BOOK_CODE && resultCode == CaptureActivity.result) {//扫描的到结果
            getBookInfo(data.getStringExtra("result"));
        }
    }

    /**
     * 获取书本信息
     *
     * @param qrStr
     */
    private void getBookInfo(String qrStr) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("ISBN", qrStr);
        PostListenner listenner = new PostListenner(context, Loading.show(null, context, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                final BookBean book = JsonUtils.objectFromJson(r.getResult(), BookBean.class);
                new Thread(new Runnable() {//下载图片
                    @Override
                    public void run() {
                        try {
                            File file = Glide.with(context).load(book.getPic()).downloadOnly(500, 500).get();
                            String newPath = MyApplication.CACHEPATH + System.currentTimeMillis() + ".jpg";
                            FileUtil.copyFile(file, newPath);
                            file = new File(newPath);
                            book.setImageFile(file);
                            AddMoreGoodsActivity.this.runOnUiThread(new Runnable() {//回主线程
                                @Override
                                public void run() {
                                    DialogUtil.show("提醒", "此操作会将部分共同属性刷新，是否继续?", "继续", "取消", (Activity) context, new
                                            DialogInterface.OnClickListener
                                                    () {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    updateBeanWithBook(book);
                                                }
                                            }, null);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
        HttpClient.getBookInfo(context, map, listenner);
    }

    /**
     * 图书扫描后修改公共属性
     */
    String ISBN = "";

    private void updateBeanWithBook(BookBean book) {
        if (book == null || book.getImageFile() == null) {
            return;
        }
        ISBN = book.getIsbnCode();
        ObjectBean tempBean = new ObjectBean();
        if (book.getImageFile() != null) {
            tempBean.setObject_url(book.getImageFile().getAbsolutePath());
        }
        if (!TextUtils.isEmpty(book.getTitle())) {
            tempBean.setName(book.getTitle());
        }
        if (!TextUtils.isEmpty(book.getSummary())) {
            tempBean.setDetail(book.getSummary());
        }
        if (!TextUtils.isEmpty(book.getChannel())) {
            tempBean.setChannel(book.getChannel());
        }
        if (!TextUtils.isEmpty(book.getPrice())) {
            tempBean.setPrice(book.getPrice());
        }
        if (book.getCategory() != null) {
            List<BaseBean> temp = new ArrayList<>();
            temp.add(book.getCategory());
            tempBean.setCategories(temp);
        }
        mDialog.setObjectBean(tempBean);
        mDialog.show();
    }

    public static void start(Context context, ObjectBean objectBean) {
        Intent intent = new Intent(context, AddMoreGoodsActivity.class);
        if (objectBean != null) {
            intent.putExtra("bean", objectBean);
        }
        context.startActivity(intent);
    }

    public String getResId(int position) {
        int i;
        if (position > 9) {
            i = position % 10;
        } else {
            i = position;
        }
        switch (i) {
            case 0:
                return "#B5B5B5";
            case 1:
                return "#9076F2";
            case 2:
                return "#F19EC2";
            case 3:
                return "#13B5B1";
            case 4:
                return "#E66868";
            case 5:
                return "#F29B76";
            case 6:
                return "#AFC4D5";
            case 7:
                return "#32B16C";
            case 8:
                return "#13B5B1";
            case 9:
                return "#7ECEF4";
            default:
                return "#35BFBB";
        }
    }

}
