import { Component, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as Highcharts from 'highcharts';
import { HighchartsChartModule } from 'highcharts-angular';
import { ApiService } from '../../core/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, HighchartsChartModule],
  template: `
    <div class="p-8 bg-gray-50 min-h-screen">
      <header class="mb-8 flex justify-between items-center">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Métricas do Sistema</h1>
          <p class="text-gray-500">Monitoramento em tempo real do Knowledge Hub</p>
        </div>
        <button (click)="loadMetrics()" class="bg-white border px-4 py-2 rounded-lg text-sm font-semibold hover:bg-gray-50 transition-colors">
          Atualizar
        </button>
      </header>

      <!-- KPI Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        @for (kpi of kpis(); track kpi.label) {
          <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
            <span class="text-gray-500 text-xs font-bold uppercase tracking-wider">{{ kpi.label }}</span>
            <div class="flex items-end justify-between mt-2">
              <span class="text-2xl font-bold text-gray-800">{{ kpi.value }}</span>
            </div>
          </div>
        }
      </div>

      <!-- Charts Row -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
          <h3 class="text-lg font-bold mb-6 text-gray-800 flex items-center gap-2">
            <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
            Volume de Consultas
          </h3>
          <highcharts-chart
            [Highcharts]="Highcharts"
            [options]="usageChartOptions"
            style="width: 100%; height: 350px; display: block;"
          ></highcharts-chart>
        </div>

        <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
          <h3 class="text-lg font-bold mb-6 text-gray-800 flex items-center gap-2">
            <div class="w-2 h-2 bg-green-500 rounded-full"></div>
            Uso por Departamento
          </h3>
          <highcharts-chart
            [Highcharts]="Highcharts"
            [options]="deptChartOptions"
            style="width: 100%; height: 350px; display: block;"
          ></highcharts-chart>
        </div>
      </div>
    </div>
  `
})
export class AdminDashboardComponent implements OnInit {
  private apiService = inject(ApiService);
  Highcharts: typeof Highcharts = Highcharts;

  kpis = signal([
    { label: 'Documentos', value: '0' },
    { label: 'Consultas Hoje', value: '0' },
    { label: 'Latência Média', value: '0ms' },
    { label: 'Custo Acumulado', value: '$0.00' }
  ]);

  usageChartOptions: Highcharts.Options = {
    chart: { type: 'spline', backgroundColor: 'transparent' },
    title: { text: '' },
    xAxis: { categories: ['00h', '04h', '08h', '12h', '16h', '20h'] },
    yAxis: { title: { text: null } },
    credits: { enabled: false },
    series: [{
      name: 'Requisições',
      data: [5, 10, 45, 80, 120, 95],
      type: 'spline',
      color: '#3b82f6'
    }]
  };

  deptChartOptions: Highcharts.Options = {
    chart: { type: 'pie', backgroundColor: 'transparent' },
    title: { text: '' },
    credits: { enabled: false },
    plotOptions: {
      pie: {
        innerSize: '60%',
        dataLabels: { enabled: true, format: '{point.name}: {point.percentage:.1f}%' }
      }
    },
    series: [{
      name: 'Consultas',
      type: 'pie',
      data: [
        { name: 'RH', y: 40, color: '#60a5fa' },
        { name: 'Jurídico', y: 25, color: '#34d399' },
        { name: 'Financeiro', y: 15, color: '#fbbf24' },
        { name: 'Comercial', y: 12, color: '#f87171' },
        { name: 'TI', y: 8, color: '#818cf8' }
      ]
    }]
  };

  ngOnInit() {
    this.loadMetrics();
  }

  loadMetrics() {
    this.apiService.getMetrics().subscribe({
      next: (data) => {
        this.kpis.set([
          { label: 'Documentos', value: data.totalDocuments.toString() },
          { label: 'Consultas Hoje', value: data.queriesToday.toString() },
          { label: 'Latência Média', value: data.averageResponseTime },
          { label: 'Custo Acumulado', value: data.costThisMonth }
        ]);
      },
      error: (err) => console.error('Failed to load metrics', err)
    });
  }
}
