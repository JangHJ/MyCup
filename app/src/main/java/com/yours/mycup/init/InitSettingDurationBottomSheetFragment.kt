package com.yours.mycup.init

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentInitSettingDurationBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class InitSettingDurationBottomSheetFragment : BottomSheetDialogFragment() {

    interface onDurationPassListener {
        fun onDurationDataPass(data : Int?)
    }
    lateinit var durationDatePassListener : onDurationPassListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        durationDatePassListener = context as onDurationPassListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentInitSettingDurationBottomSheetBinding.inflate(inflater, container, false)
        val picker = binding.durationPicker.picker
        val save = binding.saveButtonDurationpicker
        val db = FirebaseFirestore.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        picker.isFocusableInTouchMode = true

        //  순환 안되게 막기
        picker.wrapSelectorWheel = false

        //  editText 설정 해제
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소, 최대값 설정
        picker.minValue = 1
        picker.maxValue = 14

        // 초기값
        picker.value = 5

        save.setOnClickListener {
            durationDatePassListener.onDurationDataPass(picker.value)

            picker.isFocusableInTouchMode = false

            this.dismiss()
        }


        return binding.root
    }

}