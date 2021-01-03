
plugins {
    `java-library`
}

dependencies {
    implementation(project(":config"))
    implementation(project(":gitrawdata"))
    testImplementation("junit:junit:4.+")
    implementation(group="org.freemarker", name="freemarker", version="2.3.30")
}
sourceSets {
    main {
        resources {
            srcDirs ("src/main/resources")
        }
    }
}


