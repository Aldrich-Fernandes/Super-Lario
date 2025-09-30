# **Super Lario**

A 2D platformer inspired by Super Mario, built in Java as part of a university coursework project.

## Contributors

- [Fadi Mohamed Mostefai](https://github.com/Fadi-Mostefai)
- [Aldrich Antonio Fernandes](https://github.com/Aldrich-Fernandes)
- [Mehdi Belhadj](https://github.com/MehB06)
- [Kishan Prakash](https://github.com/KishPrak)

## Overview

Super Lario is a side-scrolling platformer game where players navigate through six varied maps, avoiding enemies and traps, collecting coins, and ultimately retrieving a key to unlock the final door. It pays homage to the iconic Super Mario series while offering a unique gameplay experience built from scratch using JavaFX and custom vector graphics.

## Game Features

- 2D movement using WASD or arrow keys, with jump functionality  
- 6 different levels: static start/end rooms with randomized middle levels  
- Static and dynamic spike traps that damage the player  
- Coin collection system used to unlock the key  
- A key-and-door mechanic to progress and win the game  
- Health and timer indicators in the info bar  
- FPS display for performance monitoring  
- Pause functionality  
- Multiple unique death screens based on how the player dies  
- End of game score calculation  

## Technical Details

- Built using Java and JavaFX  
- Custom delta time implementation for consistent gameplay across devices  
- Collision system with directional checks for accurate terrain interactions  

## Known Issues

- Game must be run on Windows or macOS (currently incompatible with Linux systems via the provided JAR)  
- Collision system may behave inconsistently under extremely high input frequency (e.g., long key presses), although mitigated with refined collision logic  

## Planned Features

- Background music and sound effects  
- Difficulty settings for varied playstyles  
- Power-ups and item drops  
- Enhanced level design for a more "roguelike" experience  
