package com.example.companionapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.companionapp.ui.theme.CompanionAppTheme
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.util.UUID


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private val permissions = arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)

    var bluetoothGatt: BluetoothGatt? = null



    private var existAuto = false
    private val automobile = object {
        var deviceName = ""
        var deviceAddress = ""

    }
    private val showAuto = mutableStateOf(false)
    private var autoDevice: BluetoothDevice? = null


    fun connect(autoDevice: BluetoothDevice?){
        if (ActivityCompat.checkSelfPermission(
                this,
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
        var gatt: BluetoothGatt? = null


        val gattCallback = object : BluetoothGattCallback() {
            // Implement callback methods here
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
//                super.onConnectionStateChange(gatt, status, newState)
                if(status == BluetoothGatt.GATT_SUCCESS){
                    Log.d("gatt", "GATT_SUCCESS")
                }
                if(newState == BluetoothGatt.STATE_CONNECTED){
                    Log.d("gatt", "STATE_CONNECTED")
                }
                if(newState == BluetoothGatt.STATE_DISCONNECTED){
                    Log.d("gatt", "STATE_DISCONNECTED")
                }
                if(BluetoothGatt.STATE_CONNECTED == newState){
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
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
                    bluetoothGatt!!.discoverServices()
                }
            }
            var characteristic: BluetoothGattCharacteristic? = null
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
               // super.onServicesDiscovered(gatt, status)
                val sUUID = UUID.fromString("0000fe2c-0000-1000-8000-00805f9b34fb");

                gatt?.services?.forEach {
                    Log.d("gatt milica", " uuid ${it.uuid} - ${it.characteristics}")
                    if(it.uuid == sUUID){
                        Log.d("mica", "SERVICE FOUND")
                        val service: BluetoothGattService? = gatt.getService(sUUID)
                        if(service == null){
                            Log.d("milica","service is null")
                        }else{
                            Log.d("milica","service is not null")
                        }
                        val charUUID: String =  "BCAF12D9-CCFB-485F-A0E2-388F63F40613"
                        val cUUID = UUID.fromString(charUUID)
                        characteristic = service?.getCharacteristic(cUUID)
                        if(characteristic == null){
                            Log.d("milica","char is null")
                        }else{
                            Log.d("milica","char is not null")
                        }

                        if (ActivityCompat.checkSelfPermission(
                                this@MainActivity,
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

                       //gatt.writeCharacteristic(characteristic!!, byteArrayOf(0x01,0x19), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                        if(characteristic == null){
                            Log.d("andrej","char je null")
                        }
                        if(service == null){
                            Log.d("andrej","servis je null")
                        }




                        viewModel.service = service
                        viewModel.characteristic = characteristic
                    }

                }




            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {

                super.onCharacteristicWrite(gatt, characteristic, status)
                viewModel.callback = false
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
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
                viewModel.callback = true
               // gatt?.writeCharacteristic(characteristic!!, byteArrayOf(0x01,0x32), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                //gatt?.writeCharacteristic(characteristic!!, byteArrayOf(0x03,0x00), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
            }
            override fun onCharacteristicChanged(gatt: BluetoothGatt,char:BluetoothGattCharacteristic){
                 if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
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
                    val data = gatt.readCharacteristic(char)
                Log.d("DATA","$data")
            }
        }
        val mHandler = Handler(applicationContext.mainLooper)

// Connect to BLE device from mHandler
        mHandler.post( Runnable {
            bluetoothGatt = autoDevice?.connectGatt(this,false, gattCallback)
            viewModel.gatt = bluetoothGatt
            bluetoothGatt?.discoverServices()
        })


    }
    

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompanionAppTheme {





                fun savedDevices() {
                    val bluetoothManager: BluetoothManager =
                        getSystemService(BluetoothManager::class.java)
                    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

                    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                    pairedDevices?.forEach { device ->
                        val deviceName = device.name
                        val deviceHardwareAddress = device.address // MAC address
                        if (deviceHardwareAddress == "D8:3A:DD:46:F7:5D") {
                            existAuto = true
                            automobile.deviceName = deviceName
                            automobile.deviceAddress = deviceHardwareAddress
                            autoDevice = device
                        }
                        Log.d("device", "${deviceName} $deviceHardwareAddress ")
                    }
                }




                val showDialog = viewModel.showDialog.collectAsState().value

                val launchAppSettings = viewModel.launchAppSettings.collectAsState().value

                val bluetoothPermission = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = {
                        result ->
                        permissions.forEach { permission ->
                            Log.d("tag_tag", "${result[permission]}")
                            if(result[permission] == false){
                                if(!shouldShowRequestPermissionRationale(permission)){
                                    viewModel.updateLaunchAppSettings(true)
                                }
                                viewModel.updateShowDialog(true)
                            }
                        }

                    }
                )

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = ScreenA) {
                    composable<ScreenA> {


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Button(onClick = {
                            permissions.forEach { permission ->
                                val isGranted = checkSelfPermission(permission) ==
                                        PackageManager.PERMISSION_GRANTED

                                if (!isGranted) {
                                    if (shouldShowRequestPermissionRationale(permission)) {
                                        viewModel.updateShowDialog(true)
                                    } else {
                                        bluetoothPermission.launch(permissions)
                                    }
                                }
                            }


                        }) {
                            Text(text = "Request permissions")
                        }



                        Button(onClick = {
                            savedDevices()
                            showAuto.value = true
                        }) {
                            Text(text = "Find auto")


                        }


                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Paired devices")

                        if (showAuto.value) {
                            Spacer(modifier = Modifier.height(10.dp))

                            var message = "Can't find automobile, please redo fast pair procedure"
                            if (existAuto == true) {
                                message = "${automobile.deviceName} ${automobile.deviceAddress}"
                            }
                            OutlinedCard(
                                message, existAuto, connect(autoDevice)
                            )

                            Button(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    connect(autoDevice)
                                    navController.navigate(ScreenB(name =  "Milica"))
                                }) {
                                Text(text = "Connect")


                            }
                        }


                        if (showDialog) {
                            PermissionDialog(
                                onDismiss = { viewModel.updateShowDialog(false) },
                                onConfirm = {
                                    viewModel.updateShowDialog(false)
                                    if (launchAppSettings) {
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", packageName, null)
                                        ).also { startActivity(it) }

                                        viewModel.updateLaunchAppSettings(false)
                                    } else {
                                        bluetoothPermission.launch(permissions)
                                    }
                                })


                        }


                    }


                }
                }
                    composable<ScreenB> {
                        ControllAuto(viewModel, this@MainActivity)
                    }


            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CompanionAppTheme {
        Greeting("Android")
    }
}
@Composable
fun OutlinedCard(text: String,btn:Boolean,connect:Unit) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .size(width = 240.dp, height = 130.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )

        }
    }}

@Serializable
object ScreenA

@Serializable
data class ScreenB(
    val name: String,

)