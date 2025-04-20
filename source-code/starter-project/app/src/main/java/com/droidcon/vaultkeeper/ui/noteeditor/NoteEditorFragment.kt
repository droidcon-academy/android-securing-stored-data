package com.droidcon.vaultkeeper.ui.noteeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.databinding.FragmentNoteEditorBinding
import com.google.android.material.snackbar.Snackbar

class NoteEditorFragment : Fragment(), MenuProvider {
    private var _binding: FragmentNoteEditorBinding? = null
    private val binding get() = _binding!!
    
    private val args: NoteEditorFragmentArgs by navArgs()

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
        
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        
        // In the final implementation, we'd check if we're editing an existing note
        // and load its contents from the database if noteId > 0
        if (args.noteId > 0) {
            // This would load the note by ID in the final implementation
            binding.etNoteTitle.setText("Sample Note")
            binding.etNoteContent.setText("This text would be loaded from the encrypted database.")
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_note_editor, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_save -> {
                saveNote()
                true
            }
            else -> false
        }
    }

    private fun saveNote() {
        // Validate input
        val title = binding.etNoteTitle.text.toString()
        val content = binding.etNoteContent.text.toString()
        
        if (title.isBlank()) {
            binding.etNoteTitle.error = "Title cannot be empty"
            return
        }
        
        // In the final implementation, we would encrypt and save the note to Room
        // For now, just show a message and navigate back
        
        Snackbar.make(binding.root, "Note saved (placeholder)", Snackbar.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 