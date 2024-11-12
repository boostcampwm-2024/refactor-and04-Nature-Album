package com.and04.naturealbum.ui.mypage

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.and04.naturealbum.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthenticationManager(private val context: Context) {
    private val auth = Firebase.auth

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.google_web_key) //Web client td
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential
            if (credential is CustomCredential) {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val firebaseCredential = GoogleAuthProvider.getCredential(
                            googleIdTokenCredential.idToken,
                            null
                        )

                        auth.currentUser?.linkWithCredential(firebaseCredential)
                            ?.addOnCompleteListener { authResult ->
                                if (authResult.isSuccessful) {
                                    trySend(AuthResponse.Success)
                                    val user = authResult.result.user
                                    user?.let { user ->
                                        Log.d("AuthenticationManager", "${user.email}")
                                    }

                                } else {
                                    Log.d("AuthenticationManager", "${authResult.exception}")
                                }
                            }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.d("AuthenticationManager", "error")
                    }
                }
            }
        } catch (e: Exception) {
            trySend(AuthResponse.Error(e.message.toString()))
        }
        awaitClose()
    }
}

interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val message: String) : AuthResponse
}