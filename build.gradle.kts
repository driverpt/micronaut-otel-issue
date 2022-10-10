plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.6.2"
    id("io.micronaut.aot") version "3.6.2"
}

version = "0.1"
group = "com.example"

val otelAlphaVersion = "1.15.0-alpha"
val otelVersion = "1.15.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("jakarta.annotation:jakarta.annotation-api")

    // OTEL
    implementation(enforcedPlatform("io.opentelemetry:opentelemetry-bom:$otelVersion"))
    implementation(enforcedPlatform("io.opentelemetry:opentelemetry-bom-alpha:$otelAlphaVersion"))

    constraints {
        implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:$otelAlphaVersion")
        implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-semconv:$otelAlphaVersion")
        implementation("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:$otelAlphaVersion")
        implementation("io.opentelemetry.instrumentation:opentelemetry-grpc-1.6:$otelAlphaVersion")
        implementation("io.opentelemetry.instrumentation:opentelemetry-aws-sdk-2.2:$otelAlphaVersion")
        implementation("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:$otelAlphaVersion")
    }

    implementation("io.micronaut.tracing:micronaut-tracing-opentelemetry-grpc")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.opentelemetry:opentelemetry-extension-aws")
    implementation("io.opentelemetry:opentelemetry-sdk-extension-aws")
    implementation("io.opentelemetry.instrumentation:opentelemetry-aws-sdk-2.2")

    // Logging
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.2")
    runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0")

    compileOnly("org.graalvm.nativeimage:svm")

    implementation("io.micronaut:micronaut-validation")

    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass.set("com.example.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
    targetCompatibility = JavaVersion.toVersion("11")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("lambda")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    args(
        "-XX:MaximumHeapSizePercent=80",
        "-Dio.netty.allocator.numDirectArenas=0",
        "-Dio.netty.noPreferDirect=true"
    )
}



