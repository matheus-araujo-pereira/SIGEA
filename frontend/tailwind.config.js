/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    screens: {
      'hd': '1280px',
      'hd-plus': '1600px',
      'wuxga': '1920px',
      'ultrawide': '2560px',
      'sm': '640px',
      'md': '768px',
      'lg': '1024px',
      'xl': '1280px',
      '2xl': '1536px',
    },
    extend: {
      colors: {
        clinical: {
          50: '#eff6ff',
          100: '#dbeafe',
          200: '#bfdbfe',
          300: '#93c5fd',
          400: '#60a5fa',
          500: '#3b82f6',
          600: '#2563eb', // Blue clinical primary
          700: '#1d4ed8',
          800: '#1e40af',
          900: '#1e3a8a', // Deep clinical navy
          950: '#0f172a',
        },
        hospital: {
          emerald: '#059669',
          'emerald-light': '#d1fae5',
          amber: '#d97706',
          'amber-light': '#fef3c7',
          crimson: '#dc2626',
          'crimson-light': '#fee2e2',
          surface: '#f8fafc',
          card: '#ffffff',
          border: '#e2e8f0',
          text: '#0f172a',
          muted: '#64748b',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      maxWidth: {
        'ultrawide': '1800px',
      },
    },
  },
  plugins: [],
};
