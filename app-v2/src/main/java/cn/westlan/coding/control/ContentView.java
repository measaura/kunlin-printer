package cn.westlan.coding.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

public class ContentView extends androidx.appcompat.widget.AppCompatImageView {

	public ContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContentView(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//获取控件需要重新绘制的区域
		Rect rect=canvas.getClipBounds();
//		rect.bottom--;
//		rect.right--;
		Paint paint=new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawRect(rect, paint);
	}
   
}