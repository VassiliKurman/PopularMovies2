package vkurman.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 06/03/2018.
 * Version 2.0
 */
public class Movie implements Parcelable {

    private int movieId;
    private String moviePoster;
    private String title;
    private String releaseDate;
    private String voteAverage;
    private String plotSynopsis;

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        movieId = in.readInt();
        moviePoster = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
        plotSynopsis = in.readString();
    }

    public Movie(int movieId, String moviePoster, String title, String releaseDate,
            String voteAverage, String plotSynopsis) {
        this.movieId = movieId;
        this.moviePoster = moviePoster;
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(moviePoster);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(voteAverage);
        dest.writeString(plotSynopsis);
    }
}
