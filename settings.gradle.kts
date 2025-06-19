pluginManagement {
    repositories {
        google() // 优先使用Google仓库
        mavenCentral() 
        gradlePluginPortal()
        maven ( "https://maven.aliyun.com/repository/gradle-plugin/" ) // Gradle 插件镜像
        maven("https://www.jitpack.io/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven ( "https://maven.aliyun.com/repository/public/" ) // 阿里云公共仓库
        maven ( "https://maven.aliyun.com/repository/google/" ) // Google 镜像
    }
}

rootProject.name = "Note"
include(":app")

// 暂时注释掉Flutter模块配置
// 手动配置Flutter模块
// val flutterProjectRoot = File(settingsDir, "thresh_flutter")
// val localProperties = File(flutterProjectRoot, ".android/local.properties")

// if (localProperties.exists()) {
//     val properties = java.util.Properties()
//     localProperties.inputStream().use { properties.load(it) }
//     val flutterSdkPath = properties.getProperty("flutter.sdk")
//     
//     if (flutterSdkPath != null) {
//         include(":flutter")
//         project(":flutter").projectDir = File(flutterProjectRoot, ".android/Flutter")
//         
//         // 应用Flutter模块插件加载器
//         apply(from = "$flutterSdkPath/packages/flutter_tools/gradle/module_plugin_loader.gradle")
//     }
// }
