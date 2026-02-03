package _dduartess.travelaencymanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import _dduartess.travelaencymanagement.entities.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByLogin(String login);

    String login(String login);
}