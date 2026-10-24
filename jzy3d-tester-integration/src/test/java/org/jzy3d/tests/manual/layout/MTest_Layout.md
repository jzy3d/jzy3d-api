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
- [ ] Axis text labels have the same size (Hint : moving the middle of the frame around the jonction of the two monitors will toggle HiDPI/No HiDPI for the complete frame).
- [ ] Legend text labels have the same size.
- [ ] Colorbar text labels have the same size.
- [ ] Colorbar position remains the same.

### To indicate
- Context (OS, Chip)


## Results

| Canvas | OS |  Visual Text scale is unchanged when screen change | Resize Window | Info |
|-----|-----|-----|-----|-----|
| EmuGL | Windows | OK | yes | Parfaite colorbar (no shrink text, correctly on right side, no bar width change, MAIS la bande colorée devient plus LARGE sur 2e écran ) |
| Native | Windows | x | yes | Colorbar mal placée sur premier ecran (ok sur second, mais rogne un peu le texte) |
| EmuGL | MacOS | OK | no | Colorbar bien placée, pas coupée, mais la bande colorée devient plus LARGE sur 2e écran + bordure noire invisible sur no HiDPI |
| Native | MacOS | OK | no | Colorbar bien placée mais très coupée 1er écran, un peu coupé 2e écran            |