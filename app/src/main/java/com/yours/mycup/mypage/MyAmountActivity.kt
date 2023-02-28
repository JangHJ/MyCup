package com.yours.mycup.mypage

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityMyAmountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class MyAmountActivity : AppCompatActivity(), MyAmountBottomSheet.AmountPassListener {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: ActivityMyAmountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_amount)

        db.collection("${auth.currentUser?.email}").document("Mypage").get().addOnSuccessListener { document ->
            if(document.get("men_amount") != null)
            {
                binding.tvMyamountAnswer.setText("${document.get("men_amount")}")
                binding.btnMyamountNext.setBackgroundResource(R.drawable.init_button_next_enabled_true)
            }
        }

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.tvMyamountAnswer.setOnClickListener {
            val bottomSheetDialog = MyAmountBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "myamountBottomSheet")
        }

        binding.btnMyamountBack.setOnClickListener {
            finish()
        }

        binding.btnMyamountNext.setOnClickListener {
            if (binding.tvMyamountAnswer.text.isNullOrBlank()) {
                binding.tvMyamountAnswer.setHintTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.mainPointColor
                    )
                )
                binding.tvMyamountAnswer.backgroundTintList = ContextCompat.getColorStateList(
                    applicationContext,
                    R.color.mainPointColor
                )
                Toast.makeText(this, "월경량을 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                // 파이어베이스에 저장
                val data = hashMapOf(
                    "men_amount" to binding.tvMyamountAnswer.text.toString()
                )
                db.collection("${auth.currentUser?.email}").document("Mypage").set(data, SetOptions.merge())

                //질길이 입력 시 해당유저가 저장한 후기의 질길이 값을 바꿔줌

             /*   db.collection("ALL").document("Review").get().addOnSuccessListener { document ->
                    if (document.get("list_num") != null) {
                        val listnum_all = "${document.get("list_num")}".toInt()

                        Log.e("월경량바꾸기 테스트 : ", "$listnum_all")

                        for (i in 1..listnum_all.toInt()) {
                            if (document.get("rvlist$i.email") == "${auth.currentUser?.email}") {
                                val updates =
                                    hashMapOf<String, Any>(
                                        "rvlist$i" to mapOf(
                                            "am" to binding.tvMyamountAnswer.text.toString()
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

                finish()
            }
        }
    }

    override fun onAmountPass(data: Int?) {
        binding.tvMyamountAnswer.setText(data.toString() + "ml")

        if (binding.tvMyamountAnswer.text.isNotBlank() && binding.tvMyamountAnswer.text.isNotEmpty()) {
            binding.btnMyamountNext.setBackgroundResource(R.drawable.init_button_next_enabled_true)
            binding.tvMyamountAnswer.backgroundTintList = ContextCompat.getColorStateList(
                applicationContext, R.color.black)
        }
    }
}