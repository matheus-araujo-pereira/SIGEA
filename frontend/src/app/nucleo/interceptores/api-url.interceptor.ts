import { HttpInterceptorFn } from '@angular/common/http';

declare global {
  interface Window {
    /** Variável opcional injetada em tempo de execução para sobrescrever a URL base da API. */
    __SIGEA_API_URL__?: string;
  }
}

/**
 * Interceptor HTTP responsável por prefixar a URL base da API e injetar o token JWT de autenticação.
 *
 * Em ambiente local (`localhost` ou `127.0.0.1`), utiliza caminho relativo para passar pelo proxy de desenvolvimento.
 * Em produção, utiliza o backend remoto hospedado ou o valor injetado no runtime `window.__SIGEA_API_URL__`.
 *
 * @param req Requisição HTTP em trânsito.
 * @param next Handler para o próximo interceptor ou backend.
 */
export const apiUrlInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('/api')) return next(req);

  let baseUrl = 'https://sigea-gtt-backend.onrender.com';

  if (typeof window !== 'undefined') {
    if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
      baseUrl = '';
    } else if (window.__SIGEA_API_URL__) {
      const urlConfigurada = window.__SIGEA_API_URL__.trim();
      // Se a URL contém apenas o nome interno (ex: https://sigea-gtt-backend), completa com .onrender.com
      if (
        urlConfigurada.includes('sigea-gtt-backend') &&
        !urlConfigurada.includes('.onrender.com')
      ) {
        baseUrl = 'https://sigea-gtt-backend.onrender.com';
      } else if (urlConfigurada.includes('.')) {
        baseUrl = urlConfigurada;
      }
    }
  }

  // Recupera token da sessão no localStorage
  let token: string | null = null;
  try {
    const dados = localStorage.getItem('sigea_sessao');
    if (dados) {
      const usuario = JSON.parse(dados);
      token = usuario?.token || null;
    }
  } catch {
    // Ignora erro de parse de JSON
  }

  const headers = token ? req.headers.set('Authorization', `Bearer ${token}`) : req.headers;

  return next(
    req.clone({
      url: `${baseUrl}${req.url}`,
      headers,
      withCredentials: true,
    }),
  );
};
