import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskRequest, TaskStatus } from '../models/task.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly API_URL = 'http://localhost:8080/api/tasks';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getAllTasks(status?: TaskStatus, search?: string): Observable<Task[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<Task[]>(this.API_URL, {
      headers: this.authService.getAuthHeaders(),
      params
    });
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.API_URL}/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  createTask(taskData: TaskRequest): Observable<Task> {
    return this.http.post<Task>(this.API_URL, taskData, {
      headers: this.authService.getAuthHeaders()
    });
  }

  updateTask(id: number, taskData: TaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.API_URL}/${id}`, taskData, {
      headers: this.authService.getAuthHeaders()
    });
  }

  completeTask(id: number): Observable<Task> {
    return this.http.put<Task>(`${this.API_URL}/${id}/complete`, {}, {
      headers: this.authService.getAuthHeaders()
    });
  }

  deleteTask(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`, {
      headers: this.authService.getAuthHeaders()
    });
  }
}