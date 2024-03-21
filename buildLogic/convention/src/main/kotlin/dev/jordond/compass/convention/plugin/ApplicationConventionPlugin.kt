package dev.jordond.compass.convention.plugin

import com.android.build.api.dsl.ApplicationExtension
import dev.jordond.compass.convention.configureAndroidCompose
import dev.jordond.compass.convention.configureKotlinAndroid
import dev.jordond.compass.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
        }

        extensions.configure<ApplicationExtension> {
            val packageName = libs.findVersion("packageName").get().toString()
            namespace = packageName

            defaultConfig {
                applicationId = packageName
                targetSdk = libs.findVersion("sdk-target").get().toString().toInt()
                versionName = libs.findVersion("release-version").get().toString()
                versionCode = libs.findVersion("release-code").get().toString().toInt()
            }

            configureKotlinAndroid(this)
            configureAndroidCompose(this)
        }
    }
}