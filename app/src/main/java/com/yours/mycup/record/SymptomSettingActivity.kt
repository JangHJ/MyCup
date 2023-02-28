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
import com.yours.mycup.databinding.ActivitySymptomSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SymptomSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySymptomSettingBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val docRef =
        db.collection("${auth.currentUser?.email}").document("Record")

    override fun onCreate(savedInstanceState: Bundle?) {

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = ActivitySymptomSettingBinding.inflate(layoutInflater)

        val intent = intent
        val isyellow: Boolean? = intent.getBooleanExtra("isyellow", false)
        Log.e("isyellow", "${isyellow}")

        if (isyellow == true) {
            binding.save.setTextColorRes(R.color.yellow_d7)
            binding.painPelvic.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.painHeadache.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.painUrinaryTract.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.painOvulatory.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.painMenstrual.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.painBreast.setChipBackgroundColorResource(R.color.chip_background_color_yellow)

            binding.bowelConstipation.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.bowelDiarrhea.setChipBackgroundColorResource(R.color.chip_background_color_yellow)

            binding.emotion.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.sexual.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.appetite.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.acne.setChipBackgroundColorResource(R.color.chip_background_color_yellow)
            binding.tiredness.setChipBackgroundColorResource(R.color.chip_background_color_yellow)


            binding.painPelvic.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.painHeadache.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.painUrinaryTract.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.painOvulatory.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.painMenstrual.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.painBreast.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)

            binding.bowelConstipation.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.bowelDiarrhea.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)

            binding.emotion.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.sexual.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.appetite.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.acne.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
            binding.tiredness.setChipStrokeColorResource(R.color.chip_stroke_color_yellow)
        }
        else {
            binding.save.setTextColorRes(R.color.green_32)
            binding.painPelvic.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.painHeadache.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.painUrinaryTract.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.painOvulatory.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.painMenstrual.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.painBreast.setChipBackgroundColorResource(R.color.chip_background_color_green)

            binding.bowelConstipation.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.bowelDiarrhea.setChipBackgroundColorResource(R.color.chip_background_color_green)

            binding.emotion.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.sexual.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.appetite.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.acne.setChipBackgroundColorResource(R.color.chip_background_color_green)
            binding.tiredness.setChipBackgroundColorResource(R.color.chip_background_color_green)


            binding.painPelvic.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.painHeadache.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.painUrinaryTract.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.painOvulatory.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.painMenstrual.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.painBreast.setChipStrokeColorResource(R.color.chip_stroke_color_green)

            binding.bowelConstipation.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.bowelDiarrhea.setChipStrokeColorResource(R.color.chip_stroke_color_green)

            binding.emotion.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.sexual.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.appetite.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.acne.setChipStrokeColorResource(R.color.chip_stroke_color_green)
            binding.tiredness.setChipStrokeColorResource(R.color.chip_stroke_color_green)
        }

        docRef.get().addOnSuccessListener { document ->
            val date = document.get("clicked_date")
            val docRef2 = docRef.collection("$date").document("Menstruation")
            val docRef3 = docRef.collection("$date").document("Symptom")
            docRef2.get().addOnSuccessListener { document ->
                if (document.get("isEnabled") != null) { // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
//                    var contextThemeWrapper = ContextThemeWrapper(this, R.style.CustomChipStyle_red)
//                    var layoutInflater = this.layoutInflater.cloneInContext(contextThemeWrapper)
                    binding.save.setTextColorRes(R.color.red_e1)
                    binding.painPelvic.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.painHeadache.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.painUrinaryTract.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.painOvulatory.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.painMenstrual.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.painBreast.setChipBackgroundColorResource(R.color.chip_background_color)

                    binding.bowelConstipation.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.bowelDiarrhea.setChipBackgroundColorResource(R.color.chip_background_color)

                    binding.emotion.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.sexual.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.appetite.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.acne.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.tiredness.setChipBackgroundColorResource(R.color.chip_background_color)


                    binding.painPelvic.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.painHeadache.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.painUrinaryTract.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.painOvulatory.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.painMenstrual.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.painBreast.setChipStrokeColorResource(R.color.chip_stroke_color)

                    binding.bowelConstipation.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.bowelDiarrhea.setChipStrokeColorResource(R.color.chip_stroke_color)

                    binding.emotion.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.sexual.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.appetite.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.acne.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.tiredness.setChipStrokeColorResource(R.color.chip_stroke_color)
                } else {
                }
            }
            docRef3.get().addOnSuccessListener { document ->
                var bowel: String = ""
                if (document.get("isEnabled") == true) {
                    //if(document.get("pain") != null)
                    val pain = document.get("pain") as ArrayList<String>
                    if (document.get("bowel") != null)
                        bowel = document.get("bowel") as String
                    //if(document.get("etc") != null)
                    val etc = document.get("etc") as ArrayList<String>

                    Log.e("증상 문서 출력 : ", "${document.get("bowel")}")


                    if (bowel != null) {
                        when (bowel) {
                            "변비" -> {
                                binding.bowelConstipation.isChecked = true
                            }
                            "설사" -> {
                                binding.bowelDiarrhea.isChecked = true
                            }
                        }
                    }
                    for (i in 0..5) {
                        if (pain[i] != null) {
                            when (pain[i]) {
                                "골반 통증" -> binding.painPelvic.isChecked = true
                                "두통" -> binding.painHeadache.isChecked = true
                                "배뇨통" -> binding.painUrinaryTract.isChecked = true
                                "배란통" -> binding.painOvulatory.isChecked = true
                                "월경통" -> binding.painMenstrual.isChecked = true
                                "유방 통증" -> binding.painBreast.isChecked = true
                            }
                        }
                    }
                    Log.e("pain 결과 : ", "$pain")


                    for (i in 0..4) {
                        if (etc[i] != null) {
                            when (etc[i]) {
                                "감정기복" -> binding.emotion.isChecked = true
                                "성욕 변화" -> binding.sexual.isChecked = true
                                "식욕 변화" -> binding.appetite.isChecked = true
                                "여드름" -> binding.acne.isChecked = true
                                "피로감" -> binding.tiredness.isChecked = true
                            }
                        }
                    }
                    Log.e("etc 결과 : ", "$etc")
                    Log.e("증상 결과1 : ", "$bowel, $pain, $etc")
                }
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnInitClose.setOnClickListener {
            finish()
        }


        binding.save.setOnClickListener {
            docRef.get().addOnSuccessListener { document ->
                val date = document.get("clicked_date")
                var pain = arrayOfNulls<Any>(6)
                var bowel: String? = null
                var etc = arrayOfNulls<Any>(5)
                var cnt_p = 0
                var cnt_b = 0
                var cnt_e = 0

                //통증 추가
                if (binding.painPelvic.isChecked == true) {
                    pain[0] = "${binding.painPelvic.text}" // 골반 통증
                    cnt_p++
                }

                if (binding.painHeadache.isChecked == true) {
                    pain[1] = "${binding.painHeadache.text}" // 두통
                    cnt_p++
                }

                if (binding.painUrinaryTract.isChecked == true) {
                    pain[2] = "${binding.painUrinaryTract.text}" // 배뇨통
                    cnt_p++
                }

                if (binding.painOvulatory.isChecked == true) {
                    pain[3] = "${binding.painOvulatory.text}" // 배란통
                    cnt_p++
                }

                if (binding.painMenstrual.isChecked == true) {
                    pain[4] = "${binding.painMenstrual.text}" // 월경통
                    cnt_p++
                }

                if (binding.painBreast.isChecked == true) {
                    pain[5] = "${binding.painBreast.text}" // 유방 통증
                    cnt_p++
                }

                //배변 추가
                if (binding.bowelConstipation.isChecked == true) {
                    bowel = "${binding.bowelConstipation.text}" // 변비
                    cnt_b++

                } else if (binding.bowelDiarrhea.isChecked == true) {
                    bowel = "${binding.bowelDiarrhea.text}" // 설사
                    cnt_b++
                } else {
                    Log.e("오류!", "배변 칩선택이 실패하였습니다.")
                }

                //기타 추가
                if (binding.emotion.isChecked == true) {
                    etc[0] = "${binding.emotion.text}" // 감정기복
                    cnt_e++
                }
                if (binding.sexual.isChecked == true) {
                    etc[1] = "${binding.sexual.text}" // 성욕변화
                    cnt_e++
                }
                if (binding.appetite.isChecked == true) {
                    etc[2] = "${binding.appetite.text}" // 식욕변화
                    cnt_e++
                }
                if (binding.acne.isChecked == true) {
                    etc[3] = "${binding.acne.text}" // 여드름
                    cnt_e++
                }
                if (binding.tiredness.isChecked == true) {
                    etc[4] = "${binding.tiredness.text}" // 피로감
                    cnt_e++
                }

                if (cnt_p == 0 && cnt_b == 0 && cnt_e == 0) {
                    docRef.collection("$date")
                        .document("Symptom").delete()
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
                } else {
                    val data = hashMapOf(
                        "pain" to listOf(pain[0], pain[1], pain[2], pain[3], pain[4], pain[5]),
                        "bowel" to bowel,
                        "etc" to listOf(etc[0], etc[1], etc[2], etc[3], etc[4]),
                        "isEnabled" to true
                    )
                    docRef.collection("$date")
                        .document("Symptom").set(data, SetOptions.merge())
                }
            }

            Toast.makeText(applicationContext, "저장되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }


    }

}