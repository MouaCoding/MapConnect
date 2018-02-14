package com.mouaincorporate.matt.MapConnect.MapsUtilities;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route {

    @SerializedName("legs")
    private List<Legs> legs;

    public List<Legs> getLegs() {
        return legs;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }
}