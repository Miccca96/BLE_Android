package com.example.companionapp

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel(){

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    var gatt: BluetoothGatt? = null

    var callback: Boolean = false

    var service: BluetoothGattService? = null
    var characteristic: BluetoothGattCharacteristic? = null

    private val _launchAppSettings = MutableStateFlow(false)
    val launchAppSettings = _launchAppSettings.asStateFlow()



    fun updateShowDialog(show:Boolean){
        _showDialog.update { show }
    }

    fun updateLaunchAppSettings(launch:Boolean){
        _launchAppSettings.update { launch }
    }



}