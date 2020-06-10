# temi Welcoming Mode B2B
This is an example skill that uses temi's Welcoming Mode.


## Setup
Clone and compile from source.

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

* A feature list with 3 buttons (`WALK`, `CALL`, `PLAY`) should appear by default on the screen when a person is detected and/or when the screen is touched.

  ![Feature list](/captures/feature_list.png)

* Selecting `WALK` will show all predefined locations. Selecting a location will command temi to go to that location.

  ![Walk](/captures/walk.png)

* Selecting `CALL` will show the user administrator information (i.e. username and avatar), you can start a video call with the administrator by selecting the user's avatar.

  ![CALL](/captures/call.png)

* Selecting `PLAY` will display some of temi's features, such as `Photos` and `Follow me`.

  ![PLAY](/captures/play.png)


## Dependencies
* [RxKotlin](https://github.com/ReactiveX/RxKotlin)
* [Koin](https://github.com/InsertKoinIO/koin)
* [temi SDK](https://github.com/robotemi/sdk)

