package com.gongwu.wherecollect.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gongwu.wherecollect.object.SelectFenleiActivity;
import com.gongwu.wherecollect.adapter.GuishuListAdapter;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ChannelBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.StringUtils;
import com.zhaojin.myviews.Loading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * Function:
 * Date: 2017/11/11
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class CatagreyListView extends ListView implements AdapterView.OnItemClickListener {
    public ChannelBean selectGuishu;
    List<ChannelBean> guiShuList = new ArrayList<>();
    List<ChannelBean> currentList = new ArrayList<>();//当前显示的归属list
    GuishuListAdapter guishuAdapter;
    Context context;
    TextView guishuTxt;

    public CatagreyListView(Context context) {
        this(context, null);
    }

    public CatagreyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void init(TextView textView) {
        guishuTxt = textView;
        guishuAdapter = new GuishuListAdapter(context, currentList);
        setAdapter(guishuAdapter);
        setOnItemClickListener(this);
        if (context instanceof SelectFenleiActivity) {
            initGuishuData(null);
        } else {
            initChanalData(null);
        }
    }

    /**
     * 获取归属列表
     */
    private void initGuishuData(final ChannelBean channelBean) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        if (channelBean != null) {
            map.put("category_code", channelBean.getCode());
        }
        PostListenner listenner = new PostListenner(context, channelBean == null ? null : Loading.show(null, context,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List<ChannelBean> temp = JsonUtils.listFromJson(r.getResult(), ChannelBean.class);
                ChannelBean bean = new ChannelBean();
                bean.setName("自定义");
                temp.add(0, bean);
                if (channelBean == null) {
                    guiShuList.clear();
                    guiShuList.addAll(temp);
                    currentList = guiShuList;
                } else {
                    channelBean.setChildBeans(temp);
                    for (ChannelBean cb : temp) {
                        cb.setParentBeans(currentList);
                        cb.setParentsBean(channelBean);
                    }
                    currentList = temp;
                }
                guishuAdapter.notifyDate(currentList);
            }
        };
        HttpClient.getCategoryList(context, map, listenner);
    }

    /**
     * 获取来源列表
     */
    private void initChanalData(final ChannelBean channelBean) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        if (channelBean != null) {
            map.put("code", channelBean.getCode());
        }
        PostListenner listenner = new PostListenner(context, channelBean == null ? null : Loading.show(null, context,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List<ChannelBean> temp = JsonUtils.listFromJson(r.getResult(), ChannelBean.class);
                ChannelBean bean = new ChannelBean();
                bean.setName("自定义");
                temp.add(0, bean);
                if (channelBean == null) {
                    guiShuList.clear();
                    guiShuList.addAll(temp);
                    currentList = guiShuList;
                } else {
                    channelBean.setChildBeans(temp);
                    for (ChannelBean cb : temp) {
                        cb.setParentBeans(currentList);
                        cb.setParentsBean(channelBean);
                    }
                    currentList = temp;
                }
                guishuAdapter.notifyDate(currentList);
            }
        };
        HttpClient.getChannelList(context, map, listenner);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        guishuTxt.setText(currentList.get(position).getString());
        selectGuishu = currentList.get(position);
        if (currentList.get(position).getTag_count() != 0) {
            if (StringUtils.isEmpty(currentList.get(position).getChildBeans())) {
                if (context instanceof SelectFenleiActivity) {
                    initGuishuData(currentList.get(position));
                } else {
                    initChanalData(currentList.get(position));
                }
            } else {
                currentList = currentList.get(position).getChildBeans();
                guishuAdapter.notifyDate(currentList);
            }
        }
    }

    /**
     * 返回到上一个列表
     */
    public void lastList() {
        if (selectGuishu != null && (!StringUtils.isEmpty(selectGuishu.getParentBeans()))) {
            selectGuishu = selectGuishu.getParentsBean();
            currentList = selectGuishu.getChildBeans();
            guishuAdapter.notifyDate(currentList);
            guishuTxt.setText(selectGuishu.getString());
        } else {
            currentList = guiShuList;
            guishuAdapter.notifyDate(currentList);
            guishuTxt.setText("");
        }
    }

    /**
     * 还原到原始列表
     */
    public void resetData() {
        currentList = guiShuList;
        guishuAdapter.notifyDate(currentList);
        selectGuishu = null;
    }
}
