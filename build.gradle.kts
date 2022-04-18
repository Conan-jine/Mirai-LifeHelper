plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.10.1"
}

group = "org.lifehelper"
version = "1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("com.alibaba:fastjson:1.2.79")
}
