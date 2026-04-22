import com.scania.android.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * feature:*:api 模块的 convention plugin。
 *
 * 职责：仅包含路由 key（@Serializable 对象/数据类）和少量模型。
 * - 基于 Android library（避免引入 Compose/Hilt 带来的重量级依赖）；
 * - 允许使用 kotlinx.serialization、Navigation 通用类型；
 * - 不应依赖任何其他 feature 模块、也不应依赖 core:data/ui 等重量级模块。
 *
 * 其他 feature 的 `impl` 需要跨 feature 跳转时，只依赖目标 feature 的 `api`。
 */
class AndroidFeatureApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("scania.android.library")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                add("implementation", libs.findLibrary("kotlinx.serialization.json").get())
                add("implementation", libs.findLibrary("androidx.navigation.compose").get())
            }
        }
    }
}
