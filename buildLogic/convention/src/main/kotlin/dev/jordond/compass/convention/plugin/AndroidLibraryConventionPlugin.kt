package dev.jordond.compass.convention.plugin

import com.android.build.gradle.LibraryExtension
import dev.jordond.compass.convention.alias
import dev.jordond.compass.convention.configureKotlinAndroid
import dev.jordond.compass.convention.setNamespace
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

class AndroidLibraryConventionPlugin : Plugin<Project> {

    @ExperimentalWasmDsl
    override fun apply(target: Project) = with(target) {
        alias("android-library")

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            setNamespace(project.name.replace("-", "."))
            println("Setting namespace: $namespace")
        }
    }
}