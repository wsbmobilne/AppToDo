package pl.wsb.android.app.todo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import pl.wsb.android.app.todo.api.ApiUtils;
import pl.wsb.android.app.todo.database.ToDoEntity;
import pl.wsb.android.app.todo.database.UserDatabase;
import pl.wsb.android.app.todo.database.UserEntity;
import pl.wsb.android.app.todo.mapper.ToDoMapper;
import pl.wsb.android.app.todo.mapper.UserMapper;
import pl.wsb.android.app.todo.model.ToDo;
import pl.wsb.android.app.todo.model.User;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    private EditText edtLogin;
    private EditText edtPass;
    private Button btnLogin;
    private List<User> userList =  new ArrayList<>();
    private UserDatabase userDatabase;
    private User authorizedUser;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.userDatabase = UserDatabase.getDatabase(this.getApplicationContext());
        this.getUserApiCall();
        this.initControls();
        this.disableBtnLogin();
        this.initListeners();

    }

    private void initControls(){
        this.edtLogin = this.findViewById(R.id.edtLogin);
        this.edtPass = this.findViewById(R.id.edtPass);
        this.btnLogin = this.findViewById(R.id.btnLogin);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean authenticateUser()
    {
        if(TextUtils.isEmpty(this.edtLogin.getText())){
            Toast.makeText(this, this.getString(R.string.login__validation_login_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(this.edtPass.getText())){
            Toast.makeText(this, this.getString(R.string.login__validation_pass_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        String email = this.edtLogin.getText().toString();
        Log.i("Request login: ", email);
        this.authorizedUser = this.userList.stream()
                                            .filter(user -> email.toLowerCase().equals(user.getEmail()))
                                            .findAny()
                                            .orElse(null);

        if(this.authorizedUser == null)
            return false;

        Log.i("Uzytkownik", this.authorizedUser.getFullName());

        return true;
    }

    private void disableBtnLogin()
    {
        this.btnLogin.setEnabled(false);
    }
    private void enabledBtnLogin()
    {
        this.btnLogin.setEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initListeners(){
        if(this.btnLogin == null){
            return;
        }
        this.btnLogin.setOnClickListener(view -> {
            if(this.authenticateUser()){
                Toast.makeText(this, this.getString(R.string.login__success), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoggedInActivity.class);
                intent.putExtra("AuthorizedUserId", this.authorizedUser.getId());
                this.startActivity(intent);
                this.finish();
                return;
            }
            Toast.makeText(this, this.getString(R.string.login__failure), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUsersLocally(List<User> users) {
        if (users == null)
            return;
        this.userDatabase.getQueryExecutor().execute(() -> {
            final List<UserEntity> userEntities = this.userDatabase.userEntityDAO().getAll();

            if(userEntities != null){
                if(userEntities.size() > 0){
                    return;
                }
            }
            for (User user : users){
                UserEntity userEntity = UserMapper.create(user);
                if(userEntity != null){
                    this.userDatabase.userEntityDAO().insert(userEntity);
                }
            }
        });
    }

    public void retriveUserLocally(){
        this.userDatabase.getQueryExecutor().execute(() -> {
            final List<UserEntity> userEntities = this.userDatabase.userEntityDAO().getAll();
            runOnUiThread(() -> {
                if(userEntities == null){
                    return;
                }
                List<User> userListLocal = new ArrayList<>();
                for(UserEntity userEntity : userEntities) {
                    User user = UserMapper.create(userEntity);
                    if (user != null) {
                        Log.i("Item", user.getEmail());
                        userListLocal.add(user);
                    }
                }
                this.setUserList(userListLocal);
            });

        });
    }

    private void setUserList(List<User> items){
        this.userList = items;
    }
    private void getUserApiCall()
    {
        ApiUtils.getApiService().getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<List<User>>>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i("UserStart", "Users");
                    }

                    @Override
                    public void onNext(Response<List<User>> response) {
                        if(ApiUtils.getResponseStatusCode(response) == 200){
                            Log.i("ResponsUser", Integer.toString(response.body().size()));
                            saveUsersLocally(response.body());
                            setUserList(response.body());
                        }
                    }

                    @Override
                    public void onError( Throwable e) {
                        retriveUserLocally();
                        enabledBtnLogin();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("ResponsUser", "Completed");
                        enabledBtnLogin();
                    }
                });
    }
}