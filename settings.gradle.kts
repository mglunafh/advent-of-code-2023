pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.22"
        application
    }

}

rootProject.name = "advent-of-code-2023"
include("java-advent")
include("kotlin-advent")
