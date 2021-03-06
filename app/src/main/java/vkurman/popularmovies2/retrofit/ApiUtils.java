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

/**
 * ApiUtils - class has the base URL as a static variable and also provide the
 * {@link TMDBService @TMDBService} interface to our application through the
 * getTMDBService() static method.
 * <p>
 * Created by Vassili Kurman on 10/09/2018.
 * Version 1.0
 * </p>
 */
public class ApiUtils {
    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    public static TMDBService getTMDBService() {
        return RetrofitClient.getClient(BASE_URL).create(TMDBService.class);
    }
}