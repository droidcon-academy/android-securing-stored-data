package com.droidcon.vaultkeeper.ui.splash

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.data.preferences.EncryptedPreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navigateToHome: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val encryptedPreferenceManager = remember {
        EncryptedPreferenceManager(context)
    }

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "Alpha Animation"
    )

    var showBiometricPrompt by remember { mutableStateOf(false) }
    var showFingerprint by remember { mutableStateOf(false) }
    val loadingText = stringResource(R.string.loading)
    var statusText by remember { mutableStateOf(loadingText) }

    // Function to handle fallback authentication
    fun showAuthenticationFallbackOption() {
        // In a real app, you could show a password entry dialog or other fallback
        // For this demo, we'll just proceed to the home screen
        scope.launch {
            snackbarHostState.showSnackbar("Using fallback authentication method")
            delay(1000)
            navigateToHome()
        }
    }

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(800)

        if (encryptedPreferenceManager.isBiometricEnabled()) {
            // Check if biometric authentication is available
            when (BiometricManager.from(context)
                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    showFingerprint = true
                    statusText = context.getString(R.string.authenticate_to_continue)
                    showBiometricPrompt = true
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("This device doesn't support biometric authentication")
                        encryptedPreferenceManager.setBiometricEnabled(false)
                        showAuthenticationFallbackOption()
                    }
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Biometric features are currently unavailable")
                        showAuthenticationFallbackOption()
                    }
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("No biometrics enrolled")
                        showAuthenticationFallbackOption()
                    }
                }

                else -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Biometric authentication unavailable")
                        showAuthenticationFallbackOption()
                    }
                }
            }
        } else {
            // Skip biometric authentication and proceed after a short delay
            delay(1500)
            navigateToHome()
        }
    }

    // Handle biometric prompt
    if (showBiometricPrompt) {
        val executor = remember { ContextCompat.getMainExecutor(context) }
        val biometricPrompt = remember {
            BiometricPrompt(
                context as FragmentActivity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)

                        when (errorCode) {
                            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Authentication cancelled")
                                    showAuthenticationFallbackOption()
                                }
                            }

                            BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("No biometrics enrolled on this device")
                                    showAuthenticationFallbackOption()
                                }
                            }

                            else -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Authentication error: $errString")
                                    showAuthenticationFallbackOption()
                                }
                            }
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        scope.launch {
                            snackbarHostState.showSnackbar("Authentication successful")
                            delay(1000)
                            navigateToHome()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        scope.launch {
                            snackbarHostState.showSnackbar("Authentication failed")
                            showAuthenticationFallbackOption()
                        }
                    }
                }
            )
        }

        val promptTitle = stringResource(R.string.biometric_auth_title)
        val promptSubtitle = stringResource(R.string.biometric_auth_subtitle)
        val promptDescription = stringResource(R.string.biometric_auth_description)
        val promptNegativeButtonText = stringResource(R.string.biometric_auth_negative_button)

        val promptInfo = remember {
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(promptTitle)
                .setSubtitle(promptSubtitle)
                .setDescription(promptDescription)
                .setNegativeButtonText(promptNegativeButtonText)
                .build()
        }

        LaunchedEffect(key1 = showBiometricPrompt) {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF006A6A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = stringResource(R.string.content_description_app_logo),
                colorFilter = ColorFilter.tint(Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showFingerprint) {
                Image(
                    modifier = Modifier.size(80.dp),
                    painter = painterResource(id = R.drawable.ic_fingerprint),
                    contentDescription = stringResource(R.string.content_description_fingerprint),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = statusText,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color.White
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                snackbarData = data
            )
        }
    }
} 