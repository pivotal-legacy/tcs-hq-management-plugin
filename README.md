# tc Server Hyperic Plugin for 2.x versions

The 'tcs-hq-management-plugin' repository is where tc Server Plugin for Hyperic is configured, assembled, and packaged for download. 
This plugin monitors and manages tc Server Runtimes: tc Runtime 6.x and tc Runtime 7.x.

NOTE: 
* tc Runtime 8.x is monitored in Hyperic by this plugin: https://github.com/pivotal/tcs-hq-product-plugin  


## Build Usage


```
cd build-tcserver-plugin
./ant clean jar package
```
This will clean the build directory and assemble the tc Server plugin bundle. It will place the assembled bundles in 'build-tcserver-plugin/target/artifacts/'.  It will also create a staging area of the expanded artifact in 'build-tcserver-plugin/target/package-expanded/' which can be used to quickly examine the contents of the zip artifact.

## Build Requirements
* JDK 1.6 
* Ant 1.9.3


## Repository Requirements
* git submodule update --init (This will grab the dependent build system 'spring-build')
 



