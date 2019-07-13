package br.app.bluetoothcontrole;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener{
    int SOLICITA_BLUETOOTH;
    BluetoothAdapter myBluetooth = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
        setContentView(R.layout.activity_main);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null){
            Toast.makeText(getApplicationContext(), "Seu dispositivo não possui Bluetooth", Toast.LENGTH_LONG).show();
        }else if(!myBluetooth.isEnabled()){
            Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ativaBluetooth, SOLICITA_BLUETOOTH);
        }
    }
    public void onJoystickMoved(float xPercent, float yPercent, int id){
        Log.d("Direita Joystick", "X Percent: " + xPercent + " Y Percent: " + yPercent);
    }
}
