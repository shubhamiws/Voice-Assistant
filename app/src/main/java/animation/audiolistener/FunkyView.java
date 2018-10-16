

package animation.audiolistener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import static android.os.SystemClock.uptimeMillis;
import static java.lang.Math.max;

public class FunkyView extends View {
    public enum State {
        DULL,
        FUNKY
    }
    private final TransitioningChoreographer choreographer = new TransitioningChoreographer(State.DULL);
    private final Paint buttonPaint = new Paint();
    private int buttonMaxWidth = 0;
    private float maxTranslationY = 0;

    public FunkyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buttonPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxPadding = max(max(max(getPaddingBottom(), getPaddingTop()), getPaddingLeft()), getPaddingRight());
        buttonMaxWidth = getMeasuredWidth() - maxPadding;
        maxTranslationY = getMeasuredWidth() * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Frame f = choreographer.frameOn(uptimeMillis());
        float cx = getWidth() / 2;
        float cy = getHeight() / 2;
        int radius = (int) (buttonMaxWidth * f.scale) / 2;

        setTranslationY(f.translationY * maxTranslationY);
        buttonPaint.setColor(Color.BLUE);
        buttonPaint.setAlpha((int) (f.alpha * 500));
        canvas.drawCircle(cx, cy, radius, buttonPaint);
        invalidate();
    }


}
