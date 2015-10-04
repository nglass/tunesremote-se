# Installation Instructions #

## Requirements ##
TunesRemote-SE requires a Java 6 runtime environment to be able run.  I develop and test TunesRemote-SE using the Oracle Java runtime, though others may also work.

Windows, Solaris and Linux users can get the latest Oracle JRE here:
http://java.com/en/download/index.jsp

Currently MacOSX ships with its own Java runtime, though in the future this is going to change. Java 6 is available on OSX 10.6+ (Snow Leopard) on 32-bit and 64-bit Intel processors or on 10.5.8 (Leopard) if using 64-bit Intel processors only.

Ubuntu instructions can be found here:
https://help.ubuntu.com/community/Java

## Downloads ##
There are three download files to choose from - Mac, Windows and Other.  Pick the one relevant to your operating system.

## Windows ##
Unpack the zip file.  Double clicking on the included TunesRemoteSE.exe file should launch the application.  The first time you run TunesRemote-SE you will be asked where you want it to store files.

If you have problems with the application starting, you may also try using the generic Java download.  Unpack the zip file and double clicking on the included TunesRemoteSE.jar file should launch the app.

## Mac OSX ##
Mac users unpack the zip file and drag the TunesRemote-SE application to your Applications folder.  Double clicking the application will launch TunesRemote-SE.  The first time you run TunesRemote-SE you will be asked where you want it to store files.

## Linux / Other ##
Unpack the TunesRemoteSE.jar file from the zip file.
Launch the application by running:
```
java -jar TunesRemoteSE.jar
```
The first time you run TunesRemote-SE you will be asked where you want it to store files.

## Library Pairing ##
When you start TunesRemote-SE you will be (usually be - outstanding issue) presented with a list of all the [DACP-compatible](DACP.md) media players on your local network.  However you need to pair the application with your media player before you can control it.

![http://tunesremote-se.googlecode.com/svn/wiki/libraries.png](http://tunesremote-se.googlecode.com/svn/wiki/libraries.png)

If you try to select a library before you have paired with it a dialog will inform you that TunesRemote-SE has not yet paired with this library.

To start the pairing process click on the Pair button.  You will be presented with a screen that looks like this:

![http://tunesremote-se.googlecode.com/svn/wiki/pairing.png](http://tunesremote-se.googlecode.com/svn/wiki/pairing.png)

After a short time TunesRemote-SE should show up in the devices section of your media player.  There will be one entry for each network interface that it is broadcasting on.

On your mediaplayer select TunesRemote-SE from the devices list and enter the code displayed on the pairing screen.  Your media player should now be paired with TunesRemote-SE and you should be good to go.  Once paired you should not need to repeat the process again.

If on entering a code you do not get a response then try selecting TunesRemote-SE on a different network interface from the devices list and entering the code again.

If you still have problems pairing it is usually due to Firewall issues.  See ConnectionProblems.