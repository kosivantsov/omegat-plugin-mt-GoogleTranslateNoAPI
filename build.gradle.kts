plugins {
    java
    distribution
    maven
    id("org.omegat.gradle") version "1.5.3"
}

repositories {
    mavenCentral()
    mavenLocal()
}

version = "1.1.2"

omegat {
    version = "5.7.0"
    pluginClass = "net.libretraduko.omegat.GoogleTranslateWithoutApiKey"
}

dependencies {
    implementation("commons-io:commons-io:2.7")
    implementation("commons-lang:commons-lang:2.6")
}



distributions {
    main {
        contents {
            from(tasks["jar"], "README.md", "COPYING")
        }
    }
}
