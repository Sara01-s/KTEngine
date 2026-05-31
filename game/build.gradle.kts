plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":engine"))
    implementation(project(":editor"))
}