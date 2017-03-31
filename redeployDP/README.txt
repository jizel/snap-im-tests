This batch file will:

 - Pull from specified branch (in Data-platform and Data-platform-identity projects)
 - Build all DP modules (without tests)
 - Drop existing Maria DB tables
 - perform DB migrate
 - Delete all war files from Tomcat's webapps folder
 - Copy freshly build wars to Tomcat
 - Load fake sql data for integrations from DataPlatformQA project

How to use:

 - Open the redeployDP4me.bat in notepad
 - Set correct paths to your DataPlatform project (DP_DIR), Data-platform-identity (DP_IDENTITY_DIR), DataPlatformQA's fake data (DP_QA_FAKE_DATA_DIR), Tomcat's webapps directory (WEBAPPS_DIR) and DP branch you want to pull (git_branch)
 - Save the file, run it and wait...

	Enjoy