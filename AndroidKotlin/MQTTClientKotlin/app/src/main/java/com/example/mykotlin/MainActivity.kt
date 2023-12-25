package com.example.mykotlin

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController

class MainActivity : AppCompatActivity() {
    private var devicePolicyManager: DevicePolicyManager? = null
    private val componentName: ComponentName? = null
    private val backButtonEnabled = true
    var overdrive = true
        private set
    private val mIsLocked = false

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
        val navController = findNavController(this, R.id.nav_host_fragment)
    }

    override fun onBackPressed() {
        // Fix phone back button going to Settings in the Home fragment
        if (currentFragment == CurrentFragment.SETTINGS) {
            // If on the settings fragment, navigate back to the home fragment
            if (backButtonEnabled) {
                // If on the home fragment, perform default back navigation
                super.onBackPressed()
            }
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

    override fun onResume() {
        super.onResume()
        val isActive = devicePolicyManager!!.isAdminActive(componentName!!)
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
        private const val KIOSK_PACKAGE = "com.example.mykotlin"
        private val APP_PACKAGES = arrayOf(KIOSK_PACKAGE)
    }
}