plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fire_and_based"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fire_and_based"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //Firebase firestore and storage functionality
    //Core dependency
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    //Firestore (NoSQL Database, stores users and events data)
    implementation("com.google.firebase:firebase-firestore")
    //Storage stores image assets
    implementation("com.google.firebase:firebase-storage:20.3.0")

    //UI and navigation elements
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.fragment:fragment:1.6.2")

    //Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.6.0")
    testImplementation("org.mockito:mockito-inline:3.6.0")
    testImplementation("org.mockito:mockito-junit-jupiter:3.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Image loading and downloading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.squareup.picasso:picasso:2.71828")

    //QR Code core dependency and android-specific implementation
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

}