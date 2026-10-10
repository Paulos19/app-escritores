export interface Chapter {
  id: string;
  bookId: string;
  title: string;
  content: string;
  synopsis?: string;
  order: number;
  wordCount: number;
  lastScrollPosition?: number;
  lastSelectionStart?: number;
  lastSelectionEnd?: number;
  updatedAt: number;
  createdAt: number;
}

export interface Book {
  id: string;
  folderId?: string | null;
  title: string;
  subtitle?: string;
  author?: string;
  genre?: string;
  coverColor: string; // tom de cinza escuro/médio
  coverIcon: string;  // identificador de ícone minimalista
  synopsis?: string;
  chapters: Chapter[];
  currentChapterId?: string | null;
  updatedAt: number;
  createdAt: number;
}

export interface Folder {
  id: string;
  name: string;
  description?: string;
  color: string;
  createdAt: number;
  updatedAt: number;
}

export interface AppStateCache {
  currentScreen: 'home' | 'folder' | 'book' | 'editor';
  activeFolderId: string | null;
  activeBookId: string | null;
  activeChapterId: string | null;
  cursorPosition: { start: number; end: number };
  scrollOffset: number;
  lastVisitedTimestamp: number;
}

export interface WriterSettings {
  fontSize: number;
  lineHeightMultiplier: number;
  fontFamily: 'serif' | 'sans' | 'mono';
  dimLevel: 'ultra-dim' | 'soft-dim' | 'balanced-dim';
  autoSaveDelayMs: number;
  showWordCounter: boolean;
  typewriterMode: boolean;
}
