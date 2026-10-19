# guide —— 灵感指导书

游戏内的说明书：`guide_book`（灵感指导书），配方是**书 + 灵感**（无序），右键翻开。

## 三个部分

| 类 | 职责 |
| --- | --- |
| `GuideChapter` | 一章 = 两个语言键（标题 + 正文），只是数据 |
| `GuideContent` | 章节目录（固定顺序的 `List`），服务端与客户端共用，不需要同步 |
| `ModGuideMenus` / `GuideBookMenu` | 一个**没有槽位**的菜单，只负责把界面开起来 |
| `client/guide/GuideBookScreen` | 客户端界面：原版书页 + 自动换行的正文 + 底部翻章箭头 |
| `item/custom/GuideBookItem` | 右键 → `ServerPlayer#openMenu`，其余什么都不做 |

## 为什么不直接用原版成书

原版那套「打开书」两岸都写死了物品必须是 `Items.WRITTEN_BOOK`：

- 服务端 `ServerPlayer#openItemGui` 里判 `stack.is(Items.WRITTEN_BOOK)`；
- 客户端 `ClientPacketListener#handleOpenBook` 里又走 `BookViewScreen.BookAccess.fromItem`。

自定义物品塞不进去（除非把它做成 `Items.WRITTEN_BOOK` 的 stack，那样物品就不可注册了）。
所以这里走**菜单 + 界面**：`SimpleMenuProvider` 开一个空菜单，客户端用
`RegisterMenuScreensEvent` 绑定 `GuideBookScreen`。好处是打开、关闭、ESC、窗口缩放
这一套原版容器已经处理好了，不需要自定义网络包。

菜单是 `AbstractContainerMenu` 的最简形态：`stillValid` 恒真、`quickMoveStack` 返回空。
`GuideBookScreen` 必须实现 `MenuAccess<GuideBookMenu>`（`ScreenConstructor` 的签名要求
"`Screen` 且 `MenuAccess`"），构造函数收 `(菜单, 背包, 标题)` 三个参数。

## 内容怎么改

加一章：往 `GuideContent.CHAPTERS` 里加一项，再去 `datagen/Mod*LangProvider` 补两条文案。
正文写在语言文件里（`\n` 分段），所以中英文各自成篇，改文案不必碰 Java。

排版沿用原版成书界面的常量：书贴图 `textures/gui/book.png`（左上 192×192），
正文从相对书左上角的 `(36, 34)` 起、可用宽度 114，行高 9 —— 也就是说
**一章正文别超过约 13 行**，超了会画到书页外面。`GuiGraphics#drawWordWrap` 只负责折行，
不做裁剪。

## 灵感这条设定

正文第一章把灵感定义为「玩家思维力量的具体化」——这是设定层的东西，
数值上的对应物是：灵感既是合成材料（铜 + 石英、金 + 紫水晶），也是机器与注入的第二种消耗品。
