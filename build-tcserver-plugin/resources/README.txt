Pivotal tc Server HQ Plugin
@bundle.version@

You have downloaded the Pivotal tc Server Hyperic Plugin which enables the Hyperic HQEE 5.x Server to manage and
monitor tc Server.

NOTE: This should not be installed on releases of Hyperic HQEE prior to 5.0.


How Do I Get Started?
---------------------
See the "Getting Started with tc Server" Guide for conceptual information about tc Server, as
well as installation and usage instructions:

	https://tcserver.docs.pivotal.io/

In particular, see the tutorial on using Hyperic HQEE to manage and monitor tc Server instances:

	https://tcserver.docs.pivotal.io/docs-tcserver/topics/tuthyperic.html


To install this plugin into an existing Hyperic HQEE 5.x Server:
	
	1) Run the tc Server plugin installer 
		$ bin/install.sh <hqee-server-root>
	For example, if you installed Hyperic HQEE Server in the "/home/hyperic/server-5.0.0-EE" directory:
		$ bin/install.sh /home/hyperic/server-5.0.0-EE
	2) After a few minutes the Hyperic Server should automatically synchronize the latest plugin with the agents.
	3) We also recommend cleaning the browser cache before proceeding.
	4) If the Hyperic Server is installed on Windows it will require restarting to detect all the plugin updates.
	

Additional Information
-----------------------
See the Hyperic HQ Documentation of instructions on starting and stopping the HQ Server and Agents:

	* https://www.vmware.com/support/pubs/vfabric-hyperic.html

See the following links for additional information about tc Server:

	* Product Information: https://www.pivotal.io/products/pivotal-tc-server
	* General tc Server Documentation: https://tcserver.docs.pivotal.io/
	* Hyperic HQ Documentation: https://www.vmware.com/support/pubs/vfabric-hyperic.html
