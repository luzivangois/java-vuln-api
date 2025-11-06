package com.advocacia.api.repositories;

import com.advocacia.api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByLogin(String login);
    Optional<User> findUserByLogin(String login);
    boolean existsByLogin(String login);
    void deleteById(String id);
    User save(User user);
    List<User> findAll();

    Optional<User> findById(String id);
}