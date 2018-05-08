package com.gongwu.wherecollect.ImageSelect;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.entity.ImageData;
import com.gongwu.wherecollect.util.StringUtils;
import com.gongwu.wherecollect.view.PhotosDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
public class ImageGridActivity extends BaseViewActivity implements PhotosDialog.OndismissListener {
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static final int RESULT = 834;
    public static int max=10;
    List<ImageData> dataList;
    GridView gridView;
    ImageGridAdapter adapter;// 列表适配器
    AlbumHelper helper;
    /**
     *
     */
    ArrayList<ImageData> temp = new ArrayList<ImageData>();
    private PhotosDialog photosDialog;

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent(context, ImageGridActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);
        ButterKnife.bind(this);
        titleLayout.setTitle("选择照片");
        titleLayout.setBack(true, null);
        titleLayout.textBtn.setText("确定");
        titleLayout.textBtn.setVisibility(View.VISIBLE);
        max=getIntent().getIntExtra("max",10);
        titleLayout.textBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("list", (Serializable) adapter.chooseList);
                setResult(RESULT, intent);
                finish();
            }
        });
        helper = AlbumHelper.getHelper();
        helper.init(this);
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.init(this);
        dataList = helper.getImages();
        initView();
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,max) {
            @Override
            protected void change(List<ImageData> chooseList) {
                super.change(chooseList);
                if (StringUtils.isEmpty(chooseList)) {
                    titleLayout.setTitle("选择照片");
                } else {
                    titleLayout.setTitle(String.format("已选择(%s)", chooseList.size()));
                }
            }
        };
        gridView.setAdapter(adapter);
//        gridView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                temp.clear();
//                temp.add(dataList.get(position));
//                photosDialog = new PhotosDialog(ImageGridActivity.this, false, true, temp);
//                photosDialog.setOndismissListener(ImageGridActivity.this);
//                photosDialog.showPhotos(0);
//            }
//        });
    }

    @Override
    public void onDismiss() {
        if (temp.isEmpty()) {
            return;
        } else {
            if (temp.get(0).isSelect()) {
                if (!adapter.chooseList.contains(temp.get(0))) {
                    adapter.chooseList.add(temp.get(0));
                }
            } else {
                if (adapter.chooseList.contains(temp.get(0))) {
                    adapter.chooseList.remove(temp.get(0));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
