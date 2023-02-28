package com.yours.mycup.mypage

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityMyAppSettingBinding
import com.yours.mycup.init.LoginActivity
import com.yours.mycup.record.dpToPx
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.io.File
import java.lang.Thread.sleep


class MyAppSettingActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMyAppSettingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_my_app_setting)

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.btnMyappBack.setOnClickListener {
            finish()
        }

        binding.tvMyappTos.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/haneulKimaa/mycup_terms_conditions/blob/main/README.md"))
           // val intent = Intent(this, InitAgreeActivity::class.java)
           // intent.putExtra("from", "mypage")
            startActivity(intent)
        }

        binding.tvMyappLogout.setOnClickListener {
            // 다이얼로그
            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.dialog_logout, null)
            dialog.setView(mView)
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(R.drawable.join_dialog_background)
            dialog.window?.setLayout(dpToPx(this, 280), dpToPx(this, 100))
            dialog.create()

            val cancel: TextView = mView.findViewById(R.id.dialog_logout_no)
            val ok: TextView = mView.findViewById(R.id.dialog_logout_ok)

            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()
            }
            ok.setOnClickListener {
                Firebase.auth.signOut()
                Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                ActivityCompat.finishAffinity(this)
                startActivity(intent)

                dialog.dismiss()
                dialog.cancel()
                finish()
            }
        }

        binding.tvMyappWithdraw.setOnClickListener {
            // 다이얼로그
            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.dialog_withdrawal, null)
            dialog.setView(mView)
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(R.drawable.join_dialog_background)
            dialog.window?.setLayout(dpToPx(this, 280), dpToPx(this, 100))
            dialog.create()

            val cancel: TextView = mView.findViewById(R.id.dialog_withdrawal_no)
            val ok: TextView = mView.findViewById(R.id.dialog_withdrawal_ok)

            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()
            }
            ok.setOnClickListener {
                Log.e("탈퇴 진입", "성공!!!!!!!!!!!!!!")

                db.collection("${auth.currentUser?.email}").document("Record").get().addOnSuccessListener { document ->
                    val email = "${auth.currentUser?.email}"
                    db.collection("ALL").document("Review").get().addOnSuccessListener { document ->
                        if(document.get("list_num") != null)
                        {
                            val listnum = "${document.get("list_num")}".toInt()
                            var rvnum = 0
                            if(document.get("rv_num") != null || "${document.get("rv_num")}".toInt() > 0){
                                rvnum =  "${document.get("rv_num")}".toInt()
                            }
                            var cnt = 0

                            if(rvnum != 0)
                            {
                                for(i in 1..listnum) {
                                    if("${document.get("rvlist$i.email")}" == email){
                                        cnt++
                                        val updates = hashMapOf<String, Any>(
                                            "rvlist$i" to FieldValue.delete(),
                                            "rv_num" to rvnum - cnt
                                        )
                                        db.collection("ALL").document("Review").update(updates).addOnCompleteListener {}
                                    }
                                }
                                rvnum -= cnt
                            }
//                            if("${document.get("rv_num")}".toInt() <= 0)
                            if(rvnum <= 0)
                            {
                                db.collection("ALL").document("Review").delete()
                                    .addOnSuccessListener {
                                        Log.e("전체 후기가", "남아있지 않아 폴더를 삭제함")
                                    }.addOnFailureListener{
                                        Log.e("전체 후기 폴더", "삭제실패")
                                    }
                            }
                        }
                    }
                    //
                    if(document.get("date") != null){
                        val arr :ArrayList<String> = document.get("date") as ArrayList<String>
                        for(i in 0..arr.size-1){
                            Log.e("arr값 : ", "${arr[i]}")
                            db.collection("${auth.currentUser?.email}").document("Record")
                                .collection("${arr[i]}").document("Menstruation").delete()
                                .addOnSuccessListener { Log.d("Menstruation 삭제", "DocumentSnapshot successfully deleted!") }
                                .addOnFailureListener { e -> Log.w("Menstruation 삭제", "Error deleting document", e) }

                            db.collection("${auth.currentUser?.email}").document("Record")
                                .collection("${arr[i]}").document("Symptom").delete()
                                .addOnSuccessListener { Log.d("Symptom 삭제", "DocumentSnapshot successfully deleted!") }
                                .addOnFailureListener { e -> Log.w("Symptom 삭제", "Error deleting document", e) }

                            db.collection("${auth.currentUser?.email}").document("Record")
                                .collection("${arr[i]}").document("Discharge").delete()
                                .addOnSuccessListener { Log.d("Discharge 삭제", "DocumentSnapshot successfully deleted!") }
                                .addOnFailureListener { e -> Log.w("Discharge 삭제", "Error deleting document", e) }
                        }
                    }
                    //
                }

                //sleep(2000)
                val data = hashMapOf(
                    "secession" to true
                )
                db.collection("${auth.currentUser?.email}").document("Init").set(data, SetOptions.merge())

                dialog.dismiss()
                dialog.cancel()
                Toast.makeText(this, "탈퇴처리를 진행하고 있습니다", Toast.LENGTH_SHORT).show()
                sleep(1000)

                val intent = Intent(this, LoginActivity::class.java)
                ActivityCompat.finishAffinity(this)
                startActivity(intent)

            } // ok눌렀을때

        }
    }
}