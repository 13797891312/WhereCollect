package com.gongwu.wherecollect.record;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.volley.request.QiNiuUploadUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.gongwu.wherecollect.LocationLook.MainLocationFragment;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.RecordSaveBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.entity.RoomRecordBean;
import com.gongwu.wherecollect.util.ACacheClient;
import com.gongwu.wherecollect.util.BitmapUtil;
import com.gongwu.wherecollect.util.DialogUtil;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.GlideRoundTransform;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.SaveDate;
import com.gongwu.wherecollect.util.ToastUtil;
import com.gongwu.wherecollect.view.ChangeHeaderImgDialog;
import com.gongwu.wherecollect.view.ShijiEditDialog;
import com.zhaojin.myviews.Loading;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
public class MakeRecordActivity extends BaseViewActivity {
    public Bitmap nextbitmap;
    @Bind(R.id.activity_make_record)
    LinearLayout activityMakeRecord;
    @Bind(R.id.image_thumb)
    PhotoView imageThumb;
    @Bind(R.id.tv_space_name)
    TextView tvSpaceName;
    @Bind(R.id.time_tv)
    TextView timeTv;
    String spaceName;
    Bitmap bitmap;
    @Bind(R.id.oldTextView)
    TextView oldTextView;
    @Bind(R.id.imageview_old)
    ImageView imageviewOld;
    @Bind(R.id.oldLayout)
    CardView oldLayout;
    @Bind(R.id.memo_layout)
    LinearLayout memoLayout;
    @Bind(R.id.newTextView)
    TextView newTextView;
    @Bind(R.id.imageview_new)
    ImageView imageviewNew;
    @Bind(R.id.newLayout)
    CardView newLayout;
    @Bind(R.id.memo_btn)
    TextView memoBtn;
    @Bind(R.id.memo_tv)
    TextView memoTv;
    ChangeHeaderImgDialog imgDialog;
    File file1, file2;
    String spaceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_record);
        ButterKnife.bind(this);
        bitmap = MainLocationFragment.bitmap;
        titleLayout.setTitle("室迹");
        titleLayout.setBack(false, null);
        titleLayout.backText.setVisibility(View.VISIBLE);
        titleLayout.backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
                finish();
            }
        });
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        titleLayout.textBtn.setText("保存/分享");
        titleLayout.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memoBtn.getVisibility() == View.VISIBLE) {
                    memoLayout.setVisibility(View.GONE);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postSave();
                    }
                }, 50);
            }
        });
        spaceName = getIntent().getStringExtra("name");
        spaceCode = getIntent().getStringExtra("code");
        imageThumb.setImageBitmap(bitmap);
        initView();
        setBtnStatus();
        checkCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SaveDate.getInstence(getApplication()).isOnceShiji()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showHelp();
                }
            }, 500);
            SaveDate.getInstence(getApplication()).setIsOnceShiji(false);
        }
    }

    /**
     * 第一次用本功能的提示
     */
    private void showHelp() {
        View view = View.inflate(this, R.layout.layout_popwindow_shiji_help, null);
        PopupWindow popupWindow = new PopupWindow(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams
                .WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.pop_topin_topout);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(R.id.base_layout), Gravity.TOP, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        setBackgroundAlpha(0.5f);
    }

    private void initView() {
        tvSpaceName.setText(spaceName);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        memoBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        timeTv.setText(String.format("-  公元%s  -", format.format(new Date())));
    }

    @OnClick({R.id.imageview_old, R.id.memo_btn, R.id.imageview_new})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageview_old:
                imgDialog = new ChangeHeaderImgDialog(this, null, null) {
                    @Override
                    public void getResult(File file) {
                        super.getResult(file);
                        file1 = file;
                        Glide.with(context)
                                .load(file)
                                .diskCacheStrategy
                                        (DiskCacheStrategy.NONE)
                                .transform(new CenterCrop(context), new GlideRoundTransform(context, 4))
                                .into(imageviewOld);
                        imgDialog = null;
                        setBtnStatus();
                    }
                };
                imgDialog.setClip(true);
                imgDialog.setAspectXY(2, 1);
                break;
            case R.id.memo_btn:
                showDialog();
                break;
            case R.id.imageview_new:
                imgDialog = new ChangeHeaderImgDialog(this, null, null) {
                    @Override
                    public void getResult(File file) {
                        super.getResult(file);
                        file2 = file;
                        Glide.with(context)
                                .load(file)
                                .diskCacheStrategy
                                        (DiskCacheStrategy.NONE)
                                .transform(new CenterCrop(context), new GlideRoundTransform(context, 4))
                                .into(imageviewNew);
                        imgDialog = null;
                        setBtnStatus();
                    }
                };
                imgDialog.setClip(true);
                imgDialog.setAspectXY(2, 1);
                break;
            case R.id.newLayout:
                break;
        }
    }

    /**
     * 弹出说两句对话框
     */
    private void showDialog() {
        ShijiEditDialog dialog = new ShijiEditDialog(this, "") {
            @Override
            protected void commit(String str) {
                super.commit(str);
                memoTv.setText(str);
                memoBtn.setVisibility(View.GONE);
            }
        };
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imgDialog != null) {
            imgDialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setBtnStatus() {
        if (file1 == null || file2 == null) {
            titleLayout.textBtn.setEnabled(false);
            titleLayout.textBtn.setTextColor(getResources().getColor(R.color.btn_enable));
        } else {
            titleLayout.textBtn.setEnabled(true);
            titleLayout.textBtn.setTextColor(getResources().getColor(R.color.maincolor));
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    /**
     * 暂存
     */
    private void back() {
        //如果有类容
        if (file1 != null || file2
                != null || memoBtn.getVisibility() == View.GONE) {
            RecordSaveBean bean = new RecordSaveBean(file1 == null ? null : file1.getAbsolutePath(), file2 == null ?
                    null : file2.getAbsolutePath(),
                    memoBtn.getVisibility() == View.GONE ? memoTv.getText().toString() : null);
            ACacheClient.saveRecord(this, spaceCode, JsonUtils.jsonFromObject(bean));
            EventBusMsg.RecordChange msg = new EventBusMsg.RecordChange();
            msg.isSave = 1;
            EventBus.getDefault().post(msg);
        }
    }

    /**
     * 检测缓存
     */
    private void checkCache() {
        String cache = ACacheClient.getRecord(this, spaceCode);
        if (TextUtils.isEmpty(cache)) {
            return;
        }
        final RecordSaveBean bean = JsonUtils.objectFromJson(cache, RecordSaveBean.class);
        ACacheClient.removeCache(this, String.format("record%s", spaceCode));
        EventBusMsg.RecordChange msg = new EventBusMsg.RecordChange();
        msg.isSave = 2;
        EventBus.getDefault().post(msg);
        setCacheUi(bean);
        Dialog dialog = DialogUtil.show("提示", "该空间暂存了内容，是否继续？", "继续", "创建新", this, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setCacheUi(new RecordSaveBean(null, null, null));
            }
        });
        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R
                .color.holo_red_light));
    }

    /**
     * 设置缓存UI
     */
    private void setCacheUi(RecordSaveBean bean) {
        if (!TextUtils.isEmpty(bean.getMemo())) {
            memoTv.setText(bean.getMemo());
            memoBtn.setVisibility(View.GONE);
        } else {
            memoTv.setText("变化这么大,我来");
            memoBtn.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(bean.getFile1())) {
            file1 = new File(bean.getFile1());
            Glide.with(context)
                    .load(file1)
                    .diskCacheStrategy
                            (DiskCacheStrategy.NONE)
                    .transform(new CenterCrop(context), new GlideRoundTransform(context, 4))
                    .into(imageviewOld);
            imgDialog = null;
        } else {
            imageviewOld.setImageResource(R.color.trans);
            file1 = null;
        }
        if (!TextUtils.isEmpty(bean.getFile2())) {
            file2 = new File(bean.getFile2());
            Glide.with(context)
                    .load(file2)
                    .diskCacheStrategy
                            (DiskCacheStrategy.NONE)
                    .transform(new CenterCrop(context), new GlideRoundTransform(context, 4))
                    .into(imageviewNew);
            imgDialog = null;
        } else {
            imageviewNew.setImageResource(R.color.trans);
            file2 = null;
        }
        setBtnStatus();
    }

    /**
     * 保存到服务器
     */
    private void postSave() {
        List<File> temp = new ArrayList<>();
        temp.add(getimageFile());
        temp.add(FileUtil.getFile(bitmap, System.currentTimeMillis() + "snowp.jpg"));
        final Loading loading = Loading.show(null, context, "");
        QiNiuUploadUtil uploadUtil = new QiNiuUploadUtil(this, temp, "roomrecord/") {
            @Override
            protected void finish(List<String> list) {
                super.finish(list);
                Map<String, String> map = new TreeMap<>();
                map.put("uid", MyApplication.getUser(context).getId());
                map.put("image_url", list.get(0).endsWith("snowp.jpg") ? list.get(1) : list.get(0));
                map.put("location_name", spaceName);
                map.put("change_tex", memoBtn.getVisibility() == View.GONE ? memoTv.getText().toString() : "");
                map.put("snapshot_url", list.get(0).endsWith("snowp.jpg") ? list.get(0) : list.get(1));
                PostListenner listenner = new PostListenner(context) {
                    @Override
                    protected void code2000(final ResponseResult r) {
                        super.code2000(r);
                        SaveDate.getInstence(context).setRecordSaved(MyApplication.getUser(context).getId(), true);
                        EventBus.getDefault().post(new EventBusMsg.RecordChange());
                        RoomRecordBean bean = JsonUtils.objectFromJson(r.getResult(), RoomRecordBean.class);
                        Intent intent = new Intent(context, RecordShareActivity.class);
                        intent.putExtra("bean", bean);
                        intent.putExtra("type", 1);
                        startActivity(intent);
                        MakeRecordActivity.this.finish();
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        if (loading != null) {
                            loading.dismiss();
                        }
                    }
                };
                HttpClient.savaRoomRecord(context, map, listenner);
            }
        };
        uploadUtil.start();
    }

    /**
     * 获取文件
     */
    private File getimageFile() {
        if (nextbitmap != null) {
            nextbitmap.recycle();
        }
        nextbitmap = BitmapUtil.viewToBitmap(activityMakeRecord);
        File file = BitmapUtil.saveImageToGallery(this, nextbitmap);
        ToastUtil.show(this, "已保存到sdcard/shiji目录", Toast.LENGTH_LONG);
        return file;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nextbitmap != null) {
            nextbitmap.recycle();
        }
    }
}
