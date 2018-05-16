package com.gongwu.wherecollect.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImportHelpActivity extends BaseViewActivity {
    @Bind(R.id.imageview)
    ImageView imageview;
    @Bind(R.id.scrollView)
    PullToRefreshScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_help);
        ButterKnife.bind(this);
        titleLayout.setBack(true, null);
        titleLayout.setTitle("导入淘宝/京东单品");
        ScrollView.LayoutParams layoutParams = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                1394 * BaseViewActivity.getScreenWidth(this) / 750);
        imageview.setLayoutParams(layoutParams);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                importBuy();
            }
        });
    }

    /**
     * 导入网购商品
     */
    private void importBuy() {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData cd2 = cm.getPrimaryClip();
        String str;
        if (cd2 == null) {
            str = "";
        } else {
            str = cd2.getItemAt(0).getText().toString();

        }
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("key", str);
        PostListenner listenner = new PostListenner(context) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                scrollView.onRefreshComplete();
                final BookBean book = JsonUtils.objectFromJson(r.getResult(), BookBean.class);
                if (book == null || TextUtils.isEmpty(book.getPic())) {
                    ToastUtil.showTopToast(context, "获取商品信息失败");
                    return;
                }
                new Thread(new Runnable() {//下载图片
                    @Override
                    public void run() {
                        try {
                            File file = Glide.with(context).load(book.getPic()).downloadOnly(500, 500).get();
                            String newPath = MyApplication.CACHEPATH + System.currentTimeMillis() + ".jpg";
                            FileUtil.copyFile(file, newPath);
                            file = new File(newPath);
                            book.setImageFile(file);
                            ImportHelpActivity.this.runOnUiThread(new Runnable() {//回主线程
                                @Override
                                public void run() {
                                    DialogUtil.show("提醒", "此操作会将部分共同属性刷新，是否继续?", "继续", "取消", ImportHelpActivity.this,
                                            new
                                                    DialogInterface.OnClickListener
                                                            () {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            EventBus.getDefault().post(new EventBusMsg
                                                                    .ImportFromBugSucces(book));
                                                            finish();
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

            @Override
            protected void codeOther(ResponseResult r) {
                super.codeOther(r);
                ToastUtil.showTopToast(context, "获取商品信息失败");
                scrollView.onRefreshComplete();
            }
        };
        HttpClient.getTaobaoInfo(context, map, listenner);
    }
}
