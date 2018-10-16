

package animation.audiolistener;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import animation.LiteAnimator;
import animation.interpolator.MirrorInterpolator;
import animation.interpolator.SequentialInterpolator;

import static android.graphics.Color.CYAN;
import static animation.LiteAnimator.animator;
import static animation.audiolistener.Math.mapFromPercent;


public class DullChoreographer implements Choreographer {
    private static final int DURATION = 1500;
    private static final float BTN_SCALE_MIN = 0.90f;
    private static final float BTN_SCALE_MAX = 0.95f;

    private final Interpolator internalInterpolator = new AccelerateDecelerateInterpolator();
    private final LiteAnimator btnScaleAnimator = animator(DURATION,
            new SequentialInterpolator(.5f, internalInterpolator, new MirrorInterpolator(internalInterpolator)));

    private Frame lastFrame;

    @Override
    public Frame frameOn(long now) {
        if (lastFrame == null) {
            btnScaleAnimator.restart(now);
            lastFrame = new Frame();
            lastFrame.scale = 1;
            lastFrame.alpha = .2f;
            lastFrame.translationY = 0f;
            lastFrame.color = CYAN;
        }

        float animatedFraction = btnScaleAnimator.getAnimatedFraction(now);
        lastFrame.scale = mapFromPercent(animatedFraction, BTN_SCALE_MIN, BTN_SCALE_MAX);

        return lastFrame;
    }

    @Override
    public long getStartTime() {
        return btnScaleAnimator.getStartTime();
    }
}
