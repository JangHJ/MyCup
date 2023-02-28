package com.yours.mycup.search

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.yours.mycup.R
import com.yours.mycup.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSearchBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        binding.imgSearch1Daily.setOnClickListener{
            val intent = Intent(activity, SituationActivity::class.java)
            intent.putExtra("SITUATION", 0)
            startActivity(intent)
        }

        binding.imgSearch1Exercise.setOnClickListener{
            val intent = Intent(activity, SituationActivity::class.java)
            intent.putExtra("SITUATION", 1)
            startActivity(intent)
        }

        binding.imgSearch1Question.setOnClickListener{
            val intent = Intent(activity, SituationActivity::class.java)
            intent.putExtra("SITUATION", 2)
            startActivity(intent)
        }

        binding.clSearch1.setOnClickListener{
            val intent = Intent(activity, BrandActivity::class.java)
            startActivity(intent)
        }

        binding.clSearch2.setOnClickListener{
            val intent = Intent(activity, ByLengthActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

}