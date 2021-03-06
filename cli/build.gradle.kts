
plugins {
    java
    application
}

application.mainClass.set("up.visulog.cli.CLILauncher")

dependencies {
    implementation(project(":analyzer"))
    implementation(project(":config"))
    implementation(project(":gitrawdata"))
    testImplementation("junit:junit:4.+")
    implementation(group="commons-cli", name= "commons-cli", version="1.4")
    implementation(group="org.json", name="json", version="20201115")
}


