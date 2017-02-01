package is.uncommon.playbook.android.motion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Outline;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewOutlineProvider;

public class FabMotionActivity extends AppCompatActivity {
  private boolean hasMoved = false;
  private View rootContainer;
  private View fab;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fab_motion);
    rootContainer = findViewById(R.id.rootContainer);
    fab = findViewById(R.id.fab);
    fab.setOutlineProvider(new ViewOutlineProvider() {
      @Override public void getOutline(View view, Outline outline) {
        int fabSize = view.getHeight();
        outline.setOval(0, 0, fabSize, fabSize);
      }
    });
    fab.setClipToOutline(true);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        reveal();
      }
    });
  }

  private void reveal() {
    final int margin = getResources().getDimensionPixelSize(R.dimen.margin);

    int xTrans = (int) (rootContainer.getWidth() / 2f - fab.getWidth() / 2f - margin);
    int yTrans = (int) (rootContainer.getHeight() / 2f - fab.getHeight() / 2f - margin);

    final float controlPointX = xTrans;
    final float controlPointY = 0;

    final float startX = hasMoved ? xTrans : 0;
    final float endX = hasMoved ? 0 : xTrans;

    final float startY = hasMoved ? yTrans : 0;
    final float endY = hasMoved ? 0 : yTrans;

    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float t = (float) valueAnimator.getAnimatedValue();
        float toX = quadraticBezier(startX, endX, controlPointX, t);
        float toY = quadraticBezier(startY, endY, controlPointY, t);
        ViewCompat.setTranslationX(fab, -(int) toX);
        ViewCompat.setTranslationY(fab, -(int) toY);
      }
    });
    animator.setInterpolator(new FastOutSlowInInterpolator());
    animator.setDuration(300);
    animator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        fab.setEnabled(false);
      }

      @Override public void onAnimationEnd(Animator animation) {
        fab.setEnabled(true);
      }
    });
    animator.start();

    hasMoved = !hasMoved;
  }

  private float quadraticBezier(float start, float end, float controlPoint, float t) {
    return (float) (Math.pow((1 - t), 2) * start
        + 2 * (1 - t) * t * controlPoint
        + Math.pow(t, 2) * end);
  }
}
