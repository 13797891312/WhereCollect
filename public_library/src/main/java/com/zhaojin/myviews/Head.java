package com.zhaojin.myviews;

import com.example.zj_library.R;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 自定义listView头部类 **/
public class Head extends RelativeLayout {
	/** 记录上次状态 **/
	int last;
	/** 转状态自转动画 **/
	Animation am;
	ProgressBar pb;
	TextView tv;
	ImageView iv;
	RelativeLayout rl;
	/** 布局文件对象的宽高对象 **/
	RelativeLayout.LayoutParams lp;
	/** 隐藏状态 */
	public final static int GONE = 0X04;
	/** 下拉刷新 */
	public final static int DOWN = 0X01;
	/** 松开刷新 */
	public final static int UP = 0X02;
	/** 正在刷新 */
	public final static int UPDATA = 0X03;
	/** 刷新完成 */
	public final static int OK = 0X05;
	/** 刷新失败 */
	public final static int NO = 0X06;
	public int current = GONE;
	private Handler hd = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				rl.setLayoutParams(lp);
				break;
			case 1:
				current = GONE;
				logic(current);
				break;
			}
		};
	};

	public Head(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		// 通过布局加载器加载文件
		rl = (RelativeLayout) View.inflate(context, R.layout.head, null);
		tv = (TextView) rl.findViewById(R.id.textView1);
		iv = (ImageView) rl.findViewById(R.id.imageView1);
		pb = (ProgressBar) rl.findViewById(R.id.progressBar1);
		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 0);
		this.addView(rl, lp);

	}

	/** 改变该头部对象的高的方法 **/
	public void setHeadHeight(int add) {
		lp.height += add;
		if (lp.height < 0) {
			lp.height = 0;
		}
		rl.setLayoutParams(lp);
		if (lp.height <= 150 && lp.height > 0) {
			this.logic(DOWN);
		} else if (lp.height > 150) {
			this.logic(UP);
		}
	}

	/** 头部对象不同状态下对应的逻辑 **/
	public void logic(int stataus) {
		current = stataus;
		switch (current) {
		case DOWN:
			if (last == UP) {
				am = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
						0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				iv.startAnimation(am);
				am.setDuration(300);
				am.setFillAfter(true);
			}
			tv.setText("下拉刷新");
			pb.setVisibility(View.GONE);
			iv.setVisibility(View.VISIBLE);
			break;
		case UP:
			if (last == DOWN) {
				am = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,
						0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				iv.startAnimation(am);
				am.setDuration(300);
				am.setFillAfter(true);
			}
			tv.setText("释放立即刷新");
			pb.setVisibility(View.GONE);
			iv.setVisibility(View.VISIBLE);
			break;
		case UPDATA:
			tv.setText("正在刷新");
			pb.setVisibility(View.VISIBLE);
			if (am != null) {
				am.setFillAfter(false);
			}
			iv.setVisibility(View.GONE);
			startAnim(lp.height, 130,2);
			break;
		case OK:
			iv.setVisibility(View.GONE);
			pb.setVisibility(View.GONE);
			tv.setText("刷新完成");
			hd.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startAnim(lp.height,0,1);
				}
			}, 1000);
			break;
		case NO:
			// lp.height = 130;
			// rl.setLayoutParams(lp);
			pb.setVisibility(View.GONE);
			iv.setVisibility(View.GONE);
			tv.setText("刷新失败");
			hd.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startAnim(lp.height,0,1);
				}
			}, 1000);
			break;
		case GONE:
			startAnim(lp.height, 0, 2);
			break;
		}
		last = current;
	}

	/***测试发现系统自带的属性动画效率高，不卡顿，但是低版本不支持要加入导包所以写了两个方法**/
	private void startAnim(int start, int end,final int type){
		if (android.os.Build.VERSION.SDK_INT<11) {
			startAnim1(start, end, type);
		}else
			startAnim2(start, end, type);
	}
	
	/***低版本***/
	private void startAnim1(int start, int end,final int type) {
		// TODO Auto-generated method stub
		final ValueAnimator animator = ValueAnimator.ofInt(start, end);
		animator.setDuration(150);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				lp.height = (Integer) animation.getAnimatedValue();
				rl.setLayoutParams(lp);
			}
		});
		animator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(
					com.nineoldandroids.animation.Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(
					com.nineoldandroids.animation.Animator animation) {
				// TODO Auto-generated method stub
				switch (type) {
				case 1:
					current = GONE;
					logic(current);
					break;
				case 2:
					break;
				}
			}

			@Override
			public void onAnimationCancel(
					com.nineoldandroids.animation.Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(
					com.nineoldandroids.animation.Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		animator.start();
	}
	/***高版本***/
	private void startAnim2(int start, int end,final int type) {
		// TODO Auto-generated method stub
		final android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
		animator.setDuration(150);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(
					android.animation.ValueAnimator animation) {
				// TODO Auto-generated method stub
				lp.height = (Integer) animation.getAnimatedValue();
				rl.setLayoutParams(lp);
				
			}
		});
		animator.addListener(new android.animation.Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				switch (type) {
				case 1:
					current = GONE;
					logic(current);
					break;
				case 2:
					break;
				}
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		animator.start();
	}
}
