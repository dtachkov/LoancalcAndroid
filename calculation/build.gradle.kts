plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

sourceSets {
    test {
        resources.srcDir("src/test/res")
    }
}

dependencies {
    implementation(libs.gson)
    testImplementation(libs.junit)
}
