package pl.wsb.android.app.todo.mapper;

import pl.wsb.android.app.todo.database.UserEntity;
import pl.wsb.android.app.todo.model.User;

public class UserMapper {
    public static User create(UserEntity entity) {
        if (entity == null) {
            return null;
        } //if
        User user = new User();
        user.setUsername(entity.getFirstName());
        user.setName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setId(entity.getExternalId());
        user.setWebsite(entity.getWebsite());
        user.setPhone(entity.getPhone());
        return user;
    }

    public static UserEntity create(User user) {
        if (user == null) {
            return null;
        } //if
        UserEntity entity = new UserEntity();
        entity.setFirstName(user.getUsername());
        entity.setLastName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setExternalId(user.getId());
        entity.setWebsite(user.getWebsite());
        entity.setPhone(user.getPhone());
        return entity;
    }
}
