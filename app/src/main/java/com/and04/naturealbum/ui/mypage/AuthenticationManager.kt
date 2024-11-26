package com.and04.naturealbum.ui.mypage

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.and04.naturealbum.BuildConfig
import com.and04.naturealbum.background.workmanager.SynchronizationWorker
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface AuthResponse {
    data class Success(val token: String) : AuthResponse
    data class Error(val message: String) : AuthResponse
}

class AuthenticationManager {
    private val auth = Firebase.auth

    fun signInWithGoogle(context: Context): Flow<AuthResponse> = callbackFlow {
        try {
            val credential = getCredential(context)
            val firebaseCredential = getFirebaseCredential(credential) ?: return@callbackFlow

            handleFirebaseSignIn(context, firebaseCredential) { authResponse ->
                trySend(authResponse)
            }

        } catch (e: GoogleIdTokenParsingException) {
            trySend(AuthResponse.Error(e.message.toString()))
        } catch (e: Exception) {
            trySend(AuthResponse.Error(e.message.toString()))
        } finally {
            awaitClose()
        }
    }

    private suspend fun getCredential(context: Context): Credential {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.google_web_key)
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

        return result.credential
    }

    private fun getFirebaseCredential(credential: Credential): AuthCredential? {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(credential.data)

            val firebaseCredential = GoogleAuthProvider.getCredential(
                googleIdTokenCredential.idToken,
                null
            )

            return firebaseCredential
        }

        return null
    }

    private fun handleFirebaseSignIn(
        context: Context,
        firebaseCredential: AuthCredential,
        trySend: (AuthResponse) -> Unit,
    ) {
        auth.signInWithCredential(firebaseCredential)
            .addOnSuccessListener {
                getUserToken { authResponse -> trySend(authResponse) }
                SynchronizationWorker.runSync(context)
            }
            .addOnFailureListener { failureResult ->
                trySend(
                    AuthResponse.Error(
                        failureResult.message.toString()
                    )
                )
            }
    }

    private fun getUserToken(trySend: (AuthResponse) -> Unit) {
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.token?.let { token ->
                    trySend(AuthResponse.Success(token))
                }
            }
        }
    }
}
