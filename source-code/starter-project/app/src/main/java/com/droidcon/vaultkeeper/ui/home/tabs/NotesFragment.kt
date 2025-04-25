package com.droidcon.vaultkeeper.ui.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidcon.vaultkeeper.NavGraphDirections.Companion.actionGlobalNoteEditorFragment
import com.droidcon.vaultkeeper.data.db.VaultKeeperDatabase
import com.droidcon.vaultkeeper.data.repository.NoteRepository
import com.droidcon.vaultkeeper.databinding.FragmentNotesBinding
import com.droidcon.vaultkeeper.ui.home.tabs.adapter.NoteAdapter
import com.droidcon.vaultkeeper.viewmodel.NoteViewModel
import com.droidcon.vaultkeeper.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        val database = VaultKeeperDatabase.getDatabase(requireContext())
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = NoteAdapter { noteId ->
            // Navigate to edit note
            val action = actionGlobalNoteEditorFragment(noteId)
            findNavController().navigate(action)
        }
        
        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewNotes.adapter = adapter
    }
    
    private fun observeNotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notes.collectLatest { notes ->
                adapter.submitList(notes)
                
                // Show empty view if needed
                if (notes.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerViewNotes.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerViewNotes.visibility = View.VISIBLE
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 