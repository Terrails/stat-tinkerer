# Stat Tinkerer

### Downloads
CurseForge: https://www.curseforge.com/minecraft/mc-mods/stat-tinkerer  
Modrinth: https://modrinth.com/mod/stat-tinkerer

## What does it do?

A mod that can keep various stats on death and tinker with vanilla health system.

Aims to give users ways to make their game harder by keeping stats like hunger and saturation on death or preventing eating by applying the 'No Appetite' effect.

One of the advanced, but optional, features the mod provides is full tinkering of vanilla health system. Through the config file every aspect can be changed to give a harder experience for players.
Changing max health a user starts with, reducing max health on each death and even a hardcore like setting that turns the player into a spectator when zero health is reached after many deaths.

For a better understanding I recommend reading through the following feature list and then playing with the config file.

## Features

Experience:
- Retain experience points after death.
- Controlling whether player should drop their experience points on death.

Hunger:
- Retain hunger level after death
- Lowest value: To prevent respawning with no hunger, this can be set to a reasonable value.
- No Appetite status effect: Prevents eating while active

Saturation:
- Retain saturation level after death
- Retain only when hunger level is full
- Lowest value: Can be set to a reasonable value

Health:
- Amount of health to respawn with

---
Additionally, there is support for Tough As Nails on Forge

Thirst
- Retain thirst level after death
- Lowest value: To prevent respawning with no thirst, this can be set to a reasonable value.

Hydration
- Retain hydration level after death
- Retain only when thirst level is full
- Lowest value: Can be set to a reasonable value

Temperature
- Retain temperature level after death
- Keep temperature between two levels after death

## Health System
**WARNING: There is no guarantee that this feature will work in tandem with any mod that touches the health attribute in any way. Expect things to break, the universe to implode, etc...**

The health system in the game can be fully customized with the following options:
- Maximum Health: This determines the highest amount of health a player can have.
- Minimum Health: This sets the lowest amount of health a player can have. 
- Starting Health: It defines the starting amount of health when a player spawns.
- Health Decrease on Death: Each time a player dies, their maximum health is reduced by a certain amount. However, this decrease is limited by the minimum health value.

These options work together to create a punishing system where dying in the game leads to a decrease in health. To balance the gameplay, regenerative items can be introduced to allow players to regain health.

To make the game easier for players, there is an additional option using health thresholds. When a player reaches a specific threshold through regenerative items, their minimal health is automatically set to the defined value. Additionally, the lowest threshold can be set to a negative value to ensure that players never lose health when at or below that threshold.

Furthermore, there is the option of setting the minimum health to zero and enabling "hardcore mode". In this mode, the player's health gradually decreases through deaths until it reaches zero. Once the health reaches zero, the player becomes a spectator, similar to hardcore worlds. It's important to note that when using this mode, the health thresholds should be removed to achieve the desired effect.
