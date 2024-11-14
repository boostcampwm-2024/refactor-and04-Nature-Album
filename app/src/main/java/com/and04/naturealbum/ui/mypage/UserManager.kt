package com.and04.naturealbum.ui.mypage

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object UserManager {
    private val auth = Firebase.auth

    fun isSignIn() = auth.currentUser != null

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }
}