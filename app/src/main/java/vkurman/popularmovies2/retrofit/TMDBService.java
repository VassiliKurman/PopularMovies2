/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vkurman.popularmovies2.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import vkurman.popularmovies2.model.CreditsMovie;
import vkurman.popularmovies2.model.MovieKeywords;
import vkurman.popularmovies2.model.MovieModel;
import vkurman.popularmovies2.model.MoviesQueryResponse;
import vkurman.popularmovies2.model.PeopleQueryResponse;
import vkurman.popularmovies2.model.PersonCombinedCredits;
import vkurman.popularmovies2.model.PersonModel;
import vkurman.popularmovies2.model.RecommendationsMovieRequest;
import vkurman.popularmovies2.model.RecommendationsTVShowRequest;
import vkurman.popularmovies2.model.ResultMovieReviews;
import vkurman.popularmovies2.model.ShowModel;
import vkurman.popularmovies2.model.TVContentRatings;
import vkurman.popularmovies2.model.TVKeywords;
import vkurman.popularmovies2.model.TVQueryResponse;

/**
 * TMDBService - interface contains methods that are used to execute HTTP requests
 * such as GET, POST, PUT, PATCH, and DELETE.
 * Created by Vassili Kurman on 10/09/2018.
 * Version 1.0
 */
public interface TMDBService {

    /**
     *
     * @param selection - String for {@link Path @Path} which should be popular, top_rated and etc.
     * @param options - should include api_key as minimum
     * @return Call<MoviesQueryResponse>
     */
    @GET("discover/{selection}")
    Call<MoviesQueryResponse> discoverMovies(@Path("selection") String selection, @QueryMap Map<String, String> options);

    /**
     *
     * @param selection - String for {@link Path @Path} which should be popular, top_rated and etc.
     * @param options - should include api_key as minimum
     * @return Call<TVQueryResponse>
     */
    @GET("discover/{selection}")
    Call<TVQueryResponse> discoverTVs(@Path("selection") String selection, @QueryMap Map<String, String> options);

    /**
     *
     * @param selection - String for {@link Path @Path} which should be popular, top_rated and etc.
     * @param options - should include api_key as minimum
     * @return Call<MoviesQueryResponse>
     */
    @GET("movie/{selection}")
    Call<MoviesQueryResponse> getMovies(@Path("selection") String selection, @QueryMap Map<String, String> options);

    /**
     *
     * @param selection - String for {@link Path @Path} which should be popular, top_rated and etc.
     * @param options - should include api_key as minimum
     * @return Call<TVQueryResponse>
     */
    @GET("tv/{selection}")
    Call<TVQueryResponse> getTV(@Path("selection") String selection, @QueryMap Map<String, String> options);

    /**
     *
     * @param selection - String for {@link Path @Path} which should be popular.
     * @param options - should include api_key as minimum
     * @return Call<PeopleQueryResponse>
     */
    @GET("person/{selection}")
    Call<PeopleQueryResponse> getPeople(@Path("selection") String selection, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be movie id.
     * @param options - should include api_key as minimum
     * @return Call<MovieModel>
     */
    @GET("movie/{id}")
    Call<MovieModel> getMovie(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be persons id.
     * @param options - should include api_key as minimum
     * @return Call<PersonModel>
     */
    @GET("person/{id}")
    Call<PersonModel> getPerson(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be persons id.
     * @param options - should include api_key as minimum
     * @return Call<PersonCombinedCredits>
     */
    @GET("person/{id}/combined_credits")
    Call<PersonCombinedCredits> getPersonCombinedCredits(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be show id.
     * @param options - should include api_key as minimum
     * @return Call<ShowModel>
     */
    @GET("tv/{id}")
    Call<ShowModel> getShow(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be tvshow id.
     * @param options - should include api_key as minimum
     * @return Call<TVContentRatings>
     */
    @GET("tv/{id}/content_ratings")
    Call<TVContentRatings> getTVShowRatings(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be tvshow id.
     * @param options - should include api_key as minimum
     * @return Call<TVKeywords>
     */
    @GET("tv/{id}/keywords")
    Call<TVKeywords> getTVShowKeywords(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be movie id.
     * @param options - should include api_key as minimum
     * @return Call<MovieKeywords>
     */
    @GET("movie/{id}/keywords")
    Call<MovieKeywords> getMovieKeywords(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be tvshow id.
     * @param options - should include api_key as minimum
     * @return Call<RecommendationsTVShowRequest>
     */
    @GET("tv/{id}/recommendations")
    Call<RecommendationsTVShowRequest> getTVShowRecommendations(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be movie id.
     * @param options - should include api_key as minimum
     * @return Call<RecommendationsTVShowRequest>
     */
    @GET("movie/{id}/recommendations")
    Call<RecommendationsMovieRequest> getMovieRecommendations(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be id.
     * @param options - should include api_key as minimum
     * @return Call<CreditsMovie>
     */
    @GET("tv/{id}/credits")
    Call<CreditsMovie> getTVShowCredits(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be id.
     * @param options - should include api_key as minimum
     * @return Call<CreditsMovie>
     */
    @GET("movie/{id}/credits")
    Call<CreditsMovie> getMovieCredits(@Path("id") Long id, @QueryMap Map<String, String> options);

    /**
     *
     * @param id - String for {@link Path @Path} which should be id.
     * @param options - should include api_key as minimum
     * @return Call<ResultMovieReviews>
     */
    @GET("movie/{id}/reviews")
    Call<ResultMovieReviews> getMovieReviews(@Path("id") Long id, @QueryMap Map<String, String> options);
}