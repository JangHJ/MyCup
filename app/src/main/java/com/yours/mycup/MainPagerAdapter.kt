package com.yours.mycup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.yours.mycup.info.InfoFragment
import com.yours.mycup.record.RecordFragment
import com.yours.mycup.search.SearchFragment
import com.yours.mycup.mypage.MypageFragment

class MainPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> RecordFragment()
            1 -> SearchFragment()
            2 -> InfoFragment()
            else -> MypageFragment()
        }
    }
    override fun getCount() = 4
}