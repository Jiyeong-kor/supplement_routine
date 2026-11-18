import java.io.FileInputStream
import java.util.Properties
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("io.github.takahirom.roborazzi")
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
val hasReleaseKeystore = keystorePropertiesFile.exists()

if (hasReleaseKeystore) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.jiyeong.supplementroutine.kmp.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jiyeong.supplement_routine"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            signingConfig = if (hasReleaseKeystore) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
        all {
            it.maxHeapSize = "1536m"
            it.maxParallelForks = 1
            it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
        }
    }
}

roborazzi {
    outputDir.set(file("../../docs/assets/screenshots/compose-previews"))
    @OptIn(ExperimentalRoborazziApi::class)
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("com.jiyeong.supplementroutine.kmp.android.ui.preview")
        robolectricConfig = mapOf(
            "sdk" to "[35]",
            "application" to "android.app.Application::class",
            "qualifiers" to "\"w400dp-h900dp-xhdpi\"",
        )
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("com.google.dagger:hilt-android:2.57.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    ksp("com.google.dagger:hilt-compiler:2.57.1")
    implementation(compose.materialIconsExtended)
    implementation(compose.material3)
    implementation(compose.ui)
    // Keep tooling and tests on the app's Compose 1.11 runtime line.
    //noinspection NewerVersionAvailable
    debugImplementation("org.jetbrains.compose.ui:ui-tooling-preview:1.11.0")
    //noinspection NewerVersionAvailable
    debugImplementation("org.jetbrains.compose.ui:ui-tooling:1.11.0")
    //noinspection GradleDependency
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.11.1")
    // Capture annotations are read by JVM tests and do not belong in the APK.
    debugCompileOnly("io.github.takahirom.roborazzi:roborazzi-annotations:1.79.0")
    testImplementation("junit:junit:4.13.2")
    // Keep the verified API 35 renderer stable; review captures when upgrading it.
    //noinspection NewerVersionAvailable
    testImplementation("org.robolectric:robolectric:4.16.1")
    //noinspection NewerVersionAvailable
    testImplementation("org.jetbrains.compose.ui:ui-test-junit4:1.11.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:1.79.0")
    // Roborazzi 1.79.0 calls the preview invocation API provided by scanner 0.9.1.
    //noinspection NewerVersionAvailable
    testImplementation("io.github.sergio-sastre.ComposablePreviewScanner:android:0.9.1")
}
