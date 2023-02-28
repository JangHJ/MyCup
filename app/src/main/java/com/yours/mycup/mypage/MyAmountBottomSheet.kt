package com.yours.mycup.mypage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yours.mycup.R
import com.yours.mycup.record.RecordPadBottomSheetFragment
import com.yours.mycup.record.RecordStartTimeBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyAmountBottomSheet : BottomSheetDialogFragment() {
    lateinit var amountPassListener : MyAmountBottomSheet.AmountPassListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_my_amount, container, false)

        val amount : NumberPicker = view.findViewById(R.id.picker_myamount)
        val save : TextView = view.findViewById(R.id.btn_myamount_select)

        amount.isFocusableInTouchMode = true

        //  순환 안되게 막기
        amount.wrapSelectorWheel = false

        //  editText 설정 해제
        amount.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소, 최대값 설정
        amount.minValue = 30
        amount.maxValue = 120

        // 초기값
        amount.value = 60


        //  완료 버튼 클릭 시
        save.setOnClickListener {
            amountPassListener.onAmountPass(amount.value)
            dismiss()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        amountPassListener = context as AmountPassListener
    }

    // 선택한 타입을 넘겨주기 위한 리스너
    interface AmountPassListener {
        fun onAmountPass(data : Int?)
    }
}