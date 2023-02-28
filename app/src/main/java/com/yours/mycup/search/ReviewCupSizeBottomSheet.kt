package com.yours.mycup.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
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

class ReviewCupSizeBottomSheet : BottomSheetDialogFragment() {
    lateinit var reviewcupsizePassListener : ReviewCupSizePassListener
    lateinit var cupsize_list : Array<String>
    lateinit var product : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_review_cup_size, container, false)

        val cupsize : NumberPicker = view.findViewById(R.id.picker_reviewcupsize)
        val save : TextView = view.findViewById(R.id.btn_reviewcupsize_select)

        val extra: Bundle? = arguments
//        val extra: Bundle = requireArguments()
//        cupsize_list = extra.getStringArrayList("cupsize_list") as Array<String>

        product = extra?.getString("product").toString()

        when (product) {
            "루나컵 링" -> {
                cupsize_list = arrayOf("타이니", "스몰", "라지")
            }
            "루나컵 쇼티" -> {
                cupsize_list = arrayOf("타이니", "스몰", "라지")
            }
            "루나컵 클래식" -> {
                cupsize_list = arrayOf("스몰", "라지")
            }
            "한나컵" -> {
                cupsize_list = arrayOf("엑스트라 스몰", "스몰", "미디움")
            }
            "이브컵" -> {
                cupsize_list = arrayOf("미니", "스몰", "라지")
            }
            else -> {
                cupsize_list = arrayOf("타이니", "스몰", "라지")
            }
        }

        cupsize.isFocusableInTouchMode = true

        //  순환 안되게 막기
        cupsize.wrapSelectorWheel = false

        //  editText 설정 해제
        cupsize.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소값 설정
        cupsize.minValue = 0

        //  최대값 설정
        cupsize.maxValue = cupsize_list.size - 1

        cupsize.displayedValues = cupsize_list


        //  완료 버튼 클릭 시
        save.setOnClickListener {
            reviewcupsizePassListener.onReviewCupSizePass(cupsize.value)
            dismiss()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        reviewcupsizePassListener = context as ReviewCupSizePassListener
    }

    // 선택한 타입을 넘겨주기 위한 리스너
    interface ReviewCupSizePassListener {
        fun onReviewCupSizePass(data : Int?)
    }
}