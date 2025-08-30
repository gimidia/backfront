package com.taskmanagement.api.repository;

import com.taskmanagement.api.model.Task;
import com.taskmanagement.api.model.TaskStatus;
import com.taskmanagement.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUserOrderByDataCriacaoDesc(User user);
    
    List<Task> findByUserAndStatusOrderByDataCriacaoDesc(User user, TaskStatus status);
    
    Optional<Task> findByIdAndUser(Long id, User user);
    
    List<Task> findByUserAndTituloContainingIgnoreCaseOrderByDataCriacaoDesc(User user, String titulo);
}