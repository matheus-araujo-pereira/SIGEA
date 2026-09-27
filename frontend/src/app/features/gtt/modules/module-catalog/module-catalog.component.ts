import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { GttModule } from '../../../../core/models/gtt-module.model';

/**
 * Tela de Catálogo Educacional de Módulos IHI-GTT para todos os perfis.
 */
@Component({
  selector: 'app-module-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './module-catalog.component.html',
})
export class ModuleCatalogComponent implements OnInit {
  private readonly moduleService = inject(GttModuleService);

  readonly modules = signal<GttModule[]>([]);
  readonly isLoading = signal<boolean>(false);

  ngOnInit(): void {
    this.loadCatalog();
  }

  loadCatalog(): void {
    this.isLoading.set(true);
    this.moduleService.getCatalog().subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.modules.set(res.data);
      },
      error: () => this.isLoading.set(false),
    });
  }

  getModuleColor(code: string): string {
    const normalized = (code || '').trim().toUpperCase();
    switch (normalized) {
      case 'C':
      case 'CUIDADOS':
        return 'bg-blue-50 text-blue-700 border border-blue-200';
      case 'M':
      case 'MEDICACAO':
        return 'bg-amber-50 text-amber-700 border border-amber-200';
      case 'S':
      case 'CIRURGICO':
        return 'bg-emerald-50 text-emerald-700 border border-emerald-200';
      case 'I':
      case 'UTI':
        return 'bg-purple-50 text-purple-700 border border-purple-200';
      case 'P':
      case 'PERINATAL':
        return 'bg-rose-50 text-rose-700 border border-rose-200';
      case 'E':
      case 'URGENCIA':
        return 'bg-orange-50 text-orange-700 border border-orange-200';
      default:
        return 'bg-slate-50 text-slate-700 border border-slate-200';
    }
  }
}
