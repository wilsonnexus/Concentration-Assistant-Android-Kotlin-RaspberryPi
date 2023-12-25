package com.example.mykotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

class SecondFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pomodoroButton = view.findViewById<Button>(R.id.pomodoro_button)
        val overdriveButton = view.findViewById<Button>(R.id.overdrive_button)
        val backButton = view.findViewById<Button>(R.id.back_button)
        val buttonSecond = view.findViewById<Button>(R.id.button_second)
        pomodoroButton.setOnClickListener { (requireActivity() as MainActivity).startPomodoroTimer() }
        overdriveButton.setOnClickListener { (requireActivity() as MainActivity).startOverdriveTimer() }
        backButton.setOnClickListener { view ->
            val navController = findNavController(view)
            // Fix phone back button going to Settings in the Home fragment
            (requireActivity() as MainActivity).setCurrentFragmentHome()
            navController.navigate(R.id.action_secondFragment_to_mainFragment)
        }
        buttonSecond.setOnClickListener {
            NavHostFragment.findNavController(this@SecondFragment)
                .navigate(R.id.action_secondFragment_to_thirdFragment)
        }
    }
}