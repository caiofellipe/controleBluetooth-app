package br.app.bluetoothcontrole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
        setContentView(R.layout.activity_main);

    }
    public void onJoystickMoved(float xPercent, float yPercent, int id){
        Log.d("Direita Joystick", "X Percent: " + xPercent + " Y Percent: " + yPercent);
    }
}
