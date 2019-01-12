# Railcraft - A Minecraft Mod

Here you will find the source and issue tracker for the **Official Railcraft Project**.


## What is Railcraft?

Railcraft is a mod written for the hit game [Minecraft](https://minecraft.net/). It is built on top of the [Minecraft Forge](https://github.com/MinecraftForge) API.

It greatly expands and improves the Minecart system in Minecraft. Adding many new blocks, entities, and features. It has been in development since 2012 and contains over 800 class files and hundreds of thousands of lines of code.

The mod was created and is still currently maintained by the user going by the name **CovertJaguar**.

## Why are you posting the Source Code?

In the words of **CovertJaguar**:
> As a new modder, I originally feared losing control of my code, my brainchild. However, since that time, I have had the privelege of being Project Lead on two other major Minecraft Mod projects that provided access to the source: [Buildcraft](https://github.com/BuildCraft/BuildCraft) and [Forestry](https://github.com/ForestryMC/ForestryMC). I've generally found this to be a positive experience resulting in many bug fixes and increased intermod compatibility.  While I still have some concerns, I have come to feel that the benefits of providing others access to my source code outweigh the negatives and unknowns. To that end, despite my misgivings, I made Source Access a [Patreon Milestone Goal](http://www.patreon.com/CovertJaguar). I had no idea whether I'd ever meet that Goal, but I decided to let the community decide, and decide they did, overwhelmingly so! My Patrons are awesome. So, as promised, I am posting the Source Code.

## Official Links

* The Blog, Forums, and main download page: http://www.railcraft.info
* The Wiki: http://railcraft.info/wiki
* IRC: #railcraft on Esper.net - [WebChat](http://webchat.esper.net/?nick=RailcraftGithub...&channels=railcraft&prompt=1)
* Discord: [Invite](https://discord.gg/VyaUt2r) - Linked with #railcraft on IRC
* Patreon Page: http://www.patreon.com/CovertJaguar

<a href="http://www.patreon.com/CovertJaguar"> ![Patreon](http://www.railcraft.info/wp-content/uploads/2014/05/Patreon.png)</a>

## Issues

Post only confirmed bugs [here](https://github.com/CovertJaguar/Railcraft/issues). Do not post crash logs, you can post pastebin links to FML logs, but no crash logs.

You must have read and performed the proper support procedure outlined [here](http://railcraft.info/wiki/info:support) before posting.

More information about issue Labels can be found [here](https://github.com/CovertJaguar/Railcraft/wiki/Issue-Labels).

## Contributing

The Official Railcraft Project welcomes contributions from anyone, provided they have signed the Contributor Licensing Agreement (CLA) found [here](https://cla-assistant.io/CovertJaguar/Railcraft).

Signing the CLA is simple, just follow the link, and hit the "I Agree" button and you are good to go.

Regarding new features/behavior changes, please submit a Suggestion Issue to the Tracker before you write a single line of code. Keeping everyone on the same page saves time and effort and reduces negative experiences all around when a change turns out to be controversial.

Also, as a bonus. If you submit a Pull Request that ends up being merged you are eligible to receive beta access. To redeem, contact CovertJaguar on IRC.

## Building

The Railcraft Project follows standard Forge conventions for setting up and building a project, with a couple additional details (details to come).

You can create a gradle.properties file in the project root with the following properties:
```
mcUsername=Steve
mcPassword=ILoveNotch
```

Initial Setup from the Command Line:
```
gradlew setupDecompWorkspace
```

The [API](https://github.com/CovertJaguar/Railcraft-API) and [Localization](https://github.com/CovertJaguar/Railcraft-Localization) files reside in their own repositories and are pulled automatically into the main repo as git submodules. You will however need to run the following commands:
```
git submodule init
git submodule update
```

For more information on setting up an Intellij modding environement see cpw's video:
https://youtu.be/G2aPT36kf60

To build, run:
```
gradlew build
```

More information [here](https://github.com/Railcraft/Railcraft/wiki/Running-instructions-for-Minecraft-1.12.2).

## License

Railcraft is licensed under a custom usage license tailored specifically for the project. It can be read [here](https://github.com/CovertJaguar/Railcraft/blob/master/LICENSE.md).

 * Key things to keep in mind:
  * You may **NOT** create works using the Railcraft code (source or binary) without CovertJaguar's explicit permission except in the cases listed in this license.
  * You may **NOT** create derivative Jars from Railcraft source to distribute to other users.
  * You **MAY** use snippets of Railcraft Code posted on the Official Github in your own projects, but only if your project consists of less than 25% of Railcraft derived code. You must give credit to the Railcraft Project for the code used, including a link to the Github Project. Put this in your class file headers that contain Railcraft code, in your readme, and on the main download page.
  * You may **NOT** use Railcraft Art Assets in other projects **unless** the project is intended to operate alongside Railcraft. Examples are Addons, Resource Packs and InterMod Integration.
  * You **MAY** fork and edit the Github Project for the purpose of contributing to the Official Railcraft Project. You may **NOT** distribute any Jar created from a fork for any reason.
  * All contributions to the Official Railcraft Project must be covered by a Contributor Licensing Agreement signed by the contributor.

# Wisdom
“Surely there is a mine for silver, And a place where gold is refined. Iron is taken from the earth, And copper is smelted from ore.

Man puts an end to darkness, And searches every recess for ore in the darkness and the shadow of creepers. He breaks open a shaft away from villages; In places forgotten by feet they dig far away from villagers; They jump to and fro.

As for the earth, from it comes bread, But underneath it is turned up by fire and lava; Its stones are the source of emeralds, And it contains redstone dust. That path no bird knows, Nor has the parrot’s eye seen it. The proud ocelot has not trodden it, Nor has the fierce wolf passed over it.

He puts his hand on the flint; He overturns the mountains at the roots. He cuts out channels in the rocks, And his eye sees every precious thing. He dams up the streams from trickling; What is hidden he brings forth to light.

But where can wisdom be found? And where is the place of understanding? Man does not know its value, Nor is it found in the Nether or the End. The void says, ‘It is not in me’; And the sea says, ‘It is not with me.’ It cannot be purchased for gold, Nor can silver be weighed for its price. It cannot be valued in stars, in precious obsidian or the heart of the sea. Neither gold nor crystal can equal it, Nor can it be exchanged for jewelry of fine gold. No mention shall be made of coral or quartz, For the price of wisdom is above emeralds. Lapis Lazuli cannot equal it, Nor can it be valued in ingots of gold.

From where then does wisdom come? And where is the place of understanding? It is hidden from the eyes of all living, And concealed from the birds of the air. If you search through the bones and question the dead, they say, 'we've heard only rumors of it.'

God understands its way, And He knows its place. For He looks to the ends of the earth, And sees under the whole heavens, To establish a weight for the wind, And apportion the waters by measure. When He made a law for the rain, And a path for the thunderbolt, Then He saw wisdom and declared it; He prepared it, indeed, He searched it out.

And to man He said, ‘Behold, the reverence and love of the Lord, that is wisdom, And to depart from evil is understanding.’ ”

Job 28:1‭-‬28 (Minecraft Edition)
