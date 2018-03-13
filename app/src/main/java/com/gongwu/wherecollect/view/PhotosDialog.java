package com.gongwu.wherecollect.view;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.util.ImageLoader;
import com.zhaojin.myviews.TagViewPagerPhotos;
import com.zhaojin.myviews.TagViewPagerPhotos.OnGetView;
import com.zhaojin.myviews.TagViewPagerPhotos.OnSelectedListoner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
public class PhotosDialog implements OnClickListener {
    private TagViewPagerPhotos myViewPager;
    private Dialog dialog;
    OnClickListener mlistener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };
    private Context context;
    private OndismissListener listener;
    private boolean isSave, SelectVisible;
    private ImageView imageSelect;
    private List<ImageData> imageList;
    private int currentPositon;
    private TextView save;

    /**
     * @param context
     * @param uriList
     */
    public PhotosDialog(Context context, boolean isSave, boolean selectVisible, List<ImageData>
            uriList) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.SelectVisible = selectVisible;
        this.isSave = isSave;
        dialog = new Dialog(context, R.style.blackDialog);
        View popView = View.inflate(context, R.layout.layout_pop_showphotos, null);
        dialog.setContentView(popView);
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });
        this.imageList = uriList;
        initPagerView(popView);
    }

    public void setOndismissListener(OndismissListener listener) {
        this.listener = listener;
    }

    private void initPagerView(View view) {
        // TODO Auto-generated method stub
        save = (TextView) view.findViewById(R.id.textView1);
        if (isSave) {
            save.setVisibility(View.VISIBLE);
            save.setOnClickListener(this);
        } else {
            save.setVisibility(View.GONE);
        }
        imageSelect = (ImageView) view.findViewById(R.id.image_pager_selector);
        imageSelect.setOnClickListener(this);
        if (SelectVisible) {
            imageSelect.setVisibility(View.VISIBLE);
        } else {
            imageSelect.setVisibility(View.GONE);
        }
        imageSelect.setSelected(imageList.get(currentPositon).isSelect());
        myViewPager = (TagViewPagerPhotos) view.findViewById(R.id.showPhotos_viewpager);
        myViewPager.setAutoNext(false, 0);
        myViewPager.init(R.drawable.shape_photo_tag_select, R.drawable.shape_photo_tag_nomal, 14,
                4, 2, 100);
        myViewPager.setOnGetView(new OnGetView() {
            @Override
            public View getView(final ViewGroup container, int position) {
                // TODO Auto-generated method stub
                View view = View.inflate(container.getContext(), R.layout
                        .layout_photodialog_page, null);
                final PhotoView iv = (PhotoView) view.findViewById(R.id.photo_iv);
                iv.setOnClickListener(mlistener);
                iv.setId(position);
                final ProgressBar bar = (ProgressBar) view.findViewById(R.id.progress_bar);
                container.addView(view);
                if (TextUtils.isEmpty(imageList.get(position).getUrl()) && !TextUtils.isEmpty(imageList.get(position)
                        .getBigUri())) {
                    ImageLoader.loadUrlAsBitmap(new File(imageList.get(position).getBigUri()), iv, container
                            .getContext());
                    bar.setVisibility(View.GONE);
                } else {
                    Glide.with(container.getContext())
                            .load(imageList.get(position).getUrl())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                                           boolean isFirstResource) {
                                    Toast.makeText(context, "图片加载失败", Toast.LENGTH_SHORT).show();
                                    bar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target, boolean
                                                                       isFromMemoryCache, boolean isFirstResource) {
                                    bar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv);
                    bar.setVisibility(View.GONE);
                }
                return view;
            }
        });
        myViewPager.setOnSelectedListoner(new OnSelectedListoner() {
            @Override
            public void onSelected(int position) {
                // TODO Auto-generated method stub
                currentPositon = position;
                imageSelect.setSelected(imageList.get(currentPositon).isSelect());
            }
        });
    }

    /**
     * 预览大图
     */
    public void showPhotos(int position) {
        currentPositon = position;
        myViewPager.setAdapter(imageList.size(), position);
        dialog.show();
    }

    /**
     * 退出预览
     */
    public void dismissPhotos() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * @return 当前显示的第几项；
     */
    public int getCurrentItem() {
        return currentPositon;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView1:
                saveBitmap();
                break;
            case R.id.image_pager_selector:
                imageSelect.setSelected(!imageSelect.isSelected());
                imageList.get(currentPositon).setSelect(imageSelect.isSelected());
                break;
        }
    }

    private void saveBitmap() {
        PhotoView iv = (PhotoView) myViewPager.findViewById(currentPositon);
        Bitmap bitmap = ((GlideBitmapDrawable) (iv.getDrawable())).getBitmap();
        saveCroppedImage(bitmap);
    }

    private void saveCroppedImage(Bitmap bmp) {
        File file = new File(MyApplication.CACHEPATH);
        if (!file.exists())
            file.mkdir();
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
        // /sdcard/myFolder/temp_cropped.jpg
        String newFilePath = MyApplication.CACHEPATH + fileName;
        file = new File(newFilePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            Toast.makeText(dialog.getContext(), "图片已保存至" + newFilePath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(dialog.getContext(), "保存图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OndismissListener {
        public void onDismiss();
    }
}
