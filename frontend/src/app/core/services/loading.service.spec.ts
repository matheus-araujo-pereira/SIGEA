import { TestBed } from '@angular/core/testing';
import { LoadingService } from './loading.service';

describe('LoadingService', () => {
  let service: LoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoadingService);
  });

  it('deve ser instanciado com isLoading falso', () => {
    expect(service).toBeTruthy();
    expect(service.isLoading()).toBe(false);
  });

  it('deve ativar loading ao chamar show e desativar com hide', () => {
    service.show();
    expect(service.isLoading()).toBe(true);

    service.hide();
    expect(service.isLoading()).toBe(false);
  });

  it('deve gerenciar múltiplas requisições simultâneas', () => {
    service.show();
    service.show();
    expect(service.isLoading()).toBe(true);

    service.hide();
    expect(service.isLoading()).toBe(true);

    service.hide();
    expect(service.isLoading()).toBe(false);
  });

  it('não deve ficar negativo com chamadas excessivas de hide', () => {
    service.hide();
    service.hide();
    expect(service.isLoading()).toBe(false);

    service.show();
    expect(service.isLoading()).toBe(true);
    service.hide();
    expect(service.isLoading()).toBe(false);
  });

  it('deve redefinir o estado ao chamar reset', () => {
    service.show();
    service.show();
    expect(service.isLoading()).toBe(true);

    service.reset();
    expect(service.isLoading()).toBe(false);
  });
});
