package pl.wsb.android.app.todo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ToDoEntity.class}, version = 3, exportSchema = false)
public abstract class ToDoDatabase extends RoomDatabase {

    public abstract ToDoEntityDAO toDoEntityDAO();

    private static volatile ToDoDatabase INSTANCE;
    private static final int NUMBER_OF_THREAD = 4;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREAD);

    public static ToDoDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (ToDoDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ToDoDatabase.class, "app_database")
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }
        return INSTANCE;

    }

}
