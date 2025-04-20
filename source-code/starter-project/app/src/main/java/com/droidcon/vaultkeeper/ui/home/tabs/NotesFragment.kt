package com.droidcon.vaultkeeper.ui.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidcon.vaultkeeper.databinding.FragmentNotesBinding
import com.droidcon.vaultkeeper.ui.home.tabs.adapter.NoteAdapter
import com.droidcon.vaultkeeper.viewmodel.NoteViewModel

class NotesFragment : Fragment() {
    
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        observeNotes()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = NoteAdapter { noteId ->
            // TODO: Handle note click in the codelab implementation
        }
        
        binding.recyclerviewNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NotesFragment.adapter
        }
    }
    
    private fun observeNotes() {
        // Observe notes LiveData
        // In the codelab, this will be implemented to use encrypted storage
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 