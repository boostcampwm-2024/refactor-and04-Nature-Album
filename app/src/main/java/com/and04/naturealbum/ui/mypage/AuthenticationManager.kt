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
            val currentUser = auth.currentUser

            if (currentUser?.isAnonymous == false) { // 이미 로그인한 사용자
                trySend(AuthResponse.Success) // or Error
                awaitClose() // TODO 필요성 확인
                return@callbackFlow
            }

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

                        currentUser?.let { anonymousUser ->
                            anonymousUser.linkWithCredential(firebaseCredential) // 익명회원 -> 구글 전환 시도
                                .addOnSuccessListener { trySend(AuthResponse.Success) }
                                .addOnFailureListener { // 구글계정이 이미 가입되어있을때 : 익명계정 삭제후 로그인
                                    currentUser.delete()
                                    auth.signInWithCredential(firebaseCredential)
                                        .addOnSuccessListener { trySend(AuthResponse.Success) }
                                        .addOnFailureListener { failureResult ->
                                            trySend(
                                                AuthResponse.Error(
                                                    failureResult.message.toString()
                                                )
                                            )
                                        }
                                }
                        }
                            ?: auth.signInWithCredential(firebaseCredential) // 앱시작시 네트워크 문제등 익명 로그인 실패시
                                .addOnSuccessListener { trySend(AuthResponse.Success) }
                                .addOnFailureListener { failureResult ->
                                    trySend(AuthResponse.Error(failureResult.message.toString()))
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
