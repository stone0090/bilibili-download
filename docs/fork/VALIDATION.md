# 第一期验证结果（2026-09-29）

- 构建：`:app:assembleDebug` 成功。
- 回归测试：21 项通过，0 失败、0 错误。范围为链接路由、B 站输入分类、分享文案以及 Android 16（Robolectric API 36）的分享 Intent。
- ARM64 APK：`dist/OfflineVideo-0.1.0-arm64.apk`。
- APK 签名：`apksigner verify` 通过（debug 签名，v2）。
- APK：minSdk 29，targetSdk 36，包名 `io.github.stone0090.offlinevideo.debug`。
- 16KB 检查：`zipalign -c -P 16 4` 通过；ARM64 的 11 个原生库 ELF LOAD 段对齐均为 16384。
- APK SHA-256：`acca3232a1fb643546fd6a40220e678b7013f6ed61d3fa5f26d01eec2c6546a6`。
- 样例：BV1WZjp6SEf5 元信息返回 20 个分 P；第 1 集音视频资源各读取 1024 字节，均为 HTTP 206。

尚未验证：用户真机安装/启动、完整单集下载和合并、断网播放、锁屏与后台行为、20 集全部下载、大会员登录与高画质。Robolectric 测试不能代替这些真机验收。

构建日志位于 `.tmp/build-relay.log`；测试报告位于 `app/build/reports/tests/testDebugUnitTest/index.html`。
