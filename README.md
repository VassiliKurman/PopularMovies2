# PopularMovies2
Popular Movies 2 is a stage 2 Android App project created for Android Developer Nanodegree Program.



## Popular Movies App Stage 2 Project Specification

### Common Project Requirements
* App is written solely in the Java Programming Language.

* App conforms to common standards found in the Android Nanodegree General Project Guidelines [udacity.github.io]

 

### User Interface - Layout
* UI contains an element (e.g., a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.

* Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.

* UI contains a screen for displaying the details for a selected movie.

* Movie Details layout contains title, release date, movie poster, vote average, and plot synopsis.

* Movie Details layout contains a section for displaying trailer videos and user reviews.

 

### User Interface - Function
* When a user changes the sort criteria (most popular, highest rated, and favourites) the main view gets updated correctly.

* When a movie poster thumbnail is selected, the movie details screen is launched.

* When the trailer is selected, app uses an Intent to launch the trailer.

* In the movies detail screen, a user can tap a button (for example, a star) to mark it as a Favourite.

 

### Network API Implementation
* In a background thread, app queries the /movie/popular or /movie/top_ratedAPI for the sort criteria specified in the settings menu.

* App requests for related videos for a selected movie via the /movie/{id}/videos endpoint in a background thread and displays those details when the user selects a movie.

* App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint in a background thread and displays those details when the user selects a movie.

 

### Data Persistence
* The titles and IDs of the user’s favourite movies are stored in a native SQLite database and are exposed via a ContentProvider. This ContentProvider is updated whenever the user favourites or unfavourites a movie. No other persistence libraries are used.

* When the "favourites" setting option is selected, the main view displays the entire favourites collection based on movie ids stored in the ContentProvider.

### Suggestions to Make Your Project Stand Out!
* Extend the favorites ContentProviderto store the movie poster, synopsis, user rating, and release date, and display them even when offline.

* Implement sharing functionality to allow the user to share the first trailer’s YouTube URL from the movie details screen.



## Major features that came from stage 1 app:
  * Upon launch the grid arrangement of movie posters are presented to the user.
  * User can change sort order via a setting:
    - The sort order can be by most popular, or by top rated
  * User can tap on a movie poster and will be transited to a details screen with additional information such as:
    - original title
    - movie poster image thumbnail
    - A plot synopsis (called overview in the api)
    - user rating (called vote_average in the api)
    - release date

## Following features has been added to movie details view:
  * Allow users to view and play trailers ( either in the youtube app or a web browser).
  * Allow users to read reviews of a selected movie.
  * Allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that does not require an API request.
  * Modified the existing sorting criteria for the main view to include an additional pivot to show user favorites collection.

## Additional information
This app has been updated since project review and some new features have been added. The 'udacity-accepted' branch has original project that has been passed by Udacity's project reviewer.

Following libraries are used in original version:
  * Picasso for handling and cashing images
  * Butterknife for binding resources
  
Following new features has been added since project review:
  * Redesigned UI
  * Introduced Navigation Drawer to the main screen
  * Introduced Collapsible Toolbar to the project
  * Added new section "TV Shows" and "People" to Navigation Drawer
  * Introduced Retrofit 2 library to the project to handle network requests
