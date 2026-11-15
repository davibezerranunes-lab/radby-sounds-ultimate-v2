// app/build.gradle.kts — configurações do módulo do app
// applicationId: com.radby.sounds (empacotamento original: com.muzk.app, renomeado)
// versões androidx pinadas conforme META-INF/*.version do APK original (v2)
plugins {
    id("com.android.application")
}

android {
    namespace = "com.radby.sounds"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.radby.sounds"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = false // as classes *Binding descompiladas já estão em src (pacote .databinding)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    signingConfigs {
        create("release") {
            // CI: assina com o keystore de debug quando não há segredos configurados.
            // Para release real, exporte RADBY_KEYSTORE_FILE / RADBY_KEYSTORE_PASSWORD /
            // RADBY_KEY_ALIAS / RADBY_KEY_PASSWORD como secrets do GitHub Actions.
            val ksPath = System.getenv("RADBY_KEYSTORE_FILE")
            if (ksPath != null) {
                storeFile = file(ksPath)
                storePassword = System.getenv("RADBY_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("RADBY_KEY_ALIAS")
                keyPassword = System.getenv("RADBY_KEY_PASSWORD")
            } else {
                storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += setOf("META-INF/*.kotlin_module", "META-INF/DEPENDENCIES", "META-INF/LICENSE*", "META-INF/NOTICE*")
        }
    }
}

dependencies {
    // --- AndroidX (versões originais do APK) ---
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.startup:startup-runtime:1.1.1")
    implementation("androidx.databinding:viewbinding:8.7.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // Navegação
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // Mídia (ExoPlayer/Media3)
    implementation("androidx.media3:media3-common:1.5.1")
    implementation("androidx.media3:media3-container:1.5.1")
    implementation("androidx.media3:media3-extractor:1.5.1")
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-session:1.5.1")

    // Imagens
    implementation("io.coil-kt:coil:2.7.0")

    // Rede / JSON / utilitários
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("com.google.guava:guava:33.4.0-android")
    implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("commons-io:commons-io:2.18.0")

    // Kotlin (o código foi compilado em Kotlin e descompilado para Java)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
}
