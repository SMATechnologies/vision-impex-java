# Vision ImpEx
Utility allowing one to import/export OpCon Vision workspace (cards) between OpCon environments.

# Disclaimer
No Support and No Warranty are provided by SMA Technologies for this project and related material. The use of this project's files is on your own risk.

SMA Technologies assumes no liability for damage caused by the usage of any of the files offered here via this Github repository.

# Prerequisites

1.  Uses the opCon API to perform the functions, so need an opCon-API license
2.  Requires Java version 11.
    can download OpenJDK from https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot

# Instructions
Vision Deploy provides the mechanism to move Vision Definitions between OpCon Systems.
It uses the OpCon-API to perform these functions. 
It consists of two program Export.exe and Import.exe

Export.exe
 
Is used to extract Vision definitions from an opCon System. The export program extracts information at the
top Group Tag level and all tags associated with the group.
  
It supports the following arguments.

  -url           The url of the OpCon System (https://<name>:<port> or http://<name>:<port>).
  -u             The name of an OpCon user that has the appropriate OpCon-API privileges (best to use a user which
                 is a member of the Ocadm group).
	-p             The password of the OpCon user (best to execute program from within OpCon and use encrypted 
	               property for password protection).
	-odir          The name of the directory where the exported file information will be saved. The program creates a single
	               file containing the action, frequency and card definitions. A file name consisting of VISION_EXTRACT_ and 
	               a timestamp (format yyyyMMdd_HHmmss) is created in the output directory (example VISION_EXTRACT_20200317_081140.json).
	-gn            Optional argument defining the name(s) Groups to extract at the top Group Tag level (multiple names should be 
	               comma seperated - default is all).
	-debug         Set program into debug mode.
	
Example :

<install_dir>\Export.exe -url "https://bvhtest02:9010" -u "ocadm" -p "[[password]]" -odir "<install_dir>\data" -gn "LzLabs Demo,Critical" -debug

Import.exe
 
Is used to import Vision definitions from a definitions file into OpCon System. The import program merges the information
contained in the definitions file with the data already defined on the OpCon System. It should be understood that when the
program starts, a copy of the existing Vision definitions are extracted from the OpCon System and the this is merged with
the definitions in the definitions file. Once completed the definitions are written into the OpCon system. 

To prevent loss of Vision definitions during the import process, no other users should be creating Vision definitions.  
  
It supports the following arguments.

  -url           The url of the OpCon System (https://<name>:<port> or http://<name>:<port>).
  -u             The name of an OpCon user that has the appropriate OpCon-API privileges (best to use a user which
                 is a member of the Ocadm group).
	-p             The password of the OpCon user (best to execute program from within OpCon and use encrypted 
	               property for password protection).
	-idir          The name of the directory that contains the file information to import. 
	-f             The name of the file containing the definitions to import (example VISION_EXTRACT_20200317_081140.json).
	-debug         Set program into debug mode.
	
<install_dir>\Import.exe -url "https://bvhtest02:9010" -u "ocadm" -p "[[password]]" -idir "<install_dir>\data" -f "VISION_EXTRACT_20200317_081140.json" -debug

Installation:

Download software from desired Release by selecting and saving the executables.
After download create folder and place executables in the folder <install folder>.
After downloading the OpenJDK, create a <install folder>/java directory off the folder you saved
the executables in.
Extract the Java software and copy this to the <install folder>/java directory.
 
# License
Copyright 2019 SMA Technologies

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

# Contributing
We love contributions, please read our [Contribution Guide](CONTRIBUTING.md) to get started!

# Code of Conduct
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](code-of-conduct.md)
SMA Technologies has adopted the [Contributor Covenant](CODE_OF_CONDUCT.md) as its Code of Conduct, and we expect project participants to adhere to it. Please read the [full text](CODE_OF_CONDUCT.md) so that you can understand what actions will and will not be tolerated.
