import com.moowork.gradle.node.npm.NpmTask

plugins {
    base
    id("com.moowork.node") version "1.3.1"
}

node {
    version = "10.14.1"
    npmVersion = "6.4.1"
    download = true
}

tasks {
    val npmInstall by getting

    val npmRunBuild by creating(NpmTask::class) {
        dependsOn(npmInstall)
        setArgs(listOf("run","build"))
    }

    val packageNpmApp by creating(Zip::class) {
        dependsOn(npmRunBuild)
        baseName = "npm-app"
        extension = "jar"
        entryCompression =ZipEntryCompression.STORED
        destinationDir = file("$projectDir/build_packageNpmApp")
        from("dist") {
            into("static")
        }
    }

    val assemble by getting {
        dependsOn(packageNpmApp)
    }

    val clean by getting {
        delete( packageNpmApp.archivePath)
    }

    configurations {
        create("npmResources")
    }

    configurations.default.get().extendsFrom(configurations.getByName("npmResources"))
    
    artifacts {
        add("npmResources", packageNpmApp.archivePath){
            builtBy(packageNpmApp)
            type = "jar"
        }
    }
}