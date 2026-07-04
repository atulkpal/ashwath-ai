    import org.apache.tools.ant.taskdefs.condition.Os
import java.util.Locale
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ashwathai.ashwathai"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.ashwathai.ashwathai"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        buildConfig = true
    }
}

// ─── Go Engine Build Integration ─────────────────────────────────────

val goEngineTasks = tasks.register("buildGoEngine") {
    group = "build"
    description = "Builds the Go engine shared library for all target ABIs"
}

val targetAbis = listOf("arm64-v8a", "x86_64")
val goSourceDir = file("${project.rootDir}/../engine")
val jniLibsDir = file("${projectDir}/src/main/jniLibs")

val goBinary = "C:/Program Files/Go/bin/go.exe"

targetAbis.forEach { abi ->
    val capitalizedAbi = abi.split("-", "_").joinToString("") {
        it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString() }
    }
    val taskName = "buildGoEngine$capitalizedAbi"

    val props = Properties()
    val propFile = rootProject.file("local.properties")
    if (propFile.exists()) {
        propFile.inputStream().use { props.load(it) }
    }
    val sdkDir = props.getProperty("sdk.dir") ?: System.getenv("ANDROID_HOME")
    var ndkDir = props.getProperty("ndk.dir") ?: System.getenv("ANDROID_NDK_HOME")

    if (ndkDir == null && sdkDir != null) {
        val ndkRoot = file("$sdkDir/ndk")
        if (ndkRoot.exists()) {
            val versions = ndkRoot.listFiles()?.filter { it.isDirectory }?.sortedByDescending { it.name }
            if (versions != null && versions.isNotEmpty()) {
                ndkDir = versions[0].absolutePath
            }
        }
    }

    val finalNdkDir = ndkDir ?: throw GradleException("NDK not found")

    val buildTask = tasks.register<Exec>(taskName) {
        val (goArch, clangPrefix) = when (abi) {
            "arm64-v8a" -> "arm64" to "aarch64-linux-android24"
            "x86_64" -> "amd64" to "x86_64-linux-android24"
            "armeabi-v7a" -> "arm" to "armv7a-linux-androideabi24"
            else -> throw GradleException("Unsupported ABI: $abi")
        }

        val hostTag = if (Os.isFamily(Os.FAMILY_WINDOWS)) "windows-x86_64" else "linux-x86_64"
        val extension = if (Os.isFamily(Os.FAMILY_WINDOWS)) ".cmd" else ""
        val ccPath = file("$finalNdkDir/toolchains/llvm/prebuilt/$hostTag/bin/$clangPrefix-clang$extension")

        inputs.dir(goSourceDir)
        val outputFile = file("$jniLibsDir/$abi/libashwath_engine.so")
        outputs.file(outputFile)

        workingDir(goSourceDir)
        executable(goBinary)
        args("build", "-buildmode=c-shared", "-o", outputFile.absolutePath, "./cmd/libashwath/")

        environment("CGO_ENABLED", "1")
        environment("GOOS", "android")
        environment("GOARCH", goArch)
        environment("CC", ccPath.absolutePath)

        doFirst {
            if (!ccPath.exists()) throw GradleException("NDK Clang not found at $ccPath")
            if (!outputFile.parentFile.exists()) outputFile.parentFile.mkdirs()
            println("Building Go engine for $abi using CC=$ccPath")
        }
    }
    goEngineTasks.configure { dependsOn(buildTask) }
}

tasks.named("preBuild") {
    dependsOn(goEngineTasks)
}

dependencies {
    implementation(project(":sdk"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
