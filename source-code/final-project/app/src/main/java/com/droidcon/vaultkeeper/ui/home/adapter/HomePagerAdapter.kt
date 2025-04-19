package com.droidcon.vaultkeeper.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.droidcon.vaultkeeper.ui.home.tabs.FilesFragment
import com.droidcon.vaultkeeper.ui.home.tabs.NotesFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    
    override fun getItemCount(): Int = 2
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NotesFragment()
            1 -> FilesFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
} 