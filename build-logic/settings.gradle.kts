plugins {
  id("com.gradle.enterprise") version "3.15" // sync with libraries.toml
  id("com.gradle.common-custom-user-data-gradle-plugin") version "1.11.3"
}

rootProject.name = "build-logic"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libraries.toml"))
    }
  }
}

apply(from = "../gradle/repositories.gradle.kts")
apply(from = "../gradle/ge.gradle")
