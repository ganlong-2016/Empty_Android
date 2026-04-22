# EmptyAndroid

一个模仿 [Now in Android](https://github.com/android/nowinandroid) 架构搭建的、开箱即用的 Android 项目模板。

项目只包含基础通用模块和一点点 demo 代码，不包含具体业务，你可以直接在这个框架上开发你自己的应用。

## 技术栈

- 100% Kotlin + Jetpack Compose（Material 3）
- Kotlin 2.1.20 + AGP 8.13.2 + Gradle 8.14.4 + JDK 17
- 模块化 + `build-logic` convention plugins（统一管理多模块配置）
- `gradle/libs.versions.toml` 版本目录（Version Catalog）
- Hilt 依赖注入
- Jetpack Navigation Compose 类型安全导航（基于 `kotlinx.serialization`）
- Room + DataStore 本地持久化
- Retrofit + OkHttp + kotlinx.serialization 网络层
- Coroutines + Flow 异步编程
- Coil 图片加载
- Timber 日志
- Lint & Kotlin 编译通过（0 error）

## 目录结构

```
EmptyAndroid/
├── app/                       # 应用主模块：Application / MainActivity / NavHost
├── build-logic/convention/    # 多模块统一构建规则（nowinandroid 风格）
├── core/
│   ├── common/                # 通用协程调度器、ApplicationScope、Result 等
│   ├── data/                  # 仓库层（Repository）+ DataModule
│   ├── database/              # Room 数据库、DAO、Entity
│   ├── datastore/             # DataStore（用户偏好设置）
│   ├── designsystem/          # Compose 主题、颜色、排版、通用组件
│   ├── domain/                # UseCase 示例
│   ├── model/                 # 纯 Kotlin 领域模型（JVM library）
│   ├── network/               # Retrofit API + NetworkModule
│   └── ui/                    # 跨 feature 的 UI 组件（如 DemoItemCard）
├── feature/
│   ├── home/                  # 示例首页：读取仓库数据 + 增删操作
│   └── settings/              # 示例设置页：主题切换 + 动态取色
├── gradle/libs.versions.toml  # 版本目录
└── settings.gradle.kts
```

### 模块分层

参考 nowinandroid：

```
          ┌─────────────┐
          │    :app     │
          └──────┬──────┘
                 │
        ┌────────┴────────┐
        ▼                 ▼
  :feature:home    :feature:settings
        │                 │
        └────────┬────────┘
                 │
    ┌────────────┼────────────┐
    ▼            ▼            ▼
:core:ui   :core:domain   :core:designsystem
              │
              ▼
          :core:data
              │
      ┌───────┼────────┬──────────┐
      ▼       ▼        ▼          ▼
:core:database  :core:network  :core:datastore
      │       │        │          │
      └───────┴────┬───┴──────────┘
                   ▼
              :core:model
                   │
                   ▼
              :core:common
```

## 运行

前置要求：

- Android Studio（任意近期版本，推荐 Ladybug 或更新）
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
| `emptyandroid.android.application`           | Application 模块基础配置                       |
| `emptyandroid.android.application.compose`   | Application 模块启用 Compose                   |
| `emptyandroid.android.library`               | Library 模块基础配置                           |
| `emptyandroid.android.library.compose`       | Library 模块启用 Compose                       |
| `emptyandroid.android.feature`               | feature 模块通用配置（包含 DI / Compose / 路由）|
| `emptyandroid.android.hilt`                  | 添加 Hilt 依赖和 KSP                           |
| `emptyandroid.android.room`                  | 添加 Room 依赖和 KSP                           |
| `emptyandroid.jvm.library`                   | 纯 JVM library                                 |

在新模块里只需一两行就能完成配置，比如 feature 模块：

```kotlin
plugins {
    alias(libs.plugins.emptyandroid.android.feature)
}

android {
    namespace = "com.empty.android.feature.mynewfeature"
}
```

## 业务开发指引

### 1. 新增一个 feature 模块

1. 在 `feature/` 下建目录 `feature/yourfeature/`；
2. 在 `settings.gradle.kts` 里 `include(":feature:yourfeature")`；
3. 创建 `build.gradle.kts`：

    ```kotlin
    plugins {
        alias(libs.plugins.emptyandroid.android.feature)
    }

    android {
        namespace = "com.empty.android.feature.yourfeature"
    }
    ```

4. 仿照 `feature/home` 写 `ViewModel` / `Screen` / `Navigation`；
5. 在 `app/src/main/kotlin/com/empty/android/app/navigation/AppNavHost.kt` 中添加路由。

### 2. 定义新的数据模型

- 纯模型放 `core/model`（JVM library，零 Android 依赖）；
- 数据库 Entity 放 `core/database`（带 `@Entity` 注解）；
- 网络 DTO 放 `core/network`（带 `@Serializable` 注解）；
- 跨层数据转换使用 `asExternalModel()` / `asEntity()` 扩展函数。

### 3. 新增 Repository

1. 接口放 `core/data/repository/`；
2. 实现同目录下，构造注入所需的 `Dao` / `Api` / `Dispatcher`；
3. 在 `core/data/di/DataModule.kt` 中用 `@Binds` 绑定接口到实现。

### 4. 新增网络接口

- Retrofit 接口放 `core/network/`；
- 对应 DTO 放 `core/network/model/`；
- 在 `core/network/di/NetworkModule.kt` 中通过 `retrofit.create(YourApi::class.java)` 暴露出来。

### 5. 主题/组件

- 颜色/排版/主题统一在 `core/designsystem/theme/`；
- 全局通用 Compose 组件放 `core/designsystem/component/`；
- 和业务相关、跨 feature 使用的组件放 `core/ui/`。

## 常见任务

- **切换 Base URL**：改 `core/network/build.gradle.kts` 的 `BASE_URL` buildConfigField（建议区分 debug/release）。
- **加入新库**：编辑 `gradle/libs.versions.toml`，然后在 convention plugin 或模块里 `implementation(libs.xxx)`。
- **迁移 Room schema**：升级版本号并提供 `Migration`，或者像模板里一样用 `fallbackToDestructiveMigration`（开发期间）。

## License

本仓库为模板项目，你可以随意使用、修改、发布。
