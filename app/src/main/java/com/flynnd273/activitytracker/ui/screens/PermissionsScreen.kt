package com.flynnd273.activitytracker.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun PermissionsScreen(
    onPermissionsGranted: () -> Unit,
    checkBatteryPerms: () -> Boolean,
) {
    val context = LocalContext.current

    var notificationsGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            notificationsGranted = granted
        }
    )

    var batteryGranted by remember {
        mutableStateOf(
            checkBatteryPerms()
        )
    }

    val batteryIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { batteryGranted = checkBatteryPerms() }
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Text("Permissions", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)

            Button(
                enabled = !notificationsGranted,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        notificationsGranted = true
                    }
                }) {
                Text("Enable Notifications")
            }

            Button(
                enabled = !batteryGranted,
                onClick = {
                    val intent = Intent().apply {
                        action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        data = Uri.parse("package:${context.packageName}")
                    }
                    batteryIntentLauncher.launch(intent)
                }) {
                Text("Allow Unrestricted Battery")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onPermissionsGranted() },
                enabled = notificationsGranted && batteryGranted
            ) {
                Text("Continue")
            }
        }
    }
}