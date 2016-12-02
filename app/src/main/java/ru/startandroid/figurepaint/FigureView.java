package ru.startandroid.figurepaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class FigureView extends View {

    private float coordX;
    private float coordY;

    private ArrayList<RectF> circleList = new ArrayList<>();

    private String photoPath;
    private PaintFragment paintFragment;
    private boolean canDraw = false;
    private boolean startDraw = false;
    private int lastSelectedInstrumentId;
    private int selectedColor;
    private Bitmap bitmap;
    private Paint bitmapPaint;
    private Paint paint;
    private Matrix matrix;
    private RectF rectfSizeOfBitmap;
    private RectF rectfSizeOfView;

    public FigureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("ZHEKA", "constructor");
        bitmapPaint = new Paint();
        paint = new Paint();
        paint.setColor(Color.parseColor("#F57C00"));
        matrix = new Matrix();
        rectfSizeOfBitmap = new RectF();
        rectfSizeOfView = new RectF();

        Log.i("ZHEKA", String.format("constructor Total memory = %s",
                (int) (Runtime.getRuntime().totalMemory() / 1024)));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("ZHEKA", "onSizeChanged -> width = " + w + " height = " + h);
        Bitmap srcBitmap = decodeBitmapToRequaredSize(photoPath, w , h);
        Log.d("ZHEKA", "srcBitmapInfo; width = " + srcBitmap.getWidth() +
                                    " height = " + srcBitmap.getHeight() +
                                    " bytes = " + srcBitmap.getByteCount());

        Log.i("ZHEKA", String.format("srcBitmap Total memory = %s",
                (int) (Runtime.getRuntime().totalMemory() / 1024)));

        rectfSizeOfBitmap.set(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        rectfSizeOfView.set(0, 0, w, h);

        matrix.setRectToRect(rectfSizeOfBitmap, rectfSizeOfView, Matrix.ScaleToFit.CENTER);
        bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, false);
        Log.d("ZHEKA", "bitmapInfo; width = " + bitmap.getWidth() +
                " height = " + bitmap.getHeight() +
                " bytes = " + bitmap.getByteCount());

        srcBitmap = null;
        Log.i("ZHEKA", String.format("srcBitmap = null Total memory = %s",
                (int) (Runtime.getRuntime().totalMemory() / 1024)));

        Log.i("ZHEKA", String.format("bitmap Total memory = %s",
                (int) (Runtime.getRuntime().totalMemory() / 1024)));

        coordX = (w - bitmap.getWidth()) / 2;
        coordY = (h - bitmap.getHeight()) / 2;
        Log.d("ZHEKA", "coordX = " + coordX);
        Log.d("ZHEKA", "coordY = " + coordY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("ZHEKA", "onDraw");
        canvas.drawBitmap(bitmap, coordX, coordY, bitmapPaint);
        for(RectF rectf : circleList){
            canvas.drawOval(rectf, paint);
        }
    }

    boolean canDrag = false;
    boolean canScale = false;
    int touchCount = 0;
    float deltaDragX = 0;
    float deltaDragY = 0;
    RectF rectf = new RectF();
    PointF touchCoord;
    HashMap<Integer, PointF> pointMap = new HashMap<>();
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(lastSelectedInstrumentId){
            case R.id.imageButtonCircle:

                int action = event.getActionMasked();
                int actionIndex = event.getActionIndex();
                float x = event.getX(actionIndex);
                float y = event.getY(actionIndex);

                switch(action){
                    case MotionEvent.ACTION_DOWN:

                        if(!canDraw){
                            return true;
                        }

                        if(startDraw){
                            if(x >= rectf.left && x <= rectf.right &&
                               y >= rectf.top && y <= rectf.bottom){
                                canDrag = true;
                                deltaDragX = x - rectf.left;
                                deltaDragY = y - rectf.top;
                            }
                            touchCoord = new PointF();
                            touchCoord.set(x, y);
                            pointMap.put(event.getPointerId(actionIndex), touchCoord);
                            touchCount++;
                        }else{
                            if(x >= coordX && x <= bitmap.getWidth() + coordX &&
                               y >= coordY && y <= bitmap.getHeight() + coordY){
                                float left = x - 100;
                                float top = y - 100;
                                float right = x + 100;
                                float bottom = y + 100;
                                rectf.set(left, top, right, bottom);
                                circleList.add(rectf);
                                invalidate();
                                startDraw = true;
                                paintFragment.getPaintActivity().showFab();
                            }
                        }

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:

                        if(touchCount < 2){
                            canDrag = false;
                            canScale = true;
                            touchCoord = new PointF();
                            touchCoord.set(x, y);
                            pointMap.put(event.getPointerId(actionIndex), touchCoord);
                            touchCount++;
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:

                        if(!canDraw){
                            return true;
                        }

                        if(canDrag){
                            float left = x - deltaDragX;
                            float top = y - deltaDragY;
                            float right = left + rectf.right - rectf.left;
                            float bottom = top + rectf.bottom - rectf.top;
                            rectf.set(left, top, right, bottom);
                            invalidate();
                        }else if(canScale){
                            for(int i = 0; i < event.getPointerCount(); i++){
                                int id = event.getPointerId(i);
                                int index = event.findPointerIndex(id);
                                if(pointMap.containsKey(id)){
                                    pointMap.get(id).set(event.getX(index), event.getY(index));
                                }
                            }
                            PointF firstTouch = pointMap.get(0);
                            PointF secondTouch = pointMap.get(1);
                            float left = Math.min(firstTouch.x, secondTouch.x);
                            float top = Math.min(firstTouch.y, secondTouch.y);
                            float right = Math.max(firstTouch.x, secondTouch.x);
                            float bottom = Math.max(firstTouch.y, secondTouch.y);
                            rectf.set(left, top, right, bottom);
                            invalidate();
                        }

                        break;
                    case MotionEvent.ACTION_UP:

                        canDrag = false;
                        pointMap.remove(event.getPointerId(actionIndex));
                        touchCount = 0;

                        break;
                    case MotionEvent.ACTION_POINTER_UP:

                        canScale = false;
                        pointMap.remove(event.getPointerId(actionIndex));
                        touchCount--;

                        break;
                }

                break;
        }



        return true;
    }

    private Bitmap decodeBitmapToRequaredSize(String path, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = getSampleSize(options, reqWidth, reqHeight);
        Log.d("ZHEKA", "options.inSampleSize = " + options.inSampleSize);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private int getSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        int height = options.outHeight;
        Log.d("ZHEKA", "options.outHeight = " + options.outHeight);
        int width = options.outWidth;
        Log.d("ZHEKA", "options.outWidth = " + options.outWidth);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void setPhotoPath(String path){
        photoPath = path;
        Log.d("ZHEKA", "setPhotoPath photoPath = " + photoPath);
    }

    public void setCanDraw(boolean can){
        canDraw = can;
    }

    public void setFragmentInstance(PaintFragment fragment){
        paintFragment = fragment;
    }

    public void setLastSelectedInstrumentId(int instrumentId){
        lastSelectedInstrumentId = instrumentId;
    }

    public void setPaintColor(int color){
        selectedColor = color;
        paint.setColor(selectedColor);
        invalidate();
        Log.d("ZHEKA", "setPaintColor = " + Integer.toHexString(color));
    }
}
