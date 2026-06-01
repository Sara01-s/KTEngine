plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    api(project(":engine"))

    val imguiVersion = "1.86.11"
    api("io.github.spair:imgui-java-binding:$imguiVersion")
    api("io.github.spair:imgui-java-lwjgl3:$imguiVersion")

    implementation("io.github.spair:imgui-java-natives-windows:${imguiVersion}")
}