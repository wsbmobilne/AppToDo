package pl.wsb.android.app.todo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import pl.wsb.android.app.todo.adapter.ToDosAdapter;
import pl.wsb.android.app.todo.api.ApiUtils;
import pl.wsb.android.app.todo.database.ToDoDatabase;
import pl.wsb.android.app.todo.database.ToDoEntity;
import pl.wsb.android.app.todo.mapper.ToDoMapper;
import pl.wsb.android.app.todo.model.ToDo;
import retrofit2.Response;

public class LoggedInActivity extends AppCompatActivity {




    private ToDoDatabase toDoDatabase;
    private Button btnLogout;
    private CheckBox cbxItem;
    private RecyclerView rvItems;
    private Integer AuthorizedUserId;
    private List<ToDo> baseToDoList;
    private List<ToDo> currentToDoList;

    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_logged_in);

        this.initControls();
        this.initListeners();

        this.AuthorizedUserId = this.getIntent().getIntExtra("AuthorizedUserId", -1);
        this.toDoDatabase = ToDoDatabase.getDatabase(this.getApplicationContext());
        Log.i("AutthorizedUserId:", Integer.toString(this.AuthorizedUserId));

        this.getToDosApiCall(this.AuthorizedUserId);



    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_show_only_done:
                this.currentToDoList = this.baseToDoList.stream()
                                            .filter(todos -> todos.getCompleted() ==  true)
                                            .collect(Collectors.toList());
                this.initRecyclerView(this.currentToDoList);
                return true;
            case R.id.action_show_only_todos:
                this.currentToDoList = this.baseToDoList.stream()
                        .filter(todos -> todos.getCompleted() ==  false)
                        .collect(Collectors.toList());
                this.initRecyclerView(this.currentToDoList);
                return true;
            case R.id.action_show_all:
                this.initRecyclerView(this.baseToDoList);
                return true;
            case R.id.action_add_new:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Title");

                final EditText input = new EditText(this);

                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addItemToDos(input.getText().toString());
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                this.initRecyclerView(this.baseToDoList);
            default: return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addItemToDos(String todoText)
    {

        ToDo maxValue = this.baseToDoList.stream()
                .max(Comparator.comparing(v -> v.getId())).get();
        ToDoEntity toDoEntity = new ToDoEntity();
        toDoEntity.setUserId(this.AuthorizedUserId);
        toDoEntity.setCompleted(false);
        toDoEntity.setTitle(todoText);
        toDoEntity.setUserId(this.AuthorizedUserId);
        toDoEntity.setExternalId(maxValue.getId());

        this.toDoDatabase.getQueryExecutor().execute(() -> {
            this.toDoDatabase.toDoEntityDAO().insert(toDoEntity);
        });
        ToDo toDo = ToDoMapper.create(toDoEntity);
        this.baseToDoList.add(toDo);

    }

    private void initControls(){
        this.btnLogout = this.findViewById(R.id.btnLogout);
        this.cbxItem = this.findViewById(R.id.cbTodosListDone);
        this.rvItems = this.findViewById(R.id.rvLoggedInToDosList);
    }
    private void initListeners(){
        if(this.btnLogout == null)
            return;

        this.btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
        });


        if(this.cbxItem ==null)
            return;

        this.cbxItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("Button:", buttonView.getText().toString());
                Log.i("ButtonState:", Boolean.toString(isChecked));
            }
        });
    }

    public void saveTodosLocally(List<ToDo> toDos) {
        if (toDos == null)
            return;

        this.baseToDoList = toDos;

        this.toDoDatabase.getQueryExecutor().execute(() -> {
            final List<ToDoEntity> toDoEntities = this.toDoDatabase.toDoEntityDAO().getAll();

            if(toDoEntities != null){
                if(toDoEntities.size() > 0){
                    return;
                }
            }
            for (ToDo toDo : toDos){
                ToDoEntity toDoEntity = ToDoMapper.create(toDo);
                if(toDoEntity != null){
                    this.toDoDatabase.toDoEntityDAO().insert(toDoEntity);
                }
            }

        });
    }

    public void retriveToDosLocally(){
        this.toDoDatabase.getQueryExecutor().execute(() -> {
            final List<ToDoEntity> toDoEntities = this.toDoDatabase.toDoEntityDAO().getAll();
            runOnUiThread(() -> {
                if(toDoEntities == null){
                    return;
                }
                List<ToDo> toDos = new ArrayList<>();
                for(ToDoEntity toDoEntity : toDoEntities) {
                    ToDo toDo = ToDoMapper.create(toDoEntity);
                    if (toDo != null) {
                        toDos.add(toDo);
                    }
                }
                this.baseToDoList = toDos;
                this.initRecyclerView(toDos);
            });
        });
    }

    public void initRecyclerView(List<ToDo> toDos ){
        try {

            Log.i("TODO-WIELKOSC:", Integer.toString(toDos.size()));
            RecyclerView recyclerView = this.findViewById(R.id.rvLoggedInToDosList);

            if (recyclerView == null)
                return;

            if (toDos == null)
                return;

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //recyclerView.setHasFixedSize(true);
            ToDosAdapter toDosAdapter = new ToDosAdapter(this, toDos);
            recyclerView.setAdapter(toDosAdapter);
        }
        catch (Exception e)
        {
            Log.i("InitRecycleView", e.getMessage(), e);
        }
    }

    private void getToDosApiCall(Integer userId){
        try {
            Log.i("TEST:", "Rozpoczescie pracy");
            ApiUtils.getApiService().getUserToDos(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<Response<List<ToDo>>>() {
                        @Override
                        protected void onStart() {
                            super.onStart();
                            Log.i("TEST:", "onStart()");
                        }

                        @Override
                        public void onNext(Response<List<ToDo>> listResponse) {
                            Log.i("Test", "Pobierany zdalnie");
                            if(ApiUtils.getResponseStatusCode(listResponse) == 200){
                                Log.i("Response: ", listResponse.body().toString());
                                saveTodosLocally(listResponse.body());
                                initRecyclerView(listResponse.body());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("API_CALL", e.getMessage());
                            retriveToDosLocally();
                        }

                        @Override
                        public void onComplete() {
                            Log.i("Complete:", "done");
                        }
                    });
        }
        catch (Exception e)
        {
            Log.e("Error", e.getMessage());
        }

    }

}