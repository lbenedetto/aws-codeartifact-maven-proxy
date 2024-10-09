plugins {
    application
    kotlin("jvm")
    id("com.google.cloud.tools.jib") version "3.4.3"
}


dependencies {
    implementation(project(":aws-codeartifact-maven-proxy"))
    implementation(libs.joptsimple)
    implementation(libs.bundles.log4j)
}


application {
    applicationName = "aws-codeartifact-maven-proxy"
    mainClass.set("org.unbrokendome.awscodeartifact.mavenproxy.cli.CodeArtifactMavenProxyCli")
}


tasks.named<Jar>("jar") {
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
}


tasks.named<Tar>("distTar") {
    compression = Compression.GZIP
    archiveExtension.set("tar.gz")
}


jib {
    from {
        image = "eclipse-temurin:11.0.24_8-jre-focal"
    }
    to {
        image = "unbroken-dome/aws-codeartifact-maven-proxy"
        tags = setOf(project.version.toString())
    }
}
