# lite-material-components-4a
A Material 3 component library compatible with Android 4.1

## Todo
* [ ] Material3e
  * [x] Button
  * [ ] Slider/RangeSlider
  * [ ] ...
* [x] Material3
  * [x] Color
  * [x] Button
  * [x] Switches
  * [x] Slider/RangeSlider
  * [x] Dialog
  * [x] RadioButton
  * [x] BottomSheet
  * [x] NavigationRail
  * [x] CardView 
  * [x] TextField
  * [x] FloatingActionButton
  * [x] TopAppBar
  * [x] Checkbox
  * [x] LinearProgressIndicator
  * [x] CircularProgressIndicator
  * [x] Tabs
  * [x] Chip
  * [ ] PopupMenu
  * [ ] BottomAppBar
  * [ ] SnackBars
  * [ ] Drawer
  * [ ] ...
* [ ] Material Design
  * [ ] Button
  * [ ] ...

# For Agent
禁止写注释。

## API 命名规范

所有组件遵循同一套命名规范，新增 API 时请保持一致：

| 类别 | 命名 | 说明 |
| --- | --- | --- |
| 属性读写 | `setXxx(...)` / `getXxx()` | `getXxx()` 返回"当前生效值"（有覆盖返回覆盖值，否则返回配色方案角色色） |
| 布尔属性 | `isXxx()` / `hasXxx()` | 状态用 `isXxx()`，"是否已显式设置"用 `hasXxx()` |
| 单位 | `setXxxDp(float)` / `setXxxSp(float)` | dp / sp 后缀必须写清；组件本身继承 `TextView` 时文字大小与颜色直接用平台 API，不再重复提供 `setXxxSp` / `setTextColor` |
| 枚举属性 | `setStyle(ButtonStyle)`、`setSize(SizeVariant)`、`setColorVariant(ColorVariant)`、`setVariant(TopAppBarVariant)` | 不写 `setButtonStyle` / `setTextFieldStyle` 这类"组件名 + 属性名"的重复命名 |
| 配色 | `setColorScheme(...)` / `getColorScheme()` | 应用配色方案并清除该组件全部颜色覆盖；`null` 回落到 `publicColorScheme` |
| 颜色覆盖 | `setXxxColor(int)` / `getXxxColor()` / `clearXxxColor()` | 角色名（Container / Content / Icon）或元素名（Thumb / Track / Indicator / Title / Label …）三件套齐全，`clear` 后回到角色色 |
| 外部绑定 | `bindTo(Xxx)` / `unbind()` / `isBound()` | 不使用 `attachToXxx` / `detachFromXxx` / `unbindTo` / `unbindXxxView` |
| 监听器 | `setOnXxxListener(OnXxxListener)` | 单一监听器；不使用 `addOnXxxListener` / `removeOnXxxListener` / `clearOnXxxListeners` |
| 槽位 / 子项 | `addXxx(...)` / `removeXxx(...)` / `getXxx(int)` / `getXxxCount()` | 同一槽位只保留一套方法名 |
| 显示 / 隐藏 | `show()` / `hide()` | 不使用 `dismiss()` |
| 图标 | `setIcon(Icon)` / `getIcon()` | 全库统一使用 `Icon` 包装类型 |

同一属性只能有一个 setter / getter 名字（例如不允许同时存在 `getBackgroundColor()` 与 `getContainerColor()`）。
