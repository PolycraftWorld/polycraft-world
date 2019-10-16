polycraft
=========
The ultimate biology and chemistry mod.
## Setup as AI Gym 
Clone this repository.<br>
Install the Java development kit.<br>
run "LaunchPolycraft.bat" to build and run the client. By default the client will start up and be accessible through our API socket on port 9000<br>
Make sure you have Python 3.7 installed. <br>
After the game starts up, you can test the API by running "testSocket.py" in "PolycraftAIGym/"<br>
Always run the "START" command first. Other commands assume the world has already been initialized. After the world has been started, you can test the pogostick experiment by sending "RESET POGO".  Try moving around the map as the bot with "MOVE_FORWARD", "TURN_NORTH", "TURN_SOUTH", "TURN_EAST", "TURN_WEST". 

## Installing the Java Development Kit
Java SE JDK 8 is needed for this environment. At the moment, the Gradle programs will not work with Java 10.
You can install the JDK through <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">this site</a>.<br>
Scroll down until you see "Java SE 8u231" and select the download button below "JDK".<br>
Click the "Accept License Agreement" button and select the product corresponding to your operating system and run the installation.

## Installing the Eclipse IDE
Installing at least Eclipse Oxygen or Neon is recommended, though versions as early as Kepler version (including Luna and Mars) should work as well. Photon/2018-XX and later versions are unconfirmed as working but should work. The installation site can be found at [eclipse.org](http://www.eclipse.org/downloads/packages/).

The majority of Eclipse versions should come with the Git plugin by default. If you don't know which one to select, download "Eclipse IDE for Java Developers" and the corresponding OS and architecture. This will come in a zipped archive. You can unpack this and place it anywhere you want; Eclipse will run as is.

## Setting up the Forge Environment
The files for Minecraft Forge 1.8.9 can be found [here](http://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.8.9.html). Download the "Src" of the first (latest) version: 11.15.1.2318.<br>
Select a folder that you will have your Polycraft development environment in. We will call this directory A. Place the contents of the zip directly inside directory A.

Open a command prompt and navigate to directory A and run the following...

Windows:
```
gradlew setupDecompWorkspace --refresh-dependencies
gradlew eclipse
```

Mac/Linux:
```
./gradlew setupDecompWorkspace --refresh-dependencies
./gradlew eclipse
```

Both commands should be successful. If not, double check your steps prior to running these.

### Copying the Polycraft Repository
You can download or ``git clone`` this Polycraft repository into any folder other than directory A. (I am not sure if ``git clone`` will delete any extra files.)

After you have downloaded the repository files, move them all into directory A and replace existing files.

## Setting up Eclipse with the Forge Workspace
Open your Eclipse installation and point the workspace to directory A/eclipse/<br>
You will have to wait a bit for Eclipse to set itself up for the first time. When it is done, there will be several hundred errors from different packages. This is normal and we will fix some of these in the next step.

In the left view of Eclipse, the "Package Explorer", we have a single project named "Minecraft". Expand this, expand "src/main/java" and then delete the package "com.example.examplemod".

### Adding the Azure Storage Library
Right click the project "Minecraft" and navigate to "Build Path" -> "Configure Build Path...".<br>
At the "Libraries" tab, select "Add External JARs..." and navigate to this file:<br>
A/lib/azure-storage-2.0.0.jar<br>
(Don't worry about the other .jar files. There will still be errors, but now the environment can be run.)

### Syncing to the Polycraft Repository
At the top of Eclipse select the menu item "Window" -> "Show View" -> "Other..." and select "Git" -> "Git Repositories" from the menu. A new view will open in the bottom-left corner.<br>
In the bottom-left corner, select "Add an existing local Git repository" and navigate to the directory A folder and press "OK".<br>
(At this point, if you've deleted the two things I told you to, this repository will be perfectly synchronized with the repository you cloned/downloaded from.)

### Configuring the Test Client
At the top of Eclipse select the menu item "Run" -> "Run Configurations...".<br>
Alternatively, next to the green play button, select the drop down arrow and select "Run Configurations...".

In the "Java Application" drop down, select the first "Client" icon, select the "Arguments" tab and paste the following in the "Program arguments" textbox:
```
-Dfml.coreMods.load=edu.utd.minecraft.mod.polycraft.dynamiclights.DLFMLCorePlugin
-DcheatRecipes=true
-username=a
```
Replace "a" with whatever username you want to use. Now you can run the client. Select "Proceed" when asked about errors. It's only because the some third-party libraries aren't linked.

Optionally if you want to login as your actual character, you can add "-password=pass" your password to complete authentication to Minecraft and have your skin in the test environment.<br>
IF YOUR ACCOUNT IS MIGRATED: Change the "-username" parameter to your Mojang account email. This will return an error if your "-username" is not set to your email address of your Mojang account.

### Configuring the Test Server
To run the server, you need to run it once and let it crash. The error is due to a "eula.txt" being created and set to false.<br>
Once it runs and stops, navigate to directory A/eclipse/eula.txt and change the "eula=false" to “eula=true”. You can now run a local server.

Configuration for "A/eclipse/server.properties" file:
```
online-mode=false
op-permission-level=2
gamemode=0
allow-flight=true
```

To run an experiments server, duplicate the debug configuration of any preexisting Server and add the ``-DisExperimentServer`` flag to the *VM Arguments* textbox in the **Arguments** tab. Be sure to separate it from other flags with a simple space or a line break:

### (Optional) Not Enough Items Configuration:
Right click the project "Minecraft" and navigate to "Build Path" -> "Configure Build Path...".<br>
At the "Libraries" tab, select "Add External JARs..." and navigate to A/lib/ and select all the .jar files that contain the word "dev" in them. (After this there should be zero errors in your environment lol.)

The next time you run the client or server, you will be asked to "Select an mcp conf dir for the deobfuscator". Navigate to the following where "user" is your username on your computer:

Windows: C:/Users/user/.gradle/caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/unpacked/conf<br>
Mac (?): /Users/user/.gradle/caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/unpacked/conf<br>
Linux: /home/user/.gradle/caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/unpacked/conf

Optionally, but useful, you can link the source files to the NEI libraries if you're working with the NEI plugin code.<br>
Right click the project "Minecraft" and navigate to "Build Path" -> "Configure Build Path...".<br>
At the "Libraries" tab, locate "CodeChickenCore-1.7.10-1.0.7.47-dev.jar" and click the drop down arrow next to it.<br>
Double click "Source attachment", select "External location" and click "External File..."<br>
Navigate to the file A/lib/CodeChickenCore-1.7.10-1.0.7.47-src.jar, select it and click "OK".<br>
Do the same for "NotEnoughItems-1.7.10-1.0.5-120-dev.jar" and its corresponding source. (You don't need a source attachment for CodeChickenLib.)

## Old Installation Steps
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
