import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Commit 2: lee local.properties (si existe) para sacar la clave de Maps sin ponerla en el código.
val localProps = Properties().apply {
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

android {
    namespace = "com.flores.campusgps"
    compileSdk {
        // Commit 1: compileSdk 37 porque Maps Compose y AndroidX lo exigen.
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.flores.campusgps"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Commit 2: pasa MAPS_API_KEY al Manifest como ${MAPS_API_KEY}; vacío si no está definida.
        manifestPlaceholders["MAPS_API_KEY"] = localProps.getProperty("MAPS_API_KEY", "")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose + Material 3: el BOM fija las versiones y material3 trae Button, Scaffold, TopAppBar, etc.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    // Mejora 3: íconos de Material (libro, restaurante, navegación, etc.) para la interfaz.
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Commit 1: ubicación (Google), corrutinas para await(), mapa (Maps Compose) y ViewModel.
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("com.google.maps.android:maps-compose:8.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // Mejora 2: DataStore para guardar el último lugar visitado.
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}