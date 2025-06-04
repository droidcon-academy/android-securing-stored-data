# VaultKeeper: Starter Project for Android Encryption Codelab

This starter project is the foundation for the "Securing Stored Data in Android: Implementing Encryption with Keystore API" codelab. VaultKeeper is a privacy-focused note and file storage application designed to demonstrate practical encryption techniques in Android.

## Overview

VaultKeeper demonstrates how to securely store sensitive user data on Android devices using the Android Keystore system and encryption best practices. The starter project provides a complete UI and app structure, allowing you to focus exclusively on implementing the security features during the codelab.

## Key Features to Implement

Throughout the codelab, you'll transform this starter project by implementing:

- **Secure Key Management**: Using Android Keystore to securely generate and store encryption keys
- **Preference Encryption**: Protecting app settings with DataStore and manual encryption
- **File Encryption**: Securing imported text files with AES encryption
- **Database Encryption**: Storing encrypted notes in Room database
- **Security Best Practices**: Implementing proper error handling, key management, and more

## Project Structure

The project follows a clean MVVM architecture with clear separation of concerns:

```
com.droidcon.vaultkeeper
├── data/                       # Data layer
│   ├── db/                     # Database configuration
│   │   ├── EncryptedFileDao.kt # DAO for encrypted files
│   │   ├── NoteDao.kt          # DAO for notes
│   │   └── VaultKeeperDatabase.kt # Room database setup
│   ├── model/                  # Data models
│   │   ├── EncryptedFile.kt    # Model for encrypted files
│   │   └── Note.kt             # Model for notes
│   ├── preferences/            # Preference management
│   │   └── EncryptedPreferenceManager.kt # Secure preferences
│   └── repository/             # Data repositories
│       ├── EncryptedFileRepository.kt # File operations repo
│       └── NoteRepository.kt   # Note operations repo
├── ui/                         # UI layer
│   ├── splash/                 # Splash screen with auth
│   │   └── SplashFragment.kt
│   ├── home/                   # Home screen with tabs
│   │   ├── HomeFragment.kt
│   │   ├── adapter/
│   │   └── tabs/               # Tab fragments
│   ├── noteeditor/             # Note editor
│   │   └── NoteEditorFragment.kt
│   └── fileimport/             # File import
│       └── FileImportFragment.kt
├── utils/                      # Utilities
│   ├── CryptoUtils.kt          # Core encryption utilities (TODO)
│   └── FileUtils.kt            # File operations (TODO)
├── viewmodel/                  # ViewModels
│   ├── FileViewModel.kt        # ViewModel for files
│   └── NoteViewModel.kt        # ViewModel for notes
└── MainActivity.kt             # Main activity and nav host
```

## Dependencies and Libraries

All necessary dependencies are pre-configured in the project to avoid build issues:

### Core Android Components
```gradle
implementation 'androidx.core:core-ktx:1.16.0'
implementation 'androidx.appcompat:appcompat:1.7.0'
implementation 'com.google.android.material:material:1.12.0'
```

### Architecture Components
```gradle
// MVVM Architecture with ViewModel and LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.8.7'

// Navigation Component
implementation 'androidx.navigation:navigation-fragment-ktx:2.8.9'
implementation 'androidx.navigation:navigation-ui-ktx:2.8.9'
```

### Data Storage
```gradle
// Room for database storage
implementation 'androidx.room:room-runtime:2.7.0'
implementation 'androidx.room:room-ktx:2.7.0'
kapt 'androidx.room:room-compiler:2.7.0'

// DataStore for preference storage
implementation 'androidx.datastore:datastore-preferences:1.0.0'
```

### Security
```gradle
// Android Biometric API for authentication
implementation 'androidx.biometric:biometric:1.2.0-alpha05'
```

### Concurrency
```gradle
// Kotlin Coroutines for async operations
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2'
```

## Getting Started

Follow these steps to start working with the project:

1. **Clone the repository** or download the starter project
   ```
   git clone https://github.com/droidcon/android-securing-stored-data.git
   ```

2. **Open the project** in Android Studio Hedgehog (2023.1.1) or newer

3. **Build and run** the starter project to make sure everything compiles correctly
   
4. **Locate the TODOs** - The main files you'll need to modify are:
   - `utils/CryptoUtils.kt` - Core encryption functionality
   - `data/preferences/EncryptedPreferenceManager.kt` - Secure preferences
   - `utils/FileUtils.kt` - File encryption/decryption
   - `data/repository/NoteRepository.kt` - Database encryption

5. **Follow the codelab instructions** to implement each security feature

## What's Included

The starter project provides:

- **Complete UI**: All screens, navigation, and layouts are fully implemented
- **Database Structure**: Room database and DAOs are set up and ready to use
- **ViewModels**: All necessary ViewModels for MVVM architecture
- **Repositories**: Repository interfaces with placeholder security implementations
- **TODOs**: Clear comments indicating what code you need to implement

## What You'll Implement

During the codelab, you'll implement:

1. **Key Generation**: Create AES encryption keys securely stored in Android Keystore
2. **String Encryption/Decryption**: Implement AES-GCM encryption for text data
3. **File Encryption/Decryption**: Create encrypted file streams
4. **DataStore Encryption**: Add encryption to preference storage
5. **Database Security**: Implement secure note storage with encryption

## Running the App

When you run the starter project, you'll see a functional app with the following features:

- Home screen with tabs for notes and files
- Note creation and viewing (without encryption initially)
- File import (without encryption initially)
- Navigation between screens

As you progress through the codelab, your app will gradually become more secure, with all sensitive data properly encrypted.

## Need Help?

If you encounter any issues with the starter project:

- Check your implementation against the TODO comments
- Refer to the codelab instructions for detailed guidance
- Consult the Android security documentation for reference
- Compare with the final project solution if you get stuck

Happy coding, and enjoy building a secure Android application! 