package com.yours.mycup.record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentRecordPadBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecordPadBottomSheetFragment : BottomSheetDialogFragment() {

    var pads = arrayOf("라이너", "소형", "중형", "대형", "오버나이트")

    // 선택한 타입을 넘겨주기 위한 리스너
    interface onSizePassListener {
        fun onSizeDataPass(data : Int?)
    }
    lateinit var sizePassListener : onSizePassListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sizePassListener = context as onSizePassListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentRecordPadBottomSheetBinding.inflate(inflater, container, false)
        val picker: NumberPicker = binding.padpicker.numberpicker
        val save: TextView = binding.saveButtonPadpicker

        //  순환 안되게 막기
        picker.wrapSelectorWheel = false

        //  editText 설정 해제
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소값 설정
        picker.minValue = 0

        //  최대값 설정
        picker.maxValue = pads.size -1

        picker.displayedValues = pads

        save.setOnClickListener {
            sizePassListener.onSizeDataPass(picker.value)
            this.dismiss()
        }

        return binding.root
    }

}