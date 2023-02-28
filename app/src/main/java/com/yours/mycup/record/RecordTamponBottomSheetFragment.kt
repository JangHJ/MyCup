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
import com.yours.mycup.databinding.FragmentRecordTamponBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecordTamponBottomSheetFragment : BottomSheetDialogFragment() {

    var tampons= arrayOf("레귤러", "슈퍼")

    // 선택한 타입을 넘겨주기 위한 리스너
    interface onTamponSizePassListener {
        fun onTamponSizeDataPass(data : Int?)
    }
    lateinit var tamponsizePassListener : onTamponSizePassListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tamponsizePassListener = context as onTamponSizePassListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentRecordTamponBottomSheetBinding.inflate(inflater, container, false)
        val picker: NumberPicker = binding.tamponpicker.numberpicker
        val save: TextView = binding.saveButtonTamponpicker

        //  순환 안되게 막기
        picker.wrapSelectorWheel = false

        //  editText 설정 해제
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소값 설정
        picker.minValue = 0

        //  최대값 설정
        picker.maxValue = tampons.size -1

        picker.displayedValues = tampons

        save.setOnClickListener {
            tamponsizePassListener.onTamponSizeDataPass(picker.value)
            this.dismiss()
        }
        return binding.root
    }

}