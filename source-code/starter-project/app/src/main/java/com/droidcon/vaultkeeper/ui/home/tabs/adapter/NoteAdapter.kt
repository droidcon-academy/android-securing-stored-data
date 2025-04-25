package com.droidcon.vaultkeeper.ui.home.tabs.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droidcon.vaultkeeper.data.model.Note
import com.droidcon.vaultkeeper.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(private val onNoteClicked: (Int) -> Unit) : 
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding, onNoteClicked)
    }
    
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class NoteViewHolder(
        private val binding: ItemNoteBinding,
        private val onNoteClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        
        fun bind(note: Note) {
            binding.textViewTitle.text = note.title
            binding.textViewSnippet.text = note.encryptedBody
            binding.textViewDate.text = dateFormatter.format(Date(note.createdAt))
            
            binding.root.setOnClickListener {
                onNoteClicked(note.id)
            }
        }
    }
    
    private class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
} 