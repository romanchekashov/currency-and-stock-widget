## Currency and Stock Widget: Privacy policy

Welcome to the Currency and Stock Widget app for Android!

This is an open source Android app developed by Roman Chekashov. The source code is available on GitHub under the Apache License
Version 2.0; the app is also available on Google Play.

As an avid Android user myself, I take privacy very seriously.
I know how irritating it is when apps collect your data without your knowledge.

I hereby state, to the best of my knowledge and belief, that I have not programmed this app to collect any personally identifiable information. All data (app preferences (like theme, etc.) and alarms) created by the you (the user) is stored on your device only, and can be simply erased by clearing the app's data or uninstalling it.

### Explanation of permissions requested in the widget app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file:

https://github.com/romanchekashov/currency-and-stock-widget/blob/master/android/src/main/AndroidManifest.xml#L5-L9

<br/>

| Permission | Why it is required                                                                                                                                                                                                                                                                                                         |
| :---: |----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `android.permission.INTERNET` | To refresh quotes app make request to my service which collects data from open resources in internet. It doesn't collect and data from mobile app just provides it.                                                                                                                                                        |
| `android.permission.ACCESS_NETWORK_STATE` | Before refresh quotes app should check that internet is available.                                                                                                                                                                                                                                                         |
| `android.permission.RECEIVE_BOOT_COMPLETED` | When your device restarts, all alarms set in the system are lost. This permission enables the app to receive a message from the system once the system has rebooted and you have unlocked your device the first time. When this message is received, the app creates a service to set all the active alarms in the system. |
| `android.permission.SCHEDULE_EXACT_ALARM` | This is required to schedule an exact alarm, and was introduced in Android 12. You, as the user, or the system, can revoke this permission at any time from Settings. Revoking this permission will, however, kill the app immediately if it was alive, and cancel all alarms set by the app.                              |
| `com.google.android.gms.permission.AD_ID` | While your are selecting quotes to watch you also see ads for short period of time. This permission enables the google ads. After widget configured you will not see any ads at all.                                                                                                                                       |

 <hr style="border:1px solid gray">

If you find any security vulnerability that has been inadvertently caused by me, or have any question regarding how the app protectes your privacy, please send me an email or post a discussion on GitHub, and I will surely try to fix it/help you.

Yours sincerely,  
Roman Chekashov.  
Novi Sad, Serbia.  
romanrich89@gmail.com
