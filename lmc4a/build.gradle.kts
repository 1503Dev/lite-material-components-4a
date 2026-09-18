plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "dev1503.lmc4a"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 16

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    api(libs.androidx.viewpager)
}