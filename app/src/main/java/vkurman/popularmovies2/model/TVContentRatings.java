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
package vkurman.popularmovies2.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * TVContentRatings is a response for TV Show content ratings request to TMDB.
 * Created by Vassili Kurman on 17/10/2018.
 * Version 1.0
 */
public class TVContentRatings {
    @SerializedName("results")
    @Expose
    private List<TVRating> results = new ArrayList<>();
    @SerializedName("id")
    @Expose
    private Integer id;

    public List<TVRating> getTVRating() {
        return results;
    }

    public void setTVRating(List<TVRating> results) {
        this.results = results;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}