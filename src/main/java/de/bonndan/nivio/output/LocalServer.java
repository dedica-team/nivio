package de.bonndan.nivio.output;

public class LocalServer {

    public static String host() {
        return "localhost";
    }

    public static String port() {
        return "8080";
    }

    public static String url(String path) {
        return "http://" + host() + ":" + port() + path;
    }
}
