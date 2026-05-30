plugins {
    kotlin("jvm") version "2.3.20"
    `java-library`
}

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.3"
val lwjglNatives = "natives-windows"

dependencies {
    val lwjgl = platform("org.lwjgl:lwjgl-bom:$lwjglVersion")
    api(lwjgl)

    api("org.lwjgl:lwjgl")
    api("org.lwjgl:lwjgl-glfw")
    api("org.lwjgl:lwjgl-opengl")
    api("org.lwjgl:lwjgl-stb")
    api("org.lwjgl:lwjgl-openal")
    api("org.lwjgl:lwjgl-assimp")

    runtimeOnly("org.lwjgl:lwjgl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-stb::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-openal::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-assimp::natives-windows")

    api("io.github.kotlin-graphics:glm:0.9.9.1-12")
    api("com.google.code.gson:gson:2.13.1")
}

kotlin {
    jvmToolchain(17)
}