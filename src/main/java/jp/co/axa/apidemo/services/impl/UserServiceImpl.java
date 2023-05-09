package jp.co.axa.apidemo.services.impl;

import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.entities.User;
import jp.co.axa.apidemo.repositories.UserRepository;
import jp.co.axa.apidemo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a user
     *
     * @param userDto DTO of the user
     * @return the saved user without password
     */
    @Override
    public User createUser(UserDto userDto) {
        User user = new User(userDto);
        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    /**
     * Get all users
     *
     * @return a List of  all users
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get a user by userName
     *
     * @param userName the name of the user
     * @return user of optional
     */
    @Override
    public Optional<User> findUserByName(String userName) {
        return Optional.ofNullable(userRepository.findByUserName(userName));
    }

    /**
     * Get a user by email
     *
     * @param email of the user
     * @return user of optional
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findOneByEmailIgnoreCase(email));
    }

}
