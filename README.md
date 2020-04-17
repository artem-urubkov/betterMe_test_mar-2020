# betterMe_test_mar-2020
Application almost corresponding to the minimal requirements of the following test quiz, but code-tests and sharing-for-each-movie.

Create the application which allows user to review the list of ongoing movies and add them to bookmarks with the following requirements:

Functional requirements
As a user I can login to Facebook (optional, bonus points);
As a user I can see my user picture preview inside a toolbar (optional, bonus points);
As a user I see the list of ongoing movies for two last weeks period;
As a user I can refresh the list of movies using pull-to-refresh;
As a user I can view the list of movies without internet connection;
As a user I can add or remove each movie to bookmarks (all changes should become visible instantly);
As a user I can share each movie info (via any provider);
As a user I can see the list of my bookmarked movies;
Each data-bound operation should have one of these states at UI:
- empty state
- in progress
- failed (with reason: i.e. connectivity / server issue);
- success (data is shown);

Technical requirements
Kotlin;
Clean Architecture patterns are mandatory;
Dependency injection is required;
Redux / MVP / MVVM / MVI architecture is required;
Android Jetpack dependencies (Room, Navigation, ViewModels, LiveData, WorkManager) usage is a big plus;
Unit-tests are mandatory;


Notes
We expect you to build scalable architecture based on SOLID principles;
We expect that you don’t spend a lot of time by polishing the UI, but some fancy animations will be a big plus;
100% code coverage is not necessary: you are free to test only the most important parts of logic;

Resources
The movie database API: https://www.themoviedb.org/documentation/api/discover
