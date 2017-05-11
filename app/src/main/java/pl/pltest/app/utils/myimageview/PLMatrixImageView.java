package pl.pltest.app.utils.myimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;

public class PLMatrixImageView extends ImageView {
	public GestureDetector mGestureDetector;

	private float bitmapWidth;
	private float bitmapHeight;
	private float fitcenterScale;
	private float[] fitCenterValues = new float[9];
	private PointF midPointF = new PointF();

	private OnMovingListener moveListener;
	private OnSingleTapListener singleTapListener;

	protected Handler mHandler = new Handler();
	private Matrix fitCenterMatrix = new Matrix();
	private Matrix mCurrentMatrix = new Matrix();

	private int screenWidth;
	private int screenHeight;
	private float initImageHeight;
	private float initYT;

	public float resetScale = 1;

	public float mEndRotate = 0;

	public PLMatrixImageView(Context context) {
		super(context, null);

		MatrixOnTouchListener matrixOnTouchListener = new MatrixOnTouchListener();
		setOnTouchListener(matrixOnTouchListener);
		mGestureDetector = new GestureDetector(getContext(),
				new MySimpleGesture(matrixOnTouchListener));
	}

	public PLMatrixImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		MatrixOnTouchListener matrixOnTouchListener = new MatrixOnTouchListener();
		setOnTouchListener(matrixOnTouchListener);
		mGestureDetector = new GestureDetector(getContext(),
				new MySimpleGesture(matrixOnTouchListener));
	}

	public void setOnMovingListener(OnMovingListener listener) {
		moveListener = listener;
	}

	public void setOnSingleTapListener(OnSingleTapListener onSingleTapListener) {
		this.singleTapListener = onSingleTapListener;
	}

	/**
	 * ImageView中，两函数的执行顺序：
	 * 先执行onDraw(Canvas canvas)
	 * 后执行setImageBitmap(Bitmap bitmap)
	 */
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		setScaleType(ScaleType.FIT_CENTER);
		if (getWidth() == 0) // 大小为0 表示当前控件大小未测量 设置监听函数 在绘制前赋值
		{
			ViewTreeObserver vto = getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					initFitCenter();
					// 赋值结束后，移除该监听函数
					PLMatrixImageView.this.getViewTreeObserver()
							.removeOnPreDrawListener(this);
					return true;
				}
			});
		} else {
			initFitCenter();
		}
	}

	/**
	 * 初始化模板Matrix和图片的其他数据
	 */
	private void initFitCenter() {
		DisplayMetrics dmMetrics = getResources().getDisplayMetrics();
		screenWidth = dmMetrics.widthPixels;
		screenHeight = dmMetrics.heightPixels;
		fitCenterMatrix.set(getImageMatrix());
		fitCenterMatrix.getValues(fitCenterValues);

		bitmapWidth = getWidth() / fitCenterValues[Matrix.MSCALE_X];
		bitmapHeight = (getHeight() - fitCenterValues[Matrix.MTRANS_Y] * 2)
				/ fitCenterValues[Matrix.MSCALE_Y];
		fitcenterScale = fitCenterValues[Matrix.MSCALE_X];

		RectF initRectF = new RectF(0, 0, bitmapWidth, bitmapHeight);
		fitCenterMatrix.mapRect(initRectF);

		initImageHeight = initRectF.height();
		initYT = initRectF.top;
	}

	public class MatrixOnTouchListener implements OnTouchListener {
		private static final int MODE_DRAG = 1;
		/** 拖拉模式 */
		private static final int MODE_ZOOM = 2;
		/** 手势缩放模式 */
		private static final int MODE_UNABLE = 3;
		/** 不支持Matrix */

		private int mMode = 0;
		private float mStartDis;
		/** 缩放开始时的手指间距 */

		/** 和ViewPager交互相关，判断当前是否可以左移、右移 */
		boolean mLeftDragable;
		boolean mRightDragable;
		boolean mFirstMove = false;
		/** 是否第一次移动 */

		private PointF mStartPoint = new PointF();
		private PointF zoomToPointF = new PointF();
		/** 当前Matrix */
		private Matrix realTimeZoomMatrix;
		private Matrix zoomToMatrix;		
		private Matrix dragMatrix;
		private PointF dragPointF;

		private int dragFlag = 0;
		private int zoomFlag = 0;
		private int doubleTapFlag = 0;

		private float realTimeScale;
		private float postXX;
		private float postYY;
		
//		private float mStartRotate;
//		private float backToFitScale;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				
				mMode = MODE_DRAG;
				mStartPoint.set(event.getX(), event.getY());
				
				getRealTimeAttrs();
				isMatrixEnable();
				startDrag();
				checkDragable();
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:

				stopDrag();
				break;

			case MotionEvent.ACTION_POINTER_DOWN:

				if (mMode == MODE_UNABLE)
					return true;
				mMode = MODE_ZOOM;
				mStartDis = distance(event);
//				mStartRotate = mEndRotate(event);
				getMidPointF(midPointF, event);
				break;

			case MotionEvent.ACTION_MOVE:

				if (mMode == MODE_ZOOM) {
					setZoomMatrix(event);					
				} else if (mMode == MODE_DRAG) {
					setDragMatrix(event);
				} else {
					stopDrag();
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
				/*
				 * 当手势缩放后的realTimeScale < fitCenterScale时
				 * 手指抬起后，需要弹回到fitCenterScale，添加动画过程 
				 */
				getRealTimeAttrs();
				if (realTimeScale < fitcenterScale) {
					zoomTo(fitcenterScale, screenWidth/2, screenHeight/2, 320f);
				}
				break;

			default:
				break;
			}
			return mGestureDetector.onTouchEvent(event);
		}

		/**
		 * 获取两手指触点的中点
		 * @param midPointF2
		 * @param event
		 */
		private void getMidPointF(PointF midPointF2, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
	 		float y = event.getY(0) + event.getY(1);
	 		midPointF2.set(x/2, y/2);
		}

//		private float mEndRotate(MotionEvent event) {
//			double delta_x = (event.getX(0) - event.getX(1));
//			double delta_y = (event.getY(0) - event.getY(1));
//			double radians = Math.atan2(delta_y, delta_x);
//			return (float) Math.toDegrees(radians);
//		}
		
		private void getRealTimeAttrs() {
			realTimeZoomMatrix = getImageMatrix();
			float[] realTimeValues = new float[9];
			realTimeZoomMatrix.getValues(realTimeValues);
			realTimeScale = realTimeValues[Matrix.MSCALE_X];
		}

		/**
		 * 子控件开始进入移动状态，令ViewPager无法拦截对子控件的Touch事件
		 */
		private void startDrag() {
			if (moveListener != null)
				moveListener.startDrag();
		}

		/**
		 * 子控件开始停止移动状态，ViewPager将拦截对子控件的Touch事件
		 */
		private void stopDrag() {
			if (moveListener != null)
				moveListener.stopDrag();
		}

		/**
		 * 根据当前图片左右边缘设置可拖拽状态
		 */
		private void checkDragable() {
			mLeftDragable = true;
			mRightDragable = true;
			mFirstMove = true;

			float[] values = new float[9];
			getImageMatrix().getValues(values);

			/**
			 * 图片左边缘离开左边界，表示不可右移 图片右边缘离开右边界，表示不可左移
			 */
			if (values[Matrix.MTRANS_X] >= 0)
				mRightDragable = false;

			if ((bitmapWidth) * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X] <= getWidth()) {
				mLeftDragable = false;
			}
		}

		/**
		 * 设置拖拽状态下的Matrix
		 * @param event
		 */
		public void setDragMatrix(MotionEvent event) {
			if (isZoomChanged()) {
				float dx = event.getX() - mStartPoint.x; // 得到x轴的移动距离
				float dy = event.getY() - mStartPoint.y; // 得到x轴的移动距离

				if (Math.sqrt(dx * dx + dy * dy) > 10f) // 避免和双击冲突,大于10f才算是拖动
				{
					mStartPoint.set(event.getX(), event.getY());

					mCurrentMatrix.set(getImageMatrix()); // 在当前基础上移动
					float[] values = new float[9];
					mCurrentMatrix.getValues(values);

					dy = checkDyBound(values, dy);
					dx = checkDxBound(values, dx, dy);

					mCurrentMatrix.postTranslate(dx, dy);
					setImageMatrix(mCurrentMatrix);

					dragFlag = 1;
					zoomFlag = 0;
					dragMatrix = getImageMatrix();
					dragPointF = getCenterPoint(true, true, dragMatrix);
				}
			} else {
				stopDrag();
			}
		}

		/**
		 * 判断缩放级别是否是改变过
		 * @return true表示非初始值,false表示初始值
		 */
		private boolean isZoomChanged() {
			
			float[] values = new float[9];
			getImageMatrix().getValues(values);

			float scale = values[Matrix.MSCALE_X]; // 获取当前X轴缩放级别
			return scale != fitcenterScale; // 获取模板的X轴缩放级别，两者做比较
		}

		/**
		 * 和当前矩阵对比，检验dy，使图像移动后不会超出ImageView边界
		 * @param values
		 * @param dy
		 */
		private float checkDyBound(float[] values, float dy) {
			float height = getHeight();
			if (bitmapHeight * values[Matrix.MSCALE_Y] < height)
				return 0;

			if (values[Matrix.MTRANS_Y] + dy > 0)
				dy = -values[Matrix.MTRANS_Y];

			else if (values[Matrix.MTRANS_Y] + dy < -(bitmapHeight * values[Matrix.MSCALE_Y] - height))
				dy = -(bitmapHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
			return dy;
		}

		/**
		 * 和当前矩阵对比，检验dx，使图像移动后不会超出ImageView边界
		 * @param values
		 * @param dx
		 */
		private float checkDxBound(float[] values, float dx, float dy) {
			
			float width = getWidth();
			if (!mLeftDragable && dx < 0) {
				// 加入和y轴的对比，表示在监听到垂直方向的手势时不切换Item
				if (Math.abs(dx) * 0.4f > Math.abs(dy) && mFirstMove) {
					stopDrag();
				}
				return 0;
			}

			if (!mRightDragable && dx > 0) {
				if (Math.abs(dx) * 0.4f > Math.abs(dy) && mFirstMove) {
					stopDrag();
				}
				return 0;
			}

			mLeftDragable = true;
			mRightDragable = true;
			if (mFirstMove)
				mFirstMove = false;

			if (bitmapWidth * values[Matrix.MSCALE_X] < width) {
				return 0;
			}

			if (values[Matrix.MTRANS_X] + dx > 0) {
				dx = -values[Matrix.MTRANS_X];
			}

			else if (values[Matrix.MTRANS_X] + dx < -(bitmapWidth * values[Matrix.MSCALE_X] - width)) {
				dx = -(bitmapWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
			}
			return dx;
		}

		/**
		 * 手势缩放
		 */
		private void setZoomMatrix(MotionEvent event) {
			float pointX = 0, pointY = 0;

			if (event.getPointerCount() < 2) // 只有同时触屏两个点的时候才执行
				return;

			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			float endDis = (float) Math.sqrt(dx * dx + dy * dy); // 结束距离
			
//			mEndRotate  = mEndRotate(event) - mStartRotate;

			if (endDis > 10f) // 两个手指并拢在一起的时候像素大于10
			{
				float scale = endDis / mStartDis; // 得到缩放倍数
				resetScale  = mStartDis / endDis;
				mStartDis = endDis; // 重置距离

				mCurrentMatrix.set(getImageMatrix()); // 初始化Matrix
				float[] values = new float[9];
				mCurrentMatrix.getValues(values);
				float currentScale = values[Matrix.MSCALE_X];

				/* 判断缩放的中心点   */
				if (scale > 1) {
					if (currentScale >= fitcenterScale) {
						pointX = Math.min(event.getX(1), event.getX(0)) + Math.abs(dx / 2);
						pointY = Math.min(event.getY(1), event.getY(0)) + Math.abs(dy / 2);
					} else {
						pointX = getWidth() / 2;
						pointY = getHeight() / 2;
					}
				} else {
					if (currentScale <= fitcenterScale) {
						pointX = getWidth() / 2;
						pointY = getHeight() / 2;
					} else {
						pointX = zoomToPointF.x;
						pointY = zoomToPointF.y;
					}
				}
				
				mCurrentMatrix.postScale(scale, scale, pointX, pointY);
				/* 此处可添加旋转的代码逻辑  */
				setImageMatrix(mCurrentMatrix);	

				zoomFlag = 1;
				dragFlag = 0;

				zoomToMatrix = getImageMatrix();
				zoomToPointF = getCenterPoint(true, true, zoomToMatrix);
			}
		}

		/**
		 * 图片处于放大状态，若进行双击操作，为了实现图片的平滑缩小，需要获取准确的缩放中心点。
		 * @param horizontal
		 * @param vertical
		 * @param zoomToMatrix
		 * @return PointF
		 */
		public PointF getCenterPoint(boolean horizontal, boolean vertical,
				Matrix zoomToMatrix) {
			RectF getCenterPointRectF = new RectF(0, 0, bitmapWidth,
					bitmapHeight);
			zoomToMatrix.mapRect(getCenterPointRectF);

			float width = getCenterPointRectF.width();
			float height = getCenterPointRectF.height();
			float centerX = 0, centerY = 0;

			if (vertical) {
				int viewHeight = getHeight(); // viewHeight View????
				if (height < viewHeight) {
					centerY = viewHeight / 2;
				} else {
					centerY = initYT + initImageHeight
							* (initYT - getCenterPointRectF.top)
							/ (getCenterPointRectF.height() - initImageHeight);
				}
			}

			if (horizontal) {
				int viewWidth = getWidth();
				if (width < viewWidth) {
					centerX = viewWidth / 2;
				} else {
					centerX = viewWidth
							* Math.abs(getCenterPointRectF.left)
							/ Math.abs(getCenterPointRectF.width()
									- screenWidth);
				}
			}
			return new PointF(centerX, centerY);
		}

		/**
		 * 居中操作
		 */
		private void center(boolean horizontal, boolean vertical) {
			float deltaX = 0, deltaY = 0;

			float[] values = new float[9];
			getImageMatrix().getValues(values);

			if (vertical) {
				float height = bitmapHeight * values[Matrix.MSCALE_Y];

				if (height < getHeight()) {
					float topMargin = (getHeight() - height) / 2; // 在图片真实高度小于容器高度时，Y轴居中，Y轴理想偏移量为两者高度差/2，

					if (topMargin != values[Matrix.MTRANS_Y]) {
						deltaY = topMargin - values[Matrix.MTRANS_Y];
					}
				}
			}

			if (horizontal) {
				float width = bitmapWidth * values[Matrix.MSCALE_X];

				if (width < getWidth()) {
					float leftMargin = (getWidth() - width) / 2; // 在图片真实高度小于容器高度时，Y轴居中，Y轴理想偏移量为两者高度差/2，

					if (leftMargin != values[Matrix.MTRANS_X]) {
						deltaX = leftMargin - values[Matrix.MTRANS_X];
					}
				}
			}

			mCurrentMatrix.set(getImageMatrix());
			mCurrentMatrix.postTranslate(deltaX, deltaY);
			setImageMatrix(mCurrentMatrix);
		}

		/**
		 * 判断是否支持Matrix
		 */
		private void isMatrixEnable() {
			// 当加载出错时，不可缩放
			if (getScaleType() != ScaleType.CENTER) {
				setScaleType(ScaleType.MATRIX);
			} else {
				mMode = MODE_UNABLE; // 设置为不支持手势
			}
		}

		/**
		 * 计算两个手指间的距离
		 */
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);

			return (float) Math.sqrt(dx * dx + dy * dy);
		}

		/**
		 * 双击时触发
		 * 为了提高用户体验，需要精确的判断缩放坐标点
		 */
		public void onDoubleClick(MotionEvent e) {
			float centerPointX = 0, centerPointY = 0;
			float preXX = e.getX();
			float preYY = e.getY();

			float scale = isZoomChanged() ? fitcenterScale : 2 * fitcenterScale;

			if (realTimeScale > fitcenterScale) {
				if (dragFlag == 1) {
					doubleTapFlag = 0;
					centerPointX = dragPointF.x;
					centerPointY = dragPointF.y;
					dragFlag = 0;
				} else if (zoomFlag == 1) {
					doubleTapFlag = 0;
					centerPointX = zoomToPointF.x;
					centerPointY = zoomToPointF.y;
					zoomFlag = 0;
				} else if (doubleTapFlag == 1) {
					centerPointX = postXX;
					centerPointY = postYY;
					doubleTapFlag = 0;
				}
			} else if (realTimeScale < fitcenterScale) {
				centerPointX = getWidth() / 2;
				centerPointY = getHeight() / 2;
			} else {
				centerPointX = preXX;
				centerPointY = preYY;
				postXX = preXX;
				postYY = preYY;
				doubleTapFlag = 1;
				Log.i("pre", zoomFlag + "/" + dragFlag + "/" + doubleTapFlag
						+ "/" + postXX);
			}
			Log.i("post", centerPointX + "/" + centerPointY + "/" + postXX);
			zoomTo(scale, centerPointX, centerPointY, 240f);
		}

		/**
		 * currentScale ！= fitCenterScale时，点击或是弹回都需要smooth动画
		 * 提高用户体验
		 * @param scale  目标scale
		 * @param postXX  缩放坐标之X轴坐标
		 * @param postYY  缩放坐标之Y轴坐标
		 * @param durationMs  缩放时间，float类型，值越大，缩放动画速度越慢
		 */
		public void zoomTo(float scale, final float postXX, final float postYY,
				final float durationMs) {
			final float incrementPerMs = (scale - realTimeScale) / durationMs;
			final long startTime = System.currentTimeMillis();

			mHandler.post(new Runnable() {
				public void run() {
					long now = System.currentTimeMillis();
					float currentMs = Math.min(durationMs, now - startTime);
					float target = realTimeScale + (incrementPerMs * currentMs);

					zoomTo(target, postXX, postYY);

					if (currentMs < durationMs) {
						mHandler.post(this);
					}
				}
			});
		}

		/**
		 * 具体的缩放过程
		 * @param target  被微分了的scale
		 * @param postXX  
		 * @param postYY  缩放的坐标(postXX, postYY)
		 */
		protected void zoomTo(float target, float postXX, float postYY) {
			mCurrentMatrix.set(getImageMatrix());
			float[] values = new float[9];
			mCurrentMatrix.getValues(values);

			float targetScale = target / values[Matrix.MSCALE_X];

			mCurrentMatrix.postScale(targetScale, targetScale, postXX, postYY);
			setImageMatrix(mCurrentMatrix);
			center(true, true);
		}
	}

	private class MySimpleGesture extends SimpleOnGestureListener {
		private final MatrixOnTouchListener listener;

		public MySimpleGesture(MatrixOnTouchListener listener) {
			this.listener = listener;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;  //必须return true，不然无法接收到点击事件
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			listener.onDoubleClick(e); // 触发双击事件
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (singleTapListener != null)
				singleTapListener.onSingleTap();
			return super.onSingleTapConfirmed(e);
		}
	}

	/**
	 * @ClassName: OnChildMovingListener
	 * @Description: PLMatrixImageView移动监听接口,用以组织ViewPager对Move操作的拦截
	 * @author PangLei
	 */
	public interface OnMovingListener {
		public void startDrag();

		public void stopDrag();
	}

	/**
	 * @ClassName: OnSingleTapListener
	 * @Description: 监听ViewPager屏幕单击事件，本质是监听子控件PLMatrixImageView的单击事件
	 */
	public interface OnSingleTapListener {
		public void onSingleTap();
	}

//	public void resetmEndRotate() {
//		if (mEndRotate<-315) {
//				mCurrentMatrix.postRotate(-360-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < -270) {
//			mCurrentMatrix.postRotate(-270-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < -225) {
//			mCurrentMatrix.postRotate(-270-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < -180) {
//			mCurrentMatrix.postRotate(-180-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < -135) {
//			mCurrentMatrix.postRotate(-180-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < -90) {
//			mCurrentMatrix.postRotate(-90-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < -45) {
//			mCurrentMatrix.postRotate(-90-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 0) {
//			mCurrentMatrix.postRotate(0-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 45) {
//			mCurrentMatrix.postRotate(0-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 90) {
//			mCurrentMatrix.postRotate(90-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 135) {
//			mCurrentMatrix.postRotate(90-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 180) {
//			mCurrentMatrix.postRotate(180-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 225) {
//			mCurrentMatrix.postRotate(180-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 270) {
//			mCurrentMatrix.postRotate(270-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 315) {
//			mCurrentMatrix.postRotate(270-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}else if (mEndRotate < 360) {
//			mCurrentMatrix.postRotate(360-mEndRotate, midPointF.x, midPointF.y);// 旋轉
//		}
//	}
}
