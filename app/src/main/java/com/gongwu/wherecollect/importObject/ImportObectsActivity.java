package com.gongwu.wherecollect.importObject;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.activity.LoginActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.object.AddGoodsActivity;
import com.gongwu.wherecollect.object.ObjectsAddActivity;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.EbagGridView;
import com.gongwu.wherecollect.view.ErrorView;
import com.zhaojin.myviews.Loading;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 导入物品
 */
public class ImportObectsActivity extends BaseViewActivity {
    @Bind(R.id.textview1)
    TextView textview1;
    @Bind(R.id.gridview1)
    EbagGridView gridview1;
    @Bind(R.id.textview2)
    TextView textview2;
    @Bind(R.id.gridview2)
    EbagGridView gridview2;
    ImportGridAdapter adapter1, adapter2;
    List<ObjectBean> mlist1 = new ArrayList<>();
    List<ObjectBean> mlist2 = new ArrayList<>();
    Map<String, ObjectBean> chooseMap = new HashMap<>();
    @Bind(R.id.empty)
    ErrorView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_obects);
        ButterKnife.bind(this);
        titleLayout.setTitle("归入物品");
        titleLayout.setBack(true, null);
        titleLayout.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", (Serializable) chooseMap);
                setResult(100, intent);
                finish();
            }
        });
        initView();
        getData();
    }

    private void initView() {
        adapter1 = new ImportGridAdapter(this, mlist1, chooseMap) {
            @Override
            protected void change() {
                super.change();
                changeBtn();
            }
        };
        gridview1.setAdapter(adapter1);
        adapter2 = new ImportGridAdapter(this, mlist2, chooseMap) {
            @Override
            protected void change() {
                super.change();
                changeBtn();
            }
        };
        gridview2.setAdapter(adapter2);
    }

    private void changeBtn() {
        if (chooseMap.isEmpty()) {
            titleLayout.textBtn.setVisibility(View.GONE);
        } else {
            titleLayout.textBtn.setVisibility(View.VISIBLE);
            titleLayout.textBtn.setText("确定");
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("user_id", MyApplication.getUser(this).getId());
        map.put("page", "1");
        PostListenner listenner = new PostListenner(this, Loading.show(null, this,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List<ObjectBean> temp = JsonUtils.listFromJsonWithSubKey(r.getResult().replaceAll("\"channel\"",
                        "\"channels\"")
                        .replaceAll
                                ("\"color\"", "\"colors\""), ObjectBean
                        .class, "items");
                splitList(temp);
            }
        };
        HttpClient.getNoLocationGoodsList(this, map, listenner);
    }

    private void splitList(List<ObjectBean> temp) {
        String firstTime = "";
        mlist1.clear();
        mlist2.clear();
        Collections.sort(temp, new Comparator<ObjectBean>() {
            @Override
            public int compare(ObjectBean o1, ObjectBean o2) {
                return o2.getUpdated_at().compareTo(o1.getUpdated_at());
            }
        });
        for (int i = 0; i < StringUtils.getListSize(temp); i++) {
            if (i == 0) {
                firstTime = temp.get(i).getUpdated_at();
                mlist1.add(temp.get(i));
            } else {
                if (firstTime.equals(temp.get(i).getUpdated_at())) {
                    mlist1.add(temp.get(i));
                } else {
                    mlist2.add(temp.get(i));
                }
            }
        }
        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
        setEmpty();
    }

    /**
     * 空数据
     */
    private void setEmpty() {
        if (mlist1.isEmpty()) {
            textview1.setVisibility(View.GONE);
            gridview1.setVisibility(View.GONE);
        } else {
            textview1.setVisibility(View.VISIBLE);
            gridview1.setVisibility(View.VISIBLE);
        }
        if (mlist2.isEmpty()) {
            textview2.setVisibility(View.GONE);
            gridview2.setVisibility(View.GONE);
        } else {
            textview2.setVisibility(View.VISIBLE);
            gridview2.setVisibility(View.VISIBLE);
        }
        if (mlist1.isEmpty() && mlist2.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.add_btn)
    public void onClick() {
        if (MyApplication.getUser(context).isTest()) {
            DialogUtil.show("注意", "目前为试用账号，登录后将清空试用账号所有数据", "去登录", "知道了", this, new
                    DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                        }
                    }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, AddGoodsActivity.class);
                    intent.putExtra("type", 1);
                    startActivityForResult(intent, 43);
                }
            }).setCancelable(true);
        } else {
            Intent intent = new Intent(context, AddGoodsActivity.class);
            intent.putExtra("type", 1);
            startActivityForResult(intent, 43);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 43 && resultCode == RESULT_OK) {
            getData();
        }
    }
}
