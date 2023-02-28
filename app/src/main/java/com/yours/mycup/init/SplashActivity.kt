package com.yours.mycup.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.yours.mycup.MainActivity
import com.yours.mycup.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그아웃 하고 다시 로그인하는 경우 스플래시 -> 로그인 -> 메인
        // 탈퇴하고 다시 가입하는 경우 스플래시 -> 로그인 -> InitActivity
        // 자동로그인이라면 스플래시 -> 메인

        // 상태바 보이게 설정
        this.window?.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            val docRef1 = db.collection("${auth.currentUser?.email}").document("Init")
            docRef1.get().addOnSuccessListener { document ->
                if (document.get("isJoined") == true) { // 자동로그인
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            }
        }
        else{
            // 로그아웃 후 재로그인 or 약관동의 하지 않은 경우(회원가입)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}