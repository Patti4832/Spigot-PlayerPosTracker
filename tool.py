
#install libraries via pip
import subprocess
import sys

def install(package):
    subprocess.check_call([sys.executable, "-m", "pip", "install", package])

install("numpy")
install("matplotlib")

#import
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import json, random

plt.figure(figsize=[60, 60])

#load json file (geofences) if existing
try:
    f = open("fences.json")
    content = f.read()
    f.close()
    obj = json.loads(content)

    for fence in obj:
        name = fence['name']
        x = fence['x']
        z = -fence['z']
        r = fence['radius']

        #plot
        circle = plt.Circle((x, z), r, color='gray', clip_on=False)
        plt.gca().add_patch(circle)

except:
    pass

#user input
path = str(input("Select file (default: poslog.json): "))
if(path == ""):
    path = "poslog.json"
print("Selected file:",path)

#load json file
f = open(path)
content = f.read()
f.close()
obj = json.loads(content)

def get_cmap(n, name='hsv'):
    return plt.cm.get_cmap(name, n)
colors = get_cmap(30)

li = {}
last = {}

patches=[]

for track in obj:
    time = track['time']    #timestamp
    poslist = track['posList']  #positions list
    for p in poslist:
        player = p['player']
        world = p['world']
        x = p['x']
        y = p['y']  #Not useable in 2d
        z = -p['z']

        y = z #in our case, y is the z axis in minecraft for 2d

        if(world=='world'): #filter to show overworld only
            #plot
            if(player not in li):
                col = colors(random.randint(0,30))
                li[player] = col
                patches.append(mpatches.Patch(color=li[player], label=player))
            plt.plot(x, y, marker='o', markersize=1, color=li[player])
            if(player in last and last[player] != None):
                plt.plot([last[player][0],x],[last[player][1],y], linestyle='solid', color=li[player])
            last[player] = [x,y]
        else:
            last[player] = None

patches.append(mpatches.Patch(color='gray', label='Fences'))
plt.legend(handles=patches)

plt.axis('scaled')
plt.savefig("figure.jpg", dpi=300)
plt.show()