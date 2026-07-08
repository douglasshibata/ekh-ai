import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/v1';

  ask(question: string, department?: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/chat/ask`, { question, department });
  }

  getMetrics(): Observable<any> {
    return this.http.get(`${this.baseUrl}/admin/metrics`);
  }

  uploadDocument(file: File, department: string, confidentiality: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('department', department);
    formData.append('confidentiality', confidentiality);
    return this.http.post(`${this.baseUrl}/documents/upload`, formData);
  }
}
