# 📲 Demo

A demo is available in the `/demo` folder.

### Before running

* Check your system with [KDoctor](https://github.com/Kotlin/kdoctor)
* Install the [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)

### Running

Clone the repository and open it in Android Studio. In the "Run configuration" drop-down you should see the following:

* demo.composeApp
* demo.browser
* demo.desktop
* demo.ios

Select the target you want and click Run.

### Manual

To manually run the demo you can do the following:

#### Android

To run the application on android device/emulator:

* open project in Android Studio and run imported android run configuration

To build the application bundle:

* run `./gradlew :demo:composeApp:assembleDebug`
* find `.apk` file in `demo/composeApp/build/outputs/apk/debug/composeApp-debug.apk`

#### iOS

To run the application on iPhone device/simulator:

* Open `iosApp/iosApp.xcworkspace` in Xcode and run standard configuration
