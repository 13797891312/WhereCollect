package com.gongwu.wherecollect.furnitureEdit;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.FunitureTypeBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.ErrorView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.zhaojin.myviews.Loading;

import org.angmarch.views.NiceSpinner;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class FurnitureSysListActivity extends BaseViewActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.mListView)
    PullToRefreshListView mListView;
    @Bind(R.id.empty)
    ErrorView empty;
    @Bind(R.id.mTextView)
    TextView mTextView;
    @Bind(R.id.spinner)
    NiceSpinner spinner;
    @Bind(R.id.serch_btn)
    LinearLayout serchBtn;
    @Bind(R.id.edit_search)
    EditText editSearch;
    @Bind(R.id.cancel_btn)
    TextView cancelBtn;
    @Bind(R.id.seach_layout)
    LinearLayout seachLayout;
    @Bind(R.id.activity_furniture_sys_list)
    LinearLayout activityFurnitureSysList;
    private FurnitureSysListViewAdapter gridViewAdapter;
    private List<ObjectBean> mList = new ArrayList<>();
    private List<FunitureTypeBean> spinnerDatas = new ArrayList();
    private String code;
    private int spacePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_sys_list);
        ButterKnife.bind(this);
        code = getIntent().getStringExtra("code");
        spacePosition = getIntent().getIntExtra("position", 0);
        titleLayout.setTitle("添加家具");
        titleLayout.setBack(true, null);
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.textBtn.setText("自定义");
        titleLayout.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FurnitureSysListActivity.this, CreateFurenitureActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("position", spacePosition);
                startActivityForResult(intent, 23);
            }
        });
        initView();
        getTypeData();
        getData(true);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(editSearch.getText())) {
                        getData(true);
                    }
                    return false;
                }
                return false;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getData(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        gridViewAdapter = new FurnitureSysListViewAdapter(this, mList);
        mListView.setAdapter(gridViewAdapter);
        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mListView.setOnItemClickListener(this);
        //        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
        //            @Override
        //            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        //                page = 1;
        //                getData(false);
        //            }
        //
        //            @Override
        //            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        //                page++;
        //                getData(false);
        //            }
        //        });
    }

    @OnClick(R.id.mTextView)
    public void onClick() {
        Intent intent = new Intent(this, FurenitureAddActivity.class);
        startActivity(intent);
    }

    /**
     * 获取物品列表
     */
    public void getData(boolean isShowDialog) {
        if (MyApplication.getUser(this) == null)
            return;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("page", "1");
        if (!TextUtils.isEmpty(editSearch.getText())) {
            map.put("keyword", editSearch.getText().toString());
        }
        map.put("type", spinnerDatas.isEmpty() ? "" : spinnerDatas.get(spinner.getSelectedIndex()).get_id());
        PostListenner listenner = new PostListenner(this, isShowDialog ? Loading.show(null, this,
                "正在加载") : null) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                mList.clear();
                List temp = JsonUtils.listFromJsonWithSubKey(r.getResult(), ObjectBean.class, "items");
                SaveDate.getInstence(FurnitureSysListActivity.this).setObjectList(JsonUtils.jsonFromObject(temp));
                mList.addAll(temp);
                gridViewAdapter.notifyDataSetChanged();
                mListView.setEmptyView(empty);
            }
        };
        HttpClient.getSysfurniturelist(this, map, listenner);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        creatFurniture(mList.get(position - 1));
        MobclickAgent.onEvent(context, "060201");
    }

    /**
     * 创建家具
     */
    private void creatFurniture(ObjectBean furnitureBean) {
        List<Map> layers = new ArrayList<>();
        for (int i = 0; i < StringUtils.getListSize(furnitureBean.getLayers()); i++) {
            Map<String, Object> layer = new HashMap<>();
            layer.put("name", furnitureBean.getLayers().get(i).getName());
            layer.put("position", furnitureBean.getLayers().get(i).getPosition());
            layer.put("scale", furnitureBean.getLayers().get(i).getScale());
            layers.add(layer);
        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("location_code", code);
        map.put("name", furnitureBean.getName());
        ObjectBean.Point scale = new ObjectBean.Point();
        scale.setX(1);
        scale.setY(1);
        map.put("scale", JsonUtils.jsonFromObject(scale));
        map.put("position", JsonUtils.jsonFromObject(getPostion()));
        map.put("ratio", furnitureBean.getRatio() + "");
        map.put("background_url", furnitureBean.getBackground_url());
        map.put("image_url", furnitureBean.getImage_url());
        map.put("layers", JsonUtils.jsonFromObject(layers));
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                ObjectBean temp = JsonUtils.objectFromJson(r.getResult(), ObjectBean.class);
                EventBusMsg.AddFurnitureMsg msg = new EventBusMsg.AddFurnitureMsg();
                msg.objectBean = temp;
                EventBus.getDefault().post(msg);
                EventBus.getDefault().post(new EventBusMsg.EditLocationMsg(spacePosition));
                finish();
            }
        };
        HttpClient.createFurniture(this, map, listenner);
    }

    @OnClick({R.id.serch_btn, R.id.cancel_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.serch_btn:
                seachLayout.setVisibility(View.VISIBLE);
                serchBtn.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                editSearch.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, 0);
                break;
            case R.id.cancel_btn:
                spinner.setVisibility(View.VISIBLE);
                serchBtn.setVisibility(View.VISIBLE);
                seachLayout.setVisibility(View.GONE);
                editSearch.setText("");
                InputMethodManager inputManager1 = (InputMethodManager) this.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                inputManager1.toggleSoftInput(0, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
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
                        if (temp.getX() == j && temp.getY() == i) {
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

    private void getTypeData() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        PostListenner listenner = new PostListenner(this, Loading.show(null, this, "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                spinnerDatas.clear();
                FunitureTypeBean all = new FunitureTypeBean();
                all.set_id("");
                all.setName("全部");
                List<FunitureTypeBean> temp = JsonUtils.listFromJson(r.getResult(), FunitureTypeBean.class);
                spinnerDatas.add(all);
                spinnerDatas.addAll(temp);
                spinner.attachDataSource(spinnerDatas);
            }
        };
        HttpClient.getFurnitureType(this, map, listenner);
    }
}
