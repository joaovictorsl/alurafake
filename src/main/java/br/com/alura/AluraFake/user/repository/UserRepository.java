package br.com.alura.AluraFake.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.AluraFake.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
