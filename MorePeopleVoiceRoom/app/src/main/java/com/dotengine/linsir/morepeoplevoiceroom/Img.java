package com.dotengine.linsir.morepeoplevoiceroom;

/**
 *  Created by linSir 
 *  date at 2017/5/25.
 *  describe: img model has two
 */

public class Img {

    private String name;
    private String url;
    private boolean talking;

    public Img(String name, String url, boolean talking) {
        this.name = name;
        this.url = url;
        this.talking = talking;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isTalking() {
        return talking;
    }

    public void setTalking(boolean talking) {
        this.talking = talking;
    }
}
