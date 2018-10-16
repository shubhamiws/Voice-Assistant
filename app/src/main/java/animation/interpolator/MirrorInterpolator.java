
package animation.interpolator;

import android.view.animation.Interpolator;

public class MirrorInterpolator implements Interpolator {
    private final Interpolator interpolator;

    public MirrorInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public float getInterpolation(float input) {
        return 1 - interpolator.getInterpolation(input);
    }
}
