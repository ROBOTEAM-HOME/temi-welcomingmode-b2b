# temi-welcomingmode-b2b

| SDK Version | Robox OS | Launcher OS |
|-------------|----------|-------------|
|<b>Not supported yet</b>|<b>115+</b>|<b>Not supported yet</b>|

## Synopsis
This project is a Welcoming Mode B2B skill and as an example to teach developers how to use the Welcoming Mode of tēmi in their own App.

## Development
RxKotlin, Koin, temi SDK

## Usage flow

### Start this skill in temi

1. Clone this project to your computer and run it to temi.

2. On temi - go to Settings -> Kiosk mode -> Turn on the kiosk mode from the top right corner.

3. Select this skill from the list of skills.

4. Return back to Settings -> General Settings -> Turn on the Welcoming Mode.

5. Return to home page.

### Enjoy

1. When some body is detected by temi, "Hello" will be showing on the screen.

  ![Hello](/captures/hello.png)

2. A feature list with 3 buttons ("WALK", "CALL", "PLAY") would apper on the screen if some body is detected or screen be clicked.

  ![Feature list](/captures/feature_list.png)

3. Click "WALK", screen will show you the location list you saved. And temi will go to that location if you click the button with location name.

  ![Walk](/captures/walk.png)

4. Click "CALL", screen will show you the administrator information with username and avatar, you can start a video call with the administrator by clicking the button.

  ![CALL](/captures/call.png)

5. Click "PLAY", screen will show you the best features of temi such as "Take a selfi" and "Follow me". you also can enjoy these features by clicking the corresponding buttons.

  ![PLAY](/captures/play.png)

6. Lastly, you can long press the top right corner of the screen to return to the App list of temi Launcher.
