rootProject.name = "Scriptive"

include("scriptive-core")
include("scriptive-formatify")

pluginManagement {
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    versionCatalogs {

        create("libs") {
            val jetbrainsAnnotations: String by settings
            library("jetbrains-annotations", "org.jetbrains:annotations:$jetbrainsAnnotations")

            val junit: String by settings
            library("junit-api", "org.junit.jupiter:junit-jupiter-api:$junit")
            library("junit-engine", "org.junit.jupiter:junit-jupiter-engine:$junit")
            library("junit-params", "org.junit.jupiter:junit-jupiter-params:$junit")

            val googleGson: String by settings
            library("google-gson", "com.google.code.gson:gson:$googleGson")
        }

    }
}
