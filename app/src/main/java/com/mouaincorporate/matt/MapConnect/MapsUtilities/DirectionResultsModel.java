package com.mouaincorporate.matt.MapConnect.MapsUtilities;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DirectionResultsModel {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}