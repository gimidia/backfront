export interface Task {
  id: number;
  titulo: string;
  descricao: string;
  dataCriacao: string;
  dataVencimento: string;
  status: TaskStatus;
}

export interface TaskRequest {
  titulo: string;
  descricao: string;
  dataVencimento: string;
  status?: TaskStatus;
}

export enum TaskStatus {
  PENDENTE = 'PENDENTE',
  EM_ANDAMENTO = 'EM_ANDAMENTO',
  CONCLUIDA = 'CONCLUIDA'
}

export const TaskStatusDisplay = {
  [TaskStatus.PENDENTE]: 'Pendente',
  [TaskStatus.EM_ANDAMENTO]: 'Em Andamento',
  [TaskStatus.CONCLUIDA]: 'Concluída'
};