/*
 * Copyright (C) 2016 Original Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.fabric8.docker.client.impl;

import com.squareup.okhttp.OkHttpClient;
import io.fabric8.docker.api.model.Volume;
import io.fabric8.docker.api.model.VolumesListResponse;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.DockerClientException;
import io.fabric8.docker.dsl.volume.AllFiltersInterface;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListVolume extends BaseVolumeOperation implements
        AllFiltersInterface<List<Volume>> {

    private static final String FILTERS = "filters";
    private static final String ALL = "all";

    private final Map<String,String[]> filters;

    public ListVolume(OkHttpClient client, Config config) {
        this(client, config, null);
    }

    public ListVolume(OkHttpClient client, Config config, Map<String, String[]> filters) {
        super(client, config, null, EMPTY);
        this.filters = filters;
    }

    private List<Volume> doList() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(getOperationUrl().toString());

            if (filters != null && !filters.isEmpty()) {
                sb.append(Q).append(FILTERS).append(EQUALS)
                        .append(JSON_MAPPER.writeValueAsString(filters));
            }
            URL requestUrl = new URL(sb.toString());
            return handleGet(requestUrl,VolumesListResponse.class).getVolumes();
        } catch (Exception e) {
            throw DockerClientException.launderThrowable(e);
        }
    }

    @Override
    public AllFiltersInterface<List<Volume>> filters(String key, String value) {
        Map<String, String[]> newFilters = new HashMap<>(this.filters);
        newFilters.put(key, new String[]{value});
        return new ListVolume(client, config, newFilters);
    }

    @Override
    public List<Volume> all() {
        return doList();
    }
}
