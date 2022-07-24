# JADA (Just Another Dictionary App)
(Licensed under the MIT License)

<img src="./assets/icon.png" alt="drawing" width="125"/>

### Update as of Monday, 25th of July, 2022
JADA is no longer work under progress or being maintained. You are free to send fork, send pull requests to, clone or maintain this project. The reason for this termination is the change in Oxford's pricing model (and the end of the Protoype plan which allowed JADA 1000 calls per month) and the lifting of support for other languages by the API developed by MeetDeveloper (@meetDeveloper).

---

**Keep in mind that JADA is not a translator app. Nothing does a better job of being a translator app than Google Translate. JADA provides you definitions of a word in the same language as the one the word is from.**

### Collaborators
* [Patricia B.](https://github.com/pborlongan): The designer of the app's new UI overhaul. Most of the design was designed by her, I am highly thankful to her for helping me out.

## Download
~~Currently the app is only available on Github releases due to some compilcated personal reasons. You can [download the app from Github releases here.](https://github.com/sbeve72/JADA/releases/latest)~~. JADA no longer works. You can still download the apk from Github releases if you need it for other purposes.

## Multi-language Support

This dictionary supports multiple language as the API I am using does too. The languages supported are the following

* English US
* English UK
* Hindi
* French
* Gujarati
* Latvian
* Romanian
* Tamil
* Swahili
* Spanish

## Versioning

The app uses a special blend of [Semantic Versioning](https://semver.org/#semantic-versioning-200) and some conventions decided by me to follow for this app since the guidelines for MAJOR in MAJOR.MINOR.PATCH are not compatible with Android Apps.
Everything is essentially the same except for a few key differences with MAJOR:
* MAJOR would be incremented if MINOR reaches 9
* It may also be incremented whenever the app stops supporting an old API level
* It may also be incremented upon any change that I consider major.

## What's special

The dictionary was made with clean architecture practices and ease of reading of the code in mind.

It uses Kotlin entirely and it uses Navigation Components and parts of the MVVM architecture pattern.

It uses Retrofit2 to access a webserver that uses the API mentioned above to retrieve information about the word entered by the user in the search bar and to show the results it uses a recycler view. It also uses Material Components throughout the app wherever possible.

This app can be used by absolute beginners as a way to learn some basic things like using ViewModels or LiveData or Material Components.

I have commented every piece of code that I believed was not part of common things you do in Android Development. All the logic and operations done by me that are not common practices and unique to this particular app were explained with comments.

There can be found many parts that could use some improvement since I by no means claim to be a perfectly skilled or experienced developer, in fact, relative to so many awesome developers out there, I am very much new. I would like to get suggestions to improve my code or app from other experiened developers.
