package com.example.companionapp

import android.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun PermissionDialog(
   onDismiss: () -> Unit,
   onConfirm: ()-> Unit
){
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        title = { Text(text = "Permission required")},
        text = {
               Text(text = "This app needs to use bluetooth to control the vehicle")
        },
        confirmButton = {
                       Button(onClick = onConfirm) {
                           Text(text = "Confirm")
                        }

        },
        dismissButton = {
            Button(onClick = onDismiss ) {
                Text(text = "Deny")
            }

            
        })
}

