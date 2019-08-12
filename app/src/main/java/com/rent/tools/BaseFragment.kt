package com.rent.tools


import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.rent.R

interface HasToolbar {
    val toolbar: Toolbar? // Return null to hide the toolbar
}

interface HasBackButton

abstract class BaseFragment : Fragment() {

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
//        if (this is HasToolbar) {
//            requireActivity().homeToolbar.makeVisible()
//            (requireActivity() as AppCompatActivity).setSupportActionBar(requireActivity().homeToolbar)
//        }

        if (this is HasBackButton) {
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            actionBar?.title = context?.getString(R.string.app_name)
            actionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

}
