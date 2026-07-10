package com.kaushalprajapati.ecoscan.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    userName: String?,
    userEmail: String?,
    profilePictureUri: String?,
    notificationsEnabled: Boolean,
    darkModeEnabled: Boolean,
    scanCount: Int,
    averageScore: Double?,
    onLogin: (String) -> Unit,
    onRegister: (String, String) -> Unit,
    onLogout: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onUpdateProfile: (String, String, String?) -> Unit,
    onHelpSupportClick: () -> Unit,
    onAboutAppClick: () -> Unit
) {
    if (!isLoggedIn) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AuthForm(onLogin, onRegister)
        }
    } else {
        ProfileContent(
            userName = userName,
            userEmail = userEmail,
            profilePictureUri = profilePictureUri,
            notificationsEnabled = notificationsEnabled,
            darkModeEnabled = darkModeEnabled,
            scanCount = scanCount,
            averageScore = averageScore,
            onLogout = onLogout,
            onToggleNotifications = onToggleNotifications,
            onToggleDarkMode = onToggleDarkMode,
            onHelpSupportClick = onHelpSupportClick,
            onAboutAppClick = onAboutAppClick
        )
    }
}

@Composable
fun ProfileContent(
    userName: String?,
    userEmail: String?,
    profilePictureUri: String?,
    notificationsEnabled: Boolean,
    darkModeEnabled: Boolean,
    scanCount: Int,
    averageScore: Double?,
    onLogout: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onHelpSupportClick: () -> Unit,
    onAboutAppClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Profile Header
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (profilePictureUri != null) {
                AsyncImage(
                    model = profilePictureUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = userName ?: "User",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = userEmail ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileStatCard(
                label = "Total Scans",
                value = scanCount.toString(),
                icon = Icons.Default.QrCodeScanner,
                modifier = Modifier.weight(1f)
            )
            ProfileStatCard(
                label = "Avg Score",
                value = averageScore?.let { "%.0f".format(it) } ?: "—",
                icon = Icons.Default.Eco,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Settings Section
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        SettingsToggleItem(
            label = "Notifications",
            icon = Icons.Default.Notifications,
            checked = notificationsEnabled,
            onCheckedChange = onToggleNotifications
        )
        SettingsToggleItem(
            label = "Dark Mode",
            icon = Icons.Default.DarkMode,
            checked = darkModeEnabled,
            onCheckedChange = onToggleDarkMode
        )
        
        SettingsClickItem(
            label = "Help & Support",
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            onClick = onHelpSupportClick
        )
        SettingsClickItem(
            label = "About EcoScan",
            icon = Icons.Default.Info,
            onClick = onAboutAppClick
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun ProfileStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun SettingsToggleItem(label: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsClickItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun AuthForm(onLogin: (String) -> Unit, onRegister: (String, String) -> Unit) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRegisterMode) "Create Account" else "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isRegisterMode) "Join the green revolution" else "Login to see your impact",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        if (isRegisterMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isRegisterMode) onRegister(name, email) else onLogin(email)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isRegisterMode) "Sign Up" else "Sign In")
        }

        TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
            Text(if (isRegisterMode) "Switch to Login" else "New to EcoScan? Register")
        }
    }
}
