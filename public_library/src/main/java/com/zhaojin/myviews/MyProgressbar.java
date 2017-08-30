package com.zhaojin.myviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MyProgressbar extends View {

	private int progress = 0;
	private int strokeWidth = 14;
	private int maxProgress = 100;
	private int backColor = 0x90D5F8F8;
	private int progressColor = Color.BLACK;
	private int textSize = 20;
	private int textColor = Color.BLACK;
	private Paint paint = new Paint();
	private Paint paintText = new Paint();
	private RectF oval2;

	public MyProgressbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		init();
	}

	public MyProgressbar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Style.STROKE);
		paint.setColor(progressColor);
		paint.setAntiAlias(true);
		paintText.setColor(textColor);
		paintText.setTextSize(textSize);
		paintText.setAntiAlias(true);
		this.invalidate();
	}
/**
 * 
 * @param strokeWidth 圆圈宽度
 * @param maxProgress 最大进度
 * @param backColor 圆圈背景颜色
 * @param progressColor 进度条颜�?
 * @param textSize 中间字体大小
 * @param textColor 字体颜色
 */
	public void init(int strokeWidth, int maxProgress, int backColor,
			int progressColor, int textSize, int textColor) {
		this.backColor=backColor;
		this.strokeWidth=strokeWidth;
		this.maxProgress=maxProgress;
		this.progressColor=progressColor;
		this.textSize=textSize;
		this.textColor=textColor;
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (oval2 == null) {
			oval2 = new RectF(strokeWidth / 2, strokeWidth / 2, this.getWidth()
					- strokeWidth / 2, this.getHeight() - strokeWidth / 2);// 设置个新的长方形，扫描测�?
		}
		paint.setColor(backColor);
		canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2,
				this.getWidth() / 2 - strokeWidth / 2, paint);
		paint.setColor(progressColor);
		canvas.drawArc(oval2, 270, 360 * progress / maxProgress, false, paint);
		int lenght = ((int) (progress / (float) maxProgress * 100) + "/%")
				.length();
		canvas.drawText((int) (progress / (float) maxProgress * 100) + "/%",
				this.getWidth() / 2 - (float) textSize * lenght / 4,
				this.getHeight() / 2 + (float) textSize /3, paintText);
	}

	public void setProgress(int progress) {
		this.progress = progress;
		this.invalidate();
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public int getBackColor() {
		return backColor;
	}

	public void setBackColor(int backColor) {
		this.backColor = backColor;
	}

	public int getProgressColor() {
		return progressColor;
	}

	public void setProgressColor(int progressColor) {
		this.progressColor = progressColor;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

}
