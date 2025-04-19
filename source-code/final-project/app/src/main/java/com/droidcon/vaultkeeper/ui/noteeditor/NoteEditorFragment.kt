package com.droidcon.vaultkeeper.ui.noteeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.db.VaultKeeperDatabase
import com.droidcon.vaultkeeper.data.repository.NoteRepository
import com.droidcon.vaultkeeper.databinding.FragmentNoteEditorBinding
import com.droidcon.vaultkeeper.viewmodel.NoteViewModel
import com.droidcon.vaultkeeper.viewmodel.NoteViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class NoteEditorFragment : Fragment() {

    private var _binding: FragmentNoteEditorBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: NoteViewModel
    private val args: NoteEditorFragmentArgs by navArgs()
    private var isEditMode = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteEditorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        
        // Check if we're editing an existing note
        isEditMode = args.noteId != -1
        if (isEditMode) {
            loadExistingNote()
            // Update title in the action bar
            activity?.title = getString(R.string.edit_note)
            
            // Setup menu only in edit mode
            setupMenu()
        } else {
            // Creating a new note
            activity?.title = getString(R.string.create_note)
        }
        
        setupSaveButton()
    }
    
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_note_editor, menu)
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_delete -> {
                        confirmDelete()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    
    private fun setupViewModel() {
        val database = VaultKeeperDatabase.getDatabase(requireContext())
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModelFactory(repository)
        
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
    }
    
    private fun loadExistingNote() {
        viewLifecycleOwner.lifecycleScope.launch {
            val note = viewModel.getNoteById(args.noteId)
            if (note != null) {
                binding.editTextTitle.setText(note.title)
                binding.editTextContent.setText(note.encryptedBody)
            } else {
                // Handle case where note doesn't exist
                Toast.makeText(requireContext(), "Note not found", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }
    
    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            saveNote()
        }
    }
    
    private fun saveNote() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()
        
        if (title.isEmpty()) {
            binding.inputLayoutTitle.error = "Title is required"
            return
        } else {
            binding.inputLayoutTitle.error = null
        }
        
        if (content.isEmpty()) {
            binding.inputLayoutContent.error = "Content is required"
            return
        } else {
            binding.inputLayoutContent.error = null
        }
        
        if (isEditMode) {
            // Update existing note
            viewModel.updateNote(args.noteId, title, content)
        } else {
            // Create new note
            viewModel.createNote(title, content)
        }
        
        // Go back to the notes list
        findNavController().navigateUp()
    }
    
    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteNote(args.noteId)
                findNavController().navigateUp()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 