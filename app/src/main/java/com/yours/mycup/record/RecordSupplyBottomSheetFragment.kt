package com.yours.mycup.record

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentRecordSupplyBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecordSupplyBottomSheetFragment : BottomSheetDialogFragment() {

    // 선택한 타입을 넘겨주기 위한 리스너
    interface onTypePassListener {
        fun onTypeDataPass(data : Int?)
    }
    lateinit var typePassListener : onTypePassListener


    var bloodVolume:Int = 0
    var supplies = arrayOf("생리대", "탐폰", "월경컵")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        typePassListener = context as onTypePassListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecordSupplyBottomSheetBinding.inflate(inflater, container, false)
        val picker: NumberPicker = binding.typepicker.numberpicker
        val save: TextView = binding.saveButtonSupplypicker
        //  순환 안되게 막기
        picker.wrapSelectorWheel = false

        //  editText 설정 해제
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소값 설정
        picker.minValue = 0

        //  최대값 설정
        picker.maxValue = supplies.size -1

        picker.displayedValues = supplies

        //  완료 버튼 클릭 시
        save.setOnClickListener {
            typePassListener.onTypeDataPass(picker.value)
            this.dismiss()
            Log.e("picked", "${supplies[picker.value]}")
        }
        // Inflate the layout for this fragment
        return binding.root
    }

}