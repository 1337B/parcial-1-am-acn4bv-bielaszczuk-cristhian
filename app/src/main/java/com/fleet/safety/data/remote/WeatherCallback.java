package com.fleet.safety.data.remote;

import com.fleet.safety.domain.WeatherSnapshot;

public interface WeatherCallback {

    void onSuccess(WeatherSnapshot snapshot);

    void onError(Exception e);
}
