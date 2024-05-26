plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.mqttkotlinclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mqttkotlinclient"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //implementation(name:'dpcsupport-yyyymmdd', ext:'aar')
    implementation("com.google.android.gms:play-services-auth:11.4.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.4.1")
    implementation("androidx.navigation:navigation-ui:2.4.1")
    implementation("androidx.test:monitor:1.6.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("org.mockito:mockito-core:3.10.0")
    testImplementation("org.mockito:mockito-inline:3.10.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")

    implementation("androidx.appcompat:appcompat:1.6.1")
    testImplementation("org.robolectric:robolectric:4.10.3")

    // paho dependencies
    //implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    /*implementation ('org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'){
        exclude module: 'support-v4'
    }*/
    //implementation("androidx.legacy:legacy-support-v4:1.0.0")
    //implementation("com.github.hannesa2:paho.mqtt.android:3.5.1")
    // Add the MQTT 5.0 dependencies
    /*implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv5:1.2.0")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1") {
        exclude(module = "support-v4")
    }*/
    implementation("androidx.legacy:legacy-support-v4:1.0.0'")
    implementation("com.github.hannesa2:paho.mqtt.android:3.6.4")
    // (Java only)
    implementation("androidx.work:work-runtime:2.8.0-beta02")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")

}