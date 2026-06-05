import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

val supplementRoutineSharedXcFramework = XCFramework("SupplementRoutineShared")

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SupplementRoutineShared"
            isStatic = true
            supplementRoutineSharedXcFramework.add(this)
        }
    }

    sourceSets {
        commonMain.dependencies {
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.jiyeong.supplementroutine.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
    }
}
