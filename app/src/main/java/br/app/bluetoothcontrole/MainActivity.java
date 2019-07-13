package br.app.bluetoothcontrole;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener{
    private static final int SOLICITA_BLUETOOTH = 1;
    private static final int SOLICITA_CONEXAO = 2;
    BluetoothAdapter myBluetooth = null;
    Button btnConectar;
    boolean conexao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
        setContentView(R.layout.activity_main);

        btnConectar = (Button)findViewById(R.id.btnConectar);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null){
            Toast.makeText(getApplicationContext(), "Seu dispositivo não possui Bluetooth", Toast.LENGTH_LONG).show();
        }else if(!myBluetooth.isEnabled()){
            Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ativaBluetooth, SOLICITA_BLUETOOTH);
        }
        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conexao){

                }else{
                    Intent lista = new Intent(MainActivity.this, ListDispositivos.class);
                    startActivityForResult(lista, SOLICITA_CONEXAO);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SOLICITA_BLUETOOTH:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Bluetooth ativado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "O Bluetooth não foi ativado! O app foi encerrado", Toast.LENGTH_LONG).show();
                    finish();
                }
            break;
        }
    }

    public void onJoystickMoved(float xPercent, float yPercent, int id){
        Log.d("Direita Joystick", "X Percent: " + xPercent + " Y Percent: " + yPercent);
    }
}
