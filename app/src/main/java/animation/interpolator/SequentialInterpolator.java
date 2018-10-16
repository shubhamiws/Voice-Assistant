
package animation.interpolator;
import android.view.animation.Interpolator;

import static animation.audiolistener.Math.mapToPercent;

public class SequentialInterpolator implements Interpolator {
    private final float peakPoint;
    private final Interpolator prePeakInterpolator;
    private final Interpolator postPeakInterpolator;

    public SequentialInterpolator(float peakPoint, Interpolator prePeakInterpolator, Interpolator postPeakInterpolator) {
        this.peakPoint = peakPoint;
        this.prePeakInterpolator = prePeakInterpolator;
        this.postPeakInterpolator = postPeakInterpolator;
    }

    @Override
    public float getInterpolation(float input) {
        if (input <= peakPoint) {
            float translatedInput = mapToPercent(input, 0, peakPoint);
            return prePeakInterpolator.getInterpolation(translatedInput);
        } else {
            float translatedInput = mapToPercent(input, peakPoint, 1);
            return postPeakInterpolator.getInterpolation(translatedInput);
        }
    }
}