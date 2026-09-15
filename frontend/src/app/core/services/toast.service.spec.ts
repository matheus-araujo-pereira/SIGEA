import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ToastService } from './toast.service';

describe('ToastService', () => {
  let service: ToastService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ToastService);
  });

  it('deve ser instanciado', () => {
    expect(service).toBeTruthy();
    expect(service.toasts()).toEqual([]);
  });

  it('deve adicionar notificação com sucesso', () => {
    service.success('Sucesso', 'Operação concluída');
    expect(service.toasts().length).toBe(1);
    expect(service.toasts()[0].type).toBe('success');
    expect(service.toasts()[0].title).toBe('Sucesso');
    expect(service.toasts()[0].message).toBe('Operação concluída');
  });

  it('deve adicionar notificação de erro', () => {
    service.error('Erro', 'Falha na requisição');
    expect(service.toasts().length).toBe(1);
    expect(service.toasts()[0].type).toBe('error');
    expect(service.toasts()[0].duration).toBe(5000);
  });

  it('deve adicionar notificação de alerta', () => {
    service.warning('Aviso', 'Atenção necessária');
    expect(service.toasts().length).toBe(1);
    expect(service.toasts()[0].type).toBe('warning');
  });

  it('deve adicionar notificação informativa', () => {
    service.info('Info', 'Informação do sistema');
    expect(service.toasts().length).toBe(1);
    expect(service.toasts()[0].type).toBe('info');
  });

  it('deve utilizar mensagem vazia por padrão quando não informada', () => {
    service.success('Sucesso');
    service.error('Erro');
    service.warning('Aviso');
    service.info('Info');
    expect(service.toasts().length).toBe(4);
    expect(service.toasts()[0].message).toBe('');
    expect(service.toasts()[1].message).toBe('');
    expect(service.toasts()[2].message).toBe('');
    expect(service.toasts()[3].message).toBe('');
  });

  it('deve chamar show com valores padrão de mensagem e duração', () => {
    service.show('info', 'Sem opcionais');
    expect(service.toasts().length).toBe(1);
    expect(service.toasts()[0].message).toBe('');
    expect(service.toasts()[0].duration).toBe(4000);
  });

  it('deve remover notificação manualmente pelo ID', () => {
    service.show('info', 'Teste', 'Msg', 0);
    const id = service.toasts()[0].id;
    expect(service.toasts().length).toBe(1);

    service.remove(id);
    expect(service.toasts().length).toBe(0);
  });

  it('deve remover notificação automaticamente após o timeout', fakeAsync(() => {
    service.show('info', 'Temporário', 'Msg', 1000);
    expect(service.toasts().length).toBe(1);

    tick(1000);
    expect(service.toasts().length).toBe(0);
  }));
});
