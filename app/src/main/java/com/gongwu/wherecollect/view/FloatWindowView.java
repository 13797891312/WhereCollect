package com.gongwu.wherecollect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gongwu.wherecollect.R;
import com.gongwu.wherecollect.adapter.OnFloatClickListener;

/**
 * @ClassName: FloatWindowView
 * @Description: 全局悬浮按钮
 */
public class FloatWindowView extends LinearLayout {

    private TextView nameTv;
    private TextView hintTv;
    private ImageView floatIv;
    private Context mContext;
    private boolean enabled;

    public FloatWindowView(Context context) {
        super(context);
        initView(context, null);
    }

    public boolean getEnable() {
        return enabled;
    }

    public FloatWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet object) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_float_window_layout, this);
        nameTv = view.findViewById(R.id.float_name_tv);
        hintTv = view.findViewById(R.id.hint_text_view);
        floatIv = view.findViewById(R.id.float_iv);
        nameTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!enabled) return;
                if (onClickListener != null) {
                    onClickListener.onClick(nameTv);
                }
            }
        });

        nameTv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onLongClick(nameTv);
                }
                return false;
            }
        });
    }

    public void setNameTv(String text) {
        if (nameTv != null) {
            nameTv.setText(text);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            floatIv.setVisibility(View.GONE);
            hintTv.setText(mContext.getString(R.string.float_hint_text));
        } else {
            floatIv.setVisibility(View.VISIBLE);
            hintTv.setText(mContext.getString(R.string.float_un_hint_text));
        }
    }

    public OnFloatClickListener onClickListener;

    public void setOnFloatClickListener(OnFloatClickListener listener) {
        this.onClickListener = listener;
    }
}
