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

class ReviewCupBottonSheet : BottomSheetDialogFragment() {
    lateinit var reviewcupPassListener : ReviewCupPassListener
    var product_list = arrayOf("루나컵 링", "루나컵 쇼티", "루나컵 클래식", "한나컵", "이브컵")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_review_cup, container, false)

        val cup : NumberPicker = view.findViewById(R.id.picker_reviewcup)
        val save : TextView = view.findViewById(R.id.btn_reviewcup_select)


        cup.isFocusableInTouchMode = true

        //  순환 안되게 막기
        cup.wrapSelectorWheel = false

        //  editText 설정 해제
        cup.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소값 설정
        cup.minValue = 0

        //  최대값 설정
        cup.maxValue = product_list.size - 1

        cup.displayedValues = product_list


        //  완료 버튼 클릭 시
        save.setOnClickListener {
            reviewcupPassListener.onReviewCupPass(cup.value)
            dismiss()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        reviewcupPassListener = context as ReviewCupPassListener
    }

    // 선택한 타입을 넘겨주기 위한 리스너
    interface ReviewCupPassListener {
        fun onReviewCupPass(data : Int?)
    }
}