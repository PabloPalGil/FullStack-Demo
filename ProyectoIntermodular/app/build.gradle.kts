plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    namespace = "com.pablo.eventos_retrofit"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pablo.eventos_retrofit"
        minSdk = 29
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //Para usar hilt en las pruebas Ui
        testInstrumentationRunner = "com.pablo.eventos_retrofit.CustomTestRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coil (para cargar imágenes en Compose)
    implementation("io.coil-kt:coil-compose:2.2.2")

    //Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6")
    implementation(libs.androidx.navigation.compose)

    //Todos los iconos:
    implementation("androidx.compose.material:material-icons-extended-android:1.6.7")
    implementation(libs.androidx.ui.test.android)


    //Dependencias para pruebas unitarias
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test.junit)

    //Dependencias para pruebas instrumentadas (UI tests con Compose)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.7")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.7") //Opcional, para inspección visual en debug
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.7") //Requerido para pruebas de UI
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)

    //MockWebServer
    testImplementation(libs.okhttp)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    // For Android tests (if needed)
    androidTestImplementation ("androidx.test:runner:1.5.0")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")


    // Dagger-Hilt
    val hilt_navigation_compose_version = "1.0.0"
    val hilt_version = "2.55"
    implementation ("com.google.dagger:hilt-android:$hilt_version")
    kapt ("com.google.dagger:hilt-android-compiler:$hilt_version")

    // Para la integración con Compose
    implementation ("androidx.hilt:hilt-navigation-compose:$hilt_navigation_compose_version")

    // Para las pruebas
    androidTestImplementation ("com.google.dagger:hilt-android-testing:$hilt_version")
    kaptAndroidTest ("com.google.dagger:hilt-compiler:$hilt_version")




    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}