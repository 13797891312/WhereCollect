package com.gongwu.wherecollect.view;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.object.ObjectsAddActivity;
import com.gongwu.wherecollect.util.ImageLoader;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * Function:
 * Date: 2017/9/13
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class AddPhoteGridLayout extends GridLayout {
    public List<File> mlist = new ArrayList<>();
    ImageEditDialog dialog;
    private Context context;
    private SelectImgDialog selectImgDialog;
    private ImageView camera;

    public AddPhoteGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public AddPhoteGridLayout(Context context) {
        this(context, null);
        init();
    }

    public void init() {
        View v = View.inflate(context, R.layout.item_addphote_gridlayout, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(BaseViewActivity.getScreenWidth(((Activity) context))
                / 4,
                BaseViewActivity.getScreenWidth(((Activity) context)) / 4);
        addView(v, lp);
        camera = (ImageView) v.findViewById(R.id.item_image);
        ImageView delete = (ImageView) v.findViewById(R.id.delete_image);
        camera.setScaleType(ImageView.ScaleType.CENTER);
        delete.setVisibility(GONE);
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mlist.size() >= 10) {
                    ToastUtil.show(context, "最多一次添加10件物品！", Toast.LENGTH_LONG);
                    return;
                }
                selectImgDialog = new SelectImgDialog(((Activity) context), null, 10 - mlist.size()) {
                    @Override
                    public void getResult(List<File> list) {
                        super.getResult(list);
                        for (int i = 0; i < StringUtils.getListSize(list); i++) {
                            addItem(list.get(i));
                        }
                        selectImgDialog = null;
                    }

                    @Override
                    protected void getBookResult(BookBean book) {
                        super.getBookResult(book);
                        selectImgDialog = null;
                        if (context instanceof ObjectsAddActivity) {
                            ((ObjectsAddActivity) context).updateBeanWithBook(book);
                        }
                    }
                };
            }
        });
    }

    public void addItem(final File file) {
        mlist.add(file);
        final View v = View.inflate(context, R.layout.item_addphote_gridlayout, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(BaseViewActivity.getScreenWidth(((Activity) context))
                / 4, BaseViewActivity.getScreenWidth(((Activity) context)) / 4);
        addView(v, mlist.size() - 1, lp);
        final ImageView image = (ImageView) v.findViewById(R.id.item_image);
        ImageView delete = (ImageView) v.findViewById(R.id.delete_image);
        ImageLoader.loadFromFile(context, file, image);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    if (getChildAt(i) == view) {
                        index = i;
                    }
                }
                final int finalIndex = index;
                dialog = new ImageEditDialog(((Activity) context), mlist.get(finalIndex)) {//裁剪图片
                    @Override
                    protected void resultFile(File resultFile) {
                        super.resultFile(resultFile);
                        ImageLoader.loadFromFile(context, resultFile, image);
                        mlist.remove(finalIndex);
                        mlist.add(finalIndex, resultFile);
                        dialog = null;
                    }

                    @Override
                    protected void delete() {
                        super.delete();
                        remove(file, v);
                    }
                };
            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(file, v);
            }
        });
        if (mlist.size() < 10) {
            camera.setVisibility(VISIBLE);
        } else {
            camera.setVisibility(GONE);
        }
    }

    private void remove(File file, View v) {
        removeView(v);
        mlist.remove(file);
        if (mlist.size() < 10) {
            camera.setVisibility(VISIBLE);
        } else {
            camera.setVisibility(GONE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (selectImgDialog != null) {
            selectImgDialog.onActivityResult(requestCode, resultCode, data);
        }
        if (dialog != null) {
            dialog.onActivityResult(requestCode, resultCode, data);
        }
    }
}
