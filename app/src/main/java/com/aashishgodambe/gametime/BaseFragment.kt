package com.aashishgodambe.gametime


import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun getCardCountRow(): Int {
        // Current width of team cardview
        val cardWidth = 180.0f
        val displaymetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        return ((displaymetrics.widthPixels / displaymetrics.density)/cardWidth).toInt()
    }

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}
