package com.example.android.myrooms;

import android.util.Log;

import com.estimote.sdk.Nearable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Rotairo on 8/5/2016.
 */
public class MyNearableModel {
    private List<Nearable> myNearables= new ArrayList<>();
    private List<String> NearableRooms = new ArrayList<>() ;
    private String NearableColor;

    public MyNearableModel(List<Nearable> newNearable){
        super();
        myNearables= newNearable;


    }



    public List<String> parseNearableString(){
        List<String> NearableIds = new ArrayList<>();
        if (!myNearables.isEmpty()) {
            for (int i = 0; i < myNearables.size(); i++) {

                String[] NearableStrings = myNearables.get(i).toString().split("\\{");
                NearableIds.add(NearableStrings[1].split(",")[0].replaceAll("identifier=|'", ""));
            }
            //return NearableIds;
            NearableRooms = MapId(NearableIds);
        }
        else
            NearableRooms.add("I should have crashed");

        return NearableRooms;
        //return NearableIds;
    }

    public boolean isEmpty(){
        return myNearables.isEmpty();
    }
    private List<String> MapId(List<String> nearableIds){
        List<String> localIds= new ArrayList<>();
        localIds=nearableIds;
        for(int i=0;i<localIds.size();i++){
            switch(localIds.get(i)) {
                case "832efaf0bd9225c2":
                    NearableRooms.add("Pilipinas");
                    break;
                case "3d248ff3a6577481":
                    NearableRooms.add("Luzon");
                    break;
                case "1fabf178e76a5489":
                    NearableRooms.add("Visayas");
                    break;
                case "1958ec35795ed47b":
                    NearableRooms.add("Mindanao");
                    break;
                case "2a93499721b0cdc5":
                    NearableRooms.add("Wroclaw");
                    break;
                case "482309d2854ee6e7":
                    NearableRooms.add("Krakow");
                    break;
                case "3eeeb963a606baf7":
                    NearableRooms.add("Helsinki");
                    break;
                case "8825f2e2b0ee97f4":
                    NearableRooms.add("Rovaniemi");
                    break;
                case "fa0747d1c1e8e3db":
                    NearableRooms.add("Oulu");
                    break;
                default:
                    NearableRooms.add("UnknownID");
            }
        }
        return NearableRooms;

    }

}
