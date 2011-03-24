AreaGuard
=========
### by zand ###

Setting Up
---------------

### Dependencies ###
To run this plugin you need a JDBC Driver and, 
for building you need the permissions plugin but, 
its optional for running.

#### JDBC Drivers ####
- [SqliteJDBC](http://www.zentus.com/sqlitejdbc/) if using Sqlite, is a JDBC Driver for Sqlite
- [Connector/J](http://www.mysql.com/downloads/connector/j/) if using MySql, is a JDBC Driver for MySql

#### Bukkit Plugins ####
- [Permissions](http://forums.bukkit.org/threads/1403/) provides an permission system for Bukkit

### Installing ###
Place the `AreaGuard.jar` into the `plugins` folder of your CraftBucket directory.
Download and place the JDBC Drivers that your going to use in the CraftBucket directory.
Load the plugin and it will create the `plugins/AreaGuard/` folder and store the default files in there.
Edit the file `plugins/AreaGuard/areaguard.properties` to your needs.
Give the server the command `ag admin reconfig` to reload the configuration.

### Permissions ###
If using the Permissions plugin you have to assign the following permissions.

#### Nodes ####
- `areaguard.area.modify.owned` To allow them to modify areas they own.
- `areaguard.area.modify.created` To allow them to modify areas they created.
- `areaguard.area.modify.all` To allow them to modify any area.
- `areaguard.cuboid.activate` To allow them to activate a cuboid.
- `areaguard.cuboid.modify.owned` To allow them to modify cuboids in areas they own.
- `areaguard.cuboid.modify.created` To allow them to modify cuboids they created.
- `areaguard.cuboid.modify.all` To allow them to modify any cuboid.

Players also need to have the following variables be set in order to create areas or cuboids.

#### Variables ####
- `areaguard-area-create` The maximum number of areas that player can create.
- `areaguard-cuboid-create` The maximum number of cuboids that player can create.

The following is what a part of a Permissions `.yml` file would look like with AreaGuard.

    Default:
        default: true
        info:
            build: true
            areaguard-area-create: 3
            areaguard-cuboid-create: 5
        inheritance: 
        permissions:
           - 'areaguard.area.modify.owned'
           - 'areaguard.area.modify.created'
           - 'areaguard.cuboid.modify.owned'
           - 'areaguard.cuboid.modify.created'
    Moderator:
        default: false
        info:
            build: true
            areaguard-area-create: 10
            areaguard-cuboid-create: 25
        inheritance:
           - Default
        permissions:
           - 'areaguard.area.modify.*'
           - 'areaguard.cuboid.modify.*'
           - 'areaguard.cuboid.activate'
    Admins:
        default: false
        info:
            build: true
            areaguard-area-create: 100
            areaguard-cuboid-create: 250
        inheritance:
           - Moderator
        permissions:
           - '*'

Commands
--------
### Main ###
- `ag ver` Shows the version info.
- `ag admin [...]` Admin Commands.
- `ag area [...]` Area Commands.
- `ag cuboid [...]` Cuboid Commands.
- `ag debug [...]` Debug Commands.
- `ag info [...]` Displays the current Session info.

### Admin ###
- `ag admin info` Shows the plugin status.
- `ag admin reconfig` Reloads the plugins config.

### Area ###
- `area create [name]` Creates a new area.
- `area delete` Deletes the selected area.
- `area owned [by <player>]` Gets areas owned by player.
- `area select [<player>|my] <name>` Selects an area with that name owned by player if given.
- `area select <id>` Selects an area with that id.
- `area select <x> <y> <z>` Selects an area at that position.
- `area add <list> [values...]` Adds the values to the list.
- `area remove <list> [values...]` Removes the values from the list.
- `area clear <list>` Removes the list.
- `area msg <event> [message...]` Sets the message for that event.
- `area show <event>` Shows the message for that event.

### Cuboid ###
- `cuboid create` Creates a new cuboid for the selected area.
- `cuboid activate` Activates the selected cuboid.
- `cuboid deactivate` Deactivates the selected cuboid.
- `cuboid delete` Deletes the selected cuboid.
- `cuboid list` Lists the cuboids in the selected area.
- `cuboid move` Moves the selected area.
- `cuboid select <id>` Selects a cuboid by ID.

### Point ###

Lists
=====
All lists accept for `owners` and `restrict` have a alternate list with a `no-` prefix.

### Main Lists ###
- `owners` A list of the areas owners.
- `restrict` A list of what events to restrict.
- `allow` A list of players that can bypass all restrictions.
- `enter` A list of players that can enter the area.
- `build` A list of players that can create and destroy blocks.
- `open` A list of players that can use, create and, destroy container blocks.
- `pvp` A list of players that can harm or, be harmed by players.
- `mobs` A list of players that can harm or, be harmed by mobs.
- `use` A list of players that can use, create and, destroy blocks.

### Mob Lists ###
Each has anything matching the first 4 letters of the name as an the alias.  
`chicken` `cow` `creeper` `ghast` `pig` `pigzombie` `sheep` `skeleton`
`spider` `zombie` `squid` `slime`

### Block Lists ###
These are lists of players that can use, create and, destroy blocks with the same name as the list.
Each one has the block id as an the alias.  
`stone` `grass` `dirt` `cobblestone` `wood` `sapling` `bedrock` `water`
`stationary-water` `lava` `stationary-lava` `sand` `gravel` `gold-ore`
`iron-ore` `coal-ore` `log` `leaves` `sponge` `glass` `lapis-ore`
`lapis-block` `dispenser` `sandstone` `note-block` `bed-block` `wool`
`yellow-flower` `red-rose` `brown-mushroom` `red-mushroom` `gold-block`
`iron-block` `double-step` `step` `brick` `tnt` `bookshelf`
`mossy-cobblestone` `obsidian` `torch` `fire` `mob-spawner`
`wood-stairs` `chest` `redstone-wire` `diamond-ore` `diamond-block`
`workbench` `crops` `soil` `furnace` `burning-furnace` `sign-post`
`wooden-door`

Msgs
=====
All lists accept for `owners` and `restrict` have a alternate list with a `no-` prefix.

### Main Msgs ###
- `allow` Sent when players do anything.
- `enter` Sent when players enter the area.
- `leave` Sent when players leave the area.
- `build` Sent when players create and destroy blocks.
- `open` Sent when players use, create and, destroy container blocks.
- `pvp` Sent when players harm players.
- `mobs` Sent when players harm mobs.
- `use` Sent when players use, create and, destroy blocks.

### Mob Msgs ###
Sent when players harm mobs with the same name as the msg.
Each has anything matching the first 4 letters of the name as an the alias.  
`chicken` `cow` `creeper` `ghast` `pig` `pigzombie` `sheep` `skeleton`
`spider` `zombie` `squid` `slime`

### Block Msgs ###
These are lists that are sent when players use, create and, destroy blocks with the same name as the msg.
Each one has the block id as an the alias.  
`stone` `grass` `dirt` `cobblestone` `wood` `sapling` `bedrock` `water`
`stationary-water` `lava` `stationary-lava` `sand` `gravel` `gold-ore`
`iron-ore` `coal-ore` `log` `leaves` `sponge` `glass` `lapis-ore`
`lapis-block` `dispenser` `sandstone` `note-block` `bed-block` `wool`
`yellow-flower` `red-rose` `brown-mushroom` `red-mushroom` `gold-block`
`iron-block` `double-step` `step` `brick` `tnt` `bookshelf`
`mossy-cobblestone` `obsidian` `torch` `fire` `mob-spawner`
`wood-stairs` `chest` `redstone-wire` `diamond-ore` `diamond-block`
`workbench` `crops` `soil` `furnace` `burning-furnace` `sign-post`
`wooden-door`