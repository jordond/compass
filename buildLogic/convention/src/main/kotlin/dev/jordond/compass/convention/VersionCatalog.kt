package dev.jordond.compass.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.plugin(name: String) = libs.findPlugin(name).get().get().pluginId

internal fun Project.alias(name: String) = pluginManager.apply(plugin(name))

internal val Project.jvmTargetVersion: Int
    get() = libs.findVersion("jvmTarget").get().toString().toInt()