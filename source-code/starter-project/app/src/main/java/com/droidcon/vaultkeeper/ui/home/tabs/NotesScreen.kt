package com.droidcon.vaultkeeper.ui.home.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.droidcon.vaultkeeper.data.repository.NoteRepository
import com.droidcon.vaultkeeper.viewmodel.NoteViewModel
import com.droidcon.vaultkeeper.viewmodel.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesScreen(onNoteClick: (Int) -> Unit) {
    val context = LocalContext.current

    // Create repository and view model
    val database = remember { VaultKeeperDatabase.getDatabase(context) }
    val repository = remember { NoteRepository(database.noteDao()) }
    val factory = remember { NoteViewModelFactory(repository) }
    val viewModel: NoteViewModel = viewModel(factory = factory)

    // Collect notes
    val notes by viewModel.notes.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        if (notes.isEmpty()) {
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
                items(notes) { note ->
                    NoteItem(
                        title = note.title,
                        content = note.encryptedBody,
                        createdAt = note.createdAt,
                        onClick = { onNoteClick(note.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    title: String,
    content: String,
    createdAt: Long,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()) }
    val formattedDate = remember(createdAt) { dateFormat.format(Date(createdAt)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
} 