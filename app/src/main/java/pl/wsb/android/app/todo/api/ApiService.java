package pl.wsb.android.app.todo.api;


import java.util.List;

import io.reactivex.Observable;
import pl.wsb.android.app.todo.model.ToDo;
import pl.wsb.android.app.todo.model.User;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("/todos")
    Observable<Response<List<ToDo>>> getToDos();

    @GET("/users/{Id}/todos")
    Observable<Response<List<ToDo>>> getUserToDos(@Path("Id") Integer userId);

    @GET("/users")
    Observable<Response<List<User>>> getUsers();
}
