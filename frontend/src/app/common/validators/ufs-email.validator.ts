import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Expressão regular estrita para validação de e-mail institucional da UFS.
 * Somente aceita o sufixo oficial: `@academico.ufs.br`.
 */
export const UFS_EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@academico\.ufs\.br$/;

/**
 * Validador síncrono para Reactive Forms do Angular que valida se o valor
 * do controle pertence exclusivamente ao domínio institucional `@academico.ufs.br`.
 *
 * @returns ValidatorFn Função de validação compatível com FormGroup/FormControl.
 */
export function ufsEmailValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value) {
      return null; // Deixa a obrigatoriedade para Validators.required
    }
    const isValid = UFS_EMAIL_REGEX.test(String(value).trim());
    return isValid ? null : { ufsEmail: { value, message: 'O e-mail deve pertencer obrigatoriamente ao domínio @academico.ufs.br' } };
  };
}
