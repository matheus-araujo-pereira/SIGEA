import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { GttTrigger } from '../../../../core/models/gtt-trigger.model';
import { GttModule } from '../../../../core/models/gtt-module.model';

/**
 * Tela de Consulta Rápida e Guia Educacional de Gatilhos IHI-GTT para todos os perfis.
 */
@Component({
  selector: 'app-trigger-guide',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trigger-guide.component.html',
})
export class TriggerGuideComponent implements OnInit {
  private readonly triggerService = inject(GttTriggerService);
  private readonly moduleService = inject(GttModuleService);
  private readonly route = inject(ActivatedRoute);

  readonly triggers = signal<GttTrigger[]>([]);
  readonly modules = signal<GttModule[]>([]);
  readonly expandedIds = signal<Set<string>>(new Set());
  readonly isLoading = signal<boolean>(false);

  searchQuery = '';
  selectedModuleId = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['moduleId']) {
        this.selectedModuleId = params['moduleId'];
      }
      this.loadData();
    });
  }

  loadData(): void {
    this.isLoading.set(true);
    this.moduleService.getCatalog().subscribe({
      next: (modRes) => {
        this.modules.set(modRes.data);
        this.triggerService.getCatalog().subscribe({
          next: (trigRes) => {
            this.isLoading.set(false);
            this.triggers.set(trigRes.data);
            // Expande os primeiros por conveniência
            if (trigRes.data.length > 0) {
              this.expandedIds.set(new Set([trigRes.data[0].id]));
            }
          },
          error: () => this.isLoading.set(false),
        });
      },
      error: () => this.isLoading.set(false),
    });
  }

  selectModule(moduleId: string): void {
    this.selectedModuleId = moduleId;
  }

  toggleExpand(id: string): void {
    const current = new Set(this.expandedIds());
    if (current.has(id)) {
      current.delete(id);
    } else {
      current.add(id);
    }
    this.expandedIds.set(current);
  }

  isExpanded(id: string): boolean {
    return this.expandedIds().has(id);
  }

  filteredTriggers(): GttTrigger[] {
    const q = this.searchQuery.toLowerCase().trim();
    return this.triggers().filter((t) => {
      const matchesModule = !this.selectedModuleId || t.moduleId === this.selectedModuleId;
      const matchesQuery =
        !q ||
        t.code.toLowerCase().includes(q) ||
        t.name.toLowerCase().includes(q) ||
        t.description.toLowerCase().includes(q) ||
        t.moduleName.toLowerCase().includes(q);

      return matchesModule && matchesQuery;
    });
  }
}
