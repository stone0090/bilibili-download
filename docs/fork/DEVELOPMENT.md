# 随身视频：第一期

## 来源与设备目标

- 上游：<https://github.com/happycola233/BiliTools>
- 基线提交：`e8bbdf5450e9832789d74ddb955bfa40a32a9b90`，2026-09-29。
- 继承许可证：GPL-3.0-or-later。保留原始 README、LICENSE 与应用内开源组件说明。
- 应用包名：`io.github.stone0090.offlinevideo`，调试包追加 `.debug`。
- 应用名：随身视频（暂定），版本：0.1.0。
- 设备目标：MagicOS 10.0 / Android 16；最低 Android 10。未连接用户设备。

## 本次改动

1. 独立包名与版本号，可与原 BiliTools 共存；禁用上游更新源，防止把原项目 APK 当成本分支更新。
2. 保留上游 B 站解析、账号登录、多 P 选择、后台队列、音视频合并与导出功能。
3. 分享与粘贴复用 `platform/PlatformLinks.kt` 链接路由。分享文案有多个网址时优先提取已支持平台的链接，清理尾随标点，保留 `p` 参数。
4. 浏览器“打开方式”只匹配 B 站域名；其他应用的文字分享仍可进入输入校验。
5. 关于页面注明派生来源。

## 后续平台边界

当前注册的适配器只有 B 站。新增 `PlatformLinkAdapter` 仅解决链接识别，**不代表该平台已经能下载**。

第二个平台接入前，需要继续抽离媒体模型（平台 ID、条目 ID、剧集、清晰度、音视频轨）和解析服务；现有 `MediaRepository`、账号与部分下载重试逻辑仍与 B 站耦合。平台 Cookie 必须按域和账号分别管理，不得复用 B 站的网络客户端向其他平台发送登录信息。下载传输、文件导出和队列 UI 可逐步复用。

腾讯、爱奇艺、优酷尚未实现，也未验证。其 DRM 内容不能因会员登录就假定支持导出。

## 构建

使用 JDK 21、Android SDK Platform 37.0、Build Tools 36.0.0（AGP 默认）；构建配置仍以仓库中的 Gradle 文件为准。`targetSdk` 为 36（Android 16）。

在 `local.properties` 中设置 `sdk.dir`，配置 `JAVA_HOME` 后运行：

```sh
bash gradlew :app:assembleDebug
bash gradlew :app:testDebugUnitTest --tests '*MediaInputUrlParserTest' --tests '*PlatformLinkResolverTest' --tests '*MediaInputClassifierTest' --tests '*ExternalDownloadContractTest'
```

调试 APK 在 `app/build/outputs/apk/debug/`，使用 `arm64-v8a` 或 `universal` 包。调试签名只适合本次设备试用；正式发布前应确定并备份长期签名。

本机下载的工具、构建日志和在线验证临时文件位于 `.tmp/`，被 Git 忽略。不要提交账号凭据或设备标识。

本机 Java 直连 Google Maven / Maven Central 出现 TLS 握手失败；临时使用仅监听 `127.0.0.1` 的下载中转，通过 curl 访问原官方 HTTPS 仓库。此设置仅在 `.tmp/maven-relay.init.gradle` 中，不改变项目仓库配置，也不进入 APK。常规网络环境直接使用上述构建命令。

## 样例与验收

样例：<https://www.bilibili.com/video/BV1WZjp6SEf5>，《滚石爱情故事》（2016），接口返回 20 个分 P。

2026-09-29 初步验证：无账号 Cookie 时，第 1 集播放接口返回成功，音视频轨均能读取前 1024 字节（HTTP 206）。此结果只验证媒体接口与资源可达，不代表整集下载、合并、登录画质或真机后台运行通过。

真机验收步骤：

1. 安装本分支 APK，启动后从 B 站分享样例链接，或粘贴完整分享文案。
2. 确认出现 20 个分 P，先选择第 1 集与可用画质下载。
3. 允许通知，确认后台进度、暂停、继续正常。
4. 下载完成后导出 MP4，用手机本地播放器打开；断网后再次播放，确认完整时长与声音。
5. 再测试多集选择、手机锁屏期间下载和重新启动应用后的任务恢复。
6. 需要更高画质时在手机应用内登录 B 站，复测账号实际可用的清晰度。

验收前不宣称真机兼容、20 集全部可下载或会员画质已验证。
