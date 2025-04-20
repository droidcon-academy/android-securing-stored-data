package com.droidcon.vaultkeeper.ui.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidcon.vaultkeeper.databinding.FragmentFilesBinding
import com.droidcon.vaultkeeper.ui.home.tabs.adapter.FileAdapter
import com.droidcon.vaultkeeper.viewmodel.FileViewModel

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
        // In the codelab, FileViewModel will be properly initialized with repository
        viewModel = ViewModelProvider(requireActivity())[FileViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = FileAdapter(
            onFileClicked = { fileId ->
                // TODO: Implement file viewing with decryption in the codelab
            },
            onFileDeleted = { fileId ->
                // TODO: Implement file deletion in the codelab
            }
        )
        
        binding.recyclerviewFiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FilesFragment.adapter
        }
    }
    
    private fun observeFiles() {
        // In the codelab, this will be implemented to observe encrypted files
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 