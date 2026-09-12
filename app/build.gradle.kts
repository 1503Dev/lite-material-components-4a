plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dev1503.litematerial.lmc4a.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "dev1503.litematerial.lmc4a.app"
        minSdk = 16
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    implementation(project(":lmc4a"))
}