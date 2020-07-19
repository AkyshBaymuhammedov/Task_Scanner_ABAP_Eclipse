# Task Scanner for ABAP in Eclipse

ABAP Task Scanner is an Open Source Eclipse plugin which provides the possibility to scan for TODOs, FIXMEs and XXXs in the source code of ABAP Objects such as Classes, Interfaces, Programs, Includes and Function Modules.

## Main Features:

* Scan for TODOs, FIXMEs, XXXs in ABAP Objects including in local classes and local test classes
* Scan for a custon text in ABAP Objects
* Scan objects from a package and also in subpackages (depending on the "Deep Scan" option in preferences)
* Scan objects from local temporary package ($TMP)
* Scan objects in Transport Request through the "Transport Organizer" View
* Scan only objects created by the user
* Display markers at "Problems" view for all found Tasks
* Navigate to the line in code where the tasks are found
* Highlight the line in code where the tasks are found

## How to use it

Click right on the object which you want to scan and in the context menu select the item **Task Scanner** then press **Scan Source Code**. Or you can just press the short cut **Ctrl + 9**.  

To scan objects in a Transport request, open the **Transport Organizer** view in Eclipse and click right on the Transport request which you want to scan and select **Task Scanner** then press **Scan Source Code**. Alternatively you can also press the short cut **Ctrl + 9** on the Transport request.  

Markers will be displayed on the **Problems Tab** with the task description, object name and the line number where the tasks were found. By double clicking on the marker, you can navigate to the line and see the task highlighted.

![alt text](https://github.com/AkyshBaymuhammedov/Task_Scanner_ABAP_Eclipse/raw/master/docu/screenshot_1.PNG?raw=true)  


## Settings and Preferences

In the Preferences page, you can include or exclude task types to scan, give a custom tag to scan, set if to do deep scans in packages and also set the option to scan only objects created by you in packages and transport requests.

![alt text](https://github.com/AkyshBaymuhammedov/Task_Scanner_ABAP_Eclipse/raw/master/docu/preferences.PNG?raw=true)  


## Prerequisites:

1. ABAP Development Tools (ADT) must be installed on the Eclipse installation
1. Following abapGit Repository must be installed on the target system: https://github.com/AkyshBaymuhammedov/ADT_TODO_Plugin_ABAP.git (Netweaver 7.40 SP08+)

The plugin works with Eclipse Photon and newer versions with JAVA 8 or higher.
