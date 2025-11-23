package com.example.carebuddy.emergency

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.carebuddy.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    viewModel: EmergencyViewModel = viewModel()
) {
    val context = LocalContext.current
    val contacts by viewModel.contacts.collectAsState()

    val locationHelper = remember { LocationHelper(context) }

    // Map ke liye current user location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    var showDialog by remember { mutableStateOf(false) }

    // SOS state + siren player
    var isSosActive by remember { mutableStateOf(false) }
    var sirenPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Runtime permission launcher for location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { /* user tap karega dobara */ }

    fun ensureLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (fineGranted || coarseGranted) {
            true
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            Toast.makeText(
                context,
                "Please allow location and tap again.",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    fun startSiren() {
        if (sirenPlayer == null) {
            try {
                val player = MediaPlayer.create(context, R.raw.emergency_siren)
                player.isLooping = true   // ⬅ yaha property ki jagah method
                player.start()
                sirenPlayer = player
            } catch (e: Exception) {
                Toast.makeText(context, "Siren sound not available", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun stopSiren() {
        sirenPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
            } catch (_: Exception) {
            }
        }
        sirenPlayer = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(VibratorManager::class.java)
                val vibrator = vibratorManager?.defaultVibrator
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 500),
                    0 // repeat
                )
                vibrator?.vibrate(effect)
            } else {
                val vibrator =
                    context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
                val effect =
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 500, 500),
                        0
                    )
                if (effect != null) {
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        } catch (_: Exception) {
        }
    }

    fun stopVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(VibratorManager::class.java)
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.cancel()
            } else {
                val vibrator =
                    context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.cancel()
            }
        } catch (_: Exception) {
        }
    }

    if (showDialog) {
        AddContactDialog(
            onDismiss = { showDialog = false },
            onSave = { name, phone ->
                viewModel.addContact(name, phone)
                showDialog = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Add contact")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Emergency",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Use SOS to alert all contacts with your live location, and quickly reach emergency services.",
                style = MaterialTheme.typography.bodyMedium
            )

            // ---------- Big SOS Button ----------
            SosButton(
                isActive = isSosActive,
                onClick = {
                    if (!isSosActive) {
                        // START SOS
                        isSosActive = true
                        startSiren()
                        startVibration()

                        if (!ensureLocationPermission()) {
                            val body =
                                "🚨 SOS! I need immediate help. I might be in danger. Please contact me ASAP."
                            sendSmsToAllWithBody(context, contacts, body)
                            callEmergencyNumber(context, "112")
                            return@SosButton
                        }

                        locationHelper.getLastLocation { lat, lng ->
                            val body = if (lat != null && lng != null) {
                                val latLng = LatLng(lat, lng)
                                userLocation = latLng

                                "🚨 SOS! I need immediate help. My live location: https://www.google.com/maps/search/?api=1&query=$lat,$lng"
                            } else {
                                "🚨 SOS! I need immediate help. I might be in danger. Please contact me ASAP."
                            }

                            sendSmsToAllWithBody(context, contacts, body)
                            callEmergencyNumber(context, "112")
                        }
                    } else {
                        // STOP SOS
                        isSosActive = false
                        stopSiren()
                        stopVibration()
                    }
                }
            )

            // ---------- Quick Action Buttons ----------
            QuickActionsSection(
                onCall112 = { callEmergencyNumber(context) },
                onOpenMaps = {
                    if (!ensureLocationPermission()) return@QuickActionsSection

                    locationHelper.getLastLocation { lat, lng ->
                        if (lat != null && lng != null) {
                            val latLng = LatLng(lat, lng)
                            userLocation = latLng
                            openLocationInMaps(context, lat, lng)
                        } else {
                            openLocationInMaps(context)
                        }
                    }
                },
                onShareLocation = {
                    if (!ensureLocationPermission()) return@QuickActionsSection

                    locationHelper.getLastLocation { lat, lng ->
                        val text = if (lat != null && lng != null) {
                            val latLng = LatLng(lat, lng)
                            userLocation = latLng
                            "My live location: https://www.google.com/maps/search/?api=1&query=$lat,$lng"
                        } else {
                            "SOS! I need help but couldn't fetch GPS. Please contact me ASAP."
                        }
                        shareLocationText(context, text)
                    }
                },
                onSendSos = { sendSmsToAll(context, contacts) }
            )

            Text(
                text = "Emergency Contacts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (contacts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No contacts added yet. Tap + to add.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(contacts) { contact ->
                        EmergencyContactItem(
                            contact = contact,
                            onCall = { callEmergencyNumber(context, contact.phone) },
                            onDelete = { viewModel.deleteContact(contact.id) }
                        )
                    }
                }

                // ---------- Map widget as last item ----------
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LocationMap(userLocation = userLocation)
                }
            }
        }
    }
}

/* ---------- Big SOS Button ---------- */

@Composable
fun SosButton(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val label = if (isActive) "STOP SOS" else "SEND SOS"

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.error
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/* ---------- Quick Action Buttons ---------- */

@Composable
fun QuickActionsSection(
    onCall112: () -> Unit,
    onOpenMaps: () -> Unit,
    onShareLocation: () -> Unit,
    onSendSos: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(
                text = "Call 112",
                modifier = Modifier.weight(1f),
                onClick = onCall112
            )
            ActionButton(
                text = "Open Maps",
                modifier = Modifier.weight(1f),
                onClick = onOpenMaps
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(
                text = "Share Location",
                modifier = Modifier.weight(1f),
                onClick = onShareLocation
            )
            ActionButton(
                text = "Send SOS",
                modifier = Modifier.weight(1f),
                onClick = onSendSos
            )
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp)
    ) {
        Text(text)
    }
}

/* ---------- Contact item ---------- */

@Composable
fun EmergencyContactItem(
    contact: EmergencyContact,
    onCall: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(Modifier.weight(1f)) {
            Text(contact.name, fontWeight = FontWeight.Bold)
            Text(contact.phone)
        }

        IconButton(onClick = onCall) {
            Icon(Icons.Outlined.Phone, contentDescription = "Call")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "Delete")
        }
    }
}

/* ---------- Add Contact Dialog ---------- */

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Emergency Contact") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, phone) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/* ---------- Map widget (with remember state) ---------- */

@Composable
fun LocationMap(
    userLocation: LatLng?
) {
    val fallbackLocation = remember { LatLng(28.6139, 77.2090) } // Delhi default

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: fallbackLocation,
            15f
        )
    }

    val markerState = remember(userLocation) {
        MarkerState(position = userLocation ?: fallbackLocation)
    }

    LaunchedEffect(userLocation) {
        val target = userLocation ?: fallbackLocation
        cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 15f)
        markerState.position = target
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (userLocation == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Location not loaded yet.\nUse 'Open Maps' or 'Share Location' above.",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = markerState,
                    title = "You are here"
                )
            }
        }
    }
}

