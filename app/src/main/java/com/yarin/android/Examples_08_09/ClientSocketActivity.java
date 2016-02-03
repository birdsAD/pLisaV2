package com.yarin.android.Examples_08_09;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class ClientSocketActivity  extends Activity
{
    private static final String TAG = ClientSocketActivity.class.getSimpleName();
    private static final int REQUEST_DISCOVERY = 0x1;;
    private Handler _handler = new Handler();
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
    /*By JB*/
    private static final int UART_DELAY = 200;
    private static final byte ACTUAL_SPEED = 0x01;
    private static final byte READ_EEPROM = 0x07;
    private static final byte WRITE_EEPROM = 0x06;
    private static final byte FAN_GROUP = 0x0;
    private static final byte FAN_ADDRESS = 0x1;
    private static final byte FAN_STATUS = 0x2;
    private static final byte FAN_TARGET_VALUE = 0x3;
    private static final byte FAN_MAXRPM_0 = 0x0;
    private static final byte FAN_MAXRPM_1 = 0x0;
    private static final byte FAN_MAXRPM_2 = 0x0;
    private static final byte FAN_MAXPWM = 0x0;
    private static final byte FAN_MINPWM = 0x0;
    private static final byte FAN_P_FACTOR = 0x0;
    private static final byte FAN_I_FACTOR = 0x0;
    private static final byte FAN_SENSOR_MAX = 0x0;
    private static final byte FAN_SENSOR_MIN = 0x0;
    private static final byte FAN_SENSOR_UNIT_0 = 0x0;
    private static final byte FAN_SENSOR_UNIT_1 = 0x0;
    private static final byte FAN_SENSOR_UNIT_2 = 0x0;
    private static final byte FAN_SENSOR_UNIT_3 = 0x0;
    private static final byte FAN_SENSOR_UNIT_4 = 0x0;
    private static final byte FAN_SENSOR_UNIT_5 = 0x0;
    private static final byte FAN_P_BAND = 0x0;
    private static final byte FAN_SERIAL_NUMBER = 0x0;
    private static final byte FAN_OPERATION_HOUR = 0x0;
    private static final byte FAN_RATING_FACTOR = 0x11;
    /*End of by JB */
    private BluetoothSocket socket = null;
    private TextView sTextView;
    private EditText sEditText;
    /*By JB */
    private TextView sFanGroup;
    private TextView sFanAddress;
    private TextView sFanTargetValue;
    private TextView sFanActualSpeed;
    private TextView sFanRatingFactor;
    private TextView sFanMaxRPM;
    private Spinner spinnerFanControlMode;
    private TextView sFanMaxPWM;
    private TextView sFanMinPWM;
    private TextView sFanP_Factor;
    private TextView sFanI_Factor;
    private TextView sFanSensorMax;
    private TextView sFanSensorMin;
    private TextView sFanSensorUnit;
    private TextView sFanP_Band;
    private TextView sFanSerialNumber;
    private TextView sFanOperationHour;
    private TextView sFanDirection;
    private Button btnSubmit;

    /* End of by JB */
    private String str;
    private OutputStream outputStream;
    private InputStream inputStream;
    private StringBuffer sbu;

    int myFanGroup;
    int myFanAddress;
    int myFanActualSpeed;
    int myFanTargetValue;
    int myFanMaxRPM;
    int myFanRatingFactor;
    int myFanRS_Priority;
    int myFanMaxPWM;
    int myFanMinPWM;
    int myFanP_Factor;
    int myFanI_Factor;
    int myFanSensorMax;
    int myFanSensorMin;
    byte[] myFanSensorUnit = new byte[6];
    int myFanP_Band;
    int myFanSerialNumber;
    int myFanOperationHour;
    int myFanDirection;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.client_socket);
        if (!_bluetooth.isEnabled()) {
            finish();
            return;
        }
        Intent intent = new Intent(this, DiscoveryActivity.class);

		/* Prompted to select a server to connect */
        Toast.makeText(this, "select device to connect", Toast.LENGTH_SHORT).show();

		/* Select device for list */
        startActivityForResult(intent, REQUEST_DISCOVERY);

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();


        //sTextView = (TextView)findViewById(R.id.sTextView);
        //sEditText = (EditText)findViewById(R.id.sEditText);
        /*By JB*/
        sFanGroup = (TextView)findViewById(R.id.sFanGroup);
        sFanAddress = (TextView)findViewById(R.id.sFanAddress);
        sFanActualSpeed = (TextView)findViewById(R.id.sFanActualSpeed);
        sFanTargetValue = (TextView)findViewById(R.id.sFanTargetValue);
        sFanMaxRPM = (TextView)findViewById(R.id.sFanMaxRPM);
        sFanRatingFactor = (TextView)findViewById(R.id.sFanRatingFactor);
        sFanMaxPWM = (TextView)findViewById(R.id.sFanMaxPWM);
        sFanMinPWM = (TextView)findViewById(R.id.sFanMinPWM);
        sFanP_Factor = (TextView)findViewById(R.id.sFanP_Factor);
        sFanI_Factor = (TextView)findViewById(R.id.sFanI_Factor);
        sFanSensorMax = (TextView)findViewById(R.id.sFanSensorMax);
        sFanSensorMin = (TextView)findViewById(R.id.sFanSensorMin);
        sFanSensorUnit = (TextView)findViewById(R.id.sFanSensorUnit);
        sFanP_Band = (TextView)findViewById(R.id.sFanP_Band);
        sFanSerialNumber = (TextView)findViewById(R.id.sFanSerialNumber);
        sFanOperationHour = (TextView)findViewById(R.id.sFanOperationHour);
        sFanDirection = (TextView)findViewById(R.id.sFanDirection);
        /*End of by JB */
        Log.d("EF-BTBee", ">>setOnKeyListener");
    }

    public void addListenerOnSpinnerItemSelection() {
        spinnerFanControlMode = (Spinner) findViewById(R.id.spinnerFanControlMode);
        spinnerFanControlMode.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {
        spinnerFanControlMode = (Spinner) findViewById(R.id.spinnerFanControlMode);
    }

    public byte ebmBusChecksum(byte buffer[], int length){
        byte result = 0;
        for(int i=0;i<length;i++){
            result ^= buffer[i];
        }
        result ^= 0xff;
        return result;
    }
    public void ebmBusTX(byte control, byte command, byte fanAddress, byte fanGroup, byte length, byte data0, byte data1) throws IOException{
        //Log.d("EF-BTBee", ">>ebmBusTX" + "+" + control + "+" +command + "+" +fanAddress + "+" +fanGroup + "+" +length + "+" +data0 + "+" +data1);
        byte preamble = 0x15;
        byte cmd_fan = 0;
        byte checksum;
        byte[] output = new byte[7];
        preamble |= (length << 5);
        preamble |= (control << 5);
        cmd_fan |= (command << 5);
        cmd_fan += fanAddress;
        output[0] = preamble;
        output[1] = cmd_fan;
        output[2] = fanGroup;
        if(length == 1)
            output[3] = data0;
        else if(length == 2) {
            output[3] = data0;
            output[4] = data1;
        }
        output[3+length] = ebmBusChecksum(output, 3+length);
        /*
        for(int k=0;k<7;k++) {
            Log.d("EF-BTBee", ">>output" + k + "=" + (output[k]&0xff) + '\n');
        }
        */
        outputStream.write(output, 0, (3+length+1) );
    }
    public void onButtonClickRatingFactor100(View view) throws IOException {

        byte bytes[] = {(byte) 0x55, (byte) 0xC1, (byte) 0x01,
                (byte) 0x11, (byte) 0xFF, (byte) 0x84};
        outputStream.write(bytes);
    }
    public void onButtonClickScan(View view) throws IOException {
        //Toast.makeText(getApplicationContext(), "Please purchase the paid" + '\n' + "version to access advanced features", Toast.LENGTH_LONG).show();
        /*
        Toast.makeText(getApplicationContext(),
                getApplicationContext().getResources().getString(R.string.scan_button),
                Toast.LENGTH_SHORT).show();
                */
    }
    public void onButtonClickSet(View view) throws IOException {
        //Check for any changes in fan group
        String inputFanGroup = sFanGroup.getText().toString();
        byte bytesFanGroup[] = inputFanGroup.getBytes();
        int i;
        if (bytesFanGroup[0] != '.') {
            int newFanGroup = 0;
            for (i = 0; i < bytesFanGroup.length; i++) {
                bytesFanGroup[i] &= 0xff;
                bytesFanGroup[i] -= 0x30;
                newFanGroup *= 10;
                newFanGroup += bytesFanGroup[i];
            }
            //Check for any changes in fan address
            String inputFanAddress = sFanAddress.getText().toString();
            byte bytesFanAddress[] = inputFanAddress.getBytes();
            int newFanAddress = 0;
            for (i = 0; i < bytesFanAddress.length; i++) {
                bytesFanAddress[i] &= 0xff;
                bytesFanAddress[i] -= 0x30;
                newFanAddress *= 10;
                newFanAddress += bytesFanAddress[i];
            }
            if (newFanGroup != myFanGroup) {
                if (newFanGroup > 255 || newFanGroup < 1) {
                    //Error
                    Toast.makeText(getApplicationContext(), "Error!" + '\n' + "Range allowed 1 to 255", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ebmBusTX((byte) 0x0, (byte) 0x6, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2, (byte) FAN_GROUP, (byte) newFanGroup);
                }
                //refresh fan group and address
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //refresh buffers
                byte bytes[] = {(byte) 0x1D, (byte) 0x0, (byte) 0x0,
                        (byte) 0xE2};
                outputStream.write(bytes);
                Log.d("EF-BTBee", ">>fan and new fan = =" + myFanAddress + newFanAddress);
            }
            if (newFanAddress != myFanAddress) {
                if (newFanAddress > 31 || newFanAddress < 1) {
                    //Error
                    Toast.makeText(getApplicationContext(), "Error!" + '\n' + "Range allowed 1 to 31", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ebmBusTX((byte) 0x0, (byte) 0x6, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2, (byte) FAN_ADDRESS, (byte) newFanAddress);

                }
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //refresh buffers
                byte bytes[] = {(byte) 0x1D, (byte) 0x0, (byte) 0x0,
                        (byte) 0xE2};
                outputStream.write(bytes);
            }
        }
        //Check for any changes in Target Speed
        String inputFanTargetValue = sFanTargetValue.getText().toString();
        byte bytesFanTargetValue[] = inputFanTargetValue.getBytes();
        if(bytesFanTargetValue[0] != '.') {
            int newFanTargetValue = 0;
            for (i = 0; i < bytesFanTargetValue.length; i++) {
                bytesFanTargetValue[i] &= 0xff;
                bytesFanTargetValue[i] -= 0x30;
                newFanTargetValue *= 10;
                newFanTargetValue += bytesFanTargetValue[i];
            }
            if (newFanTargetValue != myFanTargetValue) {
                if (newFanTargetValue > 9999 || newFanTargetValue < 0) {
                    //Error
                    Toast.makeText(getApplicationContext(), "Error!" + '\n' + "Target Value" + '\n' + "Range allowed 0 to 9999", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2, (byte) FAN_TARGET_VALUE, (byte) newFanTargetValue);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1, (byte) FAN_TARGET_VALUE, (byte) 0);
            }
        }
        //Check for any changes in Rating Factor
        String inputFanRatingFactor = sFanRatingFactor.getText().toString();
        byte bytesFanRatingFactor[] = inputFanRatingFactor.getBytes();
        if(bytesFanRatingFactor[0] != '.') {
            int newFanRatingFactor = 0;
            for (i = 0; i < bytesFanRatingFactor.length; i++) {
                bytesFanRatingFactor[i] &= 0xff;
                bytesFanRatingFactor[i] -= 0x30;
                newFanRatingFactor *= 10;
                newFanRatingFactor += bytesFanRatingFactor[i];
            }
            if ((((newFanRatingFactor*255))/100) != myFanRatingFactor) {
                Log.d("EF-BTBee", ">>refreshing rating factor" + (((newFanRatingFactor*255))/100) + myFanRatingFactor );
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (newFanRatingFactor > 100 || newFanRatingFactor < 0) {
                    //Error
                    Toast.makeText(getApplicationContext(), "Error!" + '\n' + "Rating Factor" + '\n' + "Range allowed 0 to 100%", Toast.LENGTH_SHORT).show();
                } else {
                    newFanRatingFactor *= 255;
                    newFanRatingFactor /= 100;
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2, (byte) FAN_RATING_FACTOR, (byte) newFanRatingFactor);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1, (byte) FAN_RATING_FACTOR, (byte) 0);
            }
        }
        //Check for any changes in Max RPM
        String inputFanMaxRPM = sFanMaxRPM.getText().toString();
        byte bytesFanMaxRPM[] = inputFanMaxRPM.getBytes();
        if(bytesFanMaxRPM[0] != '.') {
            int newFanMaxRPM = 0;
            int newFanMaxRPM_0 = 0;
            int newFanMaxRPM_1 = 0;
            int newFanMaxRPM_2 = 0;
            for (i = 0; i < bytesFanMaxRPM.length; i++) {
                bytesFanMaxRPM[i] &= 0xff;
                bytesFanMaxRPM[i] -= 0x30;
                newFanMaxRPM *= 10;
                newFanMaxRPM += bytesFanMaxRPM[i];
            }
            if (newFanMaxRPM != myFanMaxRPM) {
                if (newFanMaxRPM > 9999 || newFanMaxRPM < 200) {
                    //Error
                    /*
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.set_MaxRPM),
                            Toast.LENGTH_SHORT).show();
                            */
                } else {
                    newFanMaxRPM *= 255;
                    newFanMaxRPM /= 100;
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_MAXRPM_0, (byte) newFanMaxRPM_0);
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_MAXRPM_0, (byte) newFanMaxRPM_1);
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_MAXRPM_0, (byte) newFanMaxRPM_2);

                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_MAXRPM_0, (byte) 0);
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_MAXRPM_1, (byte) 0);
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_MAXRPM_2, (byte) 0);
            }
        }
        //Check for any changes in Max PWM
        String inputFanMaxPWM = sFanMaxPWM.getText().toString();
        byte bytesFanMaxPWM[] = inputFanMaxPWM.getBytes();
        if(bytesFanMaxPWM[0] != '.') {
            int newFanMaxPWM = 0;
            for (i = 0; i < bytesFanMaxPWM.length; i++) {
                bytesFanMaxPWM[i] &= 0xff;
                bytesFanMaxPWM[i] -= 0x30;
                newFanMaxPWM *= 10;
                newFanMaxPWM += bytesFanMaxPWM[i];
            }
            if (newFanMaxPWM != myFanMaxPWM) {
                if (newFanMaxPWM > 100 || newFanMaxPWM < 0) {
                    //Error
                    /*
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.set_MaxPWM),
                            Toast.LENGTH_SHORT).show();
                            */
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    newFanMaxPWM *= 255;
                    newFanMaxPWM /= 100;
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_MAXPWM, (byte) newFanMaxPWM);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_MAXPWM, (byte) 0);
            }
        }
        //Check for any changes in Min PWM
        String inputFanMinPWM = sFanMinPWM.getText().toString();
        byte bytesFanMinPWM[] = inputFanMinPWM.getBytes();
        if(bytesFanMinPWM[0] != '.') {
            int newFanMinPWM = 0;
            for (i = 0; i < bytesFanMinPWM.length; i++) {
                bytesFanMinPWM[i] &= 0xff;
                bytesFanMinPWM[i] -= 0x30;
                newFanMinPWM *= 10;
                newFanMinPWM += bytesFanMinPWM[i];
            }
            if (newFanMinPWM != myFanMinPWM) {
                if (newFanMinPWM > 100 || newFanMinPWM < 0) {
                    //Error
                    /*
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.set_MinPWM),
                            Toast.LENGTH_SHORT).show();
                            */
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    newFanMinPWM *= 255;
                    newFanMinPWM /= 100;
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_MINPWM, (byte) newFanMinPWM);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_MINPWM, (byte) 0);
            }
        }
        //Check for any changes in P Factor
        String inputFanP_Factor = sFanP_Factor.getText().toString();
        byte bytesFanP_Factor[] = inputFanP_Factor.getBytes();
        if(bytesFanP_Factor[0] != '.') {
            int newFanP_Factor = 0;
            for (i = 0; i < bytesFanP_Factor.length; i++) {
                bytesFanP_Factor[i] &= 0xff;
                bytesFanP_Factor[i] -= 0x30;
                newFanP_Factor *= 10;
                newFanP_Factor += bytesFanP_Factor[i];
            }
            if (newFanP_Factor != myFanP_Factor) {
                if (newFanP_Factor > 100 || newFanP_Factor < 0) {
                    //Error
                    /*
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.set_P_Factor),
                            Toast.LENGTH_SHORT).show();
                            */
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    newFanP_Factor *= 255;
                    newFanP_Factor /= 100;
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_P_FACTOR, (byte) newFanP_Factor);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_P_FACTOR, (byte) 0);
            }
        }
        //Check for any changes in Sensor Max
        String inputFanI_Factor = sFanI_Factor.getText().toString();
        byte bytesFanI_Factor[] = inputFanI_Factor.getBytes();
        if(bytesFanI_Factor[0] != '.') {
            int newFanI_Factor = 0;
            for (i = 0; i < bytesFanI_Factor.length; i++) {
                bytesFanI_Factor[i] &= 0xff;
                bytesFanI_Factor[i] -= 0x30;
                newFanI_Factor *= 10;
                newFanI_Factor += bytesFanI_Factor[i];
            }
            if (newFanI_Factor != myFanI_Factor) {
                if (newFanI_Factor > 100 || newFanI_Factor < 0) {
                    //Error
                    /*
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.set_I_Factor),
                            Toast.LENGTH_SHORT).show();
                            */
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    newFanI_Factor *= 255;
                    newFanI_Factor /= 100;
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_I_FACTOR, (byte) newFanI_Factor);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_I_FACTOR, (byte) 0);
            }
        }
        //Check for any changes in Max PWM
        String inputFanSensorMax = sFanSensorMax.getText().toString();
        byte bytesFanSensorMax[] = inputFanSensorMax.getBytes();
        if(bytesFanSensorMax[0] != '.') {
            int newFanSensorMax = 0;
            for (i = 0; i < bytesFanSensorMax.length; i++) {
                bytesFanSensorMax[i] &= 0xff;
                bytesFanSensorMax[i] -= 0x30;
                newFanSensorMax *= 10;
                newFanSensorMax += bytesFanSensorMax[i];
            }
            if (newFanSensorMax != myFanSensorMax) {
                if (newFanSensorMax > 100 || newFanSensorMax < 0) {
                    //Error
                    /*
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.set_SensorMax),
                            Toast.LENGTH_SHORT).show();
                            */
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    newFanSensorMax *= 255;
                    newFanSensorMax /= 100;
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_SENSOR_MAX, (byte) newFanSensorMax);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_SENSOR_MAX, (byte) 0);
            }
        }
        //Check for any changes in Sensor Min
        String inputFanSensorMin = sFanSensorMin.getText().toString();
        byte bytesFanSensorMin[] = inputFanSensorMin.getBytes();
        if(bytesFanSensorMin[0] != '.') {
            int newFanSensorMin = 0;
            for (i = 0; i < bytesFanSensorMin.length; i++) {
                bytesFanSensorMin[i] &= 0xff;
                bytesFanSensorMin[i] -= 0x30;
                newFanSensorMin *= 10;
                newFanSensorMin += bytesFanSensorMin[i];
            }
            if (newFanSensorMin != myFanSensorMin) {
                if (newFanSensorMin > 100 || newFanSensorMin < 0) {
                    //Error
                    /*
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.set_SensorMin),
                            Toast.LENGTH_SHORT).show();
                            */
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    newFanSensorMin *= 255;
                    newFanSensorMin /= 100;
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_SENSOR_MIN, (byte) newFanSensorMin);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_SENSOR_MIN, (byte) 0);
            }
        }
        //Check for any changes in Sensor Unit
        String inputFanSensorUnit = sFanSensorUnit.getText().toString();
        byte bytesFanSensorUnit[] = inputFanSensorUnit.getBytes();
        if(bytesFanSensorUnit[0] != '.') {
            for (int v = 0; v < bytesFanSensorUnit.length; v++) {
                if (bytesFanSensorUnit[v] != myFanSensorUnit[v]) {
                    if (bytesFanSensorUnit[v] < 0x1F || bytesFanSensorUnit[v] == '.') {
                        //Error
                        /*
                        Toast.makeText(getApplicationContext(),
                                getApplicationContext().getResources().getString(R.string.set_SensorUnit),
                                Toast.LENGTH_SHORT).show();
                                */
                    } else {
                        try {
                            Thread.sleep(UART_DELAY);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                                (byte) (FAN_SENSOR_UNIT_0 + v), (byte) bytesFanSensorUnit[v]);
                    }
                    //refresh buffers
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                            (byte) (FAN_SENSOR_UNIT_0 + v), (byte) 0);
                }
            }
        }
        //Check for any changes in P Band
        String inputFanP_Band = sFanP_Band.getText().toString();
        byte bytesFanP_Band[] = inputFanP_Band.getBytes();
        if(bytesFanP_Band[0] != '.') {
            int newFanP_Band = 0;
            for (i = 0; i < bytesFanP_Band.length; i++) {
                bytesFanP_Band[i] &= 0xff;
                bytesFanP_Band[i] -= 0x30;
                newFanP_Band *= 10;
                newFanP_Band += bytesFanP_Band[i];
            }
            if (newFanP_Band != myFanP_Band) {
                if (newFanP_Band > 100 || newFanP_Band < 0) {
                    //Error
                    /*
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.set_P_Band),
                            Toast.LENGTH_SHORT).show();
                            */
                } else {
                    try {
                        Thread.sleep(UART_DELAY);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    newFanP_Band *= 255;
                    newFanP_Band /= 100;
                    ebmBusTX((byte) 0x0, (byte) WRITE_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 0x2,
                            (byte) FAN_P_BAND, (byte) newFanP_Band);
                }
                //refresh buffers
                try {
                    Thread.sleep(UART_DELAY);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1,
                        (byte) FAN_P_BAND, (byte) 0);
            }
        }

    }
    public void onButtonClickRatingFactor0(View view) throws IOException {

        byte bytes[] = {(byte) 0x55, (byte) 0xC1, (byte) 0x01,
                (byte) 0x11, (byte) 0x00, (byte) 0x7B};
        Log.d("EF-BTBee", ">>factor");
        ebmBusTX((byte) 0, (byte) 7, (byte) myFanAddress, (byte) myFanGroup, (byte) 1, (byte) 0x08, (byte) 0);
        //outputStream.write(bytes);
    }
    public void onButtonClickDiscover(View view) throws IOException {
        //Resets the Values
        sFanGroup.setText("...");
        sFanAddress.setText("...");
        sFanActualSpeed.setText("...");
        sFanTargetValue.setText("...");
        sFanMaxRPM.setText("...");
        sFanRatingFactor.setText("...");
        sFanMaxPWM.setText("...");
        sFanMinPWM.setText("...");
        sFanP_Factor.setText("...");
        sFanI_Factor.setText("...");
        sFanSensorMax.setText("...");
        sFanSensorMin.setText("...");
        sFanSensorUnit.setText("...");
        sFanP_Band.setText("...");
        sFanSerialNumber.setText("...");
        sFanOperationHour.setText("...");
        sFanDirection.setText("...");

        //Discover fan
        byte bytes[] = {(byte) 0x1D, (byte) 0x0, (byte) 0x0,
                (byte) 0xE2};
        outputStream.write(bytes);
        //Get Actual Value
        try {
            Thread.sleep(UART_DELAY);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ebmBusTX((byte) 0, (byte) ACTUAL_SPEED, (byte) myFanAddress, (byte) myFanGroup, (byte) 0, (byte) 0, (byte) 0);

        //Get Target Value
        try {
            Thread.sleep(UART_DELAY);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1, (byte) FAN_TARGET_VALUE, (byte) 0);

        //Get Rating Factor
        try {
            Thread.sleep(UART_DELAY);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ebmBusTX((byte) 0, (byte) READ_EEPROM, (byte) myFanAddress, (byte) myFanGroup, (byte) 1, (byte) FAN_RATING_FACTOR, (byte) 0);

    }



    /* after select, connect to device */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_DISCOVERY) {
            finish();
            return;
        }
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        final BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        new Thread() {
            public void run() {
                connect(device);
            };
        }.start();
    }


    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("EF-BTBee", ">>", e);
        }
    }



    protected void connect(BluetoothDevice device){
        //BluetoothSocket socket = null;
        try {
            //Create a Socket connection: need the server's UUID number of registered
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

            socket.connect();
            Log.d("EF-BTBee", ">>Client connectted");
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            int read = 0;
            final byte[] bytes = new byte[20];
            final byte[] returnArray = new byte[50];
            int n = 0;

            while ((read = inputStream.read(bytes, 0, 20)) > 0) {
                //sTextView.setText("<-- " + (bytes[0]&0xff) + "," + (bytes[1]&0xff) + "," + (bytes[2]&0xff) + (bytes[3]&0xff) +'\n');
                //Needs to combine the bytes, the 1st byte will always come in 1st, followed by the rest in 2 segments
                /*
                for(int p=0;p<read;p++){
                  returnArray[n] = (byte) (bytes[p]&0xFF);
                  n++;
                }
                */

                if((bytes[0]&0xff) == 0 &&
                        (bytes[1]&0xff) == 0 && (bytes[2]&0xff) == 226) {
                    myFanGroup = (bytes[5]&0xff);
                    myFanAddress = (bytes[4]&0xff);
                    Log.d("EF-BTBee", ">>discovery detected. Group = " + myFanGroup + "addr =" + myFanAddress);
                }
                else if( ((bytes[0]&0xff) == ((ACTUAL_SPEED << 5) | myFanAddress)) &&
                        (bytes[1]&0xff) == myFanGroup ) {
                    myFanActualSpeed = (bytes[7]&0xff);
                    Log.d("EF-BTBee", ">>Actual Speed detected = " + myFanActualSpeed);
                }
                else if( ((bytes[0]&0xff) == ((READ_EEPROM << 5) | myFanAddress)) &&
                        (bytes[1]&0xff) == myFanGroup && (bytes[2]&0xff) == FAN_TARGET_VALUE) {
                    myFanTargetValue = (bytes[7]&0xff);
                    Log.d("EF-BTBee", ">>Target Value detected = " + myFanTargetValue);
                }
                else if( ( ((bytes[0]&0xff) == ((READ_EEPROM << 5) | myFanAddress)) &&
                        (bytes[1]&0xff) == myFanGroup && (bytes[2]&0xff) == FAN_RATING_FACTOR) ){
                    myFanRatingFactor = (bytes[7]&0xff);
                    Log.d("EF-BTBee", ">>Rating Factor detected = " + myFanRatingFactor);
                }

                final int count = read;
                _handler.post(new Runnable() {
                    public void run(){
                        for (int p = 0; p < 8; p++)
                            bytes[p] &= 0xff;
                        /*
                        for(int m=0;m<returnArray.length;m++) {
                            Log.d("EF-BTBee", ">>Array" + m + " = " + (returnArray[m]&0xff) + '\n');
                        }
                        */
                        /*
                        Log.d("EF-BTBee", ">>discovery . Group = " + myFanGroup + "addr =" + myFanAddress);
                        Log.d("EF-BTBee", ">>Actual Speed  = " + myFanActualSpeed);
                        Log.d("EF-BTBee", ">>Target Value = " + myFanTargetValue);
                        Log.d("EF-BTBee", ">>Rating Factor = " + myFanRatingFactor);
                        */
                        if(myFanGroup > 0 && myFanGroup < 256) {
                            sFanGroup.setText("" + myFanGroup);
                            //spinnerFanControlMode.setSelection(myFanGroup);
                        }
                        else
                            sFanGroup.setText("Error!");
                        if(myFanAddress > 0 && myFanAddress < 32)
                            sFanAddress.setText("" + myFanAddress);
                        else
                            sFanAddress.setText("Error!");

                        sFanActualSpeed.setText("" + myFanActualSpeed);
                        sFanTargetValue.setText("" + myFanTargetValue);

                        sFanRatingFactor.setText("" + ((myFanRatingFactor*100)+50)/255);
                        /*
                        //Actual Speed return
                        if( ( ((bytes[0]&0xff) == ((ACTUAL_SPEED << 5) | myFanAddress)) && (bytes[1]&0xff) == myFanGroup && (bytes[3]&0xff) == ((ACTUAL_SPEED << 5) | myFanAddress)) ){
                            sFanActualSpeed.setText("" + (bytes[6]&0xff));
                        }
                        else if( (  (bytes[0]&0xff) == 0x15) && ((bytes[1]&0xff) == ((ACTUAL_SPEED << 5) | myFanAddress)) && (bytes[2]&0xff) == myFanGroup ) {
                            sFanActualSpeed.setText("" + (bytes[7]&0xff));
                        }
                        //Target Value Return
                        else if( ( ((bytes[0]&0xff) == ((0x7 << 5) | myFanAddress)) && (bytes[1]&0xff) == myFanGroup && (bytes[2]&0xff) == FAN_TARGET_VALUE) ){
                            sFanTargetValue.setText("" + (bytes[7]&0xff));
                        }
                        else if( ((bytes[1]&0xff) == ((0x7 << 5) | myFanAddress)) && (bytes[2]&0xff) == myFanGroup && (bytes[3]&0xff) == FAN_TARGET_VALUE) {
                            sFanTargetValue.setText("" + (bytes[8]&0xff));
                        }
                        //Rating Factor Return
                        else if( ( ((bytes[0]&0xff) == ((READ_EEPROM << 5) | myFanAddress)) && (bytes[1]&0xff) == myFanGroup && (bytes[2]&0xff) == FAN_RATING_FACTOR) ){
                            sFanRatingFactor.setText("" + (bytes[7]&0xff));
                        }
                        else if( ((bytes[1]&0xff) == ((READ_EEPROM << 5) | myFanAddress)) && (bytes[2]&0xff) == myFanGroup && (bytes[3]&0xff) == FAN_RATING_FACTOR) {
                            sFanRatingFactor.setText("" + (bytes[8]&0xff));
                        }
                        */
                        //str += '\n';

                        for (int p = 0; p < 20; p++)
                            bytes[p] = 0;

                    }
                });

            }

        } catch (IOException e) {
            Log.e("EF-BTBee", ">>", e);
            finish();
            return ;
        } finally {
            if (socket != null) {
                try {
                    Log.d("EF-BTBee", ">>Client Socket Close");
                    socket.close();
                    finish();
                    return ;
                } catch (IOException e) {
                    Log.e("EF-BTBee", ">>", e);
                }
            }
        }
    }
}

