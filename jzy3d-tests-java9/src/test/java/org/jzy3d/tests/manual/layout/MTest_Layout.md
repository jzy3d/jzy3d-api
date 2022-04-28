This tests allows verifying that the layout of the chart remains unchanged while using multiple monitors (screens) with different HiDPI capabilities.

## Setup
- Primary screen : HiDPI enabled
- Secondary screen : no HiDPI

## Contexts
- [ ] OS in {macOS 10, Windows 10, Ubuntu 20}
- [ ] Chip in {GPU (Native), CPU (EmulGL)}

## Instructions
- Run `MTest_Layout_Native` which will open on the main screen. (Use `--add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED` VM optionsà
- Drag the chart window in the secondary screen.

### To be verified
When moving chart windows from primary to secondary monitor (screen).
- [ ] Axis text labels have the same size.
- [ ] Legend text labels have the same size.
- [ ] Colorbar text labels have the same size.
- [ ] Colorbar position remains the same.

### To indicate
- Context (OS, Chip)