plugins {
    kotlin("jvm")
    id("com.google.protobuf")
}

dependencies {
    implementation("io.grpc:grpc-okhttp:1.68.0")
    implementation("io.grpc:grpc-stub:1.68.0")
    implementation("io.grpc:grpc-protobuf-lite:1.68.0")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    api("com.google.protobuf:protobuf-kotlin-lite:3.25.3")
    api("com.google.protobuf:protobuf-javalite:3.25.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    testImplementation("io.mockk:mockk:1.13.12")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.68.0"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc") {
                    option("lite")
                }
                create("grpckt") {
                    option("lite")
                }
            }
            it.builtins {
                getByName("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
