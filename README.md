
# Nostalgic Tweaks X
My personal fork of Nostalgic Tweaks with more features and other things.

## Nostalgica Reverie
This mod is now a part of the [Nostalgica Reverie Organization](https://github.com/Nostalgica-Reverie). A collection of repositories for all mods and projects focused on blending old with new! If you're interested to see what we're up to, then stop by the organization's [Discord](https://discord.gg/Un7b9AWSsu)!

# Mod Relicense
As of August 15th, 2024, Nostalgic Tweaks is now under the LGPLv3 license. All versions starting at `v2.0.0-beta.905` and later will use this license. Mod developers are allowed to fork this project and distribute their changes. However, if you would rather join the development team, then please reach out to us at the mod's [Discord](https://discord.gg/Un7b9AWSsu) server.

# Development Branches
- 1.21.4 (Beta 9.1) **Coming Soon™**
- [1.21.1 (Beta 9)](https://github.com/Adrenix/Nostalgic-Tweaks/tree/1.21) **LTS**
- [1.20.1 (Beta 9)](https://github.com/Adrenix/Nostalgic-Tweaks/tree/1.20.1) **LTS**

#### Changelogs
See the changelog in a branch for versioned changes.

# 
![Nostalgic Tweaks by Adrenix](https://i.imgur.com/1Nd06WK.png)

[<img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/available/modrinth_vector.svg">](https://modrinth.com/mod/nostalgic-tweaks) [<img alt="curseforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/available/curseforge_vector.svg">](https://www.curseforge.com/minecraft/mc-mods/nostalgic-tweaks) [<img alt="github-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/social/github-singular_vector.svg">](https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks)
 [<img alt="discord-plural" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/social/discord-plural_vector.svg">](https://discord.gg/Un7b9AWSsu) 
[<img alt="fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/supported/fabric_vector.svg">](https://fabricmc.net/) [<img alt="kofi-plural-alt" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/donate/kofi-plural-alt_vector.svg">](https://ko-fi.com/adrenix)

Inspired by Exalm's **Old Days** mod, **Nostalgic Tweaks** brings back a plethora of **classic, alpha, beta, and post-release** lost or changed gameplay elements. This mod is intended for nostalgic enthusiasts who want to relive the glory days of Minecraft Java edition by tweaking the mod to their nostalgic preference, or for those who want to give the game a new aesthetic and gameplay style.

Some of the major nostalgic tweaks included are **old classic and pre-beta 1.8 light engines**, **old fog rendering**, **old sky and void colors**, **2D floating items with disabled diffused lighting**, **old mob spawning**, **old combat mechanics**, **old game screens**, **old C418 music**, and **so much more**! There are currently over **400+** nostalgic tweaks as of 2.0.0-Beta 9 with more on the way!

For the best nostalgic experience, I recommend using the **[Golden Days](https://github.com/PoeticRainbow/golden-days/releases)** resource pack by **[PoeticRainbow](https://modrinth.com/user/PoeticRainbow)**, or the **[Programmer Art Continuation Project](https://modrinth.com/resourcepack/pacp)** resource pack by **[mzov](https://modrinth.com/user/mzov_jen)**. Golden Days comes with an abundance of nostalgic packs, such as an alpha and picture perfect pack which coincides wonderfully with **Nostalgic Tweaks**. Additionally, use the **[Moderner Beta](https://modrinth.com/mod/moderner-beta)** mod by **[BlueStaggo](https://modrinth.com/user/BlueStaggo)** for nostalgic terrain generation.

# Compatibility

This is a list of commonly reported problems and their potential solutions:

- If you are experiencing issues with chunks not updating light changes (_usually caused by mod conflicts_), then disable the `Round Robin Chunk Relighting` tweak. This can be found under `Eye Candy > Lighting Candy > World Lighting > Light Engine`.
- Optifine is known to cause problems. It is a closed source mod, so it is very difficult to support. Tweaks that are known to be impacted by Optifine are tagged in the config menu.
- Sodium & Embeddium are officially supported.

If you experience a crash or come across a compatibility issue, please let me know! It is best to report compatibility issues on our **[GitHub](https://github.com/Adrenix/Nostalgic-Tweaks/issues)** issue page for better tracking.

# Nostalgica Reverie Discord 
The Nostalgica Reverie discord is the best place for nostalgia-based projects like Re-Console, Nostalgic Tweaks, Moderner Beta, and more! Here you can discuss anything related to our organizations projects, interact with the community or talk about programming!

[Click here to join!](https://discord.gg/6pRkrYxbGW)


# Configuration

To open the mod's config menu, press **O** for "**Old**" while in-game or at the game's title screen. This hotkey can be changed in the vanilla controls menu or in the mod's config menu. If you have **[Mod Menu](https://modrinth.com/mod/modmenu)** installed for Fabric, or you are using Forge or NeoForge, then you can also access the configuration screen through the **Mods** button.

Each tweak will have **tags** above their names in the config menu. Each tag identifies different aspects of each tweak. You can hover your mouse over each tag to see more information as a tooltip. You can favorite the tweak, see its status, modify the client/server value, save, undo, reset, and modernize a tweak within its configuration box.


<details>
<summary>Tweak Info</summary>

## Search
The config menu comes with searching capabilities. Search queries that are typed into the input box will bring up fuzzy results. This means the mod will attempt to show results that it believes you were trying to search for. You can narrow search results by using **Search Tags**. To see all the search tags that are available, open the config menu and click the **Manage** button in the bottom-left corner of the screen. Then go to the `Help` category and scroll down. Additionally, the bread crumbs that appear at the top of a tweak's configuration box can be clicked to quickly jump to that crumb's section within the config menu.

## Filter
The config menu also comes with a search filter. When opened, you can choose which tweak categories to look in. This compounds with the search tags mentioned earlier. Additionally, if the favorite tweaks list is opened, any search queries typed into the input box will only bring up tweaks that are favorited.

## Config Management
The config menu also comes with a config management overlay. To open the overlay, click the `Manage` button in the bottom-left corner of the screen. In this overlay you can create and manage config backup files, create and manage config presets, import and export (_client and server_) config files, perform server operations on servers with Nostalgic Tweaks installed, and quickly toggle a lot of tweaks all at once. Each section of the overlay comes with a uniquely crafted user interface to assit with tweak and config management. 

## Keyboard Shortcuts
The config menu also comes with keyboard shortcuts. **Ctrl + F** focuses the search box, **Ctrl + S** saves the config menu, **Esc** exits the config menu, and **Ctrl + Left Arrow**, **Alt + Left Arrow**, **Ctrl + Right Arrow**, or **Alt + Right Arrow** will change the category group you are in without the need to click on a category button. You can also use the **Tab** key and **Directional Arrow** keys to cycle through and navigate the various widgets in the menu. Press the **Spacebar** key or **Enter** key to perform an action on the highlighted element.



</details>



# Frequently Asked Questions

### What resource pack is used in the demo video and screenshots?

It is the [Golden Days](https://github.com/PoeticRainbow/golden-days/releases) resource pack by **[PoeticRainbow](https://modrinth.com/user/PoeticRainbow)**. There is also a button in the mod's settings homepage that links the resource pack as well.

### Can I include this mod in my pack?

Yes, just try to distribute on the platforms this mod is officially available. (Modrinth and Curseforge)

### Is this mod server safe?

Yes! The mod is designed to disable tweaks that are not allowed on non-modded servers.

### Will there be any backport to version 1.x.x?

Not for Nostalgic Tweaks. Use **[OSECA](https://www.curseforge.com/minecraft/mc-mods/old-swing)**, for versions 1.12, 1.16, and 1.17.

### I ran into an issue running this mod.

Please make a report at our [Github](https://github.com/Adrenix/Nostalgic-Tweaks/issues)
