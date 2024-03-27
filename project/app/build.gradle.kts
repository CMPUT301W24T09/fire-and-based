import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.fire_and_based"
    compileSdk = 34

    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.example.fire_and_based"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val getApiKey: String by lazy {
            val properties = Properties()
            properties.load(rootProject.file("local.properties").inputStream())
            properties.getProperty("API_KEY")
        }
        buildConfigField("String", "API_KEY", "\"${getApiKey}\"")
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
    lint {
        abortOnError = false
    }
}

dependencies {
    //Firebase
    //Core dependency
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    //Firestore (NoSQL Database, stores users and events data)
    implementation("com.google.firebase:firebase-firestore")
    //Storage stores image assets
    implementation("com.google.firebase:firebase-storage:20.3.0")
    //push notifications
    implementation("com.google.firebase:firebase-messaging:23.4.1")

    //UI and navigation elements
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.fragment:fragment:1.6.2")

    //Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("org.mockito:mockito-core:4.1.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")

    //Image loading and downloading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.squareup.picasso:picasso:2.71828")

    //QR Code core dependency and android-specific implementation
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    //Google Maps API
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // For geocoding
    implementation("com.google.maps:google-maps-services:0.9.0")

    // Circular image view for profile image
    implementation("de.hdodenhof:circleimageview:2.1.0")

}