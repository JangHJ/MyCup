package com.yours.mycup.init

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import com.yours.mycup.MainActivity
import com.yours.mycup.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.lang.Thread.sleep

class LoginActivity : AppCompatActivity() {
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1000

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 구글 SignInButton 텍스트 변경
//    private fun setGoogleBtnText (btn_googlelogin: SignInButton, buttonText: String){
//        var i = 0
//        while (i < btn_googlelogin.childCount){
//            var v = btn_googlelogin.getChildAt(i)
//            if (v is TextView){
//                var tv = v
//                tv.setText(buttonText)
//                tv.gravity = Gravity.CENTER
//                return
//            }
//            i++
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

        val btn_googlelogin: Button = findViewById(R.id.btn_googlelogin)
//        val btn_googlelogin: SignInButton = findViewById(R.id.btn_googlelogin)
//        setGoogleBtnText(btn_googlelogin, "구글 계정으로 로그인")

        btn_googlelogin.setOnClickListener { // google login button
            val signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task?.signInAccount
                firebaseAuthWithGoogle(account!!)

                Log.d("Google Login", "firebaseAuthWithGoogle:" + account.id)
                Toast.makeText(this, "구글 로그인 성공하였습니다", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google Login", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        Log.d("Google Login", "signInWithCredential:success")
                        if (auth.currentUser != null) { // 로그인 성공했다면
                            val docRef1 = db.collection("${auth.currentUser?.email}").document("Init")
                            docRef1.get().addOnSuccessListener { document ->
                                if (document.get("isJoined") == true) { // 로그아웃했다가 다시 로그인하는 경우
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                                else{
                                    // 약관동의 하지 않은 경우 InitActivity로 (회원가입)
                                    Log.e("테스트테스트2 : ", "${Firebase.auth.currentUser?.email}")
                                    startActivity(Intent(this, InitActivity::class.java))
                                }
                                //finish()
                            }
                        }
                    } else {
                        Log.w("Google Login", "signInWithCredential:failure", it.exception)
                    }
                }
    }

    private fun moveMainPage(user: FirebaseUser?) {
//        if (user != null) {
        Log.e("테스트테스트1 : ", "${Firebase.auth.currentUser?.email}")
        if (user != null) { // 자동로그인
            val docRef1 = db.collection("${auth.currentUser?.email}").document("Init")
            docRef1.get().addOnSuccessListener { document ->
                if(document.get("secession") == true){
                    Toast.makeText(this, "탈퇴 완료되었습니다", Toast.LENGTH_SHORT).show()
                    db.collection("${auth.currentUser?.email}").document("Init").delete()
                        .addOnSuccessListener { Log.d("Init 폴더 삭제", "DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> Log.w("Init 폴더 삭제", "Error deleting document", e) }
                    db.collection("${auth.currentUser?.email}").document("Mypage").delete()
                        .addOnSuccessListener { Log.d("Mypage 폴더 삭제", "DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> Log.w("Mypage 폴더 삭제", "Error deleting document", e) }
                    db.collection("${auth.currentUser?.email}").document("Record").delete()
                        .addOnSuccessListener { Log.d("Record 폴더 삭제", "DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> Log.w("Record 폴더 삭제", "Error deleting document", e) }
                    db.collection("${auth.currentUser?.email}").document("Review").delete()
                        .addOnSuccessListener {
                            Log.d("Review 폴더 삭제", "DocumentSnapshot successfully deleted!")
                        }
                        .addOnFailureListener {
                            Log.e("탈퇴확인33", Firebase.auth.currentUser.toString())
                            Log.e("Review 폴더 삭제", "실패하였습니다")
                            Toast.makeText(this, "탈퇴되지 않았습니다", Toast.LENGTH_SHORT).show()
                        }
                    Firebase.auth.signOut()
                }
                else if(document.get("isJoined") == true){
                    Toast.makeText(this, "로그인 중입니다", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth.currentUser)
    }
}