package com.taskmanagement.api.controller;

import com.taskmanagement.api.dto.MessageResponse;
import com.taskmanagement.api.dto.TaskRequest;
import com.taskmanagement.api.model.Task;
import com.taskmanagement.api.model.TaskStatus;
import com.taskmanagement.api.model.User;
import com.taskmanagement.api.repository.TaskRepository;
import com.taskmanagement.api.repository.UserRepository;
import com.taskmanagement.api.service.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management endpoints")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    @Operation(summary = "Get all tasks for authenticated user")
    public ResponseEntity<List<Task>> getAllTasks(Authentication authentication,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String search) {
        User user = getCurrentUser(authentication);
        List<Task> tasks;
        
        if (status != null && !status.isEmpty()) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            tasks = taskRepository.findByUserAndStatusOrderByDataCriacaoDesc(user, taskStatus);
        } else if (search != null && !search.isEmpty()) {
            tasks = taskRepository.findByUserAndTituloContainingIgnoreCaseOrderByDataCriacaoDesc(user, search);
        } else {
            tasks = taskRepository.findByUserOrderByDataCriacaoDesc(user);
        }
        
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Task> task = taskRepository.findByIdAndUser(id, user);
        
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Create new task")
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest, 
                                          Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        Task task = new Task();
        task.setTitulo(taskRequest.getTitulo());
        task.setDescricao(taskRequest.getDescricao());
        task.setDataVencimento(taskRequest.getDataVencimento());
        task.setUser(user);
        
        if (taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
        }
        
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, 
                                          @Valid @RequestBody TaskRequest taskRequest,
                                          Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Task> taskOptional = taskRepository.findByIdAndUser(id, user);
        
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setTitulo(taskRequest.getTitulo());
            task.setDescricao(taskRequest.getDescricao());
            task.setDataVencimento(taskRequest.getDataVencimento());
            
            if (taskRequest.getStatus() != null) {
                task.setStatus(taskRequest.getStatus());
            }
            
            Task updatedTask = taskRepository.save(task);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark task as completed")
    public ResponseEntity<Task> completeTask(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Task> taskOptional = taskRepository.findByIdAndUser(id, user);
        
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setStatus(TaskStatus.CONCLUIDA);
            Task updatedTask = taskRepository.save(task);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Task> taskOptional = taskRepository.findByIdAndUser(id, user);
        
        if (taskOptional.isPresent()) {
            taskRepository.delete(taskOptional.get());
            return ResponseEntity.ok(new MessageResponse("Task deleted successfully!"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    private User getCurrentUser(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}