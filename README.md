# Dark Window Bar — Minecraft 26.2 Fabric port

[下载 / Releases](https://github.com/hsll233/DarkWindowBar/releases) · [原项目 / Upstream](https://github.com/txnimc/DarkWindowBar)

基于 **Txni 的 Dark Window Bar** 源码适配的非官方 26.2 分支。将 Windows 下 Minecraft 的窗口标题栏改为黑色，标题文字为白色；保留最小化、最大化和关闭按钮。

## 安装

1. 使用 Minecraft **26.2**、Fabric Loader **0.19+**、Fabric API 和 **Java 25**。
2. 从 Releases 下载 `darkwindowbar-fabric-26.2-1.0.0-port.1.jar`，放进对应实例的 `mods` 文件夹。
3. 如果已装其他版本的 Dark Window Bar，先移除旧 JAR，避免重复模组 ID。
4. 重启游戏。模组默认启用，在窗口模式或最大化窗口下生效。

Windows 11 可设置纯黑背景和白色文字。Windows 10 的支持取决于系统版本，可能只能使用深色标题栏。真正全屏时没有标题栏，因此不会显示这项变化。

配置文件为 `config/darkwindowbar.properties`。设置 `enabled=false` 后重启即可关闭。卸载时退出游戏并移除 JAR。

模组只设置 Minecraft 自身窗口的 DWM 外观，不修改全局系统主题、帧率、光影、存档、鼠标或窗口焦点。

## 从源码编译

需要 **PowerShell 7** 和 **JDK 21 或更高版本**：

```powershell
./build-local.ps1
```

脚本从 Fabric Maven 和 Maven Central 下载并校验编译依赖。输出为 `build-local/darkwindowbar-fabric-26.2-1.0.0-port.1.jar`。也可通过 `-MinecraftRoot <.minecraft目录>` 使用游戏已安装的依赖。

游戏需要 Java 25；编译使用 `--release 21` 只为了保持独立编译工具兼容性。该构建不依赖本机游戏路径或 Minecraft JAR，也不需要重新映射。

GitHub Actions 使用同一脚本构建。原来的多平台 Gradle 模板保留为上游参考，26.2 适配请使用上述脚本。

## 适配和验证

- 更新为 `Window.handle()`，使用 Fabric `CLIENT_STARTED` 事件在游戏线程上设置外观。
- 保留原来的 JNA DWM 接口；去掉旧版通过改变窗口大小刷新标题栏的方式。
- Windows 11 设置黑底白字，旧 Windows 10 回退到深色模式。
- 在隐藏测试窗口上验证 Windows 接受设置并读取到深色标志；使用实际 Minecraft 26.2 和 Fabric API 验证启动事件注册。
- 没有自动运行整套 Minecraft GUI 测试，不同 Windows 主题/版本上的视觉效果仍需实际使用确认。

原作者 **Txni**，上游源码和许可证保留；本 fork 不是作者官方发布。许可证以仓库 [LICENSE.md](LICENSE.md) 的原文为准。适配记录见 [PORT-NOTES.md](PORT-NOTES.md)。
