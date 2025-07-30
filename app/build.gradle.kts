plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pda"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pda"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.emdk)
    implementation (libs.recyclerview)

    implementation("com.android.volley:volley:1.2.1")
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_ASCII-release-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_CMN-release-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_INTERFACE-release-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_LLRP-release-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_NGE-protocolrelease-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_NGE-Transportrelease-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_NGEUSB-Transportrelease-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_READER-release-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_TRANSPORT-release-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_ZIOTC-release-null.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/API3_ZIOTCTRANSPORT-release-2.0.4.192.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/rfidhostlib.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/rfidseriallib.aar"))
    implementation(files("libs/Zebra_RFIDAPI3_SDK_2.0.4.192/Zebra_RFIDAPI3_SDK_2.0.4.192/BarcodeScannerLibrary-release-28.aar"))
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


}




