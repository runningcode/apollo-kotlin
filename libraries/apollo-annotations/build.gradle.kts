import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("apollo.library")
}

apolloLibrary {
  javaModuleName("com.apollographql.apollo3.annotations")
  mpp {}
}

kotlin {
  sourceSets {
    findByName("commonMain")?.apply {
      dependencies {
        api(golatac.lib("kotlin.stdlib"))
        api(golatac.lib("jetbrains.annotations"))
      }
    }

    findByName("jsMain")?.apply {
      dependencies {
        // See https://youtrack.jetbrains.com/issue/KT-53471
        api(golatac.lib("kotlin.stdlib.js"))
      }
    }
  }
}

tasks.all {
  if (name == "compileAppleMainKotlinMetadata") {
    doLast {
      this as KotlinNativeCompile
      println(libraries.joinToString("") { it.absolutePath + "\n" })
    }
  }
}