package com.yours.mycup.mypage

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityMyLengthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class MyLengthActivity : AppCompatActivity(), MyLengthBottomSheet.LengthPassListener {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: ActivityMyLengthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_length)
        var click = false

        db.collection("${auth.currentUser?.email}").document("Mypage").get().addOnSuccessListener { document ->
            if(document.get("vagina_len") != null)
            {
                binding.tvMylengthAnswer.setText("${document.get("vagina_len")}")
                binding.btnMylengthNext.setBackgroundResource(R.drawable.init_button_next_enabled_true)
            }
        }
        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.tvMylengthAnswer.setOnClickListener {
            val bottomSheetDialog = MyLengthBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "mylengthBottomSheet")
        }

        binding.btnMylengthTip.setOnClickListener {
            if (!click) {
                click = true
                binding.imgMylengthTip.visibility = View.VISIBLE
            } else {
                click = false
                binding.imgMylengthTip.visibility = View.GONE
            }
        }
        binding.maincl.setOnClickListener {
            if (click == true) {
                binding.imgMylengthTip.visibility = View.GONE
            }
        }

        binding.btnMylengthBack.setOnClickListener {
            finish()
        }

        binding.btnMylengthNext.setOnClickListener {
            if (binding.tvMylengthAnswer.text.isNullOrBlank()) {
                binding.tvMylengthAnswer.setHintTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.mainPointColor
                    )
                )
                binding.tvMylengthAnswer.backgroundTintList = ContextCompat.getColorStateList(
                    applicationContext,
                    R.color.mainPointColor
                )
                Toast.makeText(this, "질 길이를 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                // 파이어베이스에 저장
                val data = hashMapOf(
                    "vagina_len" to binding.tvMylengthAnswer.text.toString()
                )
                db.collection("${auth.currentUser?.email}").document("Mypage")
                    .set(data, SetOptions.merge())

                //질길이 입력 시 해당유저가 저장한 후기의 질길이 값을 바꿔줌 - 삭제
           /*     db.collection("ALL").document("Review").get().addOnSuccessListener { document ->
                    if (document.get("list_num") != null) {
                        val listnum_all = "${document.get("list_num")}".toInt()

                        Log.e("질길이바꾸기 테스트 : ", "$listnum_all")

                        for (i in 1..listnum_all.toInt()) {
                            if (document.get("rvlist$i.email") == "${auth.currentUser?.email}") {
                                val updates =
                                    hashMapOf<String, Any>(
                                        "rvlist$i" to mapOf(
                                            "len" to binding.tvMylengthAnswer.text.toString()
                                        )
                                    )
                                db.collection("ALL")
                                    .document("Review")
                                    .set(
                                        updates,
                                        SetOptions.merge()
                                    )
                            }
                        }
                    }
                }*/
            }
            finish()
        }
    }


    override fun onLengthPass(data: Int?) {
        binding.tvMylengthAnswer.setText(data.toString() + "mm")

        if (binding.tvMylengthAnswer.text.isNotBlank() && binding.tvMylengthAnswer.text.isNotEmpty()) {
            binding.btnMylengthNext.setBackgroundResource(R.drawable.init_button_next_enabled_true)
            binding.tvMylengthAnswer.backgroundTintList = ContextCompat.getColorStateList(
                applicationContext, R.color.black
            )
        }
    }

}