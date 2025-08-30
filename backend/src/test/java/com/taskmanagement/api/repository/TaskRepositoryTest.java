package com.taskmanagement.api.repository;

import com.taskmanagement.api.model.Task;
import com.taskmanagement.api.model.TaskStatus;
import com.taskmanagement.api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Test
    public void whenFindByUserOrderByDataCriacaoDesc_thenReturnTasks() {
        // given
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persistAndFlush(user);
        
        Task task1 = new Task("Task 1", "Description 1", LocalDateTime.now().plusDays(1), user);
        entityManager.persistAndFlush(task1);
        
        try {
            Thread.sleep(10); // Small delay to ensure different creation times
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now().plusDays(2), user);
        entityManager.persistAndFlush(task2);
        
        // when
        List<Task> tasks = taskRepository.findByUserOrderByDataCriacaoDesc(user);
        
        // then
        assertThat(tasks).hasSize(2);
        assertThat(tasks.get(0).getTitulo()).isEqualTo("Task 2"); // Latest first
        assertThat(tasks.get(1).getTitulo()).isEqualTo("Task 1");
    }
    
    @Test
    public void whenFindByUserAndStatus_thenReturnFilteredTasks() {
        // given
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persistAndFlush(user);
        
        Task pendingTask = new Task("Pending Task", "Description", LocalDateTime.now().plusDays(1), user);
        Task completedTask = new Task("Completed Task", "Description", LocalDateTime.now().plusDays(1), user);
        completedTask.setStatus(TaskStatus.CONCLUIDA);
        
        entityManager.persistAndFlush(pendingTask);
        entityManager.persistAndFlush(completedTask);
        
        // when
        List<Task> completedTasks = taskRepository.findByUserAndStatusOrderByDataCriacaoDesc(user, TaskStatus.CONCLUIDA);
        
        // then
        assertThat(completedTasks).hasSize(1);
        assertThat(completedTasks.get(0).getTitulo()).isEqualTo("Completed Task");
        assertThat(completedTasks.get(0).getStatus()).isEqualTo(TaskStatus.CONCLUIDA);
    }
    
    @Test
    public void whenFindByIdAndUser_thenReturnTask() {
        // given
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persistAndFlush(user);
        
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), user);
        entityManager.persistAndFlush(task);
        
        // when
        Optional<Task> found = taskRepository.findByIdAndUser(task.getId(), user);
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitulo()).isEqualTo("Test Task");
        assertThat(found.get().getUser()).isEqualTo(user);
    }
}