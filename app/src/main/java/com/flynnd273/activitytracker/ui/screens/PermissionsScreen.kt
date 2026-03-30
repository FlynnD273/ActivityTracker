package com.flynnd273.activitytracker.ui.screens

import android.Manifest
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
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
    onPermissionsDenied: () -> Unit,
    onPermissionsGranted: () -> Unit,
) {
    val context = LocalContext.current

    fun checkNotifPerms(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    val notificationManager: NotificationManager = remember {
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    fun checkPromotedNotifPerms(): Boolean = true
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
//        ContextCompat.checkSelfPermission(
//            context,
//            "android.permission.POST_PROMOTED_NOTIFICATIONS"
//        ) == PackageManager.PERMISSION_GRANTED
//    } else {
//        true
//    }

    fun checkBatteryPerms(): Boolean {
        val pm = context.getSystemService(PowerManager::class.java) as PowerManager
        val batteryGranted = pm.isIgnoringBatteryOptimizations(context.packageName)
        return batteryGranted
    }

    var notificationsGranted by remember {
        mutableStateOf(
            checkNotifPerms()
        )
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            notificationsGranted = granted
        }
    )

//    var batteryGranted by remember {
//        mutableStateOf(
//            checkBatteryPerms()
//        )
//    }

    var promotedNotificationsGranted by remember {
        mutableStateOf(
            checkPromotedNotifPerms()
        )
    }

    val promotedNotificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            promotedNotificationsGranted = granted
        }
    )

    val allPerms by remember { derivedStateOf { notificationsGranted && promotedNotificationsGranted } }

    if (allPerms) {
        onPermissionsGranted()
    } else {
        onPermissionsDenied()
    }

//    val batteryIntentLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult(),
//        onResult = { batteryGranted = checkBatteryPerms() }
//    )

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
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) {
                Text("Enable Notifications")
            }

            Button(
                enabled = !promotedNotificationsGranted,
                onClick = {
                    promotedNotificationLauncher.launch("android.permission.POST_PROMOTED_NOTIFICATIONS")
                }) {
                Text("Enable Live Notifications")
            }

//            Button(
//                enabled = !batteryGranted,
//                onClick = {
//                    val intent = Intent().apply {
//                        action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//                        data = Uri.parse("package:${context.packageName}")
//                    }
//                    batteryIntentLauncher.launch(intent)
//                }) {
//                Text("Allow Unrestricted Battery")
//            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onPermissionsGranted() },
                enabled = allPerms
            ) {
                Text("Continue")
            }
        }
    }
}