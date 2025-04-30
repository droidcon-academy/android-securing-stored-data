package com.droidcon.vaultkeeper.ui.noteeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.db.VaultKeeperDatabase
import com.droidcon.vaultkeeper.data.repository.NoteRepository
import com.droidcon.vaultkeeper.viewmodel.NoteViewModel
import com.droidcon.vaultkeeper.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Int,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Setup ViewModel
    val database = remember { VaultKeeperDatabase.getDatabase(context) }
    val repository = remember { NoteRepository(database.noteDao()) }
    val factory = remember { NoteViewModelFactory(repository) }
    val viewModel: NoteViewModel = viewModel(factory = factory)
    
    // UI state
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    
    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val isEditMode = noteId != -1
    
    // Load note data if in edit mode
    LaunchedEffect(noteId) {
        if (isEditMode) {
            val note = viewModel.getNoteById(noteId)
            if (note != null) {
                title = note.title
                content = note.encryptedBody
            }
        }
    }
    
    // Screen title
    val screenTitle = if (isEditMode) {
        stringResource(R.string.edit_note)
    } else {
        stringResource(R.string.create_note)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
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
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete note"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.note_title)) },
                isError = titleError != null,
                supportingText = if (titleError != null) {
                    { Text(titleError!!) }
                } else null
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content field
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    contentError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text(stringResource(R.string.note_content)) },
                isError = contentError != null,
                supportingText = if (contentError != null) {
                    { Text(contentError!!) }
                } else null
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Save button
            Button(
                onClick = {
                    // Validate fields
                    var isValid = true
                    
                    if (title.trim().isEmpty()) {
                        titleError = "Title is required"
                        isValid = false
                    }
                    
                    if (content.trim().isEmpty()) {
                        contentError = "Content is required"
                        isValid = false
                    }
                    
                    if (isValid) {
                        scope.launch {
                            if (isEditMode) {
                                viewModel.updateNote(noteId, title.trim(), content.trim())
                            } else {
                                viewModel.createNote(title.trim(), content.trim())
                            }
                            navigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.deleteNote(noteId)
                            navigateBack()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 