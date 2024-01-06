package com.kinetx.silentproject.onboarding

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.kinetx.silentproject.R


class OnboardThirdFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboard_third, container, false)

        val finishButton : Button = view.findViewById(R.id.onboard_third_finish)

        finishButton.setOnClickListener()
        {
            onBoardingFinished()
            view?.findNavController()?.navigate(ViewPagerFragmentDirections.actionViewPagerFragmentToProfileListFragment())
        }


        return view
    }

    private fun onBoardingFinished() {
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPref.edit()
        editor.putBoolean("onBoardingFinished",true)
        editor.apply()

    }

}