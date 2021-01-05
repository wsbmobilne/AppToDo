package pl.wsb.android.app.todo.api;

import java.util.List;

import pl.wsb.android.app.todo.model.User;
import retrofit2.Response;

public class ApiUtils {
    public static ApiService getApiService(){
        return ApiClient.getClient().create(ApiService.class);
    }

    public static <U> int getResponseStatusCode(Response<List<U>> response){
        if(response == null)
            return 404;

        return response.code();
    }
    /*public static int getResponseStatusCode(Response<List<User>> response)
    {
        if(response == null)
            return 404;

        return response.code();
    }*/
}

