package com.droidcon.vaultkeeper.ui.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidcon.vaultkeeper.data.db.VaultKeeperDatabase
import com.droidcon.vaultkeeper.data.repository.EncryptedFileRepository
import com.droidcon.vaultkeeper.databinding.FragmentFilesBinding
import com.droidcon.vaultkeeper.ui.home.tabs.adapter.FileAdapter
import com.droidcon.vaultkeeper.viewmodel.FileViewModel
import com.droidcon.vaultkeeper.viewmodel.FileViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FilesFragment : Fragment() {

    private var _binding: FragmentFilesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: FileViewModel
    private lateinit var adapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        observeFiles()
    }
    
    private fun setupViewModel() {
        val database = VaultKeeperDatabase.getDatabase(requireContext())
        val repository = EncryptedFileRepository(requireContext(), database.encryptedFileDao())
        val factory = FileViewModelFactory(repository)
        
        viewModel = ViewModelProvider(this, factory)[FileViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = FileAdapter(
            onFileClicked = { fileId ->
                // Show file contents in a dialog
                showFileContentsDialog(fileId)
            },
            onFileDeleted = { fileId ->
                // Delete the file
                viewModel.deleteFile(fileId)
            }
        )
        
        binding.recyclerViewFiles.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFiles.adapter = adapter
    }
    
    private fun observeFiles() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.files.collectLatest { files ->
                adapter.submitList(files)
                
                // Show empty view if needed
                if (files.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerViewFiles.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerViewFiles.visibility = View.VISIBLE
                }
            }
        }
    }
    
    private fun showFileContentsDialog(fileId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val content = viewModel.getFileContents(fileId)
            if (content != null) {
                // Show a dialog with the file contents
                // In a real app, you'd show this in a proper dialog with appropriate security measures
                // (e.g., preventing screenshots, clearing clipboard after a delay, etc.)
                showContentDialog(content)
            }
        }
    }
    
    private fun showContentDialog(content: String) {
        // Simple implementation for demo purposes
        // In a real app, you would use a proper dialog with more features
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Decrypted File Contents")
            .setMessage(content)
            .setPositiveButton("Close", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 