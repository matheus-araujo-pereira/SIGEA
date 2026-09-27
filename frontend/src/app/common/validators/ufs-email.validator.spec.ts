import { FormControl } from '@angular/forms';
import { ufsEmailValidator } from './ufs-email.validator';

describe('ufsEmailValidator', () => {
  const validator = ufsEmailValidator();

  it('deve aceitar e-mails válidos com @academico.ufs.br', () => {
    const control = new FormControl('matheus@academico.ufs.br');
    expect(validator(control)).toBeNull();
  });

  it('deve aceitar e-mails válidos com pontos e hífens no nome de usuário', () => {
    const control = new FormControl('ana.waleska-souza@academico.ufs.br');
    expect(validator(control)).toBeNull();
  });

  it('deve rejeitar e-mails com domínios externos (ex: gmail, hotmail)', () => {
    const control = new FormControl('usuario@gmail.com');
    const result = validator(control);
    expect(result).not.toBeNull();
    expect(result?.['ufsEmail']).toBeDefined();
  });

  it('deve rejeitar e-mails com @ufs.br geral que não sejam @academico.ufs.br', () => {
    const control = new FormControl('docente@ufs.br');
    const result = validator(control);
    expect(result).not.toBeNull();
  });

  it('deve aceitar valores vazios ou nulos delegando para Validators.required', () => {
    expect(validator(new FormControl(''))).toBeNull();
    expect(validator(new FormControl(null))).toBeNull();
    expect(validator(new FormControl(undefined))).toBeNull();
  });
});
