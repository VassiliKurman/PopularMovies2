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

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vkurman.popularmovies2.model.MoviesQueryResponse;

/**
 * TMDBService
 * Created by Vassili Kurman on 10/09/2018.
 * Version 1.0
 */
public interface TMDBService {
    @GET("/movie/popular?api_key={api_key}&language=en-US&page=1")
    Call<MoviesQueryResponse> getPopularMovies(@Path("api_key") String apiKey);

    @GET("/movie/popular?api_key={api_key}&language=en-US")
    Call<MoviesQueryResponse> getPopularMovies(@Path("api_key") String apiKey, @Query("page") int page);
}