# Spigot-PlayerPosTracker
Plugin to track the positions of selected Minecraft players on the server

- To track the position of a player, simply type /tracker on <[Player]>.
- To remove the Player, just type in /tracker off <[Player]>.
- To list all tracked players, type /tracker list.

All logs are saved to the plugin folder as poslog.json.

The contained JSON data looks like this:

[
{
"time": "2021-01-20 16:40:20.586",
"posList": [
{
"player": "Patti4832",
"world": "world",
"x": -550,
"y": 75,
"z": 662
}
]
}
]

All commands require op.
The tracking takes place every 10 seconds.
At the moment this plugin only saves 10.000 trackings (the tracking is also displayed in the console; to disable the console output, use the command /tracker quiet to toggle the console output).
