package com.yours.mycup.record

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
import com.yours.mycup.databinding.FragmentRecordStartTimeBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDateTime

class RecordStartTimeBottomSheetFragment : BottomSheetDialogFragment() {

    // 선택한 날짜를 넘겨주기 위한 리스너
    interface onStartTimePassListener {
        fun onStartDataPass(data : String?)
    }
    lateinit var startTimePassListener : onStartTimePassListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        startTimePassListener = context as onStartTimePassListener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentRecordStartTimeBottomSheetBinding.inflate(inflater, container, false)
        val bundle = arguments
        val hour : NumberPicker = binding.timepicker.hourpickerTimepicker
        val minute : NumberPicker = binding.timepicker.minutepickerTimepicker

        //  순환하도록 설정
        hour.wrapSelectorWheel = true
        minute.wrapSelectorWheel = true
        // startTime일 경우
        if (bundle?.getString("time") == "start") {
            binding.isStartedYesterday.isChecked = false

            //  editText 설정 해제
            hour.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            //  최소, 최대값 설정 (최소는 0, 최대는 현재 시간)
            hour.minValue = 0
            hour.maxValue = 23
            minute.minValue = 0
            minute.maxValue = 59

            // 초기값 지금으로 설정
            hour.value = LocalDateTime.now().hour
            minute.value = LocalDateTime.now().minute
        }
        binding.saveButtonDatepicker.setOnClickListener {
            if (binding.isStartedYesterday.isChecked == true) {
                val start = "어제 ${hour.value}시 ${minute.value}분"
                startTimePassListener.onStartDataPass(start)
            }
            else {
                val start = "${hour.value}시 ${minute.value}분"
                startTimePassListener.onStartDataPass(start)
            }

            this.dismiss()
        }
        return binding.root
    }

}