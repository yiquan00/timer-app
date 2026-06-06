# 计时器 Timer — iPhone 风格计时器（自动锁屏版）

一个美观的 iOS 风格计时器 App，支持 **倒计时结束自动锁屏**（类似 iPhone 的"停止播放"功能）。

---

## 两种使用方式

### 方式一：直接使用 PWA 网页版（0 安装，马上用）

**这就是你当前文件夹里的 `index.html`**，可以直接用浏览器打开。

1. 在 Windows 上双击 `index.html` 即可使用
2. 在安卓手机上：
   - 用 **Chrome 浏览器** 打开这个页面（可以把文件传到手机上）
   - 或搭建一个简单的网页服务器来访问
   - 点击 Chrome 菜单 → 「**添加到主屏幕**」→ 它就变成一个 App 图标了

**网页版的限制：** 无法真正锁屏，只能让手机自然熄屏（释放屏幕常亮锁后，手机按系统设置的超时时间自动关屏）

---

### 方式二：编译 APK 到手机上安装（推荐，支持真正的锁屏）

**不需要安装任何编程软件！** 用 GitHub Actions 在云端自动编译。

#### 第一步：注册 GitHub 账号
1. 打开 https://github.com 注册一个账号（免费）
2. 登录后点击右上角 + 号 → **New repository**（新建仓库）
3. 仓库名称填 `timer-app`，选择 **Public**，点击 **Create repository**

#### 第二步：上传文件
1. 在新页面点击 **uploading an existing file**（上传已有文件）
2. 把本文件夹里**所有文件和文件夹**拖进去（包括 index.html、android 文件夹、.github 文件夹等）
3. 拉到页面底部，点击 **Commit changes**

#### 第三步：自动编译 APK
1. 上传完成后，点击仓库顶部的 **Actions** 标签
2. 你会看到一个叫 **Build Android APK** 的工作流正在运行（橙色圆点）
3. 等几分钟，圆点变成绿色 ✓ 就表示编译完成了
4. 点击这个工作流 → 往下找到 **Artifacts** → 点击 **timer-app-release** 下载 APK

#### 第四步：安装到手机
1. 把下载的 APK 传到安卓手机上（用微信、QQ、数据线等都行）
2. 在手机上点击 APK 文件安装
3. 如果提示"未知来源应用"，去设置里允许安装即可

---

## 如何使用锁屏功能

1. 打开安装好的 **计时器** App
2. 屏幕顶部会出现一个 **橙色横幅**：*"启用锁屏需要激活设备管理员"*
3. 点击右边的 **「激活」** 按钮
4. 系统会弹出一个安全确认页面，点击 **「激活」** 确认
5. 横幅消失，表示锁屏权限已激活

**计时时：**
- 设置倒计时时间
- 打开「结束后锁屏」开关（默认已开启）
- 点击「开始」
- 倒计时结束 → 闹钟响起 → **屏幕自动锁定！**

---

## 项目文件说明

```
timer-app/
├── index.html                          # PWA 网页版计时器（立即可用）
├── manifest.json / sw.js               # PWA 配置（让网页像 App 一样安装）
├── icon/                               # App 图标
├── android/                            # Android 原生项目
│   ├── build.gradle / settings.gradle  # Gradle 构建配置
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml     # App 配置（声明设备管理员权限）
│   │   │   ├── java/com/timerapp/
│   │   │   │   ├── MainActivity.java   # 主界面（WebView 加载计时器）
│   │   │   │   ├── TimerWebInterface.java  # JS↔Java 桥接（锁屏关键）
│   │   │   │   └── AdminReceiver.java  # 设备管理员接收器
│   │   │   ├── assets/public/
│   │   │   │   └── index.html          # WebView 里的计时器页面
│   │   │   └── res/                    # 资源文件
│   │   └── build.gradle
│   └── gradle/
├── .github/workflows/
│   └── build-apk.yml                   # GitHub 自动编译脚本
└── README.md                           # 本说明文件
```

---

## 技术原理

**锁屏实现方式：** Android `DevicePolicyManager.lockNow()`
- App 请求成为**设备管理员**（Device Admin）
- 用户一次授权后，App 就获得了锁屏权限
- 计时结束时，JavaScript 通过 `TimerBridge.lockScreen()` 调用 Java 原生方法
- 原生代码执行 `dpm.lockNow()` → **屏幕立即锁定**

这是一个**标准的、安全的** Android API，支付宝、微信等 App 也在用同样的机制。

---

**有任何问题？直接问我就行。**
