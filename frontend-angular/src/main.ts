import { importProvidersFrom } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { ChatComponent } from './app/features/chat/chat.component';
import { AdminDashboardComponent } from './app/features/dashboard/dashboard.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ChatComponent, AdminDashboardComponent],
  template: `
    <div class="flex h-screen bg-gray-100">
      <!-- Sidebar -->
      <aside class="w-64 bg-gray-900 text-white flex flex-col">
        <div class="p-6 border-b border-gray-800">
          <h1 class="text-xl font-bold flex items-center gap-2">
            <div class="w-3 h-3 bg-blue-500 rounded-full animate-pulse"></div>
            EKH AI Hub
          </h1>
        </div>
        <nav class="flex-1 p-4 space-y-2">
          <button (click)="view.set('chat')"
            [class.bg-blue-600]="view() === 'chat'"
            class="w-full text-left px-4 py-3 rounded-xl hover:bg-gray-800 transition-all flex items-center gap-3">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M8 10h.01M12 10h.01M16 10h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path></svg>
            Chat Corporativo
          </button>
          <button (click)="view.set('dashboard')"
            [class.bg-blue-600]="view() === 'dashboard'"
            class="w-full text-left px-4 py-3 rounded-xl hover:bg-gray-800 transition-all flex items-center gap-3">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path></svg>
            Painel Admin
          </button>
        </nav>
        <div class="p-4 border-t border-gray-800 text-[10px] text-gray-500 text-center">
          v1.0.0-enterprise
        </div>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 overflow-hidden relative">
        @if (view() === 'chat') {
          <app-chat></app-chat>
        } @else {
          <app-dashboard></app-dashboard>
        }
      </main>
    </div>
  `
})
export class App {
  view = signal<'chat' | 'dashboard'>('chat');
}

bootstrapApplication(App, {
  providers: [
    provideHttpClient()
  ]
});
