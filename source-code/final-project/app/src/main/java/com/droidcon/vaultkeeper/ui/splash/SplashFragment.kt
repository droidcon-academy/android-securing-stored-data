package com.droidcon.vaultkeeper.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.preferences.EncryptedPreferenceManager
import com.droidcon.vaultkeeper.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
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
        
        // Initialize encrypted preference manager
        encryptedPreferenceManager = EncryptedPreferenceManager(requireContext())
        
        // Set up biometric authentication if enabled in preferences
        if (encryptedPreferenceManager.isBiometricEnabled()) {
            setupBiometricAuth()
        } else {
            // Skip biometric authentication and proceed after a short delay
            lifecycleScope.launch {
                delay(1500) // 1.5 seconds delay
                navigateToHome()
            }
        }
    }
    
    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(requireContext())
        
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Handle authentication error
                    Toast.makeText(
                        requireContext(), 
                        getString(R.string.auth_failed), 
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // For this demo, we proceed anyway after error
                    // In a real app, you might want to exit the app or retry
                    navigateToHome()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Authentication successful, proceed
                    navigateToHome()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Authentication failed
                    Toast.makeText(
                        requireContext(), 
                        getString(R.string.auth_failed), 
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_auth_title))
            .setSubtitle(getString(R.string.biometric_auth_subtitle))
            .setDescription(getString(R.string.biometric_auth_description))
            .setNegativeButtonText(getString(R.string.biometric_auth_negative_button))
            .build()

        // Check if biometric authentication is available
        when (BiometricManager.from(requireContext()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Biometric features are available
                biometricPrompt.authenticate(promptInfo)
            }
            else -> {
                // Biometric features are unavailable
                Toast.makeText(
                    requireContext(),
                    "Biometric authentication unavailable",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToHome()
            }
        }
    }
    
    private fun navigateToHome() {
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 