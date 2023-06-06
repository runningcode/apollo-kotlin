plugins {
  id("apollo.test") apply false
  id("net.mbonnin.golatac") version "0.0.3"
}

golatac.init(file("../gradle/libraries.toml"))

rootProject.configureNode()

tasks.register("ciBuild") {
  description = """Execute the 'build' task in subprojects and the `termination:run` task too"""
  subprojects {
    this@register.dependsOn(tasks.matching { it.name == "build" })
  }
  dependsOn(":termination:run")
  doLast {
    checkGitStatus()
  }
}

// See https://youtrack.jetbrains.com/issue/KT-58957
buildscript {
  dependencies {
    // Force latest metadata for Kotlin binary compatibility check
    classpath("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.2")
  }
}
