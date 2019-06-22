package com.gongwu.wherecollect.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gongwu.wherecollect.ImageSelect.ImageGridActivity;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.MenuAdapter;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.object.AddGoodsActivity;
import com.gongwu.wherecollect.util.AppConstant;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.cameravideo.CameraHelper;
import com.gongwu.wherecollect.util.cameravideo.ICamera2;
import com.gongwu.wherecollect.util.cameravideo.IVideoControl;
import com.gongwu.wherecollect.view.AutoFitTextureView;
import com.gongwu.wherecollect.view.AutoLocateHorizontalView;
import com.gongwu.wherecollect.view.SelectImgDialog;
import com.zsitech.oncon.barcode.core.CaptureActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * 拍照 视频
 *
 * @author ymc
 * @date 2019年5月7日 13:49:17
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraVideoActivity extends BaseViewActivity implements IVideoControl.PlaySeekTimeListener, IVideoControl.PlayStateListener, ICamera2.TakePhotoListener, SensorEventListener, ICamera2.CameraReady, AutoLocateHorizontalView.OnSelectedPositionChangedListener {

    private static final String TAG = CameraVideoActivity.class.getSimpleName();

    private static final int BOOK_CODE = 0x123;

    private boolean addMore;

    public static void start(Context context, boolean addMore) {
        Intent intent = new Intent(context, CameraVideoActivity.class);
        if (intent != null) {
            intent.putExtra("addMore", addMore);
        }
        context.startActivity(intent);
    }

    /**
     * 当前的显示面板状态
     */
    public int TEXTURE_STATE = AppConstant.TEXTURE_PREVIEW_STATE;

    @Bind(R.id.video_photo)
    ImageView videoPhoto;
    @Bind(R.id.video_texture)
    AutoFitTextureView videoTexture;
    @Bind(R.id.video_record_seek_bar)
    SeekBar videoRecordSeekBar;
    @Bind(R.id.video_switch_flash)
    ImageView videoSwitchFlash;
    @Bind(R.id.video_switch_flash_text)
    TextView switchFlashTv;
    @Bind(R.id.video_record)
    ImageButton videoRecord;
    @Bind(R.id.video_seek_bar)
    SeekBar videoSeekBar;
    @Bind(R.id.video_seek_time)
    TextView videoSeekTime;
    @Bind(R.id.video_hint_text)
    TextView videoHintText;
    /**
     * 焦点框
     */
    @Bind(R.id.video_fouces)
    ImageView videoFouces;
    /**
     * zoom 缩小
     */
    @Bind(R.id.video_minus)
    ImageView videoMinus;
    /**
     * scale zoom 条
     */
    @Bind(R.id.video_scale)
    SeekBar videoScale;
    /**
     * zoom 放大
     */
    @Bind(R.id.video_add)
    ImageView videoAdd;
    @Bind(R.id.video_scale_bar_layout)
    RelativeLayout videoScaleBarLayout;
    /**
     * 底部切换布局
     */
    @Bind(R.id.layout_bottom)
    RelativeLayout mLayoutBottom;
    /**
     * awb
     */
    @Bind(R.id.rl_camera)
    LinearLayout rlCamera;
    @Bind(R.id.txt_sb_txt)
    TextView tvSbTxt;
    @Bind(R.id.video_menu)
    AutoLocateHorizontalView videoMenu;
    @Bind(R.id.back_btn)
    ImageView backBtn;
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
     * 相机模式
     */
    private int MODE;
    /**
     * 拍照工具类
     */
    private CameraHelper cameraHelper;
    /**
     * 当前拍照模式
     */
    private int NOW_MODE;
    /**
     * 触摸事件处理类
     */
    private CameraTouch mCameraTouch;
    /**
     * 放大缩小seekBar 是否可以隐藏
     */
    private boolean isCanHind;
    /**
     * 手动对焦 动画
     */
    private FoucesAnimation mFoucesAnimation;
    /**
     * 前 后 摄像头标识
     */
    private ICamera2.CameraType mNowCameraType = ICamera2.CameraType.BACK;
    /**
     * 单点标识
     */
    private boolean hasRecordClick = false;
    /**
     * 是否在 录制中
     */
    private boolean hasRecording = false;
    /**
     * 图片路径
     */
    private String mCameraPath;
    /**
     * 倒计时
     */
    private Disposable mDisposable;
    /**
     * 是否正在播放 标识
     */
    private boolean hasPlaying = false;
    /**
     * 是否有拍照权限
     */
    private boolean isNoPremissionPause;

    /**
     * 定义文字动画
     */
    private AlphaAnimation mAlphaInAnimation;
    private AlphaAnimation mAlphaOutAnimation;
    /**
     * 菜单适配器
     */
    private MenuAdapter mMenuAdapter;
    /**
     * 判断单拍还是连拍
     */
    private boolean continuous = false;

    private ArrayList<String> files = new ArrayList<>();
    private final int maxImags = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_video);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        addMore = getIntent().getBooleanExtra("addMore", false);
        titleLayout.setVisibility(View.GONE);
        initView();
        initData();
        imagesLayout.setVisibility(View.GONE);
        selectImgView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    protected void initView() {
        // 将底部布局 依次添加到 列表中
        mLayoutList.clear();
        mLayoutList.add(mLayoutBottom);
        // 初始化 切换动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(100);

        MODE = getIntent().getIntExtra("mode", AppConstant.CAMERA_MODE);

        if (MODE == AppConstant.CAMERA_MODE) {
            //摄像头模式
            initCameraMode();
        }
        mFoucesAnimation = new FoucesAnimation();
        // 淡入动画
        mAlphaInAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaInAnimation.setDuration(500);
        // 淡出动画
        mAlphaOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        mAlphaOutAnimation.setDuration(500);

        LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager ms1 = new LinearLayoutManager(this);
        ms1.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (addMore) {
            continuous = true;
            continuousText.setVisibility(View.GONE);
            saomaIv.setImageResource(R.drawable.icon_camera_saoma_enable);
            saomaText.setTextColor(getResources().getColor(R.color.color999));
            cameraSaoma.setEnabled(false);
        }
    }

    protected void initData() {
        mCameraPath = cameraHelper.getPhotoFilePath();
    }

    /**
     * 初始化 拍照
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initCameraMode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            isNoPremissionPause = true;
        }
        initCamera(mNowCameraType);
        cameraHelper = new CameraHelper(this);
        cameraHelper.setTakePhotoListener(this);
        cameraHelper.setCameraReady(this);
        List<String> menus = new ArrayList<>();
        menus.add("拍照");
        mMenuAdapter = new MenuAdapter(this, menus, videoMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoMenu.setLayoutManager(linearLayoutManager);
        videoMenu.setAdapter(mMenuAdapter);
        videoMenu.setOnSelectedPositionChangedListener(this);

        mCameraTouch = new CameraTouch();

        videoMenu.setOnTouchListener(new HorizontalViewTouchListener());
        registerSensor();
        initScaleSeekbar();
    }

    /**
     * 初始化摄像头
     *
     * @param cameraType
     */
    private void initCamera(ICamera2.CameraType cameraType) {
        if (cameraHelper == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cameraHelper.setTextureView(videoTexture);
        cameraHelper.openCamera(cameraType);
    }


    /**
     * 初始化 scale seekBar
     */
    private void initScaleSeekbar() {
        videoScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float scale = (float) progress / (float) seekBar.getMax() * cameraHelper.getMaxZoom();
                    cameraHelper.cameraZoom(scale);
                    mCameraTouch.setScale(scale);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                removeSeekBarRunnable();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarDelayedHind();
            }
        });
    }

    @Override
    public void selectedPositionChanged(int pos) {
        switch (pos) {
            case 0: {
//                showLayout(0, false);
                NOW_MODE = AppConstant.VIDEO_TAKE_PHOTO;
                cameraHelper.setCameraState(ICamera2.CameraMode.TAKE_PHOTO);
                videoHintText.setText("点击拍照");
                break;
            }
        }
    }

    /**
     * 横向列表 touch事件 (拍照预览 缩放)
     */
    private class HorizontalViewTouchListener implements View.OnTouchListener {

        private long mClickOn;
        private float mLastX;
        private float mLastY;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (motionEvent.getPointerCount() == 1) {
                        mClickOn = System.currentTimeMillis();
                        mLastX = motionEvent.getX();
                        mLastY = motionEvent.getY();
                    }
                    break;
                // 用户两指按下事件
                case MotionEvent.ACTION_POINTER_DOWN:
                    mCameraTouch.onScaleStart(motionEvent);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (motionEvent.getPointerCount() == 2) {
                        mCameraTouch.onScale(motionEvent);
                        return true;
                    } else {
                        float x = motionEvent.getX() - mLastX;
                        float y = motionEvent.getY() - mLastY;
                        if (Math.abs(x) >= 10 || Math.abs(y) >= 10) {
                            mClickOn = 0;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (motionEvent.getPointerCount() == 1) {
                        if ((System.currentTimeMillis() - mClickOn) < 500) {
                            moveFouces((int) motionEvent.getX(), (int) motionEvent.getY());
                        }
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mCameraTouch.onScaleEnd(motionEvent);
                    return true;
                default:
                    break;
            }
            return false;
        }
    }

    /**
     * 点击事件
     *
     * @param view view
     */
    @OnClick({R.id.video_switch_flash_layout, R.id.back_view, R.id.continuous_text, R.id.images_list_layout, R.id.camera_select_img_layout, R.id.rl_camera_saoma})
    public void cameraOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.video_switch_flash_layout:
                Object o = videoSwitchFlash.getTag();
                if (o == null || ((int) o) == 0) {
                    videoSwitchFlash.setBackgroundResource(R.drawable.flash_open);
                    videoSwitchFlash.setTag(2);
                    switchFlashTv.setText("关闭闪光灯");
                    cameraHelper.flashSwitchState(ICamera2.FlashState.OPEN);
                } else {
                    videoSwitchFlash.setBackgroundResource(R.drawable.flash_close);
                    videoSwitchFlash.setTag(0);
                    switchFlashTv.setText("打开闪光灯");
                    cameraHelper.flashSwitchState(ICamera2.FlashState.CLOSE);
                }
                break;
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
                startActivityForResult(new Intent(context, CaptureActivity.class), 0x123);
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
                List<String> mlist = new ArrayList<>();
                for (ImageData id : temp) {
                    mlist.add(id.getBigUri());
                }
            }
        }
    }

    /**
     * 传感器继承方法 重力发生改变
     * 根据重力方向 动态旋转拍照图片角度(暂时关闭该方法)
     * <p>
     * 使用以下方法
     * int rotation = getWindowManager().getDefaultDisplay().getRotation();
     *
     * @param event event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
//            Log.e(TAG, "onSensorChanged: x: " + x +"   y: "+y +"  z : "+z);
            if (z > 55.0f) {
                //向左横屏
            } else if (z < -55.0f) {
                //向右横屏
            } else if (y > 60.0f) {
                //是倒竖屏
            } else {
                //正竖屏
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float light = event.values[0];
            cameraHelper.setLight(light);
        }
    }

    /**
     * 注册陀螺仪传感器
     */
    private void registerSensor() {
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        Sensor mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mSensor == null) {
            return;
        }
        mSensorManager.registerListener(this, mSensor, Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mLightSensor, Sensor.TYPE_LIGHT);
    }

    /**
     * 当已注册传感器的精度发生变化时调用
     *
     * @param sensor   sensor
     * @param accuracy 传感器的新精度
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSeekTime(int allTime, final int time) {
        if (videoSeekBar == null || videoSeekBar.getVisibility() != View.VISIBLE) {
            return;
        }
        if (videoSeekBar.getMax() != allTime) {
            videoSeekBar.setMax(allTime);
        }
        videoSeekBar.setProgress(time);
        videoSeekTime.post(new Runnable() {
            @Override
            public void run() {
                float t = (float) time / 1000.0f;
                videoSeekTime.setText(cameraHelper.secToTime(Math.round(t)));
            }
        });
    }

    @Override
    public void onStartListener(int width, int height) {
        videoTexture.setVideoAspectRatio(width, height);
    }

    @Override
    public void onCompletionListener() {
        hasPlaying = false;
    }

    /**
     * 拍照完成回调
     *
     * @param file          文件
     * @param photoRotation 角度
     * @param width         宽度
     * @param height        高度
     */
    @Override
    public void onTakePhotoFinish(final File file, int photoRotation, int width, int height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //批量
                if (continuous) {
                    selectImgIv.setImageResource(R.drawable.icon_camera_img_enable);
                    selectImgTv.setTextColor(getResources().getColor(R.color.color999));
                    selectImgView.setEnabled(false);
                    cameraSaoma.setVisibility(View.GONE);
                    imagesLayout.setVisibility(View.VISIBLE);
                    continuousText.setVisibility(View.GONE);
                    files.add(FileUtil.compress(file).getAbsolutePath());
                    imagesView.setImageURI(cameraHelper.getUriFromFile(CameraVideoActivity.this, file));
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
        });
    }

    /**
     * 相机准备完毕
     */
    @Override
    public void onCameraReady() {
        videoRecord.setClickable(true);
    }


    /**
     * 返回 取消拍照或者 录像
     */
    @OnClick(R.id.video_delete)
    public void deleteVideoOrPicture() {
        if (TEXTURE_STATE == AppConstant.TEXTURE_PHOTO_STATE) {
            File file = new File(mCameraPath);
            if (file.exists()) {
                file.delete();
            }
            cameraHelper.resumePreview();
            videoTexture.setVisibility(View.VISIBLE);
            videoPhoto.setVisibility(View.GONE);
            videoHintText.setText("点击拍照");
        }
        initData();
        TEXTURE_STATE = AppConstant.TEXTURE_PREVIEW_STATE;
        videoRecord.setVisibility(View.VISIBLE);
        videoHintText.setVisibility(View.VISIBLE);
//        videoSwitchFlash.setVisibility(View.VISIBLE);
    }

    /**
     * TextureView 触摸方法
     */
    private class CameraTouch {
        private float mOldScale = 1.0f;
        private float mScale;
        private float mSpan = 0;
        private float mOldSpan;
        private float mFirstDistance = 0;

        public void onScale(MotionEvent event) {
            if (event.getPointerCount() == 2) {
                if (mFirstDistance == 0) {
                    mFirstDistance = distance(event);
                }

                float distance = distance(event);
                float scale;
                if (distance > mFirstDistance) {
                    scale = (distance - mFirstDistance) / 80;
                    scale = scale + mSpan;
                    mOldSpan = scale;
                    mScale = scale;
                } else if (distance < mFirstDistance) {
                    scale = distance / mFirstDistance;
                    mOldSpan = scale;
                    mScale = scale * mOldScale;
                } else {
                    return;
                }

                cameraHelper.cameraZoom(mScale);
                videoScale.setProgress((int) ((mScale / cameraHelper.getMaxZoom()) * videoScale.getMax()));
                if (mScale < 1.0f) {
                    videoScale.setProgress(0);
                }
            }
        }

        /**
         * scale 开始
         *
         * @param event
         */
        public void onScaleStart(MotionEvent event) {
            mFirstDistance = 0;
            setScaleMax((int) cameraHelper.getMaxZoom());
            videoScaleBarLayout.setVisibility(View.VISIBLE);
            removeSeekBarRunnable();
        }

        /**
         * scale 结束
         *
         * @param event MotionEvent
         */
        private void onScaleEnd(MotionEvent event) {
            if (mScale < 1.0f) {
                mOldScale = 1.0f;
            } else if (mScale > cameraHelper.getMaxZoom()) {
                mOldScale = cameraHelper.getMaxZoom();
            } else {
                mOldScale = mScale;
            }
            mSpan = mOldSpan;

            if (event != null) {
                seekBarDelayedHind();
            }
        }

        /**
         * 重置 缩放
         */
        public void resetScale() {
            mOldScale = 1.0f;
            mSpan = 0f;
            mFirstDistance = 0f;
            videoScale.setProgress(0);
        }

        public void setScale(float scale) {
            mScale = scale;
            mOldSpan = scale;
            onScaleEnd(null);
        }

        /**
         * 计算两个手指间的距离
         *
         * @param event MotionEvent
         * @return 距离
         */
        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            // 使用勾股定理返回两点之间的距离
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        private void setScaleMax(int max) {
            videoScale.setMax(max * 100);
        }
    }

    /**
     * camera 点击对焦动画
     */
    private class FoucesAnimation extends Animation {

        private int width = cameraHelper.dip2px(CameraVideoActivity.this, 150);
        private int W = cameraHelper.dip2px(CameraVideoActivity.this, 65);

        private int oldMarginLeft;
        private int oldMarginTop;

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoFouces.getLayoutParams();
            int w = (int) (width * (1 - interpolatedTime));
            if (w < W) {
                w = W;
            }
            layoutParams.width = w;
            layoutParams.height = w;
            if (w == W) {
                videoFouces.setLayoutParams(layoutParams);
                return;
            }
            layoutParams.leftMargin = oldMarginLeft - (w / 2);
            layoutParams.topMargin = oldMarginTop + (w / 8);
            videoFouces.setLayoutParams(layoutParams);
        }

        public void setOldMargin(int oldMarginLeft, int oldMarginTop) {
            this.oldMarginLeft = oldMarginLeft;
            this.oldMarginTop = oldMarginTop;
            removeImageFoucesRunnable();
            imageFoucesDelayedHind();
        }
    }

    /**
     * 移除对焦 消失任务
     */
    private void removeImageFoucesRunnable() {
        videoFouces.removeCallbacks(mImageFoucesRunnable);
    }

    /**
     * 添加 延时消失任务
     */
    private void imageFoucesDelayedHind() {
        videoFouces.postDelayed(mImageFoucesRunnable, 500);
    }

    /**
     * seekBar 添加延时消失任务
     */
    private void seekBarDelayedHind() {
        if (isCanHind) {
            videoScaleBarLayout.postDelayed(SeekBarLayoutRunnalbe, 2000);
        }
        isCanHind = false;
    }

    private Runnable mImageFoucesRunnable = new Runnable() {
        @Override
        public void run() {
            videoFouces.setVisibility(View.GONE);
        }
    };

    /**
     * 移除隐藏 seekBar消失的任务
     */
    private void removeSeekBarRunnable() {
        isCanHind = true;
        videoScale.removeCallbacks(SeekBarLayoutRunnalbe);
    }

    /**
     * 3s后隐藏的runnable
     */
    private Runnable SeekBarLayoutRunnalbe = new Runnable() {
        @Override
        public void run() {
            videoScaleBarLayout.setVisibility(View.GONE);
        }
    };


    /**
     * 移动焦点图标
     *
     * @param x x坐标
     * @param y y坐标
     */
    private void moveFouces(int x, int y) {
        videoFouces.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoFouces.getLayoutParams();
        videoFouces.setLayoutParams(layoutParams);
        mFoucesAnimation.setDuration(500);
        mFoucesAnimation.setRepeatCount(0);
        mFoucesAnimation.setOldMargin(x, y);
        videoFouces.startAnimation(mFoucesAnimation);
        cameraHelper.requestFocus(x, y);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraHelper != null) {
            cameraHelper.startBackgroundThread();
        }

        if (videoTexture.isAvailable()) {
            if (MODE == AppConstant.CAMERA_MODE) {
                if (TEXTURE_STATE == AppConstant.TEXTURE_PREVIEW_STATE) {
                    //预览状态
                    initCamera(mNowCameraType);
                }
            }
        } else {
            videoTexture.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    if (MODE == AppConstant.CAMERA_MODE) {
                        if (TEXTURE_STATE == AppConstant.TEXTURE_PREVIEW_STATE) {
                            //预览状态
                            initCamera(mNowCameraType);
                        }
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isNoPremissionPause) {
            isNoPremissionPause = false;
            return;
        }
        Log.e("camera", "mode:" + MODE);
        if (MODE == AppConstant.CAMERA_MODE) {
            if (TEXTURE_STATE == AppConstant.TEXTURE_PREVIEW_STATE) {
                cameraHelper.closeCamera();
                cameraHelper.stopBackgroundThread();
            }
        }
    }

    /**
     * 底部 布局集合
     */
    private List<View> mLayoutList = new LinkedList<>();
    /**
     * visible与invisible之间切换的动画
     */
    private TranslateAnimation mShowAction;


    /**
     * 拍照或者录像
     */
    @OnClick(R.id.video_record)
    public void recordVideoOrTakePhoto() {
        if (hasRecordClick) {
            return;
        }
        hasRecordClick = true;
        //拍照
        if (NOW_MODE == AppConstant.VIDEO_TAKE_PHOTO) {
            mCameraPath = cameraHelper.getPhotoFilePath();
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            cameraHelper.setDeviceRotation(rotation);
            cameraHelper.takePhone(mCameraPath, ICamera2.MediaType.JPEG);
        }
        hasRecordClick = false;
    }


}
