package com.example.carebuddy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.carebuddy.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    settingsViewModel: SettingsViewModel,
    onLogout: () -> Unit, // 👈 non-null and required
    userName: String = "",
    userEmail: String = "rahul.sharma@example.com",
    userPhotoUrl: String? = null,
    streakDays: Int = 5,
    onManageEmergency: (() -> Unit)? = null,
    onNameChanged: ((String) -> Unit)? = null,
    onPhotoSelected: ((Uri) -> Unit)? = null,
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    // Local editable name + photo
    var editableName by remember { mutableStateOf(userName) }
    var showNameDialog by remember { mutableStateOf(false) }

    var localPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                localPhotoUri = uri
                onPhotoSelected?.invoke(uri)
            }
        }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Small page title
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // ---------- PREMIUM HEADER ----------
            ProfileHeaderPremium(
                name = editableName,
                email = userEmail,
                photoUrl = userPhotoUrl,
                localPhotoUri = localPhotoUri,
                streakDays = streakDays,
                onChangePhotoClick = { imagePickerLauncher.launch("image/*") },
                onEditNameClick = { showNameDialog = true }
            )

            // ---------- Quick Stats ----------
            ProfileStatsCard(
                calorieGoal = uiState.dailyCalorieGoal,
                waterGoalMl = uiState.dailyWaterGoalMl,
                streakDays = streakDays
            )

            // ---------- App Preferences ----------
            SettingsSection(title = "App preferences") {
                SettingsSwitchRow(
                    title = "Dark mode",
                    description = "Use a darker theme for low-light comfort.",
                    checked = uiState.isDarkMode,
                    onCheckedChange = { settingsViewModel.toggleDarkMode(it) }
                )

                SettingsSwitchRow(
                    title = "Notifications",
                    description = "Allow gentle reminders and updates.",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { settingsViewModel.toggleNotifications(it) }
                )
            }

            // ---------- Emergency ----------
            SettingsSection(title = "Emergency") {
                SettingsSwitchRow(
                    title = "Auto share location",
                    description = "Include your live location in SOS messages.",
                    checked = uiState.autoShareLocation,
                    onCheckedChange = { settingsViewModel.toggleAutoShareLocation(it) }
                )

                SettingsSwitchRow(
                    title = "SOS siren",
                    description = "Play a loud siren sound when SOS is active.",
                    checked = uiState.sosSirenEnabled,
                    onCheckedChange = { settingsViewModel.toggleSosSiren(it) }
                )

                SettingsSwitchRow(
                    title = "SOS vibration",
                    description = "Continuously vibrate when SOS is active.",
                    checked = uiState.sosVibrationEnabled,
                    onCheckedChange = { settingsViewModel.toggleSosVibration(it) }
                )

                if (onManageEmergency != null) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onManageEmergency,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Manage emergency contacts")
                    }
                }
            }

            // ---------- Health Goals ----------
            SettingsSection(title = "Health goals") {
                SettingsNumberRow(
                    title = "Daily calorie goal (kcal)",
                    value = uiState.dailyCalorieGoal.toString(),
                    onValueChange = { settingsViewModel.updateCalorieGoal(it) }
                )

                SettingsNumberRow(
                    title = "Daily water goal (ml)",
                    value = uiState.dailyWaterGoalMl.toString(),
                    onValueChange = { settingsViewModel.updateWaterGoal(it) }
                )

                SettingsSwitchRow(
                    title = "Sleep reminder",
                    description = "Get a reminder when it’s close to your sleep time.",
                    checked = uiState.sleepReminderEnabled,
                    onCheckedChange = { settingsViewModel.toggleSleepReminder(it) }
                )
            }

            // ---------- Account ----------
            SettingsSection(title = "Account") {
                SettingsTextRow(
                    title = "Profile details",
                    description = "View and update your personal information."
                )

                SettingsTextRow(
                    title = "Change password",
                    description = "Update the password for your account."
                )

                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            }

            // ---------- About ----------
            SettingsSection(title = "About") {
                SettingsTextRow(
                    title = "Privacy policy",
                    description = "How we handle and protect your data."
                )
                SettingsTextRow(
                    title = "Terms & conditions",
                    description = "Read the terms for using CareBuddy."
                )
                Text(
                    text = "App version 1.0.0",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    // ---------- Edit Name Dialog ----------
    if (showNameDialog) {
        var tempName by remember { mutableStateOf(editableName) }

        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Edit name") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    editableName = tempName
                    onNameChanged?.invoke(tempName)
                    showNameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/* ---------------- PREMIUM PROFILE HEADER ---------------- */

@Composable
private fun ProfileHeaderPremium(
    name: String,
    email: String,
    photoUrl: String?,
    localPhotoUri: Uri?,
    streakDays: Int,
    onChangePhotoClick: () -> Unit,
    onEditNameClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.80f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.60f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val effectivePhoto = localPhotoUri?.toString() ?: photoUrl

                        if (effectivePhoto != null) {
                            AsyncImage(
                                model = effectivePhoto,
                                contentDescription = "Profile photo",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = name.firstOrNull()?.uppercase() ?: "U",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = onChangePhotoClick,
                                contentPadding = PaddingValues(horizontal = 0.dp)
                            ) {
                                Text(
                                    "Change photo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }

                            TextButton(
                                onClick = onEditNameClick,
                                contentPadding = PaddingValues(horizontal = 0.dp)
                            ) {
                                Text(
                                    "Edit name",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Small bottom stats row inside header
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SmallChip(
                        label = "Wellness streak",
                        value = "$streakDays days"
                    )
                    SmallChip(
                        label = "Mood check-ins",
                        value = "Keep going 💚"
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallChip(
    label: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

/* ---------------- STATS / SETTINGS ---------------- */

@Composable
fun ProfileStatsCard(
    calorieGoal: Int,
    waterGoalMl: Int,
    streakDays: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileStatItem(
                label = "Calorie goal",
                value = "$calorieGoal kcal"
            )

            ProfileStatItem(
                label = "Water goal",
                value = "$waterGoalMl ml"
            )

            ProfileStatItem(
                label = "Streak",
                value = "$streakDays days"
            )
        }
    }
}

@Composable
fun ProfileStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsNumberRow(
    title: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SettingsTextRow(
    title: String,
    description: String? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
