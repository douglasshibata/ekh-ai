import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';

interface Message {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  sources?: any[];
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="flex flex-col h-screen max-w-4xl mx-auto p-4">
      <header class="flex items-center justify-between mb-6 border-b pb-4">
        <h1 class="text-2xl font-bold text-gray-800 text-blue-600">EKH AI Chat</h1>
        <div class="flex gap-2">
          <select [ngModel]="department()" (ngModelChange)="department.set($event)" class="border rounded p-2 text-sm bg-white">
            <option value="">Geral (Todos)</option>
            <option value="RH">Recursos Humanos</option>
            <option value="JURIDICO">Jurídico</option>
            <option value="FINANCEIRO">Financeiro</option>
            <option value="TI">Tecnologia</option>
            <option value="COMERCIAL">Comercial</option>
          </select>
        </div>
      </header>

      <div class="flex-1 overflow-y-auto mb-4 space-y-4 pr-2 scroll-smooth">
        @for (msg of messages(); track msg.timestamp) {
          <div [class]="msg.role === 'user' ? 'flex justify-end' : 'flex justify-start'">
            <div [class]="msg.role === 'user' ? 'bg-blue-600 text-white rounded-l-2xl rounded-tr-2xl' : 'bg-gray-100 text-gray-800 rounded-r-2xl rounded-tl-2xl border border-gray-200'"
                 class="max-w-[85%] p-4 shadow-sm transition-all duration-300">
              <p class="text-sm leading-relaxed whitespace-pre-wrap">{{ msg.content }}</p>

              @if (msg.sources && msg.sources.length > 0) {
                <div class="mt-4 pt-3 border-t border-gray-300/30 text-xs">
                  <span class="font-bold flex items-center gap-1 mb-2">
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
                    Referências:
                  </span>
                  <div class="flex flex-wrap gap-2">
                    @for (source of msg.sources; track source.title) {
                      <span class="bg-blue-50 text-blue-700 px-2 py-1 rounded border border-blue-100">
                        {{ source.title }}
                      </span>
                    }
                  </div>
                </div>
              }
              <div class="flex justify-end mt-1">
                <span class="text-[9px] opacity-40 uppercase font-bold">{{ msg.timestamp | date:'HH:mm:ss' }}</span>
              </div>
            </div>
          </div>
        }
        @if (isLoading()) {
          <div class="flex justify-start">
            <div class="bg-gray-50 border border-gray-100 p-4 rounded-2xl shadow-sm">
              <div class="flex space-x-2">
                <div class="h-2 w-2 bg-blue-400 rounded-full animate-bounce"></div>
                <div class="h-2 w-2 bg-blue-400 rounded-full animate-bounce delay-100"></div>
                <div class="h-2 w-2 bg-blue-400 rounded-full animate-bounce delay-200"></div>
              </div>
            </div>
          </div>
        }
      </div>

      <footer class="border-t pt-4 bg-white sticky bottom-0">
        <div class="flex gap-2 items-center bg-gray-50 p-2 rounded-xl border border-gray-200 focus-within:border-blue-400 focus-within:ring-1 focus-within:ring-blue-400 transition-all">
          <textarea
            [ngModel]="userInput()"
            (ngModelChange)="userInput.set($event)"
            (keyup.enter)="sendMessage()"
            placeholder="Qual o procedimento para aprovar fornecedores?"
            class="flex-1 bg-transparent border-none p-3 focus:outline-none resize-none h-14 text-sm"
          ></textarea>
          <button
            (click)="sendMessage()"
            [disabled]="!userInput().trim() || isLoading()"
            class="bg-blue-600 text-white p-3 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:grayscale transition-all flex items-center justify-center"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
          </button>
        </div>
        <p class="text-[10px] text-center text-gray-400 mt-2">EKH AI pode apresentar informações imprecisas. Verifique documentos importantes.</p>
      </footer>
    </div>
  `,
  styles: [`
    :host { display: block; height: 100%; background: #fdfdfd; }
    .delay-100 { animation-delay: 0.1s; }
    .delay-200 { animation-delay: 0.2s; }
  `]
})
export class ChatComponent {
  private apiService = inject(ApiService);

  messages = signal<Message[]>([]);
  userInput = signal('');
  isLoading = signal(false);
  department = signal('');

  constructor() {
    this.messages.update(msgs => [...msgs, {
      role: 'assistant',
      content: 'Bem-vindo ao EKH AI Hub. Estou conectado à base de conhecimento corporativa. Como posso auxiliar seu departamento hoje?',
      timestamp: new Date()
    }]);
  }

  sendMessage() {
    const query = this.userInput().trim();
    if (!query || this.isLoading()) return;

    this.messages.update(msgs => [...msgs, {
      role: 'user',
      content: query,
      timestamp: new Date()
    }]);

    this.userInput.set('');
    this.isLoading.set(true);

    this.apiService.ask(query, this.department()).subscribe({
      next: (res) => {
        this.messages.update(msgs => [...msgs, {
          role: 'assistant',
          content: res.answer,
          timestamp: new Date(),
          sources: res.sources
        }]);
      },
      error: (err) => {
        this.messages.update(msgs => [...msgs, {
          role: 'assistant',
          content: 'Desculpe, ocorreu um erro ao processar sua solicitação. Verifique se o servidor backend está online.',
          timestamp: new Date()
        }]);
        console.error(err);
      },
      complete: () => this.isLoading.set(false)
    });
  }
}
