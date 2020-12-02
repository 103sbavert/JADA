# JADA (Just Another Dictionary App) 
(Liscensed under the MIT Liscense)

A simple free and open-source dictionary app that uses the API created by [Meet Developer](https://github.com/meetDeveloper) which you can check out [here](https://github.com/meetDeveloper/googleDictionaryAPI). This dictionary app uses that API to show definitions for the words entered by the user. It supports multiple languages and best of all it has dark mode.

## Multi Language support

This dictionary supports multiple language as the API I am using does too. The languages supported are the following

* English
* Hindi
* Spanish
* French
* Japenese
* Russian
* German
* Italian
* Korean
* Brazilian Portuguese
* Turkish

**This dictionary does not support Arabic even though the API supports the language as there seems to be some issues with arabic side of the API. I have tested it with different arabic words and it never returns an input. I will add support for Arabic if I figure out the reason it isn't behaving like it should.**

## What's special

The dictionary was made with clean architecture practices and ease of reading the code in mind.

It uses Kotlin entirely and it uses Navigation Components and parts of the MVVM architecture pattern.

It uses Retrofit2 to access a webserver that uses the API mentioned above to retrieve information about the word entered by the user in the search bar and to show the results it uses a scroll view. It also uses Material Components throughout the app wherever possible.

This app can be used by absolute beginners as a way to learn some basic things like using ViewModels or LiveData or Material Components.

I have commented every piece of code that I believed was not part of common things you do in Android Development. All the logic and operations done by me that are not common practices and unique to this particular app were explained with comments. 

There can be parts that could use some improvement since I by no means claim to be a perfectly skilled or experienced developer, in fact, relative to so many awesome developers out there, I am very much new. I would like to get suggestions to improve my code or app from other experiened developers.
