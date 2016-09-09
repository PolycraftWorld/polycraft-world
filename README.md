polycraft
=========

http://www.minecraftforge.net/wiki/Installation/Source
Download and then extract to D:\Workspaces\UTD\PolyCraft
Open command line and run gradlew setupDecompWorkspace --refresh-dependencies
Then run gradlew eclipse
Run eclipse (kepler) and point workspace to D:\Workspaces\UTD\polycraft\eclipse
Window > Preferences > Java > Editor > Save Actions > Perform actions on save (format all lines, organize imports)
Download and install git
http://git-scm.com/download/win
Install EGit plugin: http://download.eclipse.org/egit/updates

Clone the polycraft repo to D:\Workspaces\UTD\polycraft_git
https://github.com/WalterVoit/polycraft

Copy polycraft_git into polycraft and delete the example file
Load the local repo in eclipse to link the project

./eclipse/server.properties
	online-mode=false
	op-permission-level=2
	gamemode=0
	allow-flight=true
	and ops.txt

-Dfml.coreMods.load=edu.utd.minecraft.mod.polycraft.dynamiclights.DLFMLCorePlugin
-DcheatRecipes=true
-username=a
