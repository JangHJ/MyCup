package com.yours.mycup.mypage

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityMyReviewBinding
import com.yours.mycup.search.AddReviewActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog : BottomSheetDialogFragment() {
    lateinit var bottomSheetButtonClickListener: BottomSheetButtonClickListener
    lateinit var cl_edit: ConstraintLayout
    lateinit var cl_delete: ConstraintLayout

    lateinit var product : String
    lateinit var cupsize : String
    lateinit var period : String
    lateinit var feeling : String
    lateinit var length : String
    lateinit var size : String
    lateinit var hardness : String
    lateinit var position2 : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.modal_bottom_sheet_myreview, container, false)
        cl_edit = view.findViewById(R.id.cl_edit)
        cl_delete = view.findViewById(R.id.cl_delete)

        val extra: Bundle? = arguments

        product = extra?.getString("product").toString()
        cupsize = extra?.getString("cupsize").toString()
        period = extra?.getString("period").toString()
        feeling = extra?.getString("feeling").toString()
        length = extra?.getString("length").toString()
        size = extra?.getString("size").toString()
        hardness = extra?.getString("hardness").toString()
        position2 = extra?.getString("position").toString()
        Log.e("포지션", position2)

        return view
    }

    interface BottomSheetButtonClickListener {
        fun onEditButtonClicked()
        fun onDeleteButtonClicked()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            bottomSheetButtonClickListener = context as BottomSheetButtonClickListener
        } catch (e: ClassCastException) {
            Log.e("bottomsheet", "onAttach error")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 후기 수정
        cl_edit.setOnClickListener {
            Log.e("check", "check")
            bottomSheetButtonClickListener.onEditButtonClicked()
            val intent = Intent(context, AddReviewActivity::class.java)
            intent.putExtra("MYREVIEW", 1)
            intent.putExtra("product", product)
            intent.putExtra("cupsize", cupsize)
            intent.putExtra("period", period)
            intent.putExtra("feeling", feeling)
            intent.putExtra("length", length)
            intent.putExtra("size", size)
            intent.putExtra("hardness", hardness)
            intent.putExtra("position", position2)

            startActivity(intent)
            dismiss()
        }

        // 후기 삭제
        cl_delete.setOnClickListener {
            Log.e("check2", "check2")
            bottomSheetButtonClickListener.onDeleteButtonClicked()
            dismiss()
        }
    }
}