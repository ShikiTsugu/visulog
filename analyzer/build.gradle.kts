
plugins {
    `java-library`
}

dependencies {
    implementation( group= "org.freemarker", name= "freemarker", version= "2.3.30")
    implementation(project(":config"))
    implementation(project(":gitrawdata"))
    implementation("org.testng:testng:7.1.0")
    testImplementation("junit:junit:4.+")
}


