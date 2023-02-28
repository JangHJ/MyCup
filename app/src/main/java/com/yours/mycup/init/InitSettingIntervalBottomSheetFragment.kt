package com.yours.mycup.init

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentInitSettingIntervalBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class InitSettingIntervalBottomSheetFragment : BottomSheetDialogFragment() {

    interface onIntervalPassListener {
        fun onIntervalDataPass(data : Int?)
    }
    lateinit var intervalDatePassListener : onIntervalPassListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        intervalDatePassListener = context as onIntervalPassListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var binding = FragmentInitSettingIntervalBottomSheetBinding.inflate(inflater, container, false)
        val picker = binding.intervalPicker.picker
        val save = binding.saveButtonIntervalpicker
        val db = FirebaseFirestore.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        picker.isFocusableInTouchMode = true

        //  순환 안되게 막기
        picker.wrapSelectorWheel = false

        //  editText 설정 해제
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소, 최대값 설정
        picker.minValue = 15
        picker.maxValue = 56

        // 초기값
        picker.value = 28


        save.setOnClickListener {
            intervalDatePassListener.onIntervalDataPass(picker.value)
            picker.isFocusableInTouchMode = false

            this.dismiss()
        }

        return binding.root
    }
}