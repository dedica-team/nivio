package de.bonndan.nivio.input.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CachedResponse {

    private final Header[] allHeaders;
    private final byte[] bytes;

    public CachedResponse(Header[] allHeaders, HttpEntity content) throws IOException {
        this.allHeaders = allHeaders;
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        content.writeTo(bOutput);
        this.bytes = bOutput.toByteArray();
    }

    public Header[] getAllHeaders() {
        return allHeaders;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
