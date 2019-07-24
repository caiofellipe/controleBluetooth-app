package com.br.controleBluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int SOLICITA_BLUETOOTH = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private static final int MESSAGE_READ = 3;
    UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;
    RelativeLayout layout_joystick;
    JoystickClass js;
    private static String endMAC = null;
    boolean conexao = false;
    Handler mHandler;
    Button btnConectar;
    StringBuilder dadosBluetooth = new StringBuilder();
    BluetoothAdapter myBluetooth = null;
    BluetoothDevice myDevice = null;
    BluetoothSocket mySocket = null;
    ConnectedThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);

        layout_joystick     = findViewById(R.id.layout_joystick);
        btnConectar         = findViewById(R.id.btnConectar);
        myBluetooth         = BluetoothAdapter.getDefaultAdapter();

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
                try{
                    mySocket.close();
                    conexao = false;
                    btnConectar.setText("Conectar");
                    Toast.makeText(getApplicationContext(), "Desconectado", Toast.LENGTH_LONG).show();
                }catch(IOException e){
                    Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                }
            }else{
                Intent lista = new Intent(MainActivity.this, ListDispositivos.class);
                startActivityForResult(lista, SOLICITA_CONEXAO);
            }

            }
        });
        js = new JoystickClass(getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);
        layout_joystick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conexao){
                    connectedThread.write("");

                    layout_joystick.setOnTouchListener(new OnTouchListener() {
                        public boolean onTouch(View arg0, MotionEvent arg1) {
                            //js.drawStick(arg1);
                            MainActivity.this.js.drawStick(arg1);
                            if(arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                                MainActivity.this.textView1.setText("X : " + js.getX());
                                MainActivity.this.textView2.setText("Y : " + js.getY());
                                MainActivity.this.textView3.setText("Angulo : " + js.getAngle());
                                MainActivity.this.textView4.setText("Distancia : " + js.getDistance());

                                int direction = js.get8Direction();
                                if(direction == JoystickClass.STICK_UP) {
                                    textView5.setText("Direction : Up");
                                } else if(direction == JoystickClass.STICK_UPRIGHT) {
                                    textView5.setText("Direction : Up Right");
                                } else if(direction == JoystickClass.STICK_RIGHT) {
                                    textView5.setText("Direction : Right");
                                } else if(direction == JoystickClass.STICK_DOWNRIGHT) {
                                    textView5.setText("Direction : Down Right");
                                } else if(direction == JoystickClass.STICK_DOWN) {
                                    textView5.setText("Direction : Down");
                                } else if(direction == JoystickClass.STICK_DOWNLEFT) {
                                    textView5.setText("Direction : Down Left");
                                } else if(direction == JoystickClass.STICK_LEFT) {
                                    textView5.setText("Direction : Left");
                                } else if(direction == JoystickClass.STICK_UPLEFT) {
                                    textView5.setText("Direction : Up Left");
                                } else if(direction == JoystickClass.STICK_NONE) {
                                    textView5.setText("Direction : Center");
                                }
                            } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                                textView1.setText("X :");
                                textView2.setText("Y :");
                                textView3.setText("Angulo :");
                                textView4.setText("Distancia :");
                                textView5.setText("Direção :");
                            }
                            return true;
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado", Toast.LENGTH_LONG).show();
                }
            }
        });
        /*js = findViewById(R.id.joystickAnalogico);
            js.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conexao){
                    connectedThread.write("");
                }else{
                    Toast.makeText(getApplicationContext(), "Bluetooth não está conectado", Toast.LENGTH_LONG).show();
                }
            }
        });*/
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                //super.handleMessage(msg);
                if(msg.what == MESSAGE_READ){
                    String recebe = (String) msg.obj;
                    dadosBluetooth.append(recebe);

                    int fimInformacao = dadosBluetooth.indexOf("F");
                    if(fimInformacao > 0){
                        String dadosCompletos = dadosBluetooth.substring(0, fimInformacao);
                        int tamanhoInformacao = dadosCompletos.length();
                        if(dadosBluetooth.charAt(0) == 'S'){
                            String fullDados = dadosBluetooth.substring(1,tamanhoInformacao);
                            Log.d("Recebidos: ", fullDados);
                        }
                        dadosBluetooth.delete(0, dadosBluetooth.length());
                    }
                }
            }

        };
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
            case SOLICITA_CONEXAO:
                if(resultCode == Activity.RESULT_OK){
                    endMAC = data.getExtras().getString(ListDispositivos.ENDERECO_MAC);
                    //Toast.makeText(getApplicationContext(), "MAC: " + endMAC, Toast.LENGTH_LONG).show();

                    myDevice = myBluetooth.getRemoteDevice(endMAC);
                    try{
                        mySocket = myDevice.createRfcommSocketToServiceRecord(myUUID);
                        mySocket.connect();
                        conexao = true;
                        connectedThread = new ConnectedThread(mySocket);
                        connectedThread.start();
                        btnConectar.setText("Desconectar");
                        Toast.makeText(getApplicationContext(), "Você foi conectado com: " + endMAC, Toast.LENGTH_LONG).show();

                    }catch (IOException e){
                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Falha ao obter endereço MAC", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mySocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String dadosBt = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBt).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(/*byte[] bytes*/ String enviarDados) {
            byte[] msgBuffer = enviarDados.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mySocket.close();
            } catch (IOException e) { }
        }
    }
}