
plugins {
    id("java-library")
}

apply(from = "$rootDir/gradle/kotlin.gradle")
apply(from = "$rootDir/gradle/publish.gradle")
apply(from = "$rootDir/gradle/dokka.gradle")
apply(from = "$rootDir/gradle/native.gradle")
apply(from = "$rootDir/gradle/detekt.gradle")

description = "Hexagon JSON serialization format (using DSL-JSON)."

dependencies {
    val dslJsonVersion = properties["dslJsonVersion"]

    "api"(project(":serialization"))
    "api"("com.dslplatform:dsl-json-java8:$dslJsonVersion")

    "testImplementation"("org.jetbrains.kotlin:kotlin-reflect")
    "testImplementation"(project(":serialization_test"))
}
