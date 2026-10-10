// Tema especializado em baixo contraste e proteção visual contra fotofobia e estresse óptico
// Usando paleta monocromática com tons de carvão, grafite e cinzas apagados sem picos de luz azul

export const DimColors = {
  // Planos de fundo profundos, sem preto 100% puro para evitar corte estridente
  bgCanvas: '#141416',       // Fundo mestre suave e fosco
  bgSurface: '#1c1c1f',      // Superfície de cartões e modais
  bgElevated: '#242429',     // Superfície destacada e toolbars
  bgSubtle: '#2c2c33',       // Divisores de relevo e campos de digitação

  // Bordas e contornos quase imperceptíveis
  borderSubtle: '#2e2e36',
  borderFocus: '#42424d',

  // Tipografia esmaecida (quase apagada para descanso ocular prolongado)
  textMuted: '#686873',      // Letras secundárias e labels (apagadas)
  textDimmed: '#8e8e99',     // Letras primárias de leitura confortável
  textQuiet: '#b0b0ba',      // Letras de títulos (ainda sem brilho agudo)
  textGhost: '#484852',      // Placeholders quase transparentes

  // Acentos atenuados (sem neon, sem saturação forte)
  accentSlate: '#4a505b',
  accentSubdued: '#393d45',
  accentBorder: '#565c69',
  accentActive: '#707787',
  dangerMuted: '#6b3f3f',
  successMuted: '#3f5e4b',
};

export const DimTypography = {
  fontSans: 'System',
  sizes: {
    xs: 11,
    sm: 13,
    base: 15,
    md: 17,
    lg: 20,
    xl: 24,
    editorDefault: 17,
  },
};
