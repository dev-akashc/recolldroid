// import com.google.protobuf.gradle.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.protobuf)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "org.grating.recolldroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.grating.recolldroid"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        signingConfigs {
            create("release") {
                storeFile = file(System.getenv("RELEASE_KEYSTORE_FILE"))
                storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                          "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
}

configurations {
    all {
        //Because:
        //Duplicate class org.xmlpull.v1.XmlPullParser found in modules xmlpull-1.1.3.1.jar -> xmlpull-1.1.3.1 (xmlpull:xmlpull:1.1.3.1) and xpp3-1.1.4c.jar -> xpp3-1.1.4c (xpp3:xpp3:1.1.4c)
        //Duplicate class org.xmlpull.v1.XmlPullParserException found in modules xmlpull-1.1.3.1.jar -> xmlpull-1.1.3.1 (xmlpull:xmlpull:1.1.3.1) and xpp3-1.1.4c.jar -> xpp3-1.1.4c (xpp3:xpp3:1.1.4c)
        //Duplicate class org.xmlpull.v1.XmlPullParserFactory found in modules xmlpull-1.1.3.1.jar -> xmlpull-1.1.3.1 (xmlpull:xmlpull:1.1.3.1) and xpp3-1.1.4c.jar -> xpp3-1.1.4c (xpp3:xpp3:1.1.4c)
        //Duplicate class org.xmlpull.v1.XmlSerializer found in modules xmlpull-1.1.3.1.jar -> xmlpull-1.1.3.1 (xmlpull:xmlpull:1.1.3.1) and xpp3-1.1.4c.jar -> xpp3-1.1.4c (xpp3:xpp3:1.1.4c)
        exclude(group = "xmlpull", module = "xmlpull")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // Retrofit
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.retrofit)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.safe.args.generator)

    // Proto DataStore
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)
    implementation(libs.androidx.espresso.core)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.7"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
