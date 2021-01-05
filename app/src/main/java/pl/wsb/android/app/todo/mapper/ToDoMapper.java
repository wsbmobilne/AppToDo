package pl.wsb.android.app.todo.mapper;

import pl.wsb.android.app.todo.database.ToDoEntity;
import pl.wsb.android.app.todo.model.ToDo;

public class ToDoMapper{

    public static ToDo create(ToDoEntity entity){
        if(entity == null){
            return null;
        }
        ToDo toDo = new ToDo();
        toDo.setCompleted(entity.getCompleted());
        toDo.setTitle(entity.getTitle());
        toDo.setUserId(entity.getUserId());
        toDo.setId(entity.getExternalId());
        return toDo;
    }

    public static ToDoEntity create(ToDo toDo){
        if(toDo == null)
            return null;

        ToDoEntity entity = new ToDoEntity();
        entity.setCompleted(toDo.getCompleted());
        entity.setExternalId(toDo.getId());
        entity.setTitle(toDo.getTitle());
        entity.setUserId(toDo.getUserId());
        return entity;
    }

}
