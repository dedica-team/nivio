package de.bonndan.nivio.input.rancher1;

import io.rancher.base.TypeCollection;
import io.rancher.type.Service;
import io.rancher.type.Stack;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * API to load project stacks.
 *
 *
 */
public interface ProjectStacks {

    /**
     * Returns all services beloning to the project with the given id.
     *
     * @param id accountId
     * @return stacks
     */
    @GET("projects/{id}/stacks")
    Call<TypeCollection<Stack>> getStacks(@Path("id") String id);
}
