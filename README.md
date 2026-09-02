# GraveStone 🪦

**Never lose your stuff again.** When you die, a gravestone appears right
where you fell — with all your items and XP safely inside. Walk back, break
it, and everything is yours again.

![Gravestone](https://raw.githubusercontent.com/NaySurGithub/GraveStone/main/.jarbage/icon.png "Gravestone")

## Features

- 🪦 A real custom gravestone block with its own 3D model
- 🎒 Your whole inventory, armor and XP are stored inside — nothing drops
- 💾 Graves survive server restarts
- 🔒 Only you can open your own grave (staff can bypass)
- ✨ A floating "Grave of ..." text above the grave
- 📦 The texture pack installs itself — players download it automatically

## Install

1. Put `GraveStone-x.y.z.jar` in your `plugins/` folder.
2. Restart the server. Done!

## Config

Everything is in `plugins/GraveStone/config.yml`:

| Option | Default | What it does |
| --- | --- | --- |
| `enabled-worlds` | all worlds | Where graves appear. `"*"` = everywhere. |
| `recovery-mode` | `break` | Get your stuff by breaking the grave, or set `interact` to just right-click it. |
| `gravestone-hardness` | `0.5` | How long the grave takes to break. |
| `save-xp-in-grave` | `true` | Store your XP in the grave too. |
| `owner-only` | `true` | Only the owner can open their grave. |
| `direct-to-inventory` | `true` | Items go straight back into your inventory. |
| `hologram.enabled` | `true` | Show the floating "Grave of ..." text. |
| `hologram.text` | `Grave of {player}` | Change the hologram text. |
| `messages` | — | Customize the chat messages. |

## Commands

- `/gravestone reload` — reload the config (op only)
- Permission `gravestone.bypass` — open anyone's grave (op only)

## Requirements

PowerNukkitX 3.0.0+ · Java 21

## For developers

```bash
./gradlew build
```

MIT license — do whatever you want with it.
