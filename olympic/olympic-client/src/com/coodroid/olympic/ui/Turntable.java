package com.coodroid.olympic.ui;

import com.coodroid.olympic.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

//自定义的转盘View
public class Turntable extends View implements Runnable{
	
	//界面需要的图片
	private Bitmap panpic;
	private Bitmap panhandpic;
	//旋转矩阵
	private Matrix panRotate=new Matrix();
	//平移矩阵
	private Matrix panhandTrans=new Matrix();
	
	private int x=0;
	
	private boolean ifRotate=false;

	public Turntable(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		
		Resources r=context.getResources();
		//设置指针平移矩阵为按向量（160,160-指针的高度）平移
		panhandTrans.setTranslate(160, 160-76);
		
		//生成图片
		panpic=BitmapFactory.decodeStream(r.openRawResource(R.drawable.turntable_bg));
		panhandpic=BitmapFactory.decodeStream(r.openRawResource(R.drawable.turntable_handler));
		
		//用线程来刷新界面
		Thread thread=new Thread(this);
		thread.start();
		
	}
	
	//重写View类的onDraw()函数
	@Override
	protected void onDraw(Canvas canvas) {
		//设置转盘旋转矩阵为以（160,160）坐标为圆心，旋转X角度
		panRotate.setRotate(x, 160, 160);
		canvas.drawBitmap(panpic, panRotate, null);
		
		canvas.drawBitmap(panhandpic, panhandTrans, null);
		
		
	}

	//重写的run函数，用来控制转盘转动
	public void run() {
		try {
			
			while(true){
				
				if(ifRotate){
				
						this.x+=25;
						//这个函数强制UI线程刷新界面
						this.postInvalidate();
					
						Thread.sleep(50);
					
				}
				
				
			}
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		
	}
	
	public void startRotate(){
		
		this.ifRotate=true;
		
	}
	
	public void stopRotate(){
		
		this.ifRotate=false;
		
	}

}
