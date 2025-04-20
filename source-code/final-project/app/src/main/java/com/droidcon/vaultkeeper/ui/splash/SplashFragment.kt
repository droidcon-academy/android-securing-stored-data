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
                    
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            // User clicked the negative button
                            Toast.makeText(
                                requireContext(),
                                "Authentication cancelled",
                                Toast.LENGTH_SHORT
                            ).show()
                            // In a production app, you might want to provide an alternative login method
                            // or exit the app depending on your security requirements
                            showAuthenticationFallbackOption()
                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            // No biometrics enrolled
                            Toast.makeText(
                                requireContext(),
                                "No biometrics enrolled on this device",
                                Toast.LENGTH_LONG
                            ).show()
                            navigateToHome()
                        }
                        else -> {
                            Toast.makeText(
                                requireContext(),
                                "Authentication error: $errString",
                                Toast.LENGTH_SHORT
                            ).show()
                            showAuthenticationFallbackOption()
                        }
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        requireContext(),
                        "Authentication successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToHome()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.auth_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    // Don't navigate yet, let the user try again
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
                binding.animationView.visibility = View.VISIBLE
                binding.textStatus.text = getString(R.string.authenticate_to_continue)
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // No biometric features available on this device
                Toast.makeText(
                    requireContext(),
                    "This device doesn't support biometric authentication",
                    Toast.LENGTH_LONG
                ).show()
                // Disable biometric authentication since it's not supported
                encryptedPreferenceManager.setBiometricEnabled(false)
                navigateToHome()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // Biometric features are currently unavailable
                Toast.makeText(
                    requireContext(),
                    "Biometric features are currently unavailable",
                    Toast.LENGTH_LONG
                ).show()
                navigateToHome()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // The user hasn't enrolled any biometrics
                Toast.makeText(
                    requireContext(),
                    "No biometrics enrolled. Please set up fingerprint or face unlock in system settings",
                    Toast.LENGTH_LONG
                ).show()
                navigateToHome()
            }
            else -> {
                // Other errors
                Toast.makeText(
                    requireContext(),
                    "Biometric authentication unavailable",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToHome()
            }
        }
    }
    
    private fun showAuthenticationFallbackOption() {
        // In a real app, you could show a password entry dialog or other fallback
        // For this demo, we'll just proceed to the home screen
        Toast.makeText(
            requireContext(),
            "Using fallback authentication method",
            Toast.LENGTH_SHORT
        ).show()
        navigateToHome()
    }
    
    private fun navigateToHome() {
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 