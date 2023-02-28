package com.yours.mycup.record

import android.content.ContentValues
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityDischargeSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class DischargeSettingActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDischargeSettingBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDischargeSettingBinding.inflate(layoutInflater)

        // 상태바 표시
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        val intent = intent
        val isyellow: Boolean? = intent.getBooleanExtra("isyellow", false)
        Log.e("isyellow", "${isyellow}")

        if (isyellow == true) {
            binding.save.setTextColorRes(R.color.yellow_d7)
            binding.veryLittle.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.little.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.normal.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.much.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.veryMuch.setChipBackgroundColorResource(R.color.chip_background_color_yellow)

            binding.sticky.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.creamy.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.eggWhite.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.watery.setChipBackgroundColorResource(R.color.chip_background_color_yellow)

            binding.veryLittle.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.little.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.normal.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.much.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.veryMuch.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)

            binding.sticky.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.creamy.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.eggWhite.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.watery.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
        }
        else {
            binding.save.setTextColorRes(R.color.green_32)
            binding.veryLittle.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.little.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.normal.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.much.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.veryMuch.setChipBackgroundColorResource(R.color.chip_background_color_green)

            binding.sticky.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.creamy.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.eggWhite.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.watery.setChipBackgroundColorResource(R.color.chip_background_color_green)

            binding.veryLittle.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.little.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.normal.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.much.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.veryMuch.setChipStrokeColorResource(R.color.chip_stroke_color_green)

            binding.sticky.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.creamy.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.eggWhite.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.watery.setChipStrokeColorResource(R.color.chip_stroke_color_green)
        }

        val docRef =
            db.collection("${auth.currentUser?.email}").document("Record")
        docRef.get().addOnSuccessListener { document ->
            val date = document.get("clicked_date")
            val docRef2 = docRef.collection("$date").document("Menstruation")
            val docRef3 = docRef.collection("$date").document("Discharge")
            docRef2.get().addOnSuccessListener { document ->
                if(document.get("isEnabled") != null){ // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
//                    var contextThemeWrapper = ContextThemeWrapper(this, R.style.CustomChipStyle_red)
//                    var layoutInflater = this.layoutInflater.cloneInContext(contextThemeWrapper)
                    binding.save.setTextColorRes(R.color.red_e1)
                    binding.veryLittle.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.little.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.normal.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.much.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.veryMuch.setChipBackgroundColorResource(R.color.chip_background_color)

                    binding.sticky.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.creamy.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.eggWhite.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.watery.setChipBackgroundColorResource(R.color.chip_background_color)

                    binding.veryLittle.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.little.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.normal.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.much.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.veryMuch.setChipStrokeColorResource(R.color.chip_stroke_color)

                    binding.sticky.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.creamy.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.eggWhite.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.watery.setChipStrokeColorResource(R.color.chip_stroke_color)


                }
                else {
                }
            }
            docRef3.get().addOnSuccessListener { document ->
                var nang: String = ""
                var touch: String = ""
                if (document.get("isEnabled") == true) {
                    if (document.get("nang") != null)
                        nang = document.get("nang") as String
                    if (document.get("touch") != null)
                        touch = document.get("touch") as String

                    if (nang != null) {
                        when (nang) {
                            "아주 적음" -> binding.veryLittle.isChecked = true
                            "적음" -> binding.little.isChecked = true
                            "보통" -> binding.normal.isChecked = true
                            "많음" -> binding.much.isChecked = true
                            "아주 많음" -> binding.veryMuch.isChecked = true
                        }
                    }
                    if (touch != null) {
                        when (touch) {
                            "끈적하고 점도가 높다" -> binding.sticky.isChecked = true
                            "크림 같다" -> binding.creamy.isChecked = true
                            "계란 흰자 같다" -> binding.eggWhite.isChecked = true
                            "묽다" -> binding.watery.isChecked = true
                        }
                    }

                }
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnInitClose.setOnClickListener {
            finish()
        }
        binding.save.setOnClickListener {
            val docRef = db.collection("${auth.currentUser?.email}").document("Record")
            docRef.get().addOnSuccessListener { document ->
                val date = document.get("clicked_date")
                var nang :String ?= null
                var touch :String ?= null

                //냉 분비량 추가
                if (binding.veryLittle.isChecked == true) {
                    nang = "${binding.veryLittle.text}" // 아주 적음
                } else if (binding.little.isChecked == true) {
                    nang = "${binding.little.text}" // 적음
                } else if (binding.normal.isChecked == true) {
                    nang = "${binding.normal.text}" // 보통
                } else if (binding.much.isChecked == true) {
                    nang = "${binding.much.text}" // 많음
                } else if (binding.veryMuch.isChecked == true) {
                    nang = "${binding.veryMuch.text}" // 아주많음
                } else {
                    Log.e("오류!", "냉분비량 칩선택이 실패하였습니다.")
                }

                //촉감 추가
                if (binding.sticky.isChecked == true) {
                    touch = "${binding.sticky.text}" // 끈적하고 점도가 높다
                } else if (binding.creamy.isChecked == true) {
                    touch = "${binding.creamy.text}" // 크림 같다
                } else if (binding.eggWhite.isChecked == true) {
                    touch = "${binding.eggWhite.text}" // 계란 흰자같다
                } else if (binding.watery.isChecked == true) {
                    touch = "${binding.watery.text}" // 묽다
                } else {
                    Log.e("오류!", "촉감 칩선택이 실패하였습니다.")
                }

                if(nang == null && touch == null)
                {
                    docRef.collection("$date")
                        .document("Discharge").delete()
                        .addOnSuccessListener {
                            Log.d(
                                ContentValues.TAG,
                                "DocumentSnapshot successfully deleted!"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                ContentValues.TAG,
                                "Error deleting document",
                                e
                            )
                        }
                }else{
                    val data = hashMapOf(
                        "nang" to nang,
                        "touch" to touch,
                        "isEnabled" to true
                    )
                    docRef.collection("$date").document("Discharge").set(data, SetOptions.merge())
                }

          /*      if(nang == null){
                    val updates = hashMapOf<String, Any>(
                        "nang" to FieldValue.delete()
                    )
                    db.collection("${auth.currentUser?.email}").document("Record").collection("$date").document("Discharge").update(updates).addOnCompleteListener{}
                }
                if(touch == null){
                    val updates = hashMapOf<String, Any>(
                        "touch" to FieldValue.delete()
                    )
                    db.collection("${auth.currentUser?.email}").document("Record").collection("$date").document("Discharge").update(updates).addOnCompleteListener{}
                }*/
            }

            Toast.makeText(applicationContext, "저장되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}