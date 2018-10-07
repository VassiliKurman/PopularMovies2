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

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * CreditsMovie is a response for Movie credits request.
 * Created by Vassili Kurman on 07/10/2018.
 * Version 1.0
 */
public class CreditsMovie {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cast")
    @Expose
    private List<CastMovie> cast = null;
    @SerializedName("crew")
    @Expose
    private List<CrewMovie> crew = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<CastMovie> getCast() {
        return cast;
    }

    public void setCast(List<CastMovie> cast) {
        this.cast = cast;
    }

    public List<CrewMovie> getCrew() {
        return crew;
    }

    public void setCrew(List<CrewMovie> crew) {
        this.crew = crew;
    }
}