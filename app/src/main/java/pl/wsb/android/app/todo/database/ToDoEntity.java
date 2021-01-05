package pl.wsb.android.app.todo.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity(tableName = "todo")
public class ToDoEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "external_id")
    private int externalId;
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "completed")
    private Boolean completed;

    public int getId() {
        return id;
    }

    public int getExternalId() {
        return externalId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExternalId(int externalId) {
        this.externalId = externalId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
