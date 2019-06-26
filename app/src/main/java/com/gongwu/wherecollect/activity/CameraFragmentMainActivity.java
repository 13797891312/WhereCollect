package com.gongwu.wherecollect.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.gongwu.wherecollect.ImageSelect.ImageGridActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.object.AddGoodsActivity;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.view.SelectImgDialog;
import com.zsitech.oncon.barcode.core.CaptureActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("MissingPermission")
public class CameraFragmentMainActivity extends BaseViewActivity {

    public static final String FRAGMENT_TAG = "camera";
    private static final int BOOK_CODE = 0x123;

    @Bind(R.id.record_button)
    ImageButton recordButton;
    @Bind(R.id.continuous_text)
    TextView continuousText;
    @Bind(R.id.images_list_layout)
    View imagesLayout;
    @Bind(R.id.images_view)
    ImageView imagesView;
    @Bind(R.id.num_text)
    TextView numText;
    @Bind(R.id.camera_select_img_layout)
    View selectImgView;

    @Bind(R.id.rl_camera_saoma)
    View cameraSaoma;

    @Bind(R.id.saoma_iv)
    ImageView saomaIv;
    @Bind(R.id.saoma_text)
    TextView saomaText;
    @Bind(R.id.select_img_iv)
    ImageView selectImgIv;
    @Bind(R.id.select_img_tv)
    TextView selectImgTv;

    /**
     * 判断单拍还是连拍
     */
    private boolean continuous = false;
    private boolean addMore;
    private ArrayList<String> files = new ArrayList<>();
    private final int maxImags = 10;

    public static void start(Context context, boolean addMore) {
        Intent intent = new Intent(context, CameraFragmentMainActivity.class);
        if (intent != null) {
            intent.putExtra("addMore", addMore);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerafragment_activity_main);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        initView();
        addCamera();
    }

    private void initView() {
        addMore = getIntent().getBooleanExtra("addMore", false);
        titleLayout.setVisibility(View.GONE);
        if (addMore) {
            continuous = true;
            continuousText.setVisibility(View.GONE);
            saomaIv.setImageResource(R.drawable.icon_camera_saoma_enable);
            saomaText.setTextColor(getResources().getColor(R.color.color999));
            cameraSaoma.setEnabled(false);
        }
    }

    /**
     * 点击事件
     *
     * @param view view
     */
    @OnClick({R.id.video_switch_flash_layout, R.id.back_view, R.id.continuous_text, R.id.images_list_layout, R.id.camera_select_img_layout, R.id.rl_camera_saoma})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_view:
                onBackPressed();
                break;
            case R.id.continuous_text:
                if (continuous) {
                    continuousText.setText("批量添加");
                    saomaIv.setImageResource(R.drawable.icon_camera_saoma);
                    saomaText.setTextColor(getResources().getColor(R.color.white));
                    cameraSaoma.setEnabled(true);
                } else {
                    continuousText.setText("单品添加");
                    saomaIv.setImageResource(R.drawable.icon_camera_saoma_enable);
                    saomaText.setTextColor(getResources().getColor(R.color.color999));
                    cameraSaoma.setEnabled(false);
                }
                continuous = !continuous;
                break;
            case R.id.camera_select_img_layout:
                Intent i = new Intent(context, ImageGridActivity.class);
                i.putExtra("max", continuous ? 10 : 1);
                startActivityForResult(i, SelectImgDialog.REQUST_PHOTOSELECT);
                break;
            case R.id.rl_camera_saoma:
                startActivityForResult(new Intent(context, CaptureActivity.class), BOOK_CODE);
                break;
            case R.id.images_list_layout:
                AddMoreGoodsActivity.start(context, files);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BOOK_CODE && resultCode == CaptureActivity.result) {//扫描的到结果
            String result = data.getStringExtra("result");
            AddGoodsActivity.start(context, "", result);
            finish();
        }
        if (requestCode == SelectImgDialog.REQUST_PHOTOSELECT && resultCode == ImageGridActivity.RESULT) {
            List<ImageData> temp = (ArrayList<ImageData>) data.getSerializableExtra("list");
            if (temp.size() == 1) {
                AddGoodsActivity.start(context, FileUtil.compress(new File(temp.get(0).getBigUri())).getAbsolutePath(), "");
                finish();
            } else if (temp.size() > 1) {
                for (ImageData id : temp) {
                    files.add(FileUtil.compress(new File(id.getBigUri())).getAbsolutePath());
                }
                selectImgIv.setImageResource(R.drawable.icon_camera_img_enable);
                selectImgTv.setTextColor(getResources().getColor(R.color.color999));
                selectImgView.setEnabled(false);
                cameraSaoma.setVisibility(View.GONE);
                imagesLayout.setVisibility(View.VISIBLE);
                continuousText.setVisibility(View.GONE);
                imagesView.setImageURI(FileUtil.getUriFromFile(context, new File(files.get(files.size()-1))));
                numText.setText(String.valueOf(files.size()));
                if (files.size() == maxImags) {
                    AddMoreGoodsActivity.start(context, files);
                    finish();
                }
            }
        }
    }

    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                @Override
                public void onVideoRecorded(String filePath) {
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {
                    File file = new File(filePath);
                    //批量
                    if (continuous) {
                        selectImgIv.setImageResource(R.drawable.icon_camera_img_enable);
                        selectImgTv.setTextColor(getResources().getColor(R.color.color999));
                        selectImgView.setEnabled(false);
                        cameraSaoma.setVisibility(View.GONE);
                        imagesLayout.setVisibility(View.VISIBLE);
                        continuousText.setVisibility(View.GONE);
                        files.add(FileUtil.compress(file).getAbsolutePath());
                        imagesView.setImageURI(FileUtil.getUriFromFile(context, file));
                        numText.setText(String.valueOf(files.size()));
                        if (files.size() == maxImags) {
                            AddMoreGoodsActivity.start(context, files);
                            finish();
                        }
                    } else {
                        //单拍
                        AddGoodsActivity.start(context, FileUtil.compress(file).getAbsolutePath(), "");
                        finish();
                    }
                }
            }, MyApplication.CACHEPATH, System.currentTimeMillis() + ".png");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (addMore) {
            intent.setClass(context, AddMoreGoodsActivity.class);
        } else {
            intent.setClass(context, AddGoodsActivity.class);
        }
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            addCamera();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {

        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {
            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    recordButton.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    recordButton.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                }

                @Override
                public void allowRecord(boolean allow) {
                    recordButton.setEnabled(allow);
                }

                @Override
                public void setMediaActionSwitchVisible(boolean visible) {
                }
            });
        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }
}
