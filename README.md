# PopularMovies2
Popular Movies 2 is a stage 2 Android App project created for Android Developer Nanodegree Program.

Major features that came from stage 1 app:
  * Upon launch the grid arrangement of movie posters are presented to the user.
  * User can change sort order via a setting:
    - The sort order can be by most popular, or by top rated
  * User can tap on a movie poster and will be transited to a details screen with additional information such as:
    - original title
    - movie poster image thumbnail
    - A plot synopsis (called overview in the api)
    - user rating (called vote_average in the api)
    - release date

Following features has been added to movie details view:
  * Allow users to view and play trailers ( either in the youtube app or a web browser).
  * Allow users to read reviews of a selected movie.
  * Allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that does not require an API request.
  * Modified the existing sorting criteria for the main view to include an additional pivot to show user favorites collection.
