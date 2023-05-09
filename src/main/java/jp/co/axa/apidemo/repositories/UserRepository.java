package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String username);
    User findOneByEmailIgnoreCase(String email);
}
