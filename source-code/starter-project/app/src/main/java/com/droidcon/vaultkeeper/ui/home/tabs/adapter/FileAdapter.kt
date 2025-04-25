package com.droidcon.vaultkeeper.ui.home.tabs.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droidcon.vaultkeeper.data.model.EncryptedFile
import com.droidcon.vaultkeeper.databinding.ItemFileBinding
import java.text.DecimalFormat

class FileAdapter(
    private val onFileClicked: (Int) -> Unit,
    private val onFileDeleted: (Int) -> Unit
) : ListAdapter<EncryptedFile, FileAdapter.FileViewHolder>(FileDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding, onFileClicked, onFileDeleted)
    }
    
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class FileViewHolder(
        private val binding: ItemFileBinding,
        private val onFileClicked: (Int) -> Unit,
        private val onFileDeleted: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(file: EncryptedFile) {
            binding.textViewFileName.text = file.fileName
            binding.textViewFileSize.text = formatFileSize(file.fileSize)
            
            binding.root.setOnClickListener {
                onFileClicked(file.id)
            }
            
            binding.buttonDelete.setOnClickListener {
                onFileDeleted(file.id)
            }
        }
        
        private fun formatFileSize(size: Long): String {
            val kb = size / 1024.0
            val mb = kb / 1024.0
            val formatter = DecimalFormat("#.##")
            
            return when {
                mb >= 1 -> "Size: ${formatter.format(mb)} MB"
                kb >= 1 -> "Size: ${formatter.format(kb)} KB"
                else -> "Size: $size bytes"
            }
        }
    }
    
    private class FileDiffCallback : DiffUtil.ItemCallback<EncryptedFile>() {
        override fun areItemsTheSame(oldItem: EncryptedFile, newItem: EncryptedFile): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: EncryptedFile, newItem: EncryptedFile): Boolean {
            return oldItem == newItem
        }
    }
} 