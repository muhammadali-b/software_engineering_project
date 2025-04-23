plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.carbotrackphoneapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.carbotrackphoneapplication"
        minSdk = 21
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
    // Here we are adding the MPAndroidChart package from Philipp Jahoda
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //This is for the OSMDroid dependency that will provide the phone application with a map allowing the user to know their traveling and current location.
    implementation("org.osmdroid:osmdroid-android:6.1.11")

    // This allows users to have the option od enabling location services on their mobile devices
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation("androidx.preference:preference:1.2.1")

    // This is for users to add a profile picture on the appropriate profile page.
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.material:material:1.9.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}