package com.yours.mycup.mypage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import com.yours.mycup.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyLengthBottomSheet : BottomSheetDialogFragment() {
    lateinit var lengthPassListener : LengthPassListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_my_length, container, false)

        val length : NumberPicker = view.findViewById(R.id.picker_mylength)
        val save : TextView = view.findViewById(R.id.btn_mylength_select)

        length.isFocusableInTouchMode = true

        //  순환 안되게 막기
        length.wrapSelectorWheel = false

        //  editText 설정 해제
        length.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소, 최대값 설정
        length.minValue = 30
        length.maxValue = 70

        // 초기값
        length.value = 58


        //  완료 버튼 클릭 시
        save.setOnClickListener {
            lengthPassListener.onLengthPass(length.value)
            dismiss()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lengthPassListener = context as LengthPassListener
    }

    // 선택한 타입을 넘겨주기 위한 리스너
    interface LengthPassListener {
        fun onLengthPass(data : Int?)
    }
}