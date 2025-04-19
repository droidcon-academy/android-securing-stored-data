package com.droidcon.vaultkeeper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.droidcon.vaultkeeper.R
import com.droidcon.vaultkeeper.databinding.FragmentHomeBinding
import com.droidcon.vaultkeeper.ui.home.adapter.HomePagerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupMenu()
        setupViewPager()
        setupFab()
        hideBackButton()
    }
    
    private fun hideBackButton() {
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restore back button visibility for other fragments
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        _binding = null
    }
    
    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        showBiometricDialog()
                        true
                    }
                    else -> false
                }
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    
    private fun setupViewPager() {
        val pagerAdapter = HomePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        
        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.notes)
                1 -> getString(R.string.files)
                else -> ""
            }
        }.attach()
    }
    
    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            when (binding.viewPager.currentItem) {
                0 -> { // Notes tab
                    findNavController().navigate(R.id.action_homeFragment_to_noteEditorFragment)
                }
                1 -> { // Files tab
                    findNavController().navigate(R.id.action_homeFragment_to_fileImportFragment)
                }
            }
        }
    }
    
    private fun showBiometricDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Biometric Authentication")
            .setMessage("Do you want to enable biometric authentication for app access?")
            .setPositiveButton("Enable") { _, _ ->
                // Enable biometric authentication in preferences
                // This would typically use the EncryptedPreferenceManager
            }
            .setNegativeButton("Disable") { _, _ ->
                // Disable biometric authentication in preferences
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
} 