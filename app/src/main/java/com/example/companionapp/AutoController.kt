package com.example.companionapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID


@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ControllAuto(viewModel: MainViewModel, context : Context){

    val gatt = viewModel.gatt
    val char = viewModel.characteristic
    val serv = viewModel.service

    var direction = ""
    var line = ""


    var quadrant by remember {
        mutableStateOf(0)
    }

    fun start(){
        Log.d("micika","start is called")
        if(char == null){
            Log.d("micika","CHAR IS NULL")
        }
        if(serv == null){
            Log.d("micika","SERVICE IS NULL")
        }
        if(char != null && serv != null){
            gatt?.writeCharacteristic(char, byteArrayOf(0x01,0x09),BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            line = "start"

            while (viewModel.callback == false){
                Log.d("micika","WAIT")
            }

        }


    }

    fun stop(){

        Log.d("micika","stop is called")
        if(char == null){
            Log.d("micika","CHAR IS NULL")
        }
        if(serv == null){
            Log.d("micika","SERVICE IS NULL")
        }
        if(gatt == null){
            Log.d("micika","GATT IS NULL")
        }
        if(char != null && serv != null){
//            while (viewModel.callback == false){
//                Log.d("micika","AWAIT")
//            }
            Log.d("micika","FINISH")


            gatt?.writeCharacteristic(char, byteArrayOf(0x03,0x00),BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            line = "stop"
            while (viewModel.callback == false){
                Log.d("micika","WAIT")
            }

        }
        else{
            gatt?.discoverServices()
            Log.d("micika","$gatt")
        }


    }

    fun left(){
        Log.d("micika","left is called")
        if(char == null){
            Log.d("micika","CHAR IS NULL")
        }
        if(serv == null){
            Log.d("micika","SERVICE IS NULL")
        }
        if(char != null && serv != null){
            gatt?.writeCharacteristic(char, byteArrayOf(0x04, 0x19),BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            direction = "left"

            while (viewModel.callback == false){
                Log.d("micika","WAIT")
            }
        }
    }

    fun right(){
        Log.d("micika","right is called")
        if(char == null){
            Log.d("micika","CHAR IS NULL")
        }
        if(serv == null){
            Log.d("micika","SERVICE IS NULL")
        }
        if(char != null && serv != null){
            gatt?.writeCharacteristic(char, byteArrayOf(0x05,0x19),BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            direction = "right"

            while (viewModel.callback == false){
                Log.d("micika","WAIT")
            }
        }
    }

    fun back(){
        Log.d("micika","back is called")
        if(char == null){
            Log.d("micika","CHAR IS NULL")
        }
        if(serv == null){
            Log.d("micika","SERVICE IS NULL")
        }
        if(char != null && serv != null){
            gatt?.writeCharacteristic(char, byteArrayOf(0x02,0x09),BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            line = "back"

            while (viewModel.callback == false){
                Log.d("micika","WAIT")
            }
        }
    }

    fun onCounterChanged(counter:Int){
        Log.d("counter","changed $counter")
    }
    fun sendCommand(c:Int){

        if(c == 1){

            if(direction != "right"){
                right()
            direction = "right"
            }
            Handler(Looper.getMainLooper()).postDelayed({
                 if(line != "start"){
                            start()
                            line = "start"
                            }
            }, 2000)

            Log.d("auto","LINE : $line DIRECTION $direction")

        }else if (c == 2){
            if(direction != "left"){
            left()
            direction = "left"}
            Handler(Looper.getMainLooper()).postDelayed({
                if(line != "start"){
                    start()
                    line = "start"
                }
            }, 2000)

            Log.d("auto","LINE : $line DIRECTION $direction")
        }else if(c == 3){
            if(direction != "left"){
            left()
            direction = "left"
            }
            Handler(Looper.getMainLooper()).postDelayed({
                if(line != "back"){
                    back()
                    line = "back"
                }
            }, 2000)

            Log.d("auto","LINE : $line DIRECTION $direction")
        }else if(c == 4){
            if(direction != "right"){
            right()
            direction = "right"
            }
            Handler(Looper.getMainLooper()).postDelayed({
                if(line != "back"){
                    back()
                    line = "back"
                }
            }, 2000)

            Log.d("auto","LINE : $line DIRECTION $direction")
        }
    }

    LaunchedEffect(key1 = quadrant) {
        onCounterChanged(quadrant)
        sendCommand(quadrant)
    }








    val charUUID: String =  "BCAF12D9-CCFB-485F-A0E2-388F63F40613"
    val cUUID = UUID.fromString(charUUID)

    val sUUID = UUID.fromString("0000fe2c-0000-1000-8000-00805f9b34fb");

    Log.d("uuids","$sUUID : $cUUID")

    gatt?.discoverServices()


    val services :List<BluetoothGattService> = gatt!!.services
    if(services.isEmpty()){
        Log.d("SERVICES","EMPTY LIST")
    }
    if(services.isNotEmpty()){
        Log.d("SERVICES","FOUND SERVICES")
    }

    val service: BluetoothGattService? = gatt.getService(sUUID)
    if(service == null){
        Log.d("milica","service is null")
    }

    val characteristic = service?.getCharacteristic(cUUID)
    if(characteristic == null){
        Log.d("milica","char is null")
    }

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }




    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {


            Text(text = "Control auto", fontWeight = FontWeight.Bold)



        JoyStick(
            Modifier.padding(30.dp),
            size = 150.dp,
            dotSize = 30.dp
        ){ x: Float, y: Float ->
            if(x>0 && y>0){
                Log.d("joystick","$x , $y")
                quadrant = 1
            }
            if(x<0 && y>0){
                Log.d("joystick","$x , $y")
                quadrant = 2
            }

            if(x< 0 && y < 0){
                Log.d("joystick","$x , $y")
                quadrant = 3
            }
            if(x> 0 && y < 0){
                Log.d("joystick","$x , $y")
                quadrant = 4
            }



        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly) {
            FilledButtonExample({ start() },"GO",Color.Gray)
            FilledButtonExample({ stop() }, "STOP", Color.Red)
        }



    }
}

@Composable
fun FilledButtonExample(onClick:()-> Unit, text: String, color: Color,) {
    Button(colors = ButtonDefaults.buttonColors(containerColor = color),modifier = Modifier.size(width = 100.dp, height = 80.dp),
        onClick = {
            onClick.invoke()
    }) {
        Text(text)

    }
}