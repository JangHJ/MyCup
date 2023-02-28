package com.yours.mycup.info

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentInfoBinding
import com.yours.mycup.search.BrandDetailActivity

class InfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentInfoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false)

        binding.imgMeasure.background = getResources().getDrawable(R.drawable.border_image_round_16, null)
        binding.imgMeasure.clipToOutline = true

        binding.imgUse.background = getResources().getDrawable(R.drawable.border_image_round_16, null)
        binding.imgUse.clipToOutline = true

        binding.imgChange.background = getResources().getDrawable(R.drawable.border_image_round_16, null)
        binding.imgChange.clipToOutline = true

        binding.imgManage.background = getResources().getDrawable(R.drawable.border_image_round_16, null)
        binding.imgManage.clipToOutline = true

        binding.imgTalk.background = getResources().getDrawable(R.drawable.border_image_round_16, null)
        binding.imgTalk.clipToOutline = true

        binding.imgMeasure.setOnClickListener {
            val intent = Intent(context, MeasureActivity::class.java)
            startActivity(intent)
        }

        binding.imgUse.setOnClickListener {
            val intent = Intent(context, UseActivity::class.java)
            startActivity(intent)
        }

        binding.imgChange.setOnClickListener {
            val intent = Intent(context, ChangeActivity::class.java)
            startActivity(intent)
        }

        binding.imgManage.setOnClickListener {
            val intent = Intent(context, ManageActivity::class.java)
            startActivity(intent)
        }

        binding.imgTalk.setOnClickListener {
            val intent = Intent(context, TalkActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}