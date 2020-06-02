
# temi Welcoming Mode B2B
This is an example skill that uses tēmi's Welcoming Mode.


## Installation
```
adb install -r -t /path/to/welcomingmode-b2b.apk
```

You can find releases [here](https://github.com/ray-hrst/temi-welcomingmode-b2b/releases)


## How to Modify
* All Japanese text should appear in:
	*  `app/src/main/res/values-ja-rJP/strings.xml`.
* To remove a `feature` or `button` edit: 
	* `app/src/main/res/values/arrays.xml`.
* To change the behaviour of a `feature`, edit:
	*  `app/src/main/java/com/robotemi/welcomingbtob/featurelist/FeatureListFragment.kt`. 
	* For example, a feature could be used to launch another app:
		```
	  	protected void launchApp(String packageName) {
	        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);

	        if (mIntent != null) {
	            try {
	                startActivity(mIntent);
	            } catch (ActivityNotFoundException err) {
	                Toast t = Toast.makeText(getApplicationContext(), R.string.app_not_found, Toast.LENGTH_SHORT);
	                t.show();
	            }
	        }
	    }
		```


## Setup
Clone and compile from source or download an [pre-built APK](https://github.com/ray-hrst/temi-welcomingmode-b2b/releases).

On temi,
* Go to `Settings` > `Kiosk mode` and turn on kiosk mode
* Select this skill from the list of skills
* Go to `Settings` > `General Settings` and turn on `Welcoming Mode`
* Return to home page

### Hidden Settings Menu
A long press in the top-right corner of the screen will open a hidden `Settings` menu, which will also allow the user to exit the App and return to temi's Launcher.


## Behaviour
* When a person is detected by temi, "Hello" will appear on the screen.

  ![Hello](/captures/hello.png)

* A feature list with 3 buttons (`WALK`, `CALL`, `PLAY`) should appear by default on the screen when a person is or the screen is touched.

  ![Feature list](/captures/feature_list.png)

* Selecting `WALK` will show the user pre-saved locations. temi will go to the selected location.

  ![Walk](/captures/walk.png)

* Selecting `CALL` will show the user administrator information (i.e. username and avatar), you can start a video call with the administrator by selecting the user's avatar.

  ![CALL](/captures/call.png)

* Selecting `PLAY` will show you some of temi's features, such as `Photos` and `Follow me`.

  ![PLAY](/captures/play.png)


## Dependencies
* RxKotlin
* Koin
* temi SDK

