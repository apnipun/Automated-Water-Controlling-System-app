package com.app.vortex.vortex.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kasun on 1/7/2017.
 */

public final class URLHelper {
    String base_url;
    String url;
    HashMap<String, String> queryParams = new HashMap<>();

    private URLHelper() {
    }

    public URLHelper(String base_url){
        this.base_url = base_url;
    }

    public String getBase_url() {
        return base_url;
    }

    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }

    public void addQueryParameter(String key, String value){
        queryParams.put(key, value);
    }

    @Override
    public String toString() {
        if(queryParams.isEmpty())
            return base_url;

        int i = 1;
        int max = queryParams.keySet().size();
        StringBuilder stringBuilder = new StringBuilder(base_url);
        stringBuilder.append("?");

        for(Map.Entry<String, String> entry : queryParams.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();

            stringBuilder.append(key + "=" + value);

            if(i < max)
                stringBuilder.append("&");

            i++;
        }

        url = stringBuilder.toString();

        return url;
    }


    public static final class Builder{
        private URLHelper urlHelper;

        public Builder() {
            urlHelper = new URLHelper();
        }

        public Builder setPath(String path){
            urlHelper.setBase_url(path);
            return this;
        }

        public Builder addQueryParam(String key, String value){
            urlHelper.addQueryParameter(key, value);
            return this;
        }

        public URLHelper build(){
            return urlHelper;
        }
    }
}
