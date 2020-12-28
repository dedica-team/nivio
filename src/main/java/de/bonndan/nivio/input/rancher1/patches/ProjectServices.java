package de.bonndan.nivio.input.rancher1.patches;

import io.rancher.base.TypeCollection;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Project services api.
 *
 *
 */
public interface ProjectServices {

    /**
     * Returns all services beloning to the project with the given id.
     *
     * @param id accountId
     * @return services
     */
    @GET("projects/{id}/services")
    Call<TypeCollection<Service>> getServices(@Path("id") String id);
}
