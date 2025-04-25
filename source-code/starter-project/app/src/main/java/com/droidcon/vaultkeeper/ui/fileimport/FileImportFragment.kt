package com.droidcon.vaultkeeper.ui.fileimport

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.db.VaultKeeperDatabase
import com.droidcon.vaultkeeper.data.repository.EncryptedFileRepository
import com.droidcon.vaultkeeper.databinding.FragmentFileImportBinding
import com.droidcon.vaultkeeper.viewmodel.FileViewModel
import com.droidcon.vaultkeeper.viewmodel.FileViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FileImportFragment : Fragment() {

    private var _binding: FragmentFileImportBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: FileViewModel
    private var selectedFileUri: Uri? = null
    
    // Register for file selection result
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedFileUri = uri
                binding.textViewSelectedFile.text = uri.lastPathSegment ?: "Selected file"
                binding.buttonImport.isEnabled = true
                
                // Auto-fill the file name field
                if (binding.editTextFileName.text.isNullOrEmpty()) {
                    uri.lastPathSegment?.let { path ->
                        val fileName = path.substringAfterLast('/').substringBeforeLast('.')
                        binding.editTextFileName.setText(fileName)
                    }
                }
            }
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
        
        setupViewModel()
        setupButtons()
    }
    
    private fun setupViewModel() {
        val database = VaultKeeperDatabase.getDatabase(requireContext())
        val repository = EncryptedFileRepository(requireContext(), database.encryptedFileDao())
        val factory = FileViewModelFactory(repository)
        
        viewModel = ViewModelProvider(this, factory)[FileViewModel::class.java]
    }
    
    private fun setupButtons() {
        binding.buttonSelectFile.setOnClickListener {
            openFilePicker()
        }
        
        binding.buttonImport.setOnClickListener {
            importFile()
        }
    }
    
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain" // Only allow text files
        }
        getContent.launch(intent)
    }
    
    private fun importFile() {
        val fileName = binding.editTextFileName.text.toString().trim()
        
        if (fileName.isEmpty()) {
            binding.inputLayoutFileName.error = "File name is required"
            return
        } else {
            binding.inputLayoutFileName.error = null
        }
        
        val uri = selectedFileUri
        if (uri == null) {
            Snackbar.make(binding.root, "No file selected", Snackbar.LENGTH_SHORT).show()
            return
        }
        
        // Show loading state
        binding.buttonImport.isEnabled = false
        binding.buttonImport.text = getString(R.string.importing)
        
        // Import and encrypt the file
        viewModel.importFile(uri, fileName)
        
        // Go back to the files list
        Snackbar.make(binding.root, "File encrypted and saved", Snackbar.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 