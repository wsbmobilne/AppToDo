package pl.wsb.android.app.todo.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToDoEntityDAO {

    @Query("SELECT * FROM todo")
    List<ToDoEntity> getAll();

    @Query("SELECT * FROM todo WHERE user_id = :userId")
    ToDoEntity findByUserId(int userId);

    @Insert
    void insert(ToDoEntity ... toDoEntities);

    @Update
    public void update(ToDoEntity ... toDoEntities);

    @Delete
    void delete(ToDoEntity toDo);
}
