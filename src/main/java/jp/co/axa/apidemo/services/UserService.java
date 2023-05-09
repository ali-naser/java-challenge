package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Create a user
     *
     * @param user
     * @return the saved user without password
     */
    public User createUser(UserDto user);

    /**
     * Get all users
     *
     * @return a List of  all users
     */
    public List<User> getAllUsers();

    /**
     * Get a user by userName
     *
     * @param userName the name of the user
     * @return user of optional
     */
    public Optional<User> findUserByName(String userName);

    /**
     * Get a user by email
     *
     * @param email of the user
     * @return user of optional
     */
    public Optional<User> findUserByEmail(String email);

}
