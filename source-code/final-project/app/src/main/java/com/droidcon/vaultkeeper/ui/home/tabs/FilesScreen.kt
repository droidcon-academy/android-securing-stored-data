package com.droidcon.vaultkeeper.ui.home.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.db.VaultKeeperDatabase
import com.droidcon.vaultkeeper.data.repository.EncryptedFileRepository
import com.droidcon.vaultkeeper.viewmodel.FileViewModel
import com.droidcon.vaultkeeper.viewmodel.FileViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FilesScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Create repository and view model
    val database = remember { VaultKeeperDatabase.getDatabase(context) }
    val repository = remember { EncryptedFileRepository(context, database.encryptedFileDao()) }
    val factory = remember { FileViewModelFactory(repository) }
    val viewModel: FileViewModel = viewModel(factory = factory)

    // Collect files
    val files by viewModel.files.collectAsState(initial = emptyList())

    // Dialog state
    var showContentDialog by remember { mutableStateOf(false) }
    var currentDialogContent by remember { mutableStateOf("") }
    var currentFileId by remember { mutableStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (files.isEmpty()) {
            Text(
                text = stringResource(R.string.no_items_found_tap_to_add_new_content),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(files) { file ->
                    FileItem(
                        fileName = file.fileName,
                        fileSize = file.fileSize,
                        createdAt = file.createdAt,
                        onFileClick = {
                            scope.launch {
                                val content = viewModel.getFileContents(file.id)
                                if (content != null) {
                                    currentDialogContent = content
                                    currentFileId = file.id
                                    showContentDialog = true
                                }
                            }
                        },
                        onDeleteClick = {
                            viewModel.deleteFile(file.id)
                        }
                    )
                }
            }
        }
    }

    // File content dialog
    if (showContentDialog) {
        AlertDialog(
            onDismissRequest = { showContentDialog = false },
            title = { Text("Decrypted File Contents") },
            text = { Text(currentDialogContent) },
            confirmButton = {
                TextButton(onClick = { showContentDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun FileItem(
    fileName: String,
    fileSize: Long,
    createdAt: Long,
    onFileClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()) }
    val formattedDate = remember(createdAt) { dateFormat.format(Date(createdAt)) }
    val formattedSize = remember(fileSize) {
        when {
            fileSize < 1024 -> "$fileSize bytes"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            else -> String.format("%.2f MB", fileSize / (1024.0 * 1024.0))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FilePresent,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Size: $formattedSize",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Button(
                onClick = onFileClick
            ) {
                Text("View")
            }

            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete file",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
} 