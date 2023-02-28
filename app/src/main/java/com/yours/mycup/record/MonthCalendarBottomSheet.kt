package com.yours.mycup.record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentMonthCalendarBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MonthCalendarBottomSheet : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMonthCalendarBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

}