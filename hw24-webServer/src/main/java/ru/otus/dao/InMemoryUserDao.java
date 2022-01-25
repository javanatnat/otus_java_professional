package ru.otus.dao;

import ru.otus.model.User;

import java.util.*;

public class InMemoryUserDao implements UserDao {

    private final Map<Long, User> users;

    public InMemoryUserDao() {
        users = new HashMap<>();
        users.put(1L, new User(1L, "Иванов", "admin", "123456789"));
        users.put(2L, new User(2L, "Крайнов", "admin2", "11111"));
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

//    @Override
//    public Optional<User> findRandomUser() {
//        Random r = new Random();
//        return users.values().stream().skip(r.nextInt(users.size() - 1)).findFirst();
//    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values().stream().filter(v -> v.getLogin().equals(login)).findFirst();
    }
}
