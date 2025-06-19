# Thresh 动态化框架集成指南

## 🎯 概述

**Thresh** 是满帮集团开源的基于Flutter的跨平台动态化方案。本指南将帮助您在IdeaMemo项目中完整集成Thresh框架。

## 🚀 集成步骤

### 1. 环境准备

确保您的开发环境满足以下要求：

- ✅ **Flutter SDK**: 3.7.0 或更高版本
- ✅ **Android Studio**: 最新版本
- ✅ **Java**: 17 (已配置)
- ✅ **Gradle**: 8.9 (已配置)
- ✅ **AGP**: 8.6.0 (已配置)

### 2. 创建Flutter模块

在项目根目录创建Flutter模块：

```bash
flutter create --template=module thresh_flutter
cd thresh_flutter
```

### 3. 配置Thresh依赖

修改 `thresh_flutter/pubspec.yaml`:

```yaml
name: thresh_flutter
description: IdeaMemo Flutter Module with Thresh

version: 1.0.0+1

environment:
  sdk: '>=2.19.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.2
  
  # Thresh 动态化框架
  thresh:
    git:
      url: https://github.com/ymm-tech/thresh.git
      path: thresh

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^2.0.0

flutter:
  uses-material-design: true
  
  module:
    androidX: true
    androidPackage: com.example.thresh_flutter
    iosBundleIdentifier: com.example.threshFlutter
```

### 4. 获取Flutter依赖

```bash
cd thresh_flutter
flutter pub get
```

### 5. 配置Android集成

#### 5.1 修改 `settings.gradle.kts`

```kotlin
// ... 现有配置 ...

// 添加Flutter模块配置
val flutterProjectRoot = File(settingsDir, "thresh_flutter")
val localProperties = File(flutterProjectRoot, ".android/local.properties")

if (localProperties.exists()) {
    val properties = java.util.Properties()
    localProperties.inputStream().use { properties.load(it) }
    val flutterSdkPath = properties.getProperty("flutter.sdk")
    
    if (flutterSdkPath != null) {
        include(":flutter")
        project(":flutter").projectDir = File(flutterProjectRoot, ".android/Flutter")
    }
}
```

#### 5.2 修改 `app/build.gradle.kts`

在dependencies部分添加：

```kotlin
dependencies {
    // ... 现有依赖 ...
    
    // Flutter 模块依赖
    implementation(project(":flutter"))
}
```

### 6. 创建Thresh管理类

创建 `app/src/main/java/com/ldlywt/note/utils/ThreshHelper.kt`:

```kotlin
package com.ldlywt.note.utils

import android.content.Context
import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs

/**
 * Thresh工具类，用于启动和管理Thresh Flutter页面
 */
object ThreshHelper {

    /**
     * 启动Thresh页面，使用预热的Flutter引擎
     * @param context 上下文
     * @param route 初始路由，可选
     */
    fun launchThreshPage(context: Context, route: String? = null) {
        val intent = if (route != null) {
            // 如果指定了路由，使用新引擎并设置初始路由
            FlutterActivity
                .withNewEngine()
                .initialRoute(route)
                .build(context)
        } else {
            // 使用缓存的Flutter引擎
            FlutterActivity
                .withCachedEngine("thresh_engine")
                .build(context)
        }
        
        context.startActivity(intent)
    }

    /**
     * 启动指定的Thresh JS页面
     * @param context 上下文
     * @param jsPath JS文件路径
     */
    fun launchThreshJSPage(context: Context, jsPath: String) {
        val intent = FlutterActivity
            .withCachedEngine("thresh_engine")
            .destroyEngineWithActivity(FlutterActivityLaunchConfigs.DestroyEngineWithActivity.NO)
            .build(context)
            
        // 可以通过Intent传递JS路径参数
        intent.putExtra("js_path", jsPath)
        context.startActivity(intent)
    }
}
```

### 7. 初始化Flutter引擎

修改 `app/src/main/java/com/ldlywt/note/App.kt`:

```kotlin
package com.ldlywt.note

import android.app.Application
import androidx.lifecycle.asLiveData
import com.ldlywt.note.backup.BackupScheduler
import com.ldlywt.note.utils.SettingsPreferences
import com.ldlywt.note.utils.SharedPreferencesUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Flutter和Thresh相关导入
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

@HiltAndroidApp
class App : Application() {

    lateinit var flutterEngine: FlutterEngine

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化Flutter引擎
        initFlutterEngine()
        
        // ... 其他现有代码 ...
    }
    
    private fun initFlutterEngine() {
        // 创建Flutter引擎实例
        flutterEngine = FlutterEngine(this)
        
        // 执行Dart入口点，预热Flutter引擎
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        
        // 将Flutter引擎缓存起来，供后续使用
        FlutterEngineCache
            .getInstance()
            .put("thresh_engine", flutterEngine)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
```

### 8. 配置AndroidManifest.xml

在 `app/src/main/AndroidManifest.xml` 中添加FlutterActivity声明：

```xml
<application
    android:name=".App"
    ... >

    <!-- 现有Activity配置 -->
    
    <!-- Flutter Activity 配置 -->
    <activity
        android:name="io.flutter.embedding.android.FlutterActivity"
        android:exported="false"
        android:launchMode="singleTop"
        android:theme="@style/LaunchTheme"
        android:configChanges="orientation|keyboardHidden|keyboard|screenSize|smallestScreenSize|locale|layoutDirection|fontScale|screenLayout|density|uiMode"
        android:hardwareAccelerated="true"
        android:windowSoftInputMode="adjustResize" />

</application>
```

### 9. 创建Thresh主页面

修改 `thresh_flutter/lib/main.dart`:

```dart
import 'package:flutter/material.dart';
import 'package:thresh/thresh.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'IdeaMemo Thresh',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const ThreshApp(),
    );
  }
}

class ThreshApp extends StatefulWidget {
  const ThreshApp({super.key});

  @override
  State<ThreshApp> createState() => _ThreshAppState();
}

class _ThreshAppState extends State<ThreshApp> {
  @override
  void initState() {
    super.initState();
    // 初始化Thresh引擎
    initThreshEngine();
  }

  void initThreshEngine() {
    // 初始化Thresh配置
    Thresh.init(
      debugMode: true,
      jsLogEnabled: true,
      // 其他配置...
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('IdeaMemo Thresh'),
        backgroundColor: Colors.blue,
      ),
      body: const ThreshPageView(
        pageName: 'home',  // Thresh页面名称
        // 其他配置...
      ),
    );
  }
}
```

### 10. 在原生页面中使用

在您的原生Android页面中调用Thresh页面：

```kotlin
// 在MainActivity或其他Activity中
private fun openThreshPage() {
    ThreshHelper.launchThreshPage(this, "/thresh-page")
}

// 或者在Compose中
@Composable
fun SomeComposePage() {
    val context = LocalContext.current
    
    Button(
        onClick = { 
            ThreshHelper.launchThreshPage(context, "/thresh-demo")
        }
    ) {
        Text("打开Thresh页面")
    }
}
```

## 🔧 问题排查

### 常见问题

1. **构建失败 - Gradle版本问题**
   - 确保使用Gradle 8.9+
   - 确保AGP 8.6.0+
   - 确保Java 17

2. **Flutter模块找不到**
   - 检查`thresh_flutter/.android/local.properties`是否存在
   - 确保Flutter SDK路径正确

3. **Thresh依赖问题**
   - 使用官方仓库：`https://github.com/ymm-tech/thresh.git`
   - 检查网络连接

### 调试方法

```bash
# 检查Flutter配置
flutter doctor

# 重新获取依赖
cd thresh_flutter && flutter pub get

# 清理重建
./gradlew clean app:assembleDebug
```

## 📝 下一步

1. **开发JS页面**: 使用Thresh提供的JS/TS开发模板创建动态页面
2. **热更新**: 配置Thresh的热更新服务
3. **性能优化**: 根据项目需要优化Flutter引擎启动和内存使用
4. **测试**: 编写单元测试和集成测试

## 🔗 相关链接

- [Thresh官方文档](https://github.com/ymm-tech/thresh)
- [Flutter模块集成指南](https://flutter.dev/docs/development/add-to-app)
- [Android Gradle插件文档](https://developer.android.com/studio/build)

---

**注意**: 本指南基于当前项目配置编写，实际使用时可能需要根据Thresh版本和项目需求进行调整。 