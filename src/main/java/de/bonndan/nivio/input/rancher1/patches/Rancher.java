package de.bonndan.nivio.input.rancher1.patches;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rancher.client.BasicAuthInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.io.Serializable;


public class Rancher implements Serializable {
    private transient Retrofit retrofit;
    private final io.rancher.Rancher.Config config;

    public Rancher(io.rancher.Rancher.Config config) {
        this.config = config;
    }

    private ObjectMapper configureObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    public <T> T type(Class<T> service) {
        return this.getRetrofit().create(service);
    }

    private Retrofit getRetrofit() {
        if (this.retrofit == null) {
            OkHttpClient.Builder builder = (new OkHttpClient.Builder()).addInterceptor(BasicAuthInterceptor.auth(this.config.getAccessKey(), this.config.getSecretKey())).addInterceptor(new Interceptor() {
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                    return chain.proceed(request);
                }
            });
            this.retrofit = (new retrofit2.Retrofit.Builder()).baseUrl(this.config.getUrl().toString()).client(builder.build()).addConverterFactory(JacksonConverterFactory.create(this.configureObjectMapper())).build();
        }

        return this.retrofit;
    }

}