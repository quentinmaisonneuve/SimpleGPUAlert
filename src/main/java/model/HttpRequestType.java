package model;

import org.apache.maven.shared.utils.StringUtils;

public enum HttpRequestType {

    GET, PUT, POST;

    /**
     * Convert a string name of Http request to an enum HttpRequestType
     * @param httpRequestName Name of Http request
     * @return Enum of corresponding HttpRequestType
     */
    public static HttpRequestType StringToHttpRequestType(String httpRequestName) {

        HttpRequestType httpRequestType = null;

        if(StringUtils.isNotBlank(httpRequestName)) {

            for (HttpRequestType h : HttpRequestType.values()) {

                if (httpRequestName.trim().equalsIgnoreCase(h.toString())) {

                    httpRequestType = h;
                    break;
                }
            }
        }

        return httpRequestType;
    }
}
