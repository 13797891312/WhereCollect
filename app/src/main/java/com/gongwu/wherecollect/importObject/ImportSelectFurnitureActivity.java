package com.gongwu.wherecollect.importObject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.gongwu.wherecollect.LocationEdit.LocationEditActivity;
import com.gongwu.wherecollect.LocationLook.LocationIndicatorView;
import com.gongwu.wherecollect.LocationLook.LocationPage;
import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.LocationLook.furnitureLook.FurnitureLookActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.view.ObjectView;
import com.zhaojin.myviews.TagViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
public class ImportSelectFurnitureActivity extends BaseViewActivity {
    View view;
    @Bind(R.id.indicatorView)
    LocationIndicatorView indicatorView;
    @Bind(R.id.tagViewPager)
    TagViewPager viewPager;
    private Map<Integer, LocationPage> pageMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("归入物品");
        setContentView(R.layout.activity_import_selectfurniture);
        ButterKnife.bind(this);
        titleLayout.textBtn.setText("编辑");
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImportSelectFurnitureActivity.this, LocationEditActivity.class);
                startActivity(intent);
                finish();
            }
        });
        initView();
        indicatorView.init(MainLocationFragment.mlist);
        EventBus.getDefault().register(this);
        initPage();
    }

    private void initView() {
        //桌布空间头点击事件
        indicatorView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(indicatorView.getSelection());
            }
        });
    }

    private void initPage() {
        viewPager.init(R.drawable.shape_photo_tag_select, R.drawable.shape_photo_tag_nomal, 0,
                0, 0, 0);
        viewPager.setAutoNext(false, 0);
        viewPager.setId(14);
        viewPager.setOnGetView(new TagViewPager.OnGetView() {
            @Override
            public View getView(ViewGroup container, final int position) {
                LocationPage v = new LocationPage(context);
                container.addView(v);
                v.init(false);
                v.setOnItemClickListener(new LocationPage.OnItemClickListener() {
                    @Override
                    public void itemClick(ObjectView view) {
                        Intent intent = new Intent(context, FurnitureLookActivity.class);
                        intent.putExtra("furnitureObject", view.getObjectBean());
                        intent.putExtra("list", (Serializable) MainLocationFragment.objectMap.get
                                (MainLocationFragment.mlist.get
                                (viewPager.getCurrentItem()).getCode()));
                        intent.putExtra("position", viewPager.getCurrentItem());
                        intent.putExtra("title", indicatorView.getCurrentLocation().getName() + ">" + view
                                .getObjectBean()
                                .getName());
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void bgClick() {
                    }

                    @Override
                    public void upSlide() {
                    }

                    @Override
                    public void downSlide() {
                    }
                });
                v.getLocationChild(position);
                pageMap.put(position, v);
                return v;
            }
        });
        viewPager.setAdapter(MainLocationFragment.mlist.size(), 0);
        viewPager.setOnSelectedListoner(new TagViewPager.OnSelectedListoner() {
            @Override
            public void onSelected(int position) {
                indicatorView.setSelection(position);
                indicatorView.notifyView();
            }
        });
        viewPager.setCurrentItem(indicatorView.getSelection());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String str) {
        if (EventBusMsg.SPACE_EDIT.contains(str)) {
            indicatorView.notifyView();
            initPage();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusMsg.EditLocationMsg msg) {
        LocationPage page = pageMap.get(msg.position);
        if (page != null) {
            page.getNetData("", MainLocationFragment.mlist.get(msg.position).getCode());
        }
    }
}
