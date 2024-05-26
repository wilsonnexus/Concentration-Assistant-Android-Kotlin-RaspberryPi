package com.example.mqttkotlinclient

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.mqttkotlinclient.helper.MqttHelper
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainFragment : Fragment, View.OnClickListener {
    private var disableButton: Button? = null
    private var devicePolicyManager: DevicePolicyManager? = null
    private var activityManager: ActivityManager? = null
    private var componentName: ComponentName? = null
    var mqttHelper: MqttHelper? = null
    var dataReceived: TextView? = null
    private var mIsLocked: Boolean = false
    private var timer: CountDownTimer? = null
    private var durationSpinner: Spinner? = null
    private var remainingTimeTextView: TextView? = null
    private var vibrator: Vibrator? = null

    // Default constructor with no dependencies
    constructor()

    // Constructors for dependencies
    constructor(
        devicePolicyManager: DevicePolicyManager?,
        mqttHelper: MqttHelper?,
        vibrator: Vibrator?
    ) {
        this.devicePolicyManager = devicePolicyManager
        this.mqttHelper = mqttHelper
        this.vibrator = vibrator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        devicePolicyManager =
            requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        activityManager =
            requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        componentName = ComponentName(requireContext(), Controller::class.java)
        startMqtt()
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableButton = view.findViewById(R.id.disable)
        durationSpinner = view.findViewById(R.id.durationSpinner)
        // Get the Vibrator service
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Convert values to hours and minutes
        val durations = resources.getStringArray(R.array.durations_array)
        val durationsList: MutableList<String> = ArrayList()
        for (duration in durations) {
            val durationValue = duration.toInt()
            if (durationValue == 1) {
                durationsList.add("$duration minute")
            } else if (durationValue >= 60) {
                durationsList.add(
                    (durationValue / 60).toString() +
                            (if (durationValue < 120) " hour " else " hours ") +
                            if (durationValue % 60 <= 1) "" else (durationValue - 60).toString() + " minutes"
                )
            } else {
                durationsList.add("$duration minutes")
            }
        }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custom_spinner_item, durationsList
        )
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        durationSpinner?.setAdapter(adapter)
        Log.d("debugger", durationSpinner?.getSelectedItem().toString())
        // Set test selection
        //durationSpinner.setSelection(1);
        disableButton?.setOnClickListener(this)
        remainingTimeTextView = view.findViewById(R.id.textView)
        dataReceived = view.findViewById(R.id.textview_second)
        val nextButton = view.findViewById<Button>(R.id.settings_button)
        nextButton.setOnClickListener { view ->
            val navController = Navigation.findNavController(view)
            (requireActivity() as MainActivity).setCurrentFragmentSettings()
            navController.navigate(R.id.action_mainFragment_to_secondFragment)
        }
    }

    override fun onClick(view: View) {
        if (view === disableButton) {
            if (!(requireActivity() as MainActivity).getOverdrive()) {
                pomodoroTechnique()
            } else {
                overdriveImplementation()
            }
        }
    }

    fun overdriveImplementation() {
        if (!mIsLocked) {
            // First click
            val delayDuration =
                durationSpinner!!.selectedItem.toString().replace("[^\\d.]".toRegex(), "")
                    .toInt() * 60000

            // Update a TextView to show the time remaining
            // Execute first action when timer is done
            lockApp()

            // Create a new timer for 5 seconds (adjust as desired)
            timer = object : CountDownTimer(delayDuration.toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Update a TextView to show the time remaining
                    // (optional)
                    val remainingTime = millisUntilFinished / 1000
                    remainingTimeTextView!!.text = "Time Left: " + timeToString(remainingTime)
                }

                override fun onFinish() {
                    // Execute second action when timer is done
                    disableButton?.text = "Disable"
                    (requireActivity() as MainActivity).stopLock()
                    //stopLockTask();
                    mIsLocked = false
                    remainingTimeTextView?.text = ""
                }
            }
            timer?.start() // Start the new timer
        } else {

            // Second click
            disableButton?.text = "Disable"
            timer?.cancel()
            (requireActivity() as MainActivity).stopLock()
            //stopLockTask();
            mIsLocked = false
            remainingTimeTextView?.text = "0"
            // Turn vibration motor on
            mqttHelper?.publishToTopic("sensor/temp", "1")
            vibrator?.vibrate(5000)

            // Turn vibration motor off
            val handler = Handler()
            handler.postDelayed({ mqttHelper!!.publishToTopic("sensor/temp", "0") }, 5000)
            // mqttHelper.publishToTopic("sensor/temp", "0");
        }
    }

    fun pomodoroTechnique() {
        if (!mIsLocked) {
            // First click
            val workDuration =
                durationSpinner?.selectedItem.toString().replace("[^\\d.]".toRegex(), "")
                    .toInt() * 60000
            val breakDuration = 5000 // Set your desired break duration in milliseconds

            // Lock the app (blocking)
            lockApp()
            if (workDuration / 60000 <= 5) {
                // Create a new timer for 5 seconds (adjust as desired)
                timer = object : CountDownTimer(workDuration.toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Update a TextView to show the time remaining
                        // (optional)
                        val remainingTime = millisUntilFinished / 1000
                        remainingTimeTextView?.text = "Time Left: " + timeToString(remainingTime)
                    }

                    override fun onFinish() {
                        // Execute second action when timer is done
                        disableButton?.text = "Disable"
                        (requireActivity() as MainActivity).stopLock()
                        //stopLockTask();
                        mIsLocked = false
                        remainingTimeTextView?.text = ""
                    }
                }
            } else {
                // Create a new timer for the work period
                timer = object : CountDownTimer(workDuration.toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val remainingTime = millisUntilFinished / 1000
                        remainingTimeTextView!!.text = "Time Left: " + timeToString(remainingTime)
                        if (remainingTime / 60 % 5 == 0L && mIsLocked && remainingTime != 0L) {
                            unlockApp()
                        } else if (remainingTime / 60 % 5 == 0L && !mIsLocked && remainingTime != 0L) {
                            lockApp()
                        }
                    }

                    override fun onFinish() {
                        // Execute second action when timer is done
                        disableButton?.text = "Disable"
                        (requireActivity() as MainActivity).stopLock()
                        //stopLockTask();
                        mIsLocked = false
                        remainingTimeTextView?.text = ""
                    }
                }
            }
            timer?.start() // Start the work timer
        } else {
            // Second click
            unlockApp()
            timer?.cancel()
            remainingTimeTextView?.text = "0"
        }
    }

    private fun timeToString(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        val remainingSeconds = seconds % 60
        var timeResult = ""
        if (hours > 0) {
            timeResult += hours.toString() + if (hours == 1L) " hour " else " hours "
        }
        if (minutes > 0) {
            timeResult += minutes.toString() + if (minutes == 1L) " minute " else " minutes "
        }
        if (remainingSeconds > 0) {
            timeResult += remainingSeconds.toString() + if (remainingSeconds == 1L) " second" else " seconds"
        }
        return timeResult.trim { it <= ' ' }
    }

    private fun lockApp() {
        disableButton?.text = "Cancel"
        val active = devicePolicyManager!!.isAdminActive(componentName!!)
        if (true) {
            (requireActivity() as MainActivity).startLock()
            //startLockTask();
            mIsLocked = true
        }
    }

    private fun unlockApp() {
        disableButton?.text = "Disable"
        (requireActivity() as MainActivity).stopLock()
        //stopLockTask();
        mIsLocked = false
        remainingTimeTextView?.text = ""
        // Turn vibration motor on
        mqttHelper?.publishToTopic("sensor/temp", "1")
        vibrator?.vibrate(5000)

        // Turn vibration motor off
        val handler = Handler()
        handler.postDelayed({ mqttHelper!!.publishToTopic("sensor/temp", "0") }, 5000)
    }

    // Start MQTT Client Code
    fun startMqtt() {
        mqttHelper = MqttHelper(requireContext().applicationContext)
        mqttHelper?.mqttAndroidClient?.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connected")
            }

            override fun connectionLost(throwable: Throwable) {}
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Debug", mqttMessage.toString())
                dataReceived?.text = mqttMessage.toString()
                //mChart.addEntry(Float.valueOf(mqttMessage.toString()));
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
    }

    companion object {
        // Allowlist one app
        private const val KIOSK_PACKAGE = "com.example.mqttkotlinclient"
    }
}