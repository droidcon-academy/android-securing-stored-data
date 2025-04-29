package com.droidcon.vaultkeeper.ui.fileimport

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.db.VaultKeeperDatabase
import com.droidcon.vaultkeeper.data.repository.EncryptedFileRepository
import com.droidcon.vaultkeeper.viewmodel.FileViewModel
import com.droidcon.vaultkeeper.viewmodel.FileViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileImportScreen(
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Setup ViewModel
    val database = remember { VaultKeeperDatabase.getDatabase(context) }
    val repository = remember { EncryptedFileRepository(context, database.encryptedFileDao()) }
    val factory = remember { FileViewModelFactory(repository) }
    val viewModel: FileViewModel = viewModel(factory = factory)

    // UI state
    var fileName by remember { mutableStateOf("") }
    var fileNameError by remember { mutableStateOf<String?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val noFileSelected = stringResource(R.string.no_file_selected)
    var selectedFileName by remember { mutableStateOf(noFileSelected) }
    var isImporting by remember { mutableStateOf(false) }

    // File picker launcher
    val getContent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedFileUri = uri
                selectedFileName = uri.lastPathSegment ?: "Selected file"

                // Auto-fill the file name field
                if (fileName.isEmpty()) {
                    uri.lastPathSegment?.let { path ->
                        val name = path.substringAfterLast('/').substringBeforeLast('.')
                        fileName = name
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.import_file)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.import_text_file),
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.select_a_text_file_to_encrypt_and_store_securely),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // File name field
            OutlinedTextField(
                value = fileName,
                onValueChange = {
                    fileName = it
                    fileNameError = null
                },
                label = { Text(stringResource(R.string.file_name)) },
                isError = fileNameError != null,
                supportingText = if (fileNameError != null) {
                    { Text(fileNameError!!) }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Select file button
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/plain" // Only allow text files
                    }
                    getContent.launch(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.InsertDriveFile,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(stringResource(R.string.select_file))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected file name
            Text(
                text = selectedFileName,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Import button
            Button(
                onClick = {
                    if (fileName.trim().isEmpty()) {
                        fileNameError = "File name is required"
                        return@Button
                    }

                    val uri = selectedFileUri
                    if (uri == null) {
                        scope.launch {
                            snackbarHostState.showSnackbar("No file selected")
                        }
                        return@Button
                    }

                    isImporting = true

                    // Import and encrypt the file
                    viewModel.importFile(uri, fileName.trim())

                    scope.launch {
                        snackbarHostState.showSnackbar("File encrypted and saved")
                        isImporting = false
                        navigateBack()
                    }
                },
                enabled = selectedFileUri != null && !isImporting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isImporting) stringResource(R.string.importing)
                    else stringResource(R.string.import_file)
                )
            }
        }
    }
} 