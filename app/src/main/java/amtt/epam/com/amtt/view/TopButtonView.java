package amtt.epam.com.amtt.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import amtt.epam.com.amtt.R;

/**
 * Created by Ivan_Bakach on 23.03.2015.
 */
public class TopButtonView extends FrameLayout {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public ViewGroup body;
    private ViewGroup topButtonLayout;
    private int isButtonBarVisible = GONE;
    private final static String LOG_TAG = "TAG";
    private DisplayMetrics metrics = new DisplayMetrics();

    public TopButtonView(Context context, WindowManager windowManager, WindowManager.LayoutParams layoutParams) {
        super(context);
        initComponent();
        this.windowManager = windowManager;
        this.layoutParams = layoutParams;
        body = (ViewGroup) findViewById(R.id.body);
        topButtonLayout = (LinearLayout) findViewById(R.id.top_button_layout);
//        topButtonLayout.addView(new ImageView(context).setImageResource(R.id.button_show));

    }


    private void initComponent() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.top_button_layout, this, true);
    }

    private int firstX;
    private int firstY;

    private int lastX;
    private int lastY;

    public boolean moving;

    public int threshold = 10;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int totalDeltaX = lastX - firstX;
        int totalDeltaY = lastY - firstY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();

                firstX = lastX;
                firstY = lastY;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) event.getRawX() - lastX;
                int deltaY = (int) event.getRawY() - lastY;

                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();

                if (moving
                        || Math.abs(totalDeltaX) >= threshold
                        || Math.abs(totalDeltaY) >= threshold) {
                    moving = true;

                    // update the position of the view
                    if (event.getPointerCount() == 1) {
                        if ((layoutParams.x + deltaX)>=0&&(layoutParams.x + deltaX)<=940) {
                            layoutParams.x += deltaX;
                        }else{
                            layoutParams.x += 0;
                        }
                        layoutParams.y += deltaY;
                        Log.d(LOG_TAG, layoutParams.x+" "+layoutParams.y);
                    }
                    windowManager.updateViewLayout(this, layoutParams);
                }
                break;
            case MotionEvent.ACTION_UP:
                moving = false;

                if (event.getPointerCount() == 1) {

                    boolean tap = Math.abs(totalDeltaX) < threshold
                            && Math.abs(totalDeltaY) < threshold;
                    if (tap) {
                        Log.d(LOG_TAG, "Click");
//                        if (mOnClickListener != null) {
//                            mOnClickListener.onClick(this);
//                        }
                        if (isButtonBarVisible == VISIBLE) {
                            body.setVisibility(GONE);
                            isButtonBarVisible = GONE;
                        } else {
                            body.setVisibility(VISIBLE);
                            isButtonBarVisible = VISIBLE;
                        }
                    }
                }
                break;
        }
        return true;
    }

    private OnClickListener mOnClickListener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;

    }
}
