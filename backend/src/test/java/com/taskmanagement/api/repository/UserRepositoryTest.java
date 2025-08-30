package com.taskmanagement.api.repository;

import com.taskmanagement.api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void whenFindByUsername_thenReturnUser() {
        // given
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persistAndFlush(user);
        
        // when
        Optional<User> found = userRepository.findByUsername("testuser");
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    public void whenFindByEmail_thenReturnUser() {
        // given
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persistAndFlush(user);
        
        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    public void whenExistsByUsername_thenReturnTrue() {
        // given
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persistAndFlush(user);
        
        // when
        Boolean exists = userRepository.existsByUsername("testuser");
        
        // then
        assertThat(exists).isTrue();
    }
    
    @Test
    public void whenExistsByEmail_thenReturnTrue() {
        // given
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persistAndFlush(user);
        
        // when
        Boolean exists = userRepository.existsByEmail("test@example.com");
        
        // then
        assertThat(exists).isTrue();
    }
}