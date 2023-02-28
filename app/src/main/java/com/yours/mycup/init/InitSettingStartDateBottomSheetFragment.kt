package com.yours.mycup.init

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentInitSettingStartDateBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.LocalDate
import java.util.*

class InitSettingStartDateBottomSheetFragment : BottomSheetDialogFragment() {

    interface onStartDatePassListener {
        fun onStartDataPass(data : StartDate?)
    }
    lateinit var startDatePassListener : onStartDatePassListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        startDatePassListener = context as onStartDatePassListener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentInitSettingStartDateBottomSheetBinding.inflate(inflater, container, false)
        val bundle = arguments


        val year : NumberPicker = binding.datepicker.yearpickerDatepicker
        val month : NumberPicker = binding.datepicker.monthpickerDatepicker
        val day : NumberPicker = binding.datepicker.daypickerDatepicker
        var startDate: StartDate = StartDate(0, 0, 0)


        //  순환 안되게 막기
        year.wrapSelectorWheel = false
        month.wrapSelectorWheel = false
        day.wrapSelectorWheel = false

        if (bundle?.getString("date") == "start") {
            //  editText 설정 해제
            year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            day.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            //  최소, 최대값 설정 (최대는 오늘 날짜)
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

            // year, month 바뀔 때 각 월의 마지막 날짜 받아오기
            month.setOnValueChangedListener { picker, oldVal, newVal ->
                day.maxValue = getDaysInMonth(month.value, year.value)
                if (picker.value < LocalDate.now().monthValue && year.value == LocalDate.now().year) {
                    day.wrapSelectorWheel = true
                    month.wrapSelectorWheel = false
                }
                else if (picker.value == LocalDate.now().monthValue && year.value == LocalDate.now().year) {
                    day.wrapSelectorWheel = false
                    day.maxValue = LocalDate.now().dayOfMonth
                    day.value = day.maxValue
                    month.wrapSelectorWheel = false
                }
                else if (year.value < LocalDate.now().year) {
                    month.wrapSelectorWheel = true
                    day.wrapSelectorWheel = true

                    month.maxValue = 12
                    day.maxValue = getDaysInMonth(month.value, year.value)
                }
            }
            year.setOnValueChangedListener { picker, oldVal, newVal ->
                day.maxValue = getDaysInMonth(month.value, year.value)
                if (picker.value < LocalDate.now().year) {
                    month.wrapSelectorWheel = true
                    month.maxValue = 12
                    month.value = month.maxValue

                    day.wrapSelectorWheel = true
                    day.maxValue = getDaysInMonth(month.value, year.value)
                }
                else if (picker.value == LocalDate.now().year) {
                    month.wrapSelectorWheel = false
                    month.maxValue = LocalDate.now().monthValue
                    month.value = month.maxValue
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
            }
        }

        binding.saveButtonDatepicker.setOnClickListener {
            startDate.year = year.value
            startDate.month = month.value
            startDate.day = day.value

            var endDateFragment = InitSettingEndDateBottomSheetFragment()
            var bundleForEndDate = Bundle()

            bundleForEndDate.putInt("year", startDate.year)
            bundleForEndDate.putInt("month", startDate.month)
            bundleForEndDate.putInt("day", startDate.day)

            endDateFragment.arguments = bundleForEndDate
            startDatePassListener.onStartDataPass(startDate)

            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val db = FirebaseFirestore.getInstance()

            var m = ""
            var d = ""

            if(startDate.month < 10)
                m = "0${startDate.month}"
            else
                m = "${startDate.month}"
            if(startDate.day < 10)
                d = "0${startDate.day}"
            else
                d = "${startDate.day}"

            val data = hashMapOf(
                "mendate_start" to "${startDate.year}-${m}-${d}"
            )
            db.collection("${auth.currentUser?.email}").document("Init").set(data, SetOptions.merge())

            this.dismiss()
        }
        return binding.root
    }
}
fun getDaysInMonth(month: Int, year: Int): Int {
    return when (month-1) {
        Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER
        -> 31
        Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER
        -> 30
        Calendar.FEBRUARY
        -> if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28 // 윤년 계산
        else
        -> 31
    }
}
class StartDate(var year: Int, var month: Int, var day: Int) {
    init {
    }
}
class EndDate(var year: Int, var month: Int, var day: Int) {
    init {
    }
}