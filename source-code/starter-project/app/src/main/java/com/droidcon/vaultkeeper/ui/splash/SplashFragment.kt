package com.droidcon.vaultkeeper.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.preferences.EncryptedPreferenceManager
import com.droidcon.vaultkeeper.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var encryptedPreferenceManager: EncryptedPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        encryptedPreferenceManager = EncryptedPreferenceManager(requireContext())
        
        // In the final implementation, this would check if biometric auth is enabled
        // and show the biometric prompt if needed
        
        binding.tvBiometricNote.visibility = if (encryptedPreferenceManager.isBiometricEnabled()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        
        // For now, just show the splash screen for a moment and then navigate to the home screen
        lifecycleScope.launch {
            delay(1500) // Simulate some loading or authentication
            navigateToHome()
        }
    }
    
    private fun navigateToHome() {
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
    }
    
    /**
     * This is a placeholder method for showing biometric authentication
     * Will be implemented in the final project
     */
    private fun showBiometricPrompt() {
        // In the final implementation, this would:
        // 1. Create a BiometricPrompt
        // 2. Set up authentication callbacks
        // 3. Show the biometric prompt
        // 4. Navigate to home on success or show error on failure
        
        // For now, just navigate directly
        navigateToHome()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 