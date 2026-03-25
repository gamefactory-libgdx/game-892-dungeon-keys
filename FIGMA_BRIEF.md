# Figma AI Design Brief — Dungeon Keys

---

## 1. Art Style & Color Palette

**Style:** Dark fantasy pixel-art with a modern glow treatment — chunky 16×16-tile aesthetics rendered at high resolution, with rim-lighting and ambient particle effects to give depth. Think SNES-era dungeon crawlers reimagined in 4K. Stone, lava, and ice biomes each carry a distinct palette shift while sharing the same graphic language: bold outlines, limited dithering, and luminous accent glow.

**Primary Palette:**
- `#1A1228` — Void Black (deep background base)
- `#2E4A6B` — Dungeon Slate (stone walls, UI chrome)
- `#C8A84B` — Ancient Gold (keys, treasure, highlight rings)
- `#F0EDE0` — Bone White (hero sprite, UI text panels)

**Accent Colors:**
- `#E03A2F` — Ember Red (lava biome glow, danger indicators)
- `#4ECFFF` — Glacial Cyan (ice biome glow, cryo effects)

**Font Mood:** Heavy pixel-font for game titles and scores (PressStart2P or Kongtext); clean sans-serif (Exo2) for body labels and button text. All text uses a tight drop-shadow in `#1A1228` at 2px offset to stay legible over busy backgrounds.

---

## 2. App Icon — icon_512.png (512×512px)

**Export path:** `icon_512.png` (project root)

A square canvas filled with a radial gradient from `#2E1A50` (deep purple-black at center) to `#0D0B18` (near-black at corners), giving the impression of looking down a torch-lit dungeon shaft. Centered is an oversized ornate iron key rendered in `#C8A84B` Ancient Gold with a subtle inner highlight and a warm amber outer glow (`#FFB347` at 40% opacity, 32px spread). The key's bow (top ring) is shaped as a stylized skull motif to signal danger. Behind the key, faint stone-brick tile lines radiate outward in `#2E4A6B`, barely visible at 20% opacity, adding texture without clutter. A thin hexagonal frame in `#C8A84B` borders the composition at 8px inset from the canvas edge, reinforcing the "artifact" feel.

---

## 3. UI Screens (480×854 portrait)

---

### MainMenuScreen

**A) BACKGROUND IMAGE**
Full-bleed vertical panorama of the Stone Crypt entrance: rough-hewn `#2E4A6B` granite walls frame the composition left and right, with torch sconces emitting animated-style painted flicker halos in `#FFB347`. The floor recedes in forced perspective toward a massive iron door at screen center-bottom, banded with `#C8A84B` gold rivets. Floating dust motes in near-white drift upward across the mid-ground. A decorative empty stone banner plaque — arched top, flat bottom, 320×90px — hangs centered at y≈200px, styled with chiseled borders and NO text inside (game engine writes the title). A second narrower blank panel (280×60px) sits at y≈780px for version text.

**B) BUTTON LAYOUT**
```
PLAY          | top-Y=420px | x=centered       | size=280x56
LEADERBOARD   | top-Y=496px | x=centered       | size=280x56
SETTINGS      | top-Y=572px | x=centered       | size=280x56
HOW TO PLAY   | top-Y=648px | x=centered       | size=280x56
```

---

### DungeonSelectScreen

**A) BACKGROUND IMAGE**
A split triptych background: left third bleeds a cool `#2E4A6B` stone texture, center third shows a glowing lava crack in `#E03A2F`, and right third fades to icy `#4ECFFF` crystalline formations — all three biomes visible side-by-side in a single panoramic scene seamed with dramatic lighting transitions. The top 160px is a dark `#1A1228` to `#2E1A50` gradient header band with carved decorative border (no text). Three blank decorative card frames (130×200px each) sit horizontally centered at y≈380px, with faintly glowing borders matching each biome accent color — gold, red, cyan respectively. These cards are completely empty inside; the engine renders biome name and artwork.

**B) BUTTON LAYOUT**
```
BACK                  | top-Y=20px  | x=left@20px    | size=120x44
STONE CRYPT [card]    | top-Y=290px | x=left@20px    | size=130x200
LAVA DUNGEON [card]   | top-Y=290px | x=centered     | size=130x200
ICE CAVERN [card]     | top-Y=290px | x=right@20px   | size=130x200
```

---

### CryptScreen (Stone Crypt gameplay background)

**A) BACKGROUND IMAGE**
A top-down dungeon tile-grid atmosphere: seamless `#2E4A6B` stone-brick pattern fills the entire canvas with subtle mortar lines in `#1A1228`. Torch glow pools — warm `#FFB347` radial gradients at 60% opacity — appear at the four quadrant corners to simulate wall-mounted torches just outside the frame. The overall midtone is slightly darkened toward screen edges (vignette) to focus attention on the center play area. Faint moss patches and water-seep stains in `#1E3A2A` break up the monotony. No UI shapes — purely environmental tileset background.

**B) BUTTON LAYOUT**
```
PAUSE         | top-Y=16px  | x=right@16px   | size=56x56
KEYS [counter]| top-Y=16px  | x=left@16px    | size=120x44
SCORE         | top-Y=16px  | x=centered     | size=160x44
```

---

### LavaDungeonScreen (Lava Dungeon gameplay background)

**A) BACKGROUND IMAGE**
Top-down lava dungeon floor: basalt-black tiles (`#1A0A08`) cracked by glowing magma veins in `#E03A2F` running diagonally in a natural fracture pattern. Lava veins pulse with a painted bloom glow — `#FF6B35` at center, fading to `#1A0A08` within 24px. Heat shimmer suggested by subtle horizontal wavy distortion painted into the mid-ground. Wall edges (top and bottom 60px bands) are scorched obsidian with ember-orange drip marks. Vignette is deep amber `#3D1200` at edges. No UI elements, purely atmospheric tile art.

**B) BUTTON LAYOUT**
```
PAUSE         | top-Y=16px  | x=right@16px   | size=56x56
KEYS [counter]| top-Y=16px  | x=left@16px    | size=120x44
SCORE         | top-Y=16px  | x=centered     | size=160x44
```

---

### IceCavernScreen (Ice Cavern gameplay background)

**A) BACKGROUND IMAGE**
Top-down ice cavern floor: deep `#0D1F2D` base tile with a crystalline overlay — hexagonal frost crack patterns in `#4ECFFF` at 50% opacity spread across the canvas like shattered safety glass. Subsurface light pools in pale `#B8F0FF` glow beneath thick tiles, creating an eerie backlit effect. Stalactite shadows drape from the top edge in `#1A2A3A`. Frost rime accumulates in the four corners, feathering inward in pure white at 30% opacity. The overall feel is cold, beautiful, dangerous. No UI elements.

**B) BUTTON LAYOUT**
```
PAUSE         | top-Y=16px  | x=right@16px   | size=56x56
KEYS [counter]| top-Y=16px  | x=left@16px    | size=120x44
SCORE         | top-Y=16px  | x=centered     | size=160x44
```

---

### LevelCompleteScreen

**A) BACKGROUND IMAGE**
A radiant victory burst: central sunburst rays in `#C8A84B` and `#FFD700` emanate from dead center on a deep `#1A1228` base, fading to transparent at the canvas edges. Scattered painted coin/gem particles orbit the burst at mid-radius. A wide empty ornate scroll banner (380×110px) is painted centered at y≈340px — parchment texture in `#F0EDE0` with gold corner filigree, interior completely blank (engine renders "LEVEL COMPLETE" and score). At y≈500px, a second smaller blank rectangular plaque (240×70px) in `#2E4A6B` outlined in gold holds nothing — engine renders star rating or key count.

**B) BUTTON LAYOUT**
```
NEXT LEVEL    | top-Y=600px | x=centered     | size=280x56
MENU          | top-Y=674px | x=centered     | size=280x56
```

---

### GameOverScreen

**A) BACKGROUND IMAGE**
Dark and heavy: a cracked stone slab texture in `#1A1228` fills the canvas, split diagonally by a jagged fracture line lit from beneath in dim `#E03A2F` ember light. Faint skull-and-crossbones watermark is etched into the upper stone in `#2E4A6B` at 25% opacity — decorative, not dominant. Wisps of dark smoke curl upward from the fracture in near-black at 40% opacity. An empty cracked stone banner (360×100px) centered at y≈310px has jagged broken edges and interior left blank for engine text. No bright elements — this palette stays dark and foreboding.

**B) BUTTON LAYOUT**
```
RETRY         | top-Y=580px | x=centered     | size=280x56
MENU          | top-Y=654px | x=centered     | size=280x56
```

---

### LeaderboardScreen

**A) BACKGROUND IMAGE**
A ceremonial dungeon hall in portrait: tall gothic arched ceiling implied by stone ribs converging to a keystone at top-center, painted in `#2E4A6B` with `#1A1228` shadow grooves. Torch-light casts warm `#FFB347` pools at the left and right walls. A tall empty stone tablet (380×480px) is carved into the wall face, centered at y≈380px — smooth face with no content, framed by chiseled border in `#C8A84B`. Three gold decorative divider lines are etched horizontally across the tablet at equal intervals, creating empty row sections (engine populates rank, name, score). The floor is polished dark stone reflecting torchlight faintly.

**B) BUTTON LAYOUT**
```
BACK          | top-Y=20px  | x=left@20px    | size=120x44
RANK 1 row    | top-Y=240px | x=centered     | size=360x60
RANK 2 row    | top-Y=316px | x=centered     | size=360x60
RANK 3 row    | top-Y=392px | x=centered     | size=360x60
RANK 4 row    | top-Y=468px | x=centered     | size=360x60
RANK 5 row    | top-Y=544px | x=centered     | size=360x60
```

---

### SettingsScreen

**A) BACKGROUND IMAGE**
A tidy stone alcove: flat `#1A1228` background with a carved rectangular recess (360×560px) centered at y≈430px, beveled edges in `#2E4A6B` with thin `#C8A84B` inlay trim — entirely empty inside (engine places all toggle rows and sliders). Decorative gear-cog rosettes are etched as bas-relief into the upper-left and upper-right corners of the alcove at 30% opacity. Ambient torchlight pools glow behind the alcove from out-of-frame, casting warm edge lighting. The canvas outside the alcove fades to near-black with a subtle brick texture.

**B) BUTTON LAYOUT**
```
BACK            | top-Y=20px  | x=left@20px    | size=120x44
MUSIC [toggle]  | top-Y=280px | x=centered     | size=320x52
SFX [toggle]    | top-Y=348px | x=centered     | size=320x52
VIBRATION       | top-Y=416px | x=centered     | size=320x52
LANGUAGE        | top-Y=484px | x=centered     | size=320x52
RESET PROGRESS  | top-Y=600px | x=centered     | size=280x52
```

---

### HowToPlayScreen

**A) BACKGROUND IMAGE**
A dungeon tutorial wall: `#1A1228` base with faint grid lines in `#2E4A6B` at 15% opacity suggesting dungeon tiles. A large blank stone scroll (360×520px) is centered at y≈450px with parchment-like `#2A2030` surface and double gold ruled border — completely empty inside (engine renders instruction text and swipe-direction diagrams). Four directional arrow engravings — up, down, left, right — are carved faintly into the scroll's blank area as decorative watermarks at 10% opacity only. A small decorative key icon is etched into the top of the scroll border as a cartouche ornament, below which all content is blank.

**B) BUTTON LAYOUT**
```
BACK          | top-Y=20px  | x=left@20px    | size=120x44
GOT IT        | top-Y=760px | x=centered     | size=280x56
```

---

## 4. Export Checklist

```
- icon_512.png (512x512)
- ui/main_menu.png (480x854)
- ui/dungeon_select.png (480x854)
- ui/crypt_screen.png (480x854)
- ui/lava_dungeon_screen.png (480x854)
- ui/ice_cavern_screen.png (480x854)
- ui/level_complete.png (480x854)
- ui/game_over.png (480x854)
- ui/leaderboard.png (480x854)
- ui/settings.png (480x854)
- ui/how_to_play.png (480x854)
- feature_banner.png (1024x500)
```

---

## 5. Feature Banner — feature_banner.png (1024×500 landscape)

A sweeping wide-format dungeon panorama in the Stone Crypt biome. The background stretches horizontally: ancient stone corridors recede into darkness at both left and right edges, lit by chains of hanging lanterns casting overlapping `#FFB347` warm-gold pools. Dead center in the mid-ground, a massive iron-banded dungeon door stands ajar, backlit by a burst of `#C8A84B` golden light streaming through the crack — this is the exit, calling to the player. In the left foreground, the dungeon hero (small, silhouetted, pixel-art style) leaps toward a floating oversized key in mid-air, its golden glow the brightest point in the composition. Roaming monster shadows lurk in the right mid-ground — enough to suggest threat without dominating. The game title **"DUNGEON KEYS"** is rendered in large PressStart2P-style lettering, centered horizontally across the top third of the banner, in `#C8A84B` gold with a 4px `#1A1228` shadow and a faint `#FFB347` outer glow. The overall palette stays true to Section 1: void black, dungeon slate, ancient gold — with the luminous exit-door light as the hero of the composition.