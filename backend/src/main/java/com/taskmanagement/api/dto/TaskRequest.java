package com.taskmanagement.api.dto;

import com.taskmanagement.api.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class TaskRequest {
    
    @NotBlank
    @Size(min = 1, max = 100)
    private String titulo;
    
    @Size(max = 500)
    private String descricao;
    
    @NotNull
    private LocalDateTime dataVencimento;
    
    private TaskStatus status;
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public LocalDateTime getDataVencimento() {
        return dataVencimento;
    }
    
    public void setDataVencimento(LocalDateTime dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}