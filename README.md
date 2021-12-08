# AR Reshare App

### An application that allows people to share the goods, gifts and food they no longer need or want.

The app allows a contributor to list an item they would like to share/offer for free along with their postcode. A consumer can either search for an item or they can view their local area via an AR map view. The consumer can then send a request for an item via the tool, get in touch with the contributor and arrange a pickup.

## Set Up and Build Instructions
To build and run this project we recommend using Android Studio due to its easiness of use and the ability to sync all dependencies.

In Android Studio
1. Go to *File > Open...* and choose the location of the project.
2. Select *Trust project* and wait for Gradle sync to be finished.
3. In the tool bar, select *Build > Make Project*.
4. In the tool bar, select *Run > Edit Configurations*. Ensure that the module is selected to be *AR-Reshare.app* and that the Launch Activity is specified to *Default Activity*.

To run the application on a physical Android Device:
1. Go to *Settings > About Phone > Build Number* and repeatedly press *Build Number* 7 times to enable the *Developer Options*.
2. Connect the Android device to your computer.
3. Choose to *Trust the Computer* and give all necessary permissions.
4. In Android Studio, select *Run > Select Device...* and choose your device.
5. Now select *Run > Run 'app'* to install and run the application directly on your device.

To run the application on an emulated Android Device:
1. In Android Studio, select *Run > Select Device... > AVD Manager*.
2. Select *Create Virtual Device* and choose one of the devices available. 
3. Select Android System with **API Level 27 (8.1 Oreo) or above**.
4. Once the installation is complete, select your new emulated device and go to *Run > Run 'app'*.
