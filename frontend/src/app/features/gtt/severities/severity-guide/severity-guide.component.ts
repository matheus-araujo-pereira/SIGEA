import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { HarmSeverity } from '../../../../core/models/harm-severity.model';

/**
 * Guia Clínico Interativo de Gravidades de Dano (Índice NCC MERP adaptado pelo IHI).
 * Disponível para consulta educacional de todos os perfis (ADMIN, PROFESSOR, STUDENT).
 */
@Component({
  selector: 'app-severity-guide',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './severity-guide.component.html',
})
export class SeverityGuideComponent implements OnInit {
  private readonly severityService = inject(HarmSeverityService);

  readonly severities = signal<HarmSeverity[]>([]);
  readonly isLoading = signal<boolean>(false);

  readonly selectedTab = signal<'ALL' | 'NO_HARM' | 'HARM'>('ALL');
  readonly searchQuery = signal<string>('');

  ngOnInit(): void {
    this.loadGuide();
  }

  loadGuide(): void {
    this.isLoading.set(true);
    this.severityService.getGuide().subscribe({
      next: (res) => {
        this.severities.set(res.data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  readonly filteredList = computed(() => {
    const list = this.severities();
    const query = this.searchQuery().toLowerCase().trim();

    return list.filter((sev) => {
      const matchesSearch =
        !query ||
        sev.categoryLetter.toLowerCase().includes(query) ||
        sev.name.toLowerCase().includes(query) ||
        sev.description.toLowerCase().includes(query);

      const matchesTab =
        this.selectedTab() === 'ALL' ||
        (this.selectedTab() === 'NO_HARM' && !sev.isHarm) ||
        (this.selectedTab() === 'HARM' && sev.isHarm);

      return matchesSearch && matchesTab;
    });
  });

  readonly filteredNoHarmList = computed(() => {
    return this.filteredList().filter((sev) => !sev.isHarm);
  });

  readonly filteredHarmList = computed(() => {
    return this.filteredList().filter((sev) => sev.isHarm);
  });

  readonly showNoHarmGroup = computed(() => {
    return (
      (this.selectedTab() === 'ALL' || this.selectedTab() === 'NO_HARM') &&
      this.filteredNoHarmList().length > 0
    );
  });

  readonly showHarmGroup = computed(() => {
    return (
      (this.selectedTab() === 'ALL' || this.selectedTab() === 'HARM') &&
      this.filteredHarmList().length > 0
    );
  });
}
