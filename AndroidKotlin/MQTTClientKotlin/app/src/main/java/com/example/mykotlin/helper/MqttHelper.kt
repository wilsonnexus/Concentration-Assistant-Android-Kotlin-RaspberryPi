package com.example.mykotlin.helper

import android.content.Context
import android.os.Handler
import android.util.Log
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * Created by wildan on 3/19/2017.
 */
class MqttHelper(context: Context?) {
    var mqttAndroidClient: MqttAndroidClient
    val serverUri = "tcp://driver.cloudmqtt.com:18741"
    val clientId = "WilsonAndroidClient2"
    val subscriptionTopic = "sensor/+" // You need to type this into the Websocket

    // topic and replace the + symbol or else
    // you disconnect
    val username = "kigpggpf"
    val password = "kb55FEKZNTjA"

    init {
        mqttAndroidClient = MqttAndroidClient(context!!, serverUri, clientId, Ack.AUTO_ACK)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("mqtt", s)
            }

            override fun connectionLost(throwable: Throwable) {}
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Mqtt", mqttMessage.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
        connect()
    }

    fun setCallback(callback: MqttCallbackExtended?) {
        mqttAndroidClient.setCallback(callback!!)
    }

    private fun connect() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = username
        mqttConnectOptions.password = password.toCharArray()

        //try {
        mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                val disconnectedBufferOptions = DisconnectedBufferOptions()
                disconnectedBufferOptions.isBufferEnabled = true
                disconnectedBufferOptions.bufferSize = 100
                disconnectedBufferOptions.isPersistBuffer = false
                disconnectedBufferOptions.isDeleteOldestMessages = false
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                val handler = Handler()
                handler.postDelayed({ subscribeToTopic() }, 1000)
                //subscribeToTopic();
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Log.w("Mqtt", "Failed to connect to: $serverUri$exception")
            }
        })


        //} catch (MqttException ex){
        //    ex.printStackTrace();
        //}
    }

    private fun subscribeToTopic() {
        //try {
        mqttAndroidClient.subscribe(subscriptionTopic, 0, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.w("Mqtt", "Subscribed!")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Log.w("Mqtt", "Subscribed fail!")
            }
        })

        //} catch (MqttException ex) {
        //  System.err.println("Exception whilst subscribing");
        // ex.printStackTrace();
        //}
    }

    fun publishToTopic(topic: String?, message: String) {
        val mqttMessage = MqttMessage()
        mqttMessage.payload = message.toByteArray()
        //try {
        mqttAndroidClient.publish(topic!!, mqttMessage)
        //} catch (MqttException e) {
        //Log.e("MQTT", "Error publishing message: " + e.getMessage());
        //}
    }
}