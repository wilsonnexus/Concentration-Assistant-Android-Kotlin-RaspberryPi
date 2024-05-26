package com.example.mqttkotlinclient

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


class MainActivity : AppCompatActivity() {
    private var devicePolicyManager: DevicePolicyManager? = null
    private var componentName: ComponentName? = null
    private var backButtonEnabled = true
    private var overdrive: Boolean = true
    private var mIsLocked: Boolean = false

    // Fix phone back button going to Settings in the Home fragment
    // Create current frame indicators
    private enum class CurrentFragment {
        HOME, SETTINGS
    }

    private var currentFragment = CurrentFragment.HOME // Set the initial fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        var navController = Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun onBackPressed() {
        // Fix phone back button going to Settings in the Home fragment
        if (currentFragment == CurrentFragment.SETTINGS && backButtonEnabled) {
            // If on the settings fragment, navigate back to the home fragment
            // If on the home fragment, perform default back navigation
            super.onBackPressed()

        }
    }

    // Fix phone back button going to Settings in the Home fragment
    // We are in the Settings fragment
    fun setCurrentFragmentSettings() {
        currentFragment = CurrentFragment.SETTINGS
    }

    // We are in the Home fragment
    fun setCurrentFragmentHome() {
        currentFragment = CurrentFragment.HOME
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mIsLocked) {
            startLockTask()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun startLock() {
        startLockTask()
    }

    fun stopLock() {
        stopLockTask()
    }

    fun getOverdrive(): Boolean {
        return overdrive
    }

    // Modes
    fun startPomodoroTimer() {
        // Implement the Pomodoro timer logic
        // For example, you can use the existing timer logic with some modifications
        overdrive = false
    }

    fun startOverdriveTimer() {
        // Implement the Overdrive timer logic
        // For example, you can use the existing timer logic without breaks
        overdrive = true
    }

    companion object {
        // Allowlist one app
        private const val KIOSK_PACKAGE = "com.example.mqttkotlinclient"
        private val APP_PACKAGES = arrayOf(KIOSK_PACKAGE)
    }
}