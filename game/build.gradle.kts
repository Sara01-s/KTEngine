plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("game.MainKt")
}

dependencies {
    implementation(project(":engine"))
}