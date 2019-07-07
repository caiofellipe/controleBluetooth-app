package br.app.bluetoothcontrole;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private JoystickListener joystickCallback;
    private final int ratio = 5;
    private void setupDimensions(){
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 5;
    }


    public JoystickView(Context context){
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener){
            joystickCallback = (JoystickListener) context;
        }
    }
    public JoystickView(Context context, AttributeSet attributes, int style){
        super(context, attributes, style);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener){
            joystickCallback = (JoystickListener) context;
        }
    }
    public JoystickView(Context context, AttributeSet attributes){
        super(context, attributes);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener){
            joystickCallback = (JoystickListener) context;
        }
    }
    private void drawJoystick (float newX, float newY){
        if(getHolder().getSurface().isValid()){
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            float hipo = (float) Math.sqrt(Math.pow(newX - centerX, 2) + Math.pow(newY - centerY, 2));
            float sen = (newY - centerY) / hipo;
            float cos = (newX - centerX) / hipo;


            colors.setARGB(255,100,100,100);
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors);
            for(int i = 1; i <= (int) (baseRadius / ratio); i++){
                colors.setARGB(150/i,255,0,0);
                myCanvas.drawCircle(newX - cos * hipo * (ratio/baseRadius) *i,
                        newY - sen * hipo * (ratio/baseRadius) * i, i * (hatRadius * ratio / baseRadius), colors);
            }
            for(int i = 1; i <= (int) (hatRadius / ratio); i++){
                colors.setARGB(255,(int)(i * (255 * ratio / hatRadius)), (int) (i * (255 * ratio / hatRadius)), 255);
                myCanvas.drawCircle(newX, newY, hatRadius - (float) i * (ratio) / 2, colors);
            }
            getHolder().unlockCanvasAndPost(myCanvas);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
    public boolean onTouch(View v, MotionEvent e){
        if(v.equals(this)){
            if(e.getAction() != e.ACTION_UP) {
                double displacement =  Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    drawJoystick(e.getX(), e.getY());
                    joystickCallback.onJoystickMoved((e.getX() - centerX)/baseRadius, (e.getY() - centerY)/baseRadius, getId());
                } else {
                    float ratio = (float) (baseRadius / displacement);
                    float constrainedX = centerX + (e.getX() - centerX) * ratio;
                    float constrainedY = centerY + (e.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    joystickCallback.onJoystickMoved((constrainedX - centerX)/baseRadius, (constrainedY - centerY)/baseRadius, getId());

                }
            }else{
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(0,0, getId());
            }
        }
        return true;
    }
    public interface JoystickListener{
        void onJoystickMoved(float xPercent, float yPercent, int id);
    }
}
