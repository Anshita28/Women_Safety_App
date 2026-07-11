package com.anshita.womensafety.guardiandetail

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.anshita.womensafety.R
import com.anshita.womensafety.databinding.FragmentGuardianInfoBinding

class GuardianInfo : Fragment() {

    private lateinit var binding: FragmentGuardianInfoBinding
    private lateinit var model: GuardianInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_guardian_info, container, false)

        // Modernized ViewModel initialization
        model = ViewModelProvider(this).get(GuardianInfoViewModel::class.java)

        // Specify layout for recycler view safely
        val linearLayoutManager = LinearLayoutManager(
            requireContext(), RecyclerView.VERTICAL, false)
        binding.guardianList.layoutManager = linearLayoutManager

        // Observe the model securely using viewLifecycleOwner
        model.allGuardians.observe(viewLifecycleOwner, Observer { guardians ->
            // Data bind the recycler view
            binding.guardianList.adapter = GuardianAdapter(guardians)
        })

        binding.addGuardian.setOnClickListener { openAddGuardian() }

        model.showSnackBarEvent.observe(viewLifecycleOwner, Observer { show ->
            if (show == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_LONG
                ).show()
                model.doneShowingSnackbar()
            }
        })

        setupMenu()

        return binding.root
    }

    private fun openAddGuardian() {
        findNavController().navigate(GuardianInfoDirections.actionGuardianInfoToAddGuardian())
    }

    // Modernized Option Menu Handler using MenuProvider API
    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.delete_item -> {
                        model.onClear()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}