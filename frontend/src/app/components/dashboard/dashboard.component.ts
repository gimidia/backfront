import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus, TaskStatusDisplay, TaskRequest } from '../../models/task.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  loading = false;
  error = '';
  success = '';
  
  // Task form
  taskForm: FormGroup;
  showTaskForm = false;
  editingTask: Task | null = null;
  
  // Filters
  statusFilter = '';
  searchFilter = '';
  
  // Task statuses
  taskStatuses = Object.values(TaskStatus);
  taskStatusDisplay = TaskStatusDisplay;

  constructor(
    private authService: AuthService,
    private taskService: TaskService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.taskForm = this.formBuilder.group({
      titulo: ['', [Validators.required, Validators.maxLength(100)]],
      descricao: ['', [Validators.maxLength(500)]],
      dataVencimento: ['', [Validators.required]],
      status: [TaskStatus.PENDENTE]
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.error = '';
    
    const status = this.statusFilter ? this.statusFilter as TaskStatus : undefined;
    const search = this.searchFilter || undefined;
    
    this.taskService.getAllTasks(status, search).subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erro ao carregar tarefas';
        this.loading = false;
        console.error('Load tasks error:', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredTasks = this.tasks.filter(task => {
      const matchesStatus = !this.statusFilter || task.status === this.statusFilter;
      const matchesSearch = !this.searchFilter || 
        task.titulo.toLowerCase().includes(this.searchFilter.toLowerCase()) ||
        task.descricao.toLowerCase().includes(this.searchFilter.toLowerCase());
      
      return matchesStatus && matchesSearch;
    });
  }

  onStatusFilterChange(): void {
    this.applyFilters();
  }

  onSearchFilterChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.statusFilter = '';
    this.searchFilter = '';
    this.loadTasks();
  }

  showCreateForm(): void {
    this.editingTask = null;
    this.taskForm.reset({
      status: TaskStatus.PENDENTE
    });
    this.showTaskForm = true;
  }

  showEditForm(task: Task): void {
    this.editingTask = task;
    this.taskForm.patchValue({
      titulo: task.titulo,
      descricao: task.descricao,
      dataVencimento: task.dataVencimento.split('T')[0], // Format for date input
      status: task.status
    });
    this.showTaskForm = true;
  }

  hideTaskForm(): void {
    this.showTaskForm = false;
    this.editingTask = null;
    this.taskForm.reset();
  }

  onSubmitTask(): void {
    if (this.taskForm.valid) {
      this.loading = true;
      this.error = '';
      this.success = '';

      const formValue = this.taskForm.value;
      const taskData: TaskRequest = {
        titulo: formValue.titulo,
        descricao: formValue.descricao,
        dataVencimento: formValue.dataVencimento + 'T00:00:00',
        status: formValue.status
      };

      const operation = this.editingTask 
        ? this.taskService.updateTask(this.editingTask.id, taskData)
        : this.taskService.createTask(taskData);

      operation.subscribe({
        next: (task) => {
          this.loading = false;
          this.success = this.editingTask ? 'Tarefa atualizada com sucesso!' : 'Tarefa criada com sucesso!';
          this.hideTaskForm();
          this.loadTasks();
          setTimeout(() => this.success = '', 3000);
        },
        error: (error) => {
          this.loading = false;
          this.error = 'Erro ao salvar tarefa';
          console.error('Save task error:', error);
        }
      });
    }
  }

  completeTask(task: Task): void {
    if (task.status !== TaskStatus.CONCLUIDA) {
      this.taskService.completeTask(task.id).subscribe({
        next: () => {
          this.success = 'Tarefa marcada como concluída!';
          this.loadTasks();
          setTimeout(() => this.success = '', 3000);
        },
        error: (error) => {
          this.error = 'Erro ao completar tarefa';
          console.error('Complete task error:', error);
        }
      });
    }
  }

  deleteTask(task: Task): void {
    if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
      this.taskService.deleteTask(task.id).subscribe({
        next: () => {
          this.success = 'Tarefa excluída com sucesso!';
          this.loadTasks();
          setTimeout(() => this.success = '', 3000);
        },
        error: (error) => {
          this.error = 'Erro ao excluir tarefa';
          console.error('Delete task error:', error);
        }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusClass(status: TaskStatus): string {
    switch (status) {
      case TaskStatus.PENDENTE:
        return 'bg-yellow-100 text-yellow-800';
      case TaskStatus.EM_ANDAMENTO:
        return 'bg-blue-100 text-blue-800';
      case TaskStatus.CONCLUIDA:
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  }

  // Getter methods for form fields
  get titulo() { return this.taskForm.get('titulo'); }
  get descricao() { return this.taskForm.get('descricao'); }
  get dataVencimento() { return this.taskForm.get('dataVencimento'); }
}