This batch file will:

 - Pull from specified branch
 - Build all DP modules (without tests)
 - Drop existing Maria DB tables
 - perform DB migrate
 - Delete all war files from Tomcat's webapps folder
 - Copy freshly build wars to Tomcat

How to use:

 - Open the redeployDP4me.bat in notepad
 - Set correct paths to your DataPlatform project (DP_DIR), Tomcat's webapps directory (WEBAPPS_DIR) and DP branch you want to pull (git_branch)
 - Save the file, run it and wait...

	Enjoy