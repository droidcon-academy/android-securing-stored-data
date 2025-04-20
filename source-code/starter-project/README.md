# VaultKeeper - Starter Project

This is the starter project for the "Securing Stored Data in Android: Implementing Encryption with Keystore API" codelab.

## Project Overview

VaultKeeper is a privacy-first Android app that allows users to securely store sensitive notes and personal text files. The app demonstrates real-world encryption best practices using the Android Keystore API.

In this codelab, you'll build on this starter project to implement:

- Secure key generation with Android Keystore
- AES encryption for notes and files
- Encrypted SharedPreferences for sensitive user data
- Biometric authentication for app access

## Starter Project Structure

The starter project includes:

- Basic app navigation with fragments for splash screen, home, and note editor
- Room database setup with Note entity and DAO
- Repository pattern implementation (without encryption)
- Placeholder utilities for cryptography operations
- ViewModel for notes management

## Libraries and Dependencies

All necessary dependencies are already included in the project:

- AndroidX Core & AppCompat
- Material Components
- ViewModel & StateFlow
- Room Database
- Navigation Component
- Security Crypto Library (for EncryptedSharedPreferences)
- Biometric API
- Kotlin Coroutines

## Getting Started

1. Import the project in Android Studio
2. Build and run to ensure everything works correctly
3. Follow the codelab instructions to implement encryption features

Happy coding! 