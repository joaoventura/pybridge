# PyBridge

PyBridge is a JNI implementation that allows you to use Python code in a native Android application. It allows you to send String or JSON messages to your Python interpreter without the need for any web frameworks. Instead of using web applications disguised as native applications, you can reuse your Python backend code and implement truly native Android applications.

PyBridge is being used in production on [one of my Android apps](https://play.google.com/store/apps/details?id=com.flatangle.charts) and it shares a large amount of code with [one of my web applications](http://elements.flatangle.com/).

*Shameless plug:* I do contract work, check out my website at http://joaoventura.net/ or buy my apps!


## Overview

This repository shows the source code of an empty Android application with a TextView. When the main activity is started, it simply extracts all the necessary Python files to the device, initializes the Python interpreter and calls a Python function that returns a message that is then set on the TextView.

![App image](https://github.com/joaoventura/pybridge/blob/master/pybridge.png)


## Running the project

Clone this project and open it on the latest Android Studio.

To build the pybridge shared library you need a working Android NDK and SDK. Open the terminal, cd into `app/src/main/jni`, and run `path/to/ndk/ndk-build`. You should have libpython3.8m.so and libpybridge.so in `src/main/libs`.

You must also copy the standard library files in `python-for-android/dists/.../_python_bundle` to `assets/python` along with the `bootstrap.py` file.

Finally, run the project in the Android Studio and you should see a `Hello Python 3.8` message in the screen.


## Compiling Python 3 for Android

PyBridge uses the [python-for-android](https://python-for-android.readthedocs.io/en/latest/) project to compile the Python shared library (libpython3.8m.so) and standard library modules. 

I use `p4a create --requirements=python3 --blacklist-requirements=sqlite3,android,libffi,openssl` to start the build process.  

The python-for-android compiled files are usually inside `~/.local/share/python-for-android/` on Linux. The shared library is in `build/other_builds/python3/.../android-build/` and the compiled stdlib is in `dists/.../_python_bundle`. These files are necessary to compile libpybridge.so and run the project.

The `python-for-android` folder in the root of this repository contains all the files necessary to build libpybridge.so with  Python 3.8.1 without sqlite3, libffi and openssl. If you need any of the blacklisted modules, you can run the command above removing the necessary modules from `blacklist-requirements`. Just re-compile everything and copy the shared libs to the `assets/python` folder.


## How it works

All the relevant changes from an empty Android base application can be found in [this commit
](https://github.com/joaoventura/pybridge/commit/723b7e463ff1a8a3b6ff2bfcae272ce9c07bf800).
The real meat are in the following files:

* [AssetExtractor.java](https://github.com/joaoventura/pybridge/blob/master/app/src/main/java/com/jventura/pybridge/AssetExtractor.java) -
 Extracts the python files from the APK assets folder to the device. We must extract the files to
the device as the Python import mechanism does not recognize files inside the APK file.

* [PyBridge.java](https://github.com/joaoventura/pybridge/blob/master/app/src/main/java/com/jventura/pybridge/PyBridge.java) -
 Implements the Java wrapper for the pybridge.c file. You will use the methods of this class to
start, stop, and send messages to your Python interpreter.

* [pybridge.c](https://github.com/joaoventura/pybridge/blob/master/app/src/main/jni/pybridge.c) -
 Implements the JNI C interface and it is where we really handle the CPython API.

* [bootstrap.py](https://github.com/joaoventura/pybridge/blob/master/app/src/main/assets/python/bootstrap.py) -
 Python script that runs when the Python interpreter is initialized. This file must be used to
configure all necessary Python code.

* [MainActivity.java](https://github.com/joaoventura/pybridge/blob/master/app/src/main/java/com/jventura/pyapp/MainActivity.java) -
 This file just shows how you can use PyBridge to run a Python function. It basically extracts the
Python standard lib and bootstrap file from the APK assets to the device, starts the interpreter,
gets the result from a Python function and updates the TextView accordingly.

The AssetExtractor class provides some utilities that you can use to handle application updates,
such as setting and retrieving the version of the assets or to confirm if the assets are already
extracted on the device. In a production application you will want to extract the files from the APK
only when it runs on the first time or after the application updates.


## Limitations

PyBridge uses python-for-android to cross-compile Python 3.8 to Android. In theory you can use other python-for-android recipes to cross-compile other python libraries such as Numpy. Depending on the library, bundle the compiled modules in the python assets folder together with the standard library, import them and you're probably done.

The performance of the Python interpreter on modern smartphones is more than enough for most use cases,
but you should always consider wrapping PyBridge calls in a separate thread so that you do not block
the main UI thread.

If you have a pure python module with lots of python files, consider adding them to a zip file
and adding the zip file to sys.path in bootstrap.py. It will save time when you extract the module
from the APK assets and it will prevent the creation of pycache files which will only increase the
size of the data consumed by your app. For best performance, consider using only bytecode compiled
files inside the zip file (check [this script](https://github.com/flatangle/flatlib/blob/master/scripts/build.py)
for ideas how to automatically build bytecode compiled zip files).


## License

You can use this project if you want, a simple acknowledgment is enough but not required.
