HOW TO UPDATE THE INSTALLER WITH A NEW VERSION OF POLYCRAFT
-----------------------------------------------------------
1. Open src/main/resources/install_profile.json
2. Find the "resources" section and update all of the version numbers for the new Polycraft release.
3. No need to recompile - open the polycraft-1.0.3-installer.jar (or whichever version it is currently)
4. Replace the install_profile.json with the new one that was just created
5. Rename the installer to the new version.

That's it.