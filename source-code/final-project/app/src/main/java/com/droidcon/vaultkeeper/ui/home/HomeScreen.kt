package com.droidcon.vaultkeeper.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.preferences.EncryptedPreferenceManager
import com.droidcon.vaultkeeper.ui.home.tabs.FilesScreen
import com.droidcon.vaultkeeper.ui.home.tabs.NotesScreen
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToNoteEditor: (Int) -> Unit,
    navigateToFileImport: () -> Unit,
    navigateToPasswordChecker: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Tabs state
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.notes),
        stringResource(R.string.files)
    )
    
    val encryptedPreferenceManager = remember { EncryptedPreferenceManager(context) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = navigateToPasswordChecker) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(R.string.password_strength_checker)
                        )
                    }
                    IconButton(onClick = {
                        showBiometricDialog(
                            context = context,
                            encryptedPreferenceManager = encryptedPreferenceManager,
                            onStatusChanged = { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                when (selectedTabIndex) {
                    0 -> navigateToNoteEditor(-1) // -1 indicates new note
                    1 -> navigateToFileImport()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_new_note_or_file)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }
            
            // Tab Content
            when (selectedTabIndex) {
                0 -> NotesScreen(
                    onNoteClick = navigateToNoteEditor
                )
                1 -> FilesScreen()
            }
        }
    }
}

private fun showBiometricDialog(
    context: android.content.Context,
    encryptedPreferenceManager: EncryptedPreferenceManager,
    onStatusChanged: (String) -> Unit
) {
    val isBiometricEnabled = encryptedPreferenceManager.isBiometricEnabled()
    
    val dialogTitle = context.getString(R.string.biometric_auth_title)
    val dialogMessage = if (isBiometricEnabled) {
        context.getString(R.string.biometric_authentication_is_currently_enabled)
    } else {
        context.getString(R.string.biometric_authentication_is_currently_disabled)
    }
    
    val positiveButtonText = if (isBiometricEnabled) "Disable" else "Enable"
    val negativeButtonText = if (isBiometricEnabled) "Keep Enabled" else "Keep Disabled"
    
    MaterialAlertDialogBuilder(context)
        .setTitle(dialogTitle)
        .setMessage(dialogMessage)
        .setPositiveButton(positiveButtonText) { _, _ ->
            // Toggle biometric authentication in preferences
            encryptedPreferenceManager.setBiometricEnabled(!isBiometricEnabled)
            
            val statusMessage = if (!isBiometricEnabled) {
                "Biometric authentication enabled"
            } else {
                "Biometric authentication disabled"
            }
            
            onStatusChanged(statusMessage)
        }
        .setNegativeButton(negativeButtonText, null)
        .show()
} 