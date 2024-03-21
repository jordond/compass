package dev.jordond.compass.convention.plugin

import dev.jordond.compass.convention.alias
import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        with(target) {
            with(pluginManager) {
                alias("publish")
                alias("dokka")
            }
        }
    }
}