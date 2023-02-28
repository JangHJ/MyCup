package com.yours.mycup.record

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.recyclerview.widget.RecyclerView
import com.yours.mycup.MainActivity
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityMenstruaionSettingBinding
import com.yours.mycup.databinding.ActivityMenstruationListBinding
import com.yours.mycup.databinding.WeekcalendarDayBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class MenstruationSettingActivity : AppCompatActivity(),
    RecordStartTimeBottomSheetFragment.onStartTimePassListener,
    RecordEndTimeBottomSheetFragment.onEndTimePassListener,
    RecordSupplyBottomSheetFragment.onTypePassListener,
    RecordPadBottomSheetFragment.onSizePassListener,
    RecordTamponBottomSheetFragment.onTamponSizePassListener{
    private lateinit var binding: ActivityMenstruaionSettingBinding
    private lateinit var binding2: ActivityMenstruationListBinding
    var bloodVolume: Int = 0
    var supplies = arrayOf("생리대", "탐폰", "월경컵")
    var pads = arrayOf("라이너", "소형", "중형", "대형", "오버나이트")
    var tampons = arrayOf("레귤러", "슈퍼")

    var du_cnt = 0

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val docRef = db.collection("${auth.currentUser?.email}").document("Record")

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {


        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding = ActivityMenstruaionSettingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 초기 화면 설정
        binding.supplySpinner.text = null
        binding.typePickerPad.visibility = View.VISIBLE
        binding.typeInputEdittext.visibility = View.GONE
        binding.typePickerTampon.visibility = View.GONE

        // recyclerview에서 값 받아오기
        val intent = intent
        var time: String? = intent.getStringExtra("time")
        var supplyAndType: String? = intent.getStringExtra("type")
        val amountBlood: String? = intent.getStringExtra("amount")
        val color: String? = intent.getStringExtra("color")
        val position: String? = intent.getStringExtra("pos")
        val isyellow: Boolean? = intent.getBooleanExtra("isyellow", false)
        Log.e("isyellow", "${isyellow}")

        var startTime = listOf("", "")
        var endTime = listOf("", "")
        var isTodayMenstruation: Boolean? = intent.getBooleanExtra("isTodayMenstruation", false)
        val canFixMenstruationSetting: Boolean = false

        Log.e("pos", "${position}")

        if (isyellow == true) {
            binding.save.setTextColorRes(R.color.yellow_d7)
        }
        else {
            binding.save.setTextColorRes(R.color.green_32)
        }

        if (time != null) {
            val startTimeAndEndTime = time.split(" - ")
            binding.startTimeToUseSupply.setText("${startTimeAndEndTime[0]}")
            binding.endTimeToUseSupply.setText("${startTimeAndEndTime[1]}")

            startTime = startTimeAndEndTime[0].split("시 ", "분")
            endTime = startTimeAndEndTime[1].split("시 ", "분")

            binding.startTimeToUseSupply.isEnabled = false
            binding.endTimeToUseSupply.isEnabled = false
            binding.isMenstruatingSwitch.isChecked = true
            binding.isMenstruatingSwitch.isEnabled = false
            binding.supplySpinner.isEnabled = false
            binding.typePickerPad.isEnabled = false
            binding.typeInputEdittext.isEnabled = false
            binding.typePickerTampon.isEnabled = false
        }
        if (supplyAndType != null) {
            Log.e("type은?", "${supplyAndType}")
            binding.typePickerPad.visibility = View.INVISIBLE
            binding.typeInputEdittext.visibility = View.INVISIBLE
            binding.typePickerTampon.visibility = View.INVISIBLE
            val splitSupplyAndType = supplyAndType.split(" ")
            if (supplyAndType == "월경컵") {
                binding.supplySpinner.setText("${supplyAndType}")
                binding.typePickerPad.visibility = View.GONE
                binding.supplySpinner.isEnabled = false
                binding.typeInputEdittext.visibility = View.VISIBLE
                if (amountBlood != null) {
                    binding.typeInputEdittext.setText(amountBlood.split("m")[0])
                    binding.expectaionValueTextView.text = "월경량 " + "${amountBlood}"
                }
            } else if (splitSupplyAndType[0] == "생리대") {
                binding.supplySpinner.setText("${splitSupplyAndType[0]}")
                binding.typePickerPad.setText("${splitSupplyAndType[1]}")

                binding.supplySpinner.isEnabled = false
                binding.typePickerPad.visibility = View.VISIBLE
                if (amountBlood != null) {
                    binding.typeInputEdittext.setText(amountBlood)
                    binding.expectaionValueTextView.text = "예상 월경량 " + "${amountBlood}"
                }
            } else if (splitSupplyAndType[0] == "탐폰") {
                val splitSupplyAndType = supplyAndType.split(" ")
                binding.supplySpinner.setText("${splitSupplyAndType[0]}")
                binding.typePickerTampon.setText("${splitSupplyAndType[1]}")

                binding.supplySpinner.isEnabled = false
                binding.typePickerTampon.visibility = View.VISIBLE
                if (amountBlood != null) {
                    binding.typeInputEdittext.setText(amountBlood)
                    binding.expectaionValueTextView.text = "예상 월경량 " + "${amountBlood}"
                }
            }
        }
        if (color != null) {
            when (color) {
                "밝은 붉은색" -> {
                    binding.lightRed.isEnabled = true
                    binding.lightRed.isChecked = true
                }
                "선분홍색" -> {
                    binding.lightPink.isEnabled = true
                    binding.lightPink.isChecked = true
                }
                "어두운 자주색" -> {
                    binding.darkPurple.isEnabled = true
                    binding.darkPurple.isChecked = true
                }
                "짙은 갈색" -> {
                    binding.darkBrown.isEnabled = true
                    binding.darkBrown.isChecked = true
                }
            }
        }

        // 희정님 여기서 수정해주세요
        docRef.get().addOnSuccessListener { document ->
            val date = document.get("clicked_date")
            val docRef2 = docRef.collection("$date").document("Menstruation")
            docRef2.get().addOnSuccessListener { document ->
                if(document.get("isEnabled") != null){ // 월경기록에 무슨 기록이라도 남아있을때 == 월경중일때 -> if문 실행
//                    var contextThemeWrapper = ContextThemeWrapper(this, R.style.CustomChipStyle_red)
//                    var layoutInflater = this.layoutInflater.cloneInContext(contextThemeWrapper)
                    binding.save.setTextColorRes(R.color.red_e1)
                    binding.lightPink.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.lightRed.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.darkPurple.setChipBackgroundColorResource(R.color.chip_background_color)
                    binding.darkBrown.setChipBackgroundColorResource(R.color.chip_background_color)

                    binding.lightPink.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.lightRed.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.darkPurple.setChipStrokeColorResource(R.color.chip_stroke_color)
                    binding.darkBrown.setChipStrokeColorResource(R.color.chip_stroke_color)
                }
                else {
                }
            }
        }

//        if (당일날 이미 기록된 값이 있을 경우) {
//
//        }

        // 수정을 할 것인지 확인받는 부분
        if (isTodayMenstruation != null) {

            // 받아올 값이 있는 경우 (기록된 값이 있는 경우 = 수정하려하거나 기록된 값을 다시 보는 경우)
            if (isTodayMenstruation == true) {
                binding.save.isEnabled = false
                binding.save.isVisible = false
                binding.startTimeToUseSupply.isEnabled = true
                binding.endTimeToUseSupply.isEnabled = true
                binding.isMenstruatingSwitch.isChecked = true
                binding.isMenstruatingSwitch.isEnabled = true
                binding.supplySpinner.isEnabled = true
                binding.typePickerPad.isEnabled = true
                binding.typeInputEdittext.isEnabled = true
                binding.typePickerTampon.isEnabled = true
                binding.expectaionValueTextView.setTextColorRes(R.color.red_e1)
                binding.save.setTextColorRes(R.color.red_e1)

                binding.save.setOnClickListener {
                    Log.e("menstruation", "fix")
                    val dialog = AlertDialog.Builder(this).create()
                    val edialog: LayoutInflater = LayoutInflater.from(this)
                    val mView: View = edialog.inflate(R.layout.dialog_fix, null)

                    val cancel: TextView = mView.findViewById(R.id.cancel_button)
                    val fix: TextView = mView.findViewById(R.id.save_button)

                    //  취소 버튼 클릭 시
                    cancel.setOnClickListener {
                        binding.save.isEnabled = false
                        binding.save.isVisible = false

                        // 희정씨! 여기서 이전 데이터로 다시 채워야합니다. (수정하지 않겠다고 선택한 것임)
                        dialog.dismiss()
                        dialog.cancel()
                    }
                    //  수정 버튼 클릭 시
                    fix.setOnClickListener {
                        // 희정님!! 여기서 firebase에 업데이트해야 합니다.
                        var startTime = binding.startTimeToUseSupply.text.toString()
                        var endTime = binding.endTimeToUseSupply.text.toString()
                        var supply = binding.supplySpinner.text.toString()
                        var type: String = "null"
                        var color: String = "null"
                        var amount = 0

                        if (supply == "생리대") {
                            Log.e("정혈 색상", "${binding.lightRed.text}")
                            type = binding.typePickerPad.text.toString()
                        } else if (supply == "탐폰") {
                            Log.e("정혈 색상", "${binding.lightPink.text}")
                            type = binding.typePickerTampon.text.toString()
                        } else if (supply == "월경컵") {
                            Log.e("정혈 색상", "${binding.darkPurple.text}")
                            type = binding.typeInputEdittext.text.toString()
                        } else {
                            Log.e("오류!", "칩선택이 실패하였습니다.")
                        }

                        val switch_on = binding.isMenstruatingSwitch.isChecked

                        docRef.get().addOnSuccessListener { document ->
                            val date = document.get("clicked_date") //선택된 날짜 저장

                            if (binding.lightRed.isChecked == true) {
                                Log.e("정혈 색상", "${binding.lightRed.text}")
                                color = "${binding.lightRed.text}"
                            } else if (binding.lightPink.isChecked == true) {
                                Log.e("정혈 색상", "${binding.lightPink.text}")
                                color = "${binding.lightPink.text}"
                            } else if (binding.darkPurple.isChecked == true) {
                                Log.e("정혈 색상", "${binding.darkPurple.text}")
                                color = "${binding.darkPurple.text}"
                            } else if (binding.darkBrown.isChecked == true) {
                                Log.e("정혈 색상", "${binding.darkBrown.text}")
                                color = "${binding.darkBrown.text}"
                            } else {
                                Log.e("오류!", "칩선택이 실패하였습니다.")
                            }

                            //amount 값 저장
                            if (supply == "생리대") {
                                when (type) {
                                    "라이너" -> amount = 1
                                    "소형" -> amount = 3
                                    "중형" -> amount = 5
                                    "대형" -> amount = 7
                                    "오버나이트" -> amount = 10
                                }
                            } else if (supply == "탐폰") {
                                when (type) {
                                    "레귤러" -> amount = 4
                                    "슈퍼" -> amount = 8
                                }
                            } else if (supply == "월경컵") {
                                amount = "${binding.typeInputEdittext.text}".toInt()
                            }

                            Log.e("선택된 날짜 : ", "$date")


                            val docRef2 = docRef.collection("$date").document("Menstruation")
                            docRef2.get().addOnSuccessListener { document ->
                                //선택된 리사이클러뷰 수정
                                if (document.get("list_num") != null) {
                                    val listnum = "${document.get("list_num")}"
                                    val num = listnum.toInt()
                                    val pos = "$position".toInt() + 1 //선택된 리사이클러뷰 값 0부터

                                    for (i in 1..num) { // num downTo 1
                                        Log.e("position 값 : ", "$pos")
                                        val am = "${document.get("men_list$i.amount")}".toInt()
                                        val totalamount = "${document.get("totalAmount")}".toInt()
                                        if (i == pos) {
                                            val data = hashMapOf(
                                                "men_list$i" to mapOf(
                                                    "startTime" to startTime,
                                                    "endTime" to endTime,
                                                    "supply" to supply,
                                                    "type" to type,
                                                    "color" to color,
                                                    "amount" to amount
                                                ),
                                                "totalAmount" to (totalamount - am + amount)
                                            )
                                            docRef.collection("$date").document("Menstruation")
                                                .set(data, SetOptions.merge())
                                        }
                                    }
                                }
                            }
                        }
                        ///
                        Toast.makeText(applicationContext, "수정되었습니다", Toast.LENGTH_SHORT).show()
                        finish()


                        dialog.dismiss()
                        dialog.cancel()
                    }

                    dialog.setView(mView)
                    dialog.show()
                    dialog.window?.setBackgroundDrawableResource(R.drawable.join_dialog_background)
                    dialog.window?.setLayout(dpToPx(this, 280), dpToPx(this, 100))

                    dialog.create()
                }
            }
            else {
                // 새로 기록하는 경우
                Log.e("menstruation", "false")
                binding.save.isEnabled = true
                binding.save.isVisible = true


                val docRef2 =
                    db.collection("${auth.currentUser?.email}").document("Record")
                docRef2.get().addOnSuccessListener { document ->
                    val date = document.get("clicked_date")


                    val docRef2 = docRef.collection("${date}").document("Menstruation")
                    docRef2.get().addOnSuccessListener { document ->
                        if (document.get("isEnabled") != null) {
                            val num = document.get("list_num") //현재 월경기록에 저장된 리스트 개수
                            val endTime = document.get("men_list$num.endTime") // 마지막으로 저장된 리스트의 교체시간
                            binding.isMenstruatingSwitch.isChecked = true
                            binding.startTimeToUseSupply.setText("${endTime}")
                        }
                        else {
                            val viewforFix = View(this)
                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                            viewforFix.layoutParams = layoutParams
                            binding.settingView.addView(viewforFix)
                            viewforFix.setOnClickListener {
                                val dialog = AlertDialog.Builder(this).create()
                                val edialog: LayoutInflater = LayoutInflater.from(this)
                                val mView: View =
                                    edialog.inflate(R.layout.dialog_is_menstruation, null)

                                val cancel: TextView = mView.findViewById(R.id.dialog_fix_no)
                                val okay: TextView = mView.findViewById(R.id.dialog_fix_ok)

                                //  취소 버튼 클릭 시
                                cancel.setOnClickListener {
                                    dialog.dismiss()
                                    dialog.cancel()
                                }
                                //  확인 버튼 클릭 시
                                okay.setOnClickListener {
                                    binding.startTimeToUseSupply.isEnabled = true
                                    binding.endTimeToUseSupply.isEnabled = true
                                    binding.isMenstruatingSwitch.isChecked = true
                                    binding.supplySpinner.isEnabled = true
                                    binding.typePickerPad.isEnabled = true
                                    binding.typeInputEdittext.isEnabled = true
                                    binding.typePickerTampon.isEnabled = true

                                    dialog.dismiss()
                                    dialog.cancel()

                                    viewforFix.isEnabled = false
                                    viewforFix.isVisible = false
                                }

                                dialog.setView(mView)
                                dialog.show()
                                dialog.window?.setBackgroundDrawableResource(R.drawable.join_dialog_background)
                                dialog.window?.setLayout(dpToPx(this, 280), dpToPx(this, 100))

                                dialog.create()
                            }
                        }
                    }
                }

                binding.save.setOnClickListener {
                    // 희정님!! 여기서 firebase에 업데이트해야 합니다.

                    val startTime = binding.startTimeToUseSupply.text.toString()
                    val endTime = binding.endTimeToUseSupply.text.toString()
                    val supply = binding.supplySpinner.text.toString()
                    var type: String = binding.supplySpinner.text.toString()
                    var amount = 0

                    if (startTime == "" || endTime == "" || supply == "" || binding.expectaionValueTextView.text == "월경량 - ml") {
                        Toast.makeText(applicationContext, "시간, 월경용품을 모두 기록해주세요", Toast.LENGTH_SHORT).show()
                    }
                    else {
                     if (type == "생리대") {
                        Log.e("정혈 색상", "${binding.lightRed.text.toString()}")
                        type = binding.typePickerPad.text.toString()
                    } else if (type == "탐폰") {
                        Log.e("정혈 색상", "${binding.lightPink.text.toString()}")
                        type = binding.typePickerTampon.text.toString()
                    } else if (type == "월경컵") {
                        Log.e("정혈 색상", "${binding.darkPurple.text.toString()}")
                        type = binding.typeInputEdittext.text.toString()
                    } else {
                        Log.e("오류!", "칩선택이 실패하였습니다.")
                    }

                    docRef.get().addOnSuccessListener { document ->
                        val date = "${document.get("clicked_date")}" //선택된 날짜 저장
                        val date2 = "${document.get("Today")}"

                        Log.e("date : ", "$date")

                        var color: String = ""

                        if (binding.lightRed.isChecked == true) {
                            Log.e("정혈 색상", "${binding.lightRed.text}")
                            color = "${binding.lightRed.text}"
                        } else if (binding.lightPink.isChecked == true) {
                            Log.e("정혈 색상", "${binding.lightPink.text}")
                            color = "${binding.lightPink.text}"
                        } else if (binding.darkPurple.isChecked == true) {
                            Log.e("정혈 색상", "${binding.darkPurple.text}")
                            color = "${binding.darkPurple.text}"
                        } else if (binding.darkBrown.isChecked == true) {
                            Log.e("정혈 색상", "${binding.darkBrown.text}")
                            color = "${binding.darkBrown.text}"
                        } else {
                            Log.e("오류!", "칩선택이 실패하였습니다.")
                        }

                        if (supply == "생리대") {
                            when (type) {
                                "라이너" -> amount = 1
                                "소형" -> amount = 3
                                "중형" -> amount = 5
                                "대형" -> amount = 7
                                "오버나이트" -> amount = 10
                            }
                        } else if (supply == "탐폰") {
                            when (type) {
                                "레귤러" -> amount = 4
                                "슈퍼" -> amount = 8
                            }
                        } else if (supply == "월경컵") {
                            amount = "${binding.typeInputEdittext.text}".toInt()
                        }
                        Log.e("선택된 날짜 : ", "$date")


                        val docRef2 = docRef.collection("$date").document("Menstruation")
                        var null_cnt = 0
                        var arrnum = 1
                        var cnt2 = 1
                        var arr_end = false
                        if(document.get("arr_num") != null)
                            arrnum = "${document.get("arr_num")}".toInt()
                        else{
                            val data = hashMapOf(
                                "arr_num" to 1
                            )
                            docRef.set(data, SetOptions.merge())
                        }

                        if(document.get("null_cnt") != null)
                            null_cnt = "${document.get("null_cnt")}".toInt()


                        if(document.get("arr_end") != null)
                            arr_end = document.get("arr_end") as Boolean

                        if (document.get("am_cnt") == null) {
                            val data = hashMapOf(
                                "am_cnt" to 1
                            )
                            docRef.set(data, SetOptions.merge())
                        } else {
                            cnt2 = "${document.get("am_cnt")}".toInt() //am_cnt 값 cnt에 넣어줌
                        }

                        docRef2.get().addOnSuccessListener { document ->

                     /*       for (i in 1..num.toString().toInt()) {
                                todayTotalAmount += document.get("men_list$i.amount")
                                    .toString().toInt()

                                val data2 = hashMapOf(
                                    "amount$arrnum" to mapOf(
                                        "arr$arrnum" to todayTotalAmount
                                    )
                                )
                            }*/

                            var totalamount = 0
                            if(document.get("totalAmount") != null) {
                                totalamount = "${document.get("totalAmount")}".toInt()
                                val data = hashMapOf(
                                    "am_cnt" to cnt2 + 1
                                )
                            }

                            if (document.get("list_num") == null) {
                                val data = hashMapOf(
                                    "list_num" to 1,
                                    "men_list1" to mapOf(
                                        "startTime" to startTime,
                                        "endTime" to endTime,
                                        "supply" to supply,
                                        "type" to type,
                                        "color" to color,
                                        "amount" to amount
                                    ),
                                    "isEnabled" to true,
                                    "totalAmount" to amount
                                )
                                docRef.collection("$date").document("Menstruation")
                                    .set(data, SetOptions.merge())

                                if(null_cnt < 4)
                                {
                                    val data2 = hashMapOf(
                                        "null_cnt" to null_cnt-1
                                    )
                                    docRef.set(data2, SetOptions.merge())
                                    docRef.update("duration_arr$arrnum", FieldValue.arrayUnion("$date"))
                                    val data3 = hashMapOf(
                                        "amount_arr$arrnum" to mapOf(
                                            "$date" to totalamount + amount
                                        )
                                    )
                                    docRef.set(data3, SetOptions.merge())

                                }
                                else{ //공백이 3일이상 넘어갔다는 의미
                                    arrnum += 1
                                    val data = hashMapOf(
                                        "arr_num" to arrnum,
                                        "null_cnt" to 0,
                                        "arr_end" to true // list1 배열 삽입과정이 끝남 -> 2로 넘어감
                                    )
                                    docRef.set(data, SetOptions.merge())
                                    docRef.update("duration_arr$arrnum", FieldValue.arrayUnion("$date"))

                                    val data3 = hashMapOf(
                                        "amount_arr$arrnum" to mapOf(
                                            "$date" to totalamount + amount
                                        )
                                    )
                                    docRef.set(data3, SetOptions.merge())

                                }
                            } else {
                                var temp = "${document.get("list_num")}"
                                val num = temp.toInt() + 1

                                val data = hashMapOf(
                                    "men_list$num" to mapOf(
                                        "startTime" to startTime,
                                        "endTime" to endTime,
                                        "supply" to supply,
                                        "type" to type,
                                        "color" to color,
                                        "amount" to amount
                                    ),
                                    "list_num" to num,
                                    "isEnabled" to true,
                                    "totalAmount" to totalamount + amount
                                )
                                docRef.collection("$date").document("Menstruation")
                                    .set(data, SetOptions.merge())

                                val data3 = hashMapOf(
                                    "amount_arr$arrnum" to mapOf(
                                        "$date" to totalamount + amount
                                    )
                                )
                                docRef.set(data3, SetOptions.merge())
                            }
                            //var temp = "${document.get("list_num")}"
                            //var map: Map<String, Any> = document.get("men_list".plus(temp)) as Map<String, Any>
                            //val num = temp.toInt() + 1
                            }
                    }
                        Toast.makeText(applicationContext, "저장되었습니다", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        binding.btnInitClose.setOnClickListener {
            finish()
        }


        // Switch 클릭시
        binding.isMenstruatingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.save.isEnabled = true
            binding.save.isVisible = true
            if (isChecked) {
                binding.startTimeToUseSupply.isEnabled = true
                binding.endTimeToUseSupply.isEnabled = true
                binding.endTimeToUseSupply.isEnabled = true
                binding.supplySpinner.isEnabled = true
                binding.typePickerTampon.isEnabled = true
                binding.typePickerPad.isEnabled = true
                binding.typeInputEdittext.isEnabled = true
                binding.lightPink.isEnabled = true
                binding.lightRed.isEnabled = true
                binding.darkBrown.isEnabled = true
                binding.darkPurple.isEnabled = true
                binding.save.setTextColorRes(R.color.red_e1)

                binding.lightPink.setChipBackgroundColorResource(R.color.chip_background_color)
                binding.lightRed.setChipBackgroundColorResource(R.color.chip_background_color)
                binding.darkPurple.setChipBackgroundColorResource(R.color.chip_background_color)
                binding.darkBrown.setChipBackgroundColorResource(R.color.chip_background_color)

                binding.lightPink.setChipStrokeColorResource(R.color.chip_stroke_color)
                binding.lightRed.setChipStrokeColorResource(R.color.chip_stroke_color)
                binding.darkPurple.setChipStrokeColorResource(R.color.chip_stroke_color)
                binding.darkBrown.setChipStrokeColorResource(R.color.chip_stroke_color)

                Log.e("스위치", "clicked")
            } else {
                binding.startTimeToUseSupply.isEnabled = false
                binding.endTimeToUseSupply.isEnabled = false
                binding.endTimeToUseSupply.isEnabled = false
                binding.supplySpinner.isEnabled = false
                binding.typePickerTampon.isEnabled = false
                binding.typePickerPad.isEnabled = false
                binding.typeInputEdittext.isEnabled = false
                binding.lightPink.isEnabled = false
                binding.lightRed.isEnabled = false
                binding.darkBrown.isEnabled = false
                binding.darkPurple.isEnabled = false

                binding.startTimeToUseSupply.setText("")
                binding.endTimeToUseSupply.setText("")
                binding.endTimeToUseSupply.setText("")
                binding.supplySpinner.setText("")
                binding.typePickerTampon.setText("")
                binding.typePickerPad.setText("")

                binding.typeInputEdittext.setText("")
                binding.typeInputEdittext.setTextColorRes(R.color.gray_c5)

                binding.expectaionValueTextView.setTextColorRes(R.color.gray_a7)
                binding.expectaionValueTextView.setText("예상 월경량 - ml")

                binding.lightPink.isChecked = false
                binding.lightRed.isChecked = false
                binding.darkBrown.isChecked = false
                binding.darkPurple.isChecked = false

                docRef.get().addOnSuccessListener { document ->
                    val date = document.get("clicked_date")
                    val docRef2 = docRef.collection("$date").document("Menstruation")

                    docRef2.delete()
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully deleted!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                }
                Log.e("스위치", "unclicked")
            }
        }

        // startTime 선택시
        binding.startTimeToUseSupply.setOnClickListener {
            binding.save.isEnabled = true
            binding.save.isVisible = true

            // 값 넘기기
            val bottomSheet = RecordStartTimeBottomSheetFragment()
            val bundle = Bundle()

            bundle.putString("time", "start")
            bottomSheet.arguments = bundle

            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        // endTime 선택시
        binding.endTimeToUseSupply.setOnClickListener {
            binding.save.isEnabled = true
            binding.save.isVisible = true
            // 값 넘기기
            val bottomSheet = RecordEndTimeBottomSheetFragment()
            val bundle = Bundle()

            bundle.putString("time", "end")
            bottomSheet.arguments = bundle


            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        // 용품 picker 클릭시
        binding.supplySpinner.setOnClickListener {
            binding.save.isEnabled = true
            binding.save.isVisible = true
            // 값 표시 초기화
            binding.expectaionValueTextView.text = "월경량 - ml"
            binding.typeInputEdittext.text = null
            binding.typePickerPad.text = null
            binding.typePickerTampon.text = null
            binding.expectaionValueTextView.setTextColor(Color.parseColor("#a7a1a1"))

            // 값 넘기기
            val bottomSheet = RecordSupplyBottomSheetFragment()
            val bundle = Bundle()

            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        // pad picker 클릭시
        binding.typePickerPad.setOnClickListener {
            binding.save.isEnabled = true
            binding.save.isVisible = true
            // 값 표시 초기화
            binding.expectaionValueTextView.text = "월경량 - ml"
            binding.typeInputEdittext.text = null

            // 값 넘기기
            val bottomSheet = RecordPadBottomSheetFragment()
            val bundle = Bundle()

            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)

        }

        // tampon picker 클릭시
        binding.typePickerTampon.setOnClickListener {
            binding.save.isEnabled = true
            binding.save.isVisible = true
            // 값 표시 초기화
            binding.expectaionValueTextView.text = "월경량 - ml"
            binding.typeInputEdittext.text = null

            // 값 넘기기
            val bottomSheet = RecordTamponBottomSheetFragment()
            val bundle = Bundle()

            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)

        }


        // 생리컵의 경우 바로 enter클릭 후 받아오기
        binding.typeInputEdittext.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            binding.save.isEnabled = true
            binding.save.isVisible = true
            binding.typeInputEdittext.isEnabled = true
            binding.typeInputEdittext.isCursorVisible = true

            // 값을 적지 않은 경우
            if (binding.typeInputEdittext.text.toString().length == 0) {
                binding.typeInputEdittext.hint = "용량을 입력해주세요"
                binding.typeInputEdittext.setHintTextColor(Color.RED)
                binding.expectaionValueTextView.text = "월경량 - ml"
            }
            // 값을 적은 경우
            else {
                // 희정씨!! 여기서 받으시면 될겁니다!!
                binding.expectaionValueTextView.text =
                    "월경량 " + binding.typeInputEdittext.text.toString() + " ml"
                binding.expectaionValueTextView.setTextColor(Color.parseColor("#e16248"))
            }
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 입력 끝나면 키보드 내리기
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    binding.typeInputEdittext.windowToken,
                    0
                )

                // 입력 후 커서 깜빡임 없애기 (= focus 없애기)
                binding.typeInputEdittext.clearFocus()
                handled = true
            }
            handled
        }
        binding.lightRed.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.save.isEnabled = true
            binding.save.isVisible = true
        }
        binding.lightPink.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.save.isEnabled = true
            binding.save.isVisible = true
        }
        binding.darkPurple.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.save.isEnabled = true
            binding.save.isVisible = true
        }
        binding.darkBrown.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.save.isEnabled = true
            binding.save.isVisible = true
        }
    }

    override fun onStartDataPass(data: String?) {
        Log.e("startTime", "" + data)
        binding.startTimeToUseSupply.setText(data)
    }

    override fun onEndDataPass(data: String?) {
        binding.endTimeToUseSupply.setText(data)
    }

    override fun onTypeDataPass(data: Int?) {
        when (data) {
            // 생리대
            0 -> {
                binding.typePickerPad.visibility = View.VISIBLE
                binding.typePickerTampon.visibility = View.GONE
                binding.typeInputEdittext.visibility = View.GONE
                // 희정씨!! 여기서 받으시면 될겁니다!!
                // supplies[0]이 생리대, supplies[1]이 탐폰, supplies[2]이 월경컵을 반환합니다 (바로 아랫 줄 참고)
                binding.supplySpinner.setText(supplies[0])
            }
            // 탐폰
            1 -> {
                binding.typePickerTampon.visibility = View.VISIBLE
                binding.typePickerPad.visibility = View.GONE
                binding.typeInputEdittext.visibility = View.GONE
                binding.supplySpinner.setText(supplies[1])
            }
            // 월경컵
            else -> {
                binding.typePickerPad.visibility = View.GONE
                binding.typePickerTampon.visibility = View.GONE
                binding.typeInputEdittext.visibility = View.VISIBLE
                binding.supplySpinner.setText(supplies[2])
                binding.expectaionValueTextView.text = "월경량 - ml"
            }
        }
    }

    override fun onSizeDataPass(data: Int?) {
        when (data) {
            // 라이너
            0 -> {
                binding.typePickerPad.setText(pads[0])
                bloodVolume = 1
                binding.expectaionValueTextView.text = "예상 월경량 " + bloodVolume + " ml"
                binding.expectaionValueTextView.setTextColor(Color.parseColor("#e16248"))
            }
            // 소형
            1 -> {
                binding.typePickerPad.setText(pads[1])
                bloodVolume = 3
                binding.expectaionValueTextView.text = "예상 월경량 " + bloodVolume + " ml"
                binding.expectaionValueTextView.setTextColor(Color.parseColor("#e16248"))
            }
            // 중형
            2 -> {
                binding.typePickerPad.setText(pads[2])
                bloodVolume = 5
                binding.expectaionValueTextView.text = "예상 월경량 " + bloodVolume + " ml"
                binding.expectaionValueTextView.setTextColor(Color.parseColor("#e16248"))
            }
            // 대형
            3 -> {
                binding.typePickerPad.setText(pads[3])
                bloodVolume = 7
                binding.expectaionValueTextView.text = "예상 월경량 " + bloodVolume + " ml"
                binding.expectaionValueTextView.setTextColor(Color.parseColor("#e16248"))
            }
            // 오버나이트
            else -> {
                binding.typePickerPad.setText(pads[4])
                bloodVolume = 10
                binding.expectaionValueTextView.text = "예상 월경량 " + bloodVolume + " ml"
                binding.expectaionValueTextView.setTextColor(Color.parseColor("#e16248"))
            }
        }
    }

    override fun onTamponSizeDataPass(data: Int?) {
        when (data) {
            // 레귤러
            0 -> {
                binding.typePickerTampon.setText(tampons[0])
                bloodVolume = 4
                binding.expectaionValueTextView.text = "예상 월경량 " + bloodVolume + " ml"
                binding.expectaionValueTextView.setTextColor(Color.parseColor("#e16248"))
            }
            // 슈퍼
            else -> {
                binding.typePickerTampon.setText(tampons[1])
                bloodVolume = 8
                binding.expectaionValueTextView.text = "예상 월경량 " + bloodVolume + " ml"
                binding.expectaionValueTextView.setTextColor(Color.parseColor("#e16248"))
            }
        }
    }
}
