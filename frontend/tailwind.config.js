/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts,scss}'],
  darkMode: 'class',
  theme: {
    extend: {
      screens: {
        xs: '480px',
        fhd: '1920px', // 1080p FHD Widescreen
        qhd: '2560px', // 1440p QHD Widescreen
        uw: '3440px', // 21:9 UWQHD Ultrawide
        suw: '5120px', // 32:9 Super Ultrawide
      },
      maxWidth: {
        workspace: '2560px',
        'workspace-uw': '3200px',
      },
    },
  },
  plugins: [require('tailwindcss-primeui')],
};
