# Scania

一个模仿 [Now in Android](https://github.com/android/nowinandroid) 架构搭建的、开箱即用的 Android 项目模板。

项目只包含基础通用模块和一点点 demo 代码，不包含具体业务，你可以直接在这个框架上开发你自己的应用。

## 技术栈

- 100% Kotlin + Jetpack Compose（Material 3）
- **Kotlin 2.3.20** + **AGP 9.1.1** + **Gradle 9.4.0** + JDK 17
- **KSP2 (2.3.6)** 替代 KSP1（AGP 9 起必需）
- **Hilt 2.59.2**（已原生支持 AGP 9）
- AGP 9 **built-in Kotlin**（不再手动 apply `kotlin-android` 插件）
- 模块化 + `build-logic` convention plugins（nowinandroid 风格）
- `gradle/libs.versions.toml` 版本目录
- Jetpack Navigation Compose 类型安全导航（基于 `kotlinx.serialization`）
- Room + DataStore 本地持久化
- Retrofit + OkHttp + kotlinx.serialization 网络层
- Coroutines + Flow 异步编程
- Coil 图片加载；Timber 日志

## 目录结构

```
Scania/
├── app/                           # Application / MainActivity / NavHost
├── build-logic/convention/        # 9 个 convention plugin，多模块统一配置
├── core/
│   ├── common/                    # 协程调度器、ApplicationScope、Result
│   ├── data/                      # 仓库层（Repository） + DataModule
│   ├── database/                  # Room 数据库
│   ├── datastore/                 # DataStore 偏好设置
│   ├── designsystem/              # Compose 主题/颜色/排版/通用组件
│   ├── domain/                    # UseCase 示例
│   ├── model/                     # 纯 Kotlin 领域模型（JVM library）
│   ├── network/                   # Retrofit API + NetworkModule
│   └── ui/                        # 跨 feature 通用 UI 组件
├── feature/
│   ├── home/
│   │   ├── api/                   # 只有导航 key（HomeRoute） + navigateToHome
│   │   └── impl/                  # HomeScreen / HomeViewModel / homeScreen(NavGraph)
│   └── settings/
│       ├── api/                   # SettingsRoute + navigateToSettings
│       └── impl/                  # SettingsScreen / SettingsViewModel / settingsScreen
├── gradle/libs.versions.toml
└── settings.gradle.kts
```

### Feature 模块为何拆 `api` / `impl`？

参考 nowinandroid 的 [ModularizationLearningJourney](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md)：

- **`api`**：只存放导航 key 以及跳转扩展函数；体量极小，几乎不会变动。
- **`impl`**：存放真实的 UI、ViewModel、Hilt DI；改动频繁。

好处：

1. **构建增量友好**：`impl` 变更时，依赖它的模块不需要重新编译（只有 `api` 变动才会波及下游）；
2. **防止循环依赖**：feature 之间跳转时，`feature:A:impl` 只依赖 `feature:B:api`，绝不依赖 `feature:B:impl`；
3. **多 App 复用**：benchmark、catalog 等独立 App 可以只依赖 feature 的 `api` 来组装所需模块。

### 模块依赖图

```
            ┌─────────────┐
            │    :app     │
            └──────┬──────┘
          ┌────────┴──────────┐
          ▼                   ▼
  feature:home:impl    feature:settings:impl
        │ │                  │
        │ └─────┐            │
        │       ▼            │
        │  feature:settings:api
        ▼                    │
  feature:home:api           │
        └─────┬──────────────┘
              │
     ┌────────┴────────┐
     ▼                 ▼
 :core:ui / :core:domain / :core:designsystem / ...
              │
              ▼
          :core:data
     ┌────────┼────────┬──────────┐
     ▼        ▼        ▼          ▼
 database  network  datastore   (etc.)
              │
              ▼
          :core:model
              │
              ▼
          :core:common
```

## 运行

前置要求：

- Android Studio Panda 或更新（AGP 9.1 需要 Android Studio 2025.3.x 及以上）
- JDK 17

步骤：

1. 在 Android Studio 打开本项目根目录；
2. 首次 Sync 会自动下载全部依赖；
3. 连接设备或启动模拟器，点击 Run 即可。

命令行构建：

```bash
./gradlew assembleDebug      # 生成 debug APK
./gradlew lintDebug          # 代码检查
./gradlew testDebugUnitTest  # 单元测试
```

## Convention Plugins

`build-logic/convention` 下提供了统一的 Gradle 插件：

| 插件 ID                                      | 作用                                           |
| -------------------------------------------- | ---------------------------------------------- |
| `scania.android.application`           | Application 模块基础配置                       |
| `scania.android.application.compose`   | Application 模块启用 Compose                   |
| `scania.android.library`               | Library 模块基础配置                           |
| `scania.android.library.compose`       | Library 模块启用 Compose                       |
| **`scania.android.feature.api`**       | feature `api` 模块（只含路由 key）             |
| **`scania.android.feature.impl`**      | feature `impl` 模块（Compose + Hilt + Nav）    |
| `scania.android.hilt`                  | 添加 Hilt 依赖和 KSP                           |
| `scania.android.room`                  | 添加 Room 依赖和 KSP                           |
| `scania.jvm.library`                   | 纯 JVM library                                 |

## AGP 9 迁移要点（本模板已适配，新同学请注意）

相比 AGP 8，以下几点是最容易踩坑的：

1. **built-in Kotlin**：AGP 9 直接提供 Kotlin 编译能力，不再允许手动 apply `org.jetbrains.kotlin.android`。本项目的 convention plugin 已移除该 apply。
2. **CommonExtension 去参数化**：`CommonExtension<*,*,*,*,*,*>` → `CommonExtension`。
3. **DSL 方法形式被移除**：以前的 `defaultConfig { ... }`、`compileOptions { ... }` 等 lambda 块在 AGP 9 里只剩属性访问，因此 convention plugin 中一律使用 `xxx.apply { ... }`。
4. **Library 不再有 `targetSdk`**：AGP 9 起 library 模块的 `defaultConfig.targetSdk` 被删除，由消费方 App 决定即可。
5. **KSP1 停止支持**：必须使用 KSP2（独立版本号，和 Kotlin 不再强绑定），本项目用 `2.3.6`。
6. **Hilt 2.59+**：Hilt 2.58 仅为临时兼容，2.59.2 正式要求 AGP 9.0+。
7. **`android.enableJetifier=false`**：AGP 9 与 Hilt 2.59 组合下，若不关闭 Jetifier，可能出现 `ComponentTreeDeps` 找不到。本项目已显式设置。
8. **Kotlin 2.3 `-jvm-default` 值变更**：从 `all` → `enable`；已在 convention plugin 中适配。
9. **`kotlin.incremental.useClasspathSnapshot` 已 deprecated**：Kotlin 2.3 起默认启用，已从 `gradle.properties` 移除。

更多背景参考：
- [AGP 9.0 release notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes)
- [AGP 9.1 release notes](https://developer.android.com/build/releases/agp-9-1-0-release-notes)
- [nowinandroid AGP 9 PR](https://github.com/android/nowinandroid/pull/1959)

## 业务开发指引

### 1. 新增一个 feature 模块

1. 在 `feature/` 下建目录 `feature/yourfeature/api/` 和 `feature/yourfeature/impl/`；
2. 在 `settings.gradle.kts` 加上：

    ```kotlin
    include(":feature:yourfeature:api")
    include(":feature:yourfeature:impl")
    ```

3. `feature/yourfeature/api/build.gradle.kts`：

    ```kotlin
    plugins {
        alias(libs.plugins.scania.android.feature.api)
    }
    android {
        namespace = "com.scania.android.feature.yourfeature.api"
    }
    ```

4. `api` 里写路由 key：

    ```kotlin
    @Serializable
    data object YourFeatureRoute
    
    fun NavController.navigateToYourFeature(navOptions: NavOptions? = null) =
        navigate(route = YourFeatureRoute, navOptions = navOptions)
    ```

5. `feature/yourfeature/impl/build.gradle.kts`：

    ```kotlin
    plugins {
        alias(libs.plugins.scania.android.feature.impl)
    }
    android {
        namespace = "com.scania.android.feature.yourfeature.impl"
    }
    dependencies {
        implementation(projects.feature.yourfeature.api)
        // 如需跳到其他 feature：
        // implementation(projects.feature.someother.api)
    }
    ```

6. 在 `impl` 中仿照 `feature/home/impl` 编写 Screen、ViewModel、NavGraphBuilder 扩展；
7. 在 `app/build.gradle.kts` 里加 `implementation(projects.feature.yourfeature.impl)`，并在 `AppNavHost.kt` 里挂上路由。

### 2. 定义新的数据模型 / Repository / Network API

见上一版 README 思路，此次未变动：

- 纯模型放 `core/model`；
- 数据库 Entity 放 `core/database`；
- 网络 DTO 放 `core/network`；
- Repository 接口放 `core/data/repository/`，实现同目录，通过 `core/data/di/DataModule.kt` 用 `@Binds` 绑定；
- Retrofit 接口放 `core/network/`，用 `core/network/di/NetworkModule.kt` 暴露。

### 3. 主题/组件

- 颜色/排版/主题统一在 `core/designsystem/theme/`；
- 全局通用 Compose 组件放 `core/designsystem/component/`；
- 跨 feature 使用的业务相关组件放 `core/ui/`。

## License

本仓库为模板项目，你可以随意使用、修改、发布。
