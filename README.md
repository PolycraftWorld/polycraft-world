polycraft
=========

Install Java version of eclipse (kepler or newer)
Downlaod RECOMMENDED VERSION of forge: http://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.7.10.html 
extract to C:\Users\[username]\polycraftForge
open command line in C:\Users\[username]\polycraftForge
run "gradlew setupDecompWorkspace --refresh-dependencies" or "./gradlew setupDecompWorkspace --refresh-dependencies" depending on environment. This step may take a few minutes.
Then run "gradlew eclipse" or "./gradlew eclipse"
Run eclipse and point workspace to C:\Users\[username]\polycraftForge\eclipse

open the Git perspective: Window > Perspective > Open Perspective > Git
clone the repo to an arbitrary folder C:\Users\[username]\temp
cut the contents from the cloned repo into your C:\Users\[username]\polycraftForge (you may need to close eclipse) Make sure you are copying hidden folders as well, you need the .git folder to add the repo back.
Delete C:\Users\[username]\temp

open eclipse again. Get to the Git screen. 
add local repository, select folder C:\Users\[username]\polycraftForge and check the file that pops up. should be a .git file.
Go to java perspective, right click main folder in project expolorer > build path > Configure Build Path > Libraries > Add JARs  Select all JAR files in the Lib folder.
modify eclipse setting: Window > Preferences > Java > Editor > Save Actions > Perform actions on save (format all lines, organize imports)

MCP conf folder is in you(user)\.gradle\caches\minecraft\net\minecraftforge\forge\1.7.10-10.13.2.1291(your forge version here)\unpacked\conf

./eclipse/server.properties
	online-mode=false
	op-permission-level=2
	gamemode=0
	allow-flight=true
	and ops.txt
	
-Dfml.coreMods.load=edu.utd.minecraft.mod.polycraft.dynamiclights.DLFMLCorePlugin
-DcheatRecipes=true
-username=a
