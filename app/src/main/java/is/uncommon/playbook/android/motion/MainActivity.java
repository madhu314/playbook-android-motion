package is.uncommon.playbook.android.motion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MainActivity extends AppCompatActivity {

  private MotionView motionView;
  private Button connectButton;
  private Spinner spinner;
  private Interpolator interpolator = InterpolationType.values()[0].interpolator;

  enum InterpolationType {
    LinearOutSlowInInterpolator("LinearOutSlowInInterpolator", new LinearOutSlowInInterpolator()),
    FastOutSlowInInterpolator("FastOutSlowInInterpolator", new FastOutSlowInInterpolator()),
    FastOutLinearInInterpolator("FastOutLinearInInterpolator", new FastOutLinearInInterpolator()),
    AccelerateDecelerateInterpolator("AccelerateDecelerateInterpolator",
        new AccelerateDecelerateInterpolator()),
    AnticipateInterpolator("AnticipateInterpolator", new AnticipateInterpolator()),
    AccelerateInterpolator("AccelerateInterpolator", new AccelerateInterpolator()),
    AnticipateOvershootInterpolator("AnticipateOvershootInterpolator",
        new AnticipateOvershootInterpolator()),
    BounceInterpolator("BounceInterpolator", new BounceInterpolator()),
    CycleInterpolator("CycleInterpolator", new CycleInterpolator(2)),
    DecelerateInterpolator("DecelerateInterpolator", new DecelerateInterpolator()),
    LinearInterpolator("LinearInterpolator", new LinearInterpolator()),
    OvershootInterpolator("OvershootInterpolator", new OvershootInterpolator());

    final Interpolator interpolator;
    final String name;

    InterpolationType(String name, Interpolator interpolator) {
      this.name = name;
      this.interpolator = interpolator;
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    motionView = (MotionView) findViewById(R.id.motionView);
    spinner = (Spinner) findViewById(R.id.spinner);
    String[] spinnerNames = new String[InterpolationType.values().length];
    for (int i = 0; i < spinnerNames.length; i++) {
      spinnerNames[i] = InterpolationType.values()[i].name;
    }
    SpinnerAdapter adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spinnerNames);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        interpolator = InterpolationType.values()[i].interpolator;
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
        interpolator = InterpolationType.values()[0].interpolator;
      }
    });
    connectButton = (Button) findViewById(R.id.connect);
    connectButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startAnimation();
      }
    });
  }

  private void startAnimation() {
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float value = (float) valueAnimator.getAnimatedValue();
        motionView.connect(value * 100f);
      }
    });

    valueAnimator.setInterpolator(interpolator);
    valueAnimator.setDuration(1000);
    valueAnimator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        connectButton.setEnabled(true);
        spinner.setEnabled(true);
      }

      @Override public void onAnimationStart(Animator animation) {
        connectButton.setEnabled(false);
        spinner.setEnabled(false);
      }
    });

    valueAnimator.start();
  }
}
