package com.yours.mycup.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yours.mycup.R
import com.yours.mycup.databinding.ActivityByLengthBinding
import com.yours.mycup.search.RV.ReviewAdapter
import com.yours.mycup.search.RV.SitCupAdapter
import com.yours.mycup.search.RV.SitCupData

class ByLengthActivity : AppCompatActivity() {
    lateinit var shortAdapter: SitCupAdapter
    lateinit var middleAdapter: SitCupAdapter
    lateinit var longAdapter: SitCupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityByLengthBinding = DataBindingUtil.setContentView(this, R.layout.activity_by_length)

        // 상태바 보기
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        shortAdapter = SitCupAdapter(this)
        binding.rvBylengthShort.adapter = shortAdapter
        binding.rvBylengthShort.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        middleAdapter = SitCupAdapter(this)
        binding.rvBylengthMiddle.adapter = middleAdapter
        binding.rvBylengthMiddle.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        longAdapter = SitCupAdapter(this)
        binding.rvBylengthLong.adapter = longAdapter
        binding.rvBylengthLong.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        binding.btnBylengthBack.setOnClickListener{
            finish()
        }

        shortAdapter.data = mutableListOf(
            SitCupData(img_sitcups = R.drawable.img_thum_lunacup_ring_t, "루나컵 링"),
            SitCupData(img_sitcups = R.drawable.img_thum_lunacup_shorty_ts, "루나컵 쇼티")
        )
        middleAdapter.data = mutableListOf(
            SitCupData(img_sitcups = R.drawable.img_thum_lunacup_ring_sl, "루나컵 링"),
            SitCupData(img_sitcups = R.drawable.img_thum_lunacup_shorty_l, "루나컵 쇼티"),
            SitCupData(img_sitcups = R.drawable.img_thum_hannahcup_xss, "한나컵"),
            SitCupData(img_sitcups = R.drawable.img_thum_evecup_m, "이브컵")
        )
        longAdapter.data = mutableListOf(
            SitCupData(img_sitcups = R.drawable.img_thumbnail_lunacup_classic, "루나컵 클래식"),
            SitCupData(img_sitcups = R.drawable.img_thum_hannahcup_m, "한나컵"),
            SitCupData(img_sitcups = R.drawable.img_thum_evecup_sl, "이브컵")
        )
    }
}