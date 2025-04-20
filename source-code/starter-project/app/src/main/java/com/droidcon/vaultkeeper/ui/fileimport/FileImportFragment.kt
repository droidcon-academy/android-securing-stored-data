package com.droidcon.vaultkeeper.ui.fileimport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.databinding.FragmentFileImportBinding
import com.google.android.material.snackbar.Snackbar

class FileImportFragment : Fragment() {
    
    private var _binding: FragmentFileImportBinding? = null
    private val binding get() = _binding!!
    
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Display file path in the TextView
            binding.tvSelectedFile.text = uri.path
            binding.tvSelectedFile.visibility = View.VISIBLE
            binding.btnSelectFile.text = getString(R.string.select_different_file)
            binding.btnEncryptFile.isEnabled = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileImportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnSelectFile.setOnClickListener {
            selectFile()
        }
        
        binding.btnEncryptFile.setOnClickListener {
            importAndEncryptFile()
        }
    }
    
    private fun selectFile() {
        // Launch file picker for text files
        getContent.launch("text/plain")
    }
    
    private fun importAndEncryptFile() {
        // In the final implementation, this would:
        // 1. Read the selected file
        // 2. Encrypt its contents
        // 3. Save to app's private storage
        
        // For now, just show a placeholder message
        Snackbar.make(
            binding.root,
            "File encryption will be implemented in the codelab",
            Snackbar.LENGTH_LONG
        ).show()
        
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 