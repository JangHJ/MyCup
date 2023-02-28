package com.yours.mycup.init

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentInitSettingEndDateBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.LocalDate

class InitSettingEndDateBottomSheetFragment : BottomSheetDialogFragment() {
    interface onEndDatePassListener {
        fun onEndDataPass(data : EndDate?)
    }
    lateinit var endDatePassListener : onEndDatePassListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        endDatePassListener = context as onEndDatePassListener
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentInitSettingEndDateBottomSheetBinding.inflate(inflater, container, false)
        val bundle = arguments
        val year : NumberPicker = binding.datepicker.yearpickerDatepicker
        val month : NumberPicker = binding.datepicker.monthpickerDatepicker
        val day : NumberPicker = binding.datepicker.daypickerDatepicker
        var endDate: EndDate = EndDate(0, 0, 0)


        //  순환 안되게 막기
        year.wrapSelectorWheel = false
        month.wrapSelectorWheel = false
        day.wrapSelectorWheel = false

        if (bundle?.getString("date") == "end") {
            //  editText 설정 해제
            year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            day.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            Log.e("fdas", "${bundle.getString("year")}")

            //  최소, 최대값 설정 (최대는 오늘 날짜)
            if (bundle.getString("year") == "0" && bundle.getString("month") == "0" && bundle.getString("day") == "0") {
                year.minValue = 2000
                year.maxValue = LocalDate.now().year
                month.minValue = 1
                month.maxValue = LocalDate.now().monthValue
                day.minValue = 1
                day.maxValue = LocalDate.now().dayOfMonth

                // 초기값 오늘로 설정
                year.value = year.maxValue
                month.value = month.maxValue
                day.value = day.maxValue
            }
            else {
                year.minValue = bundle.getString("year")!!.toInt()
                year.maxValue = LocalDate.now().year
                year.value = year.minValue

                if (year.value < LocalDate.now().year) {
                    month.wrapSelectorWheel = true
                    month.minValue = bundle.getString("month")!!.toInt()
                    month.maxValue = 12
                    month.value = month.minValue

                    day.wrapSelectorWheel = true
                    day.minValue = bundle.getString("day")!!.toInt()
                    day.maxValue = getDaysInMonth(month.value, year.value)
                    day.value = day.minValue
                }
                else if (year.value == LocalDate.now().year) {
                    month.minValue = bundle.getString("month")!!.toInt()
                    month.maxValue = LocalDate.now().monthValue
                    day.minValue = bundle.getString("day")!!.toInt()
                    day.maxValue = getDaysInMonth(bundle.getString("month")!!.toInt(), bundle.getString("year")!!.toInt())

                    month.value = month.minValue
                    day.value = day.minValue
                }
            }
            // year, month 바뀔 때 각 월의 마지막 날짜 받아오기
            month.setOnValueChangedListener { picker, oldVal, newVal ->
                if (picker.value != bundle.getString("month")!!.toInt() && year.value == bundle.getString("year")!!.toInt()) {
                    day.minValue = 1
                    day.value = day.minValue
                    if (picker.value == LocalDate.now().monthValue && year.value == LocalDate.now().year) {
                        day.wrapSelectorWheel = false
                        day.maxValue = LocalDate.now().dayOfMonth
                    }
                    else {
                        day.wrapSelectorWheel = true
                        day.maxValue = getDaysInMonth(month.value, year.value)
                    }
                }
                else if (picker.value == bundle.getString("month")!!.toInt() && year.value == bundle.getString("year")!!.toInt()){
                    day.minValue = bundle.getString("day")!!.toInt()
                    day.value = day.minValue
                    day.wrapSelectorWheel = false
                    day.maxValue = getDaysInMonth(LocalDate.now().monthValue, LocalDate.now().year)
                }
                else if (picker.value != bundle.getString("month")!!.toInt() && year.value != bundle.getString("year")!!.toInt()) {
                    day.minValue = 1
                    day.value = day.minValue
                    day.maxValue = getDaysInMonth(month.value, year.value)
                    if (picker.value == LocalDate.now().monthValue) {
                        day.wrapSelectorWheel = false
                        day.maxValue = LocalDate.now().dayOfMonth
                    }
                    else {
                        day.wrapSelectorWheel = true
                        day.maxValue = getDaysInMonth(month.value, year.value)
                    }
                }
                else if (picker.value == bundle.getString("month")!!.toInt() && year.value != bundle.getString("year")!!.toInt()) {
                    day.minValue = 1
                    day.value = day.minValue
                    day.wrapSelectorWheel = true
                    day.maxValue = getDaysInMonth(month.value, year.value)
                }

            }
            year.setOnValueChangedListener { picker, oldVal, newVal ->
                day.maxValue = getDaysInMonth(month.value, year.value)
                if (picker.value == bundle.getString("year")!!.toInt() && picker.value != LocalDate.now().year) {
                    month.wrapSelectorWheel = true
                    month.minValue = bundle.getString("month")!!.toInt()
                    month.maxValue = 12
                    month.value = month.maxValue

                    day.wrapSelectorWheel = true
                    day.minValue = 1
                    day.maxValue = getDaysInMonth(month.value, year.value)
                }
                else if (picker.value == bundle.getString("year")!!.toInt() && picker.value == LocalDate.now().year) {
                    month.wrapSelectorWheel = false
                    month.minValue = bundle.getString("month")!!.toInt()
                    month.maxValue = LocalDate.now().monthValue
                    month.value = month.minValue
                    if (month.value == LocalDate.now().monthValue) {
                        day.wrapSelectorWheel = false
                        day.maxValue = LocalDate.now().dayOfMonth
                        day.value = day.maxValue
                    }
                    else {
                        day.wrapSelectorWheel = true
                        day.maxValue = getDaysInMonth(month.value, year.value)
                    }
                }
                else if (picker.value != bundle.getString("year")!!.toInt() && picker.value == LocalDate.now().year) {
                    month.wrapSelectorWheel = false
                    month.minValue = 1
                    month.maxValue = LocalDate.now().monthValue
                    month.value = month.minValue
                    if (month.value == LocalDate.now().monthValue) {
                        day.wrapSelectorWheel = false
                        day.maxValue = LocalDate.now().dayOfMonth
                        day.minValue = 1
                        day.value = day.minValue
                    }
                    else {
                        day.wrapSelectorWheel = true
                        day.minValue = 1
                        day.maxValue = getDaysInMonth(month.value, year.value)

                    }
                }
            }
        }

        binding.saveButtonDatepicker.setOnClickListener {
            endDate.year = year.value
            endDate.month = month.value
            endDate.day = day.value

            endDatePassListener.onEndDataPass(endDate)

            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val db = FirebaseFirestore.getInstance()

            endDate.year = year.value
            endDate.month = month.value
            endDate.day = day.value

            var m = ""
            var d = ""

            if (endDate.month.toInt() < 10)
                m = "0${endDate.month}"
            else
                m = "${endDate.month}"
            if (endDate.day.toInt() < 10)
                d = "0${endDate.day}"
            else
                d = "${endDate.day}"


            val data = hashMapOf(
                "mendate_end" to "${endDate.year}-${m}-${d}"
            )

            db.collection("${auth.currentUser?.email}").document("Init").set(data, SetOptions.merge())
            this.dismiss()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

}