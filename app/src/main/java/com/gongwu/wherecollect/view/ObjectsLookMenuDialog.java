package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.TextView;

import com.gongwu.wherecollect.LocationLook.furnitureLook.FurnitureLookActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.importObject.ImportSelectFurnitureActivity;
import com.gongwu.wherecollect.object.AddGoodsActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.umeng.analytics.MobclickAgent;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Function://物品查看时右上角menu
 * Date: 2017/9/7
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ObjectsLookMenuDialog {
    Context context;
    Dialog dialog;
    ObjectBean bean;
    @Bind(R.id.fengcun_tv)
    TextView fengcunTv;

    public ObjectsLookMenuDialog(Activity context, ObjectBean bean) {
        this.bean = bean;
        this.context = context;
        dialog = new Dialog(context,
                R.style.Transparent2);
        dialog.setCanceledOnTouchOutside(true);
        View view = View.inflate(context,
                R.layout.layout_goods_look_menu, null);
        ButterKnife.bind(this, view);
        dialog.setContentView(view);
        Animation ani = AnimationUtils.loadAnimation(context, R.anim.push_bottom_in);
        view.findViewById(R.id.linearLayout).startAnimation(ani);
        fengcunTv.setText(bean.isIs_archive() ? "回归" : "封存");
        dialog.show();
    }

    @OnClick({R.id.edit_location, R.id.edit_goods, R.id.fengcun_tv, R.id.delete_tv, R.id.cancel, R.id.linearLayout, R
            .id.root})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_location:
                editLocation();
                dialog.dismiss();
                break;
            case R.id.edit_goods:
                editGoods();
                dialog.dismiss();
                break;
            case R.id.fengcun_tv:
                fengcun();
                dialog.dismiss();
                break;
            case R.id.delete_tv:
                deleteObject();
                dialog.dismiss();
                break;
            case R.id.cancel:
            case R.id.linearLayout:
            case R.id.root:
                dialog.dismiss();
                break;
        }
    }

    protected void editLocation(){

    }

    private void fengcun() {
        if (!bean.isIs_archive()) {
            DialogUtil.show("提醒", "是否将物品封存到“纪念碑谷”中？", "是", "否", ((Activity) context), new DialogInterface
                    .OnClickListener
                    () {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    postFengcun("1");
                    bean.setIs_archive(true);
                }
            }, null);
        } else {
            postFengcun("0");
            bean.setIs_archive(false);
        }
    }

    private void postFengcun(String is_archive) {
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(context).getId());
        map.put("is_archive", is_archive);
        map.put("object_id", bean.get_id());
        PostListenner listenner = new PostListenner(context, Loading.show(null, context,
                "正在加载")) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                ((Activity) context).finish();
            }
        };
        HttpClient.fengcun(context, map, listenner);
    }

    /**
     * 删除物品
     */
    private void deleteObject() {
        DialogUtil.show("提醒", "确认删除此物品？", "删除", "取消", ((Activity) context), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MobclickAgent.onEvent(context, "030102");
                Map<String, String> map = new TreeMap<>();
                map.put("uid", MyApplication.getUser(context).getId());
                map.put("object_id", bean.get_id());
                PostListenner listenner = new PostListenner(context, Loading.show(null, context,
                        "正在删除")) {
                    @Override
                    protected void code2000(final ResponseResult r) {
                        super.code2000(r);
                        ((Activity) context).finish();
                        EventBusMsg.DeleteGoodsMsg msg = new EventBusMsg.DeleteGoodsMsg(bean.get_id());
                        EventBus.getDefault().post(msg);
                        EventBus.getDefault().post(EventBusMsg.OBJECT_CHANGE);
                    }
                };
                HttpClient.deleteGoods(context, map, listenner);
            }
        }, null);
    }

    /**
     * 编辑物品
     */
    private void editGoods() {
        Intent intent = new Intent(context, AddGoodsActivity.class);
        intent.putExtra("bean", bean);
        ((Activity) context).startActivityForResult(intent, 0);
        MobclickAgent.onEvent(context, "050103");

    }
}
