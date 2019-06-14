package com.waszkowski.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.graphics.Path;
import android.view.ViewGroup.LayoutParams;

import java.util.HashMap;

import static android.content.ContentValues.TAG;


public class PaintView extends View {

    public PaintView(Context context) {
        super(context);
    }

//    public class MyClass {
//        private MyClass() {
//            throw new UnsupportedOperationException();
//        }
//    }


    private final Paint paint = new Paint(); // Don't forgot to init color, form etc.

    @Override
    protected void onDraw(Canvas canvas) {
        for (int size = paths.size(), i = 0; i < size; i++) {
            Path path = paths.get(i);
            if (path != null) {
                canvas.drawPath(path, paint);
            }
        }
    }

    private HashMap<Integer, Float> mX = new HashMap<Integer, Float>();
    private HashMap<Integer, Float> mY = new HashMap<Integer, Float>();
    private HashMap<Integer, Path> paths = new HashMap<Integer, Path>();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int maskedAction = event.getActionMasked();

        Log.d(TAG, "onTouchEvent");

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    Path p = new Path();
                    p.moveTo(event.getX(i), event.getY(i));
                    paths.put(event.getPointerId(i), p);
                    mX.put(event.getPointerId(i), event.getX(i));
                    mY.put(event.getPointerId(i), event.getY(i));
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    Path p = paths.get(event.getPointerId(i));
                    if (p != null) {
                        float x = event.getX(i);
                        float y = event.getY(i);
                        p.quadTo(mX.get(event.getPointerId(i)), mY.get(event.getPointerId(i)), (x + mX.get(event.getPointerId(i))) / 2,
                                (y + mY.get(event.getPointerId(i))) / 2);
                        mX.put(event.getPointerId(i), event.getX(i));
                        mY.put(event.getPointerId(i), event.getY(i));
                    }
                }
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    Path p = paths.get(event.getPointerId(i));
                    if (p != null) {
                        p.lineTo(event.getX(i), event.getY(i));
                        invalidate();
                        paths.remove(event.getPointerId(i));
                        mX.remove(event.getPointerId(i));
                        mY.remove(event.getPointerId(i));
                    }
                }
                break;
            }
        }

        return true;
    }
}