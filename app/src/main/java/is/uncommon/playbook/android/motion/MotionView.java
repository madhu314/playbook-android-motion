package is.uncommon.playbook.android.motion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class MotionView extends View {
  private static final int OFFSET = 48;
  private static final int STROKE_WIDTH = 2;
  private static final int RADIUS = 8;
  private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private RectF canvasRect;
  private RectF drawingRect;
  private float offset;
  private float strokeWidth;
  private float radius;
  private PointF startPoint;
  private PointF endPoint;
  private Path linearPath = new Path();
  private Path quadraticPath = new Path();
  private Path cubicPath = new Path();
  private float maxValue = 10;

  public MotionView(Context context) {
    super(context);
  }

  public MotionView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MotionView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public MotionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    float left = getPaddingLeft();
    float top = getPaddingTop();
    float right = getWidth() - getPaddingRight();
    float bottom = getHeight() - getPaddingBottom();
    canvasRect = new RectF(left, top, right, bottom);
    Matrix matrix = new Matrix();
    float fitRectWidth;
    float fitRectHeight;
    if (canvasRect.width() > canvasRect.height()) {
      fitRectWidth = canvasRect.height();
      fitRectHeight = fitRectWidth;
    } else {
      fitRectWidth = canvasRect.width();
      fitRectHeight = fitRectWidth;
    }
    RectF square = new RectF(0, 0, fitRectWidth, fitRectHeight);
    matrix.setRectToRect(square, canvasRect, Matrix.ScaleToFit.CENTER);
    drawingRect = new RectF();
    matrix.mapRect(drawingRect, square);
    offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, OFFSET,
        getResources().getDisplayMetrics());
    strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH,
        getResources().getDisplayMetrics());
    radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RADIUS,
        getResources().getDisplayMetrics());
    drawingRect.inset(offset, offset);
    startPoint = new PointF(drawingRect.right, drawingRect.bottom);
    endPoint = new PointF(drawingRect.left, drawingRect.top);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawRect(canvasRect, paint);

    paint.setColor(Color.BLUE);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(strokeWidth);
    canvas.drawRect(drawingRect, paint);

    paint.setColor(Color.YELLOW);
    paint.setStrokeWidth(strokeWidth);
    paint.setStyle(Paint.Style.STROKE);
    canvas.drawPath(linearPath, paint);

    paint.setColor(Color.CYAN);
    paint.setStrokeWidth(strokeWidth);
    paint.setStyle(Paint.Style.STROKE);
    canvas.drawPath(quadraticPath, paint);

    paint.setColor(Color.MAGENTA);
    paint.setStrokeWidth(strokeWidth);
    paint.setStyle(Paint.Style.STROKE);
    canvas.drawPath(cubicPath, paint);

    paint.setColor(Color.GREEN);
    paint.setStyle(Paint.Style.FILL_AND_STROKE);
    canvas.drawCircle(startPoint.x, startPoint.y, radius, paint);

    paint.setColor(Color.RED);
    paint.setStyle(Paint.Style.FILL_AND_STROKE);
    canvas.drawCircle(endPoint.x, endPoint.y, radius, paint);
  }

  public void connect(float maxValue) {
    this.maxValue = maxValue;
    connectLinear();
    connectQuadraticBezier();
    connectCubicBezier();
    invalidate();
  }

  private void connectLinear() {
    linearPath.reset();
    linearPath.moveTo(startPoint.x, startPoint.y);
    for (int i = 0; i < maxValue; i++) {
      float t = i / 100f;
      float x = startPoint.x + t * (endPoint.x - startPoint.x);
      float y = startPoint.y + t * (endPoint.y - startPoint.y);
      linearPath.lineTo(x, y);
    }
  }

  private void connectQuadraticBezier() {
    quadraticPath.reset();
    quadraticPath.moveTo(startPoint.x, startPoint.y);
    PointF p1 = new PointF(drawingRect.left, drawingRect.bottom);
    for (int i = 0; i < maxValue; i++) {
      float t = i / 100f;
      float x = (float) (Math.pow((1 - t), 2) * startPoint.x
          + 2 * (1 - t) * t * p1.x
          + Math.pow(t, 2) * endPoint.x);
      float y = (float) (Math.pow((1 - t), 2) * startPoint.y
          + 2 * (1 - t) * t * p1.y
          + Math.pow(t, 2) * endPoint.y);
      quadraticPath.lineTo(x, y);
    }
  }

  private void connectCubicBezier() {
    cubicPath.reset();
    cubicPath.moveTo(startPoint.x, startPoint.y);
    PointF p1 = new PointF(drawingRect.right - drawingRect.width() * 0.5f, drawingRect.bottom);
    PointF p2 = new PointF(drawingRect.left, drawingRect.bottom - drawingRect.width() * 0.5f);
    for (int i = 0; i < maxValue; i++) {
      float t = i / 100f;
      float x = (float) (Math.pow((1 - t), 3) * startPoint.x
          + 3 * Math.pow((1 - t), 2) * t * p1.x
          + 3 * Math.pow(t, 2) * (1 - t) * p2.x
          + Math.pow(t, 3) * endPoint.x);
      float y = (float) (Math.pow((1 - t), 3) * startPoint.y
          + 3 * Math.pow((1 - t), 2) * t * p1.y
          + 3 * Math.pow(t, 2) * (1 - t) * p2.y
          + Math.pow(t, 3) * endPoint.y);
      cubicPath.lineTo(x, y);
    }
  }
}
