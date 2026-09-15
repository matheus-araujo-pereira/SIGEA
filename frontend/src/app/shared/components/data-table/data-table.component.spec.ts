import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DataTablePaginationComponent } from './data-table.component';
import { PageResponse } from '../../../core/models/page.model';

describe('DataTablePaginationComponent', () => {
  let component: DataTablePaginationComponent;
  let fixture: ComponentFixture<DataTablePaginationComponent>;

  const mockPage: PageResponse<string> = {
    content: ['item1', 'item2'],
    page: 1,
    size: 10,
    totalElements: 25,
    totalPages: 3,
    first: false,
    last: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataTablePaginationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DataTablePaginationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('não deve renderizar paginação se pageData for nulo', () => {
    fixture.componentRef.setInput('pageData', null);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent.trim()).toBe('');
  });

  it('deve retornar valores padrão seguros quando pageData for nulo', () => {
    fixture.componentRef.setInput('pageData', null);
    fixture.detectChanges();

    expect(component.currentPage()).toBe(0);
    expect(component.totalPages()).toBe(0);
    expect(component.totalElements()).toBe(0);
    expect(component.isFirst()).toBe(true);
    expect(component.isLast()).toBe(true);
    expect(component.startItem()).toBe(0);
    expect(component.endItem()).toBe(0);
  });

  it('deve aplicar fallbacks padrão de tamanho de página e conteúdo quando ausentes', () => {
    const pageWithMissingProps: any = {
      content: undefined,
      page: 1,
      size: undefined,
      totalElements: 20,
      totalPages: 2,
      first: false,
      last: false,
    };
    fixture.componentRef.setInput('pageData', pageWithMissingProps);
    fixture.detectChanges();

    expect(component.startItem()).toBe(11);
    expect(component.endItem()).toBe(10);
  });

  it('deve exibir mensagem de nenhum registro quando totalElements for 0', () => {
    const emptyPage: PageResponse<string> = {
      content: [],
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
      first: true,
      last: true,
    };
    fixture.componentRef.setInput('pageData', emptyPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Nenhum registro encontrado');
    expect(component.startItem()).toBe(0);
    expect(component.endItem()).toBe(0);
  });

  it('deve calcular corretamente intervalo de itens e páginas', () => {
    fixture.componentRef.setInput('pageData', mockPage);
    fixture.detectChanges();

    expect(component.startItem()).toBe(11);
    expect(component.endItem()).toBe(12);
    expect(fixture.nativeElement.textContent).toContain('Exibindo 11 a 12 de 25 registros');
    expect(fixture.nativeElement.textContent).toContain('Página 2 de 3');
  });

  it('deve emitir pageChange ao navegar para página válida', () => {
    fixture.componentRef.setInput('pageData', mockPage);
    fixture.detectChanges();

    const pageSpy = jest.fn();
    component.pageChange.subscribe(pageSpy);

    component.onPage(0); // primeira
    expect(pageSpy).toHaveBeenCalledWith(0);

    component.onPage(2); // próxima
    expect(pageSpy).toHaveBeenCalledWith(2);
  });

  it('não deve emitir pageChange para página fora dos limites ou para a página atual', () => {
    fixture.componentRef.setInput('pageData', mockPage);
    fixture.detectChanges();

    const pageSpy = jest.fn();
    component.pageChange.subscribe(pageSpy);

    component.onPage(1); // página atual
    expect(pageSpy).not.toHaveBeenCalled();

    component.onPage(-1); // menor que 0
    expect(pageSpy).not.toHaveBeenCalled();

    component.onPage(5); // maior que totalPages
    expect(pageSpy).not.toHaveBeenCalled();
  });
});
