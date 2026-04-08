# 应用分身 - App Cloner

一个可以安装在安卓手机上的应用分身工具，支持后台运行保活功能。

## 功能特性

- 应用列表展示：显示手机上安装的所有第三方应用
- 应用分身：为选中的应用创建分身
- 分身管理：查看和启动已创建的分身应用
- 后台保活：通过前台服务保持应用在后台运行，不易被系统杀死
- 开机自启：设备重启后自动启动保活服务

## 项目结构

```
phone-clice/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/phoneclice/appcloner/
│   │       │   ├── AppClonerApplication.java    # 应用入口
│   │       │   ├── MainActivity.java             # 主界面
│   │       │   ├── AppInfo.java                  # 应用信息模型
│   │       │   ├── AppAdapter.java               # 应用列表适配器
│   │       │   ├── AppClonerHelper.java          # 分身核心功能
│   │       │   ├── KeepAliveService.java         # 后台保活服务
│   │       │   ├── BootReceiver.java             # 开机自启接收器
│   │       │   └── ScreenReceiver.java           # 屏幕状态监听器
│   │       ├── res/
│   │       │   ├── layout/                       # 布局文件
│   │       │   ├── values/                       # 资源文件
│   │       │   └── drawable/                     # 图片资源
│   │       └── AndroidManifest.xml               # 清单文件
│   └── build.gradle                               # app模块配置
├── build.gradle                                   # 项目配置
├── settings.gradle                                # 项目设置
└── gradle.properties                              # Gradle属性
```

## 构建说明

### 环境要求

- Android Studio Arctic Fox 或更高版本
- JDK 8 或更高版本
- Android SDK API 24 - 34

### 构建步骤

1. 克隆或下载项目
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 连接 Android 设备或启动模拟器
5. 点击运行按钮或使用命令行构建：

```bash
./gradlew assembleDebug
```

APK 文件将生成在 `app/build/outputs/apk/debug/` 目录下。

## 使用说明

1. **首次启动**：应用会自动请求必要的权限
2. **查看应用列表**：在「应用列表」标签页中查看所有已安装的第三方应用
3. **创建分身**：点击应用右侧的「分身」按钮创建应用分身
4. **查看分身**：切换到「已分身应用」标签页查看所有分身
5. **启动分身**：点击分身应用右侧的「启动」按钮启动分身
6. **后台保活**：点击右下角的悬浮按钮手动启动保活服务

## 注意事项

1. **真正的应用分身**：本项目提供的是应用分身的框架和保活机制。完整的应用分身功能（如独立的数据目录、权限管理等）需要更复杂的实现，可能需要系统级权限。

2. **后台保活限制**：
   - 不同厂商的 Android 系统对后台服务有不同的限制
   - 建议在系统设置中将应用加入「电池优化白名单」
   - 部分厂商（如小米、华为、OPPO、vivo等）需要在系统设置中手动开启「自启动」和「后台运行」权限

3. **权限说明**：
   - `QUERY_ALL_PACKAGES`：用于获取已安装应用列表
   - `FOREGROUND_SERVICE`：用于启动前台保活服务
   - `RECEIVE_BOOT_COMPLETED`：用于开机自启
   - `WAKE_LOCK`：用于防止设备休眠

## 技术要点

### 后台保活机制

1. **前台服务**：使用 `startForeground()` 启动前台服务，显示持久通知
2. **WakeLock**：持有 PARTIAL_WAKE_LOCK 防止 CPU 休眠
3. **开机自启**：监听 `BOOT_COMPLETED` 广播
4. **屏幕监听**：监听屏幕开关状态，重启保活服务
5. **服务重启**：在 `onDestroy()` 中尝试重启服务

### 应用分身思路

1. **数据隔离**：为每个分身创建独立的数据存储目录
2. **配置保存**：使用 SharedPreferences 保存分身信息
3. **Intent 传递**：通过 Intent 传递分身 ID，区分不同的分身实例

## 许可证

本项目仅供学习和研究使用。

## 免责声明

本应用仅用于技术交流和学习目的。使用本应用所产生的任何后果由使用者自行承担。
