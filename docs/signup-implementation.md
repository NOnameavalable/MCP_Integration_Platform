# Signup 实现说明

当前分支：feature/signup。实现了核心注册用例和适配层；真实 DAO、朋友负责的 View、以及 Login 页面本身仍需接入。没有改动队友的 Login、Market 或 Mcpkit 实现。

## 一次注册如何执行

```text
前端读取四个输入
→ SignupController.execute(username, email, password, repeatPassword)
→ SignupInputData
→ SignupInteractor.execute(...)
→ 校验输入、检查重名、生成用户 ID、通过 PasswordHasher 哈希密码
→ 创建 User（自动拥有自己的 Mcpkit）
→ SignupDataAccessInterface.save(user)
→ SignupOutputData（仅 ID、用户名、邮箱）
→ SignupPresenter
→ SignupViewModel 更新并发出 signupResult 事件
→ 成功时执行前端提供的跳转登录页动作
```

## 文件职责

| 文件 | 实现内容 |
|---|---|
| SignupInputData | 保存用户名、邮箱、密码、确认密码；不做业务判断，不输出日志 |
| SignupOutputData | 只携带 userId、username、email，不包含密码或哈希 |
| SignupInputBoundary | 沿用 execute(inputData) 入口 |
| SignupOutputBoundary | 沿用 prepareSuccessView / prepareFailView |
| SignupDataAccessInterface | 沿用查重、保存约定；DAO 保存时仍需保证唯一性 |
| SignupInteractor | 输入校验、用户名查重、协调 ID/哈希/User 创建/保存、分阶段处理失败 |
| SignupController | 把四个原始表单值组装成 InputData，通过 InputBoundary 发起请求 |
| SignupPresenter | 成功/失败结果更新 ViewModel，成功后调用外部提供的导航动作 |
| SignupViewModel | 保存成功标志、用户 ID、用户名、邮箱和错误消息；不保存密码；通知监听者刷新 |
| PasswordHasher | 内层哈希接口，Interactor 只认识接口 |
| Pbkdf2PasswordHasher | 外层 JDK 哈希实现：随机 16 字节 salt、PBKDF2-HMAC-SHA256、600000 次迭代、256 位输出；提供 matches 供登录模块复用 |
| SignupFactory | 将真实 DAO、Presenter、Interactor、哈希实现和 Controller 组装起来 |
| SignupTest | 使用测试用内存 DAO 验证注册用例、失败流程、页面状态链路、真实哈希与组装 |

## 当前校验规则

- 用户名不可为空白；用户名和邮箱两侧空白会被去掉，但不改变大小写。
- 邮箱使用基本格式校验，不做发送验证或邮箱唯一性检查。
- 密码不可为空白，两次密码必须一致；密码本身不 trim，不改变大小写。
- 没有擅自指定最小密码长度、复杂度或唯一邮箱政策，团队决定后再补。
- 现有用户名不得覆盖。DAO 必须再次拒绝重复用户名，以处理并发注册。
- 保存成功前不会报告注册成功。
- 不自动登录，不自动生成 API Key。

## 前端与 DAO 交接

```java
SignupViewModel viewModel = new SignupViewModel();
SignupController controller = SignupFactory.create(
    realSignupDao,
    viewModel,
    showLoginView  // Runnable：由前端实现页面切换
);
viewModel.addPropertyChangeListener(event -> {
    // 读取 viewModel.isSuccessful() / getErrorMessage() 等并更新页面。
    // 成功时清空密码输入；不要将密码保存到 ViewModel。
});
// 注册按钮触发：
controller.execute(username, email, password, repeatPassword);
```

上面的 realSignupDao 和 showLoginView 是交接参数，不是当前已经存在的变量/完整 View。DAO 必须将 User 持久化，而测试里的 Map 不等于真实持久化。

哈希和 DAO 可能耗时：JavaFX 前端应在后台 Task 执行注册，禁用重复提交，并通过 Platform.runLater 把 ViewModel 监听事件和导航动作转回 JavaFX 线程；当前 ViewModel 在调用者线程发通知，不自动切换 UI 线程。不要同时从多个线程执行同一页面的注册请求。

## 登录同学需要知道

存储编码格式：

```text
pbkdf2-sha256$600000$Base64(salt)$Base64(hash)
```

可用 Pbkdf2PasswordHasher.matches(用户输入密码, user.getPasswordHash()) 校验；不能对同一密码重新 hash 后直接比较字符串，因为每次随机 salt 不同。matches 目前只接受本版本固定参数，未来参数升级需设计兼容迁移。

## 验证结果和限制

- 使用已安装 JDK 26，按 --release 23 编译 Signup 及依赖：通过。
- SignupTest.main：66 项检查通过，包括有效注册、空输入、邮箱不合法、密码不匹配、重复用户名、读取/保存失败、保存时冲突、哈希失败、ID 生成失败、重复消息通知、导航次数以及真实密码校验。
- 测试是独立 main runner，不是 Maven 自动发现的 JUnit 测试。
- 同步远程 main（afb4910）后，全项目 mvn -o -DskipTests compile：BUILD SUCCESS。User 22 项与 ApiKey 42 项检查也通过。
- 真实持久化、真实注册界面和登录页面的端到端运行尚未验证。
- 本次 PR 范围为注册核心逻辑与适配层，不代表真实界面和持久化已完成。

参考：OWASP Password Storage Cheat Sheet（PBKDF2-HMAC-SHA256 的工作因子与随机 salt）。
https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
