import AsyncStorage from '@react-native-async-storage/async-storage';
import { Book, Folder, AppStateCache, WriterSettings, Chapter } from '../types';

const STORAGE_KEYS = {
  FOLDERS: '@writer_folders_v1',
  BOOKS: '@writer_books_v1',
  CACHE_STATE: '@writer_state_cache_v1',
  SETTINGS: '@writer_settings_v1',
};

export const defaultSettings: WriterSettings = {
  fontSize: 17,
  lineHeightMultiplier: 1.6,
  fontFamily: 'serif',
  dimLevel: 'balanced-dim',
  autoSaveDelayMs: 600,
  showWordCounter: true,
  typewriterMode: false,
};

export const defaultCacheState: AppStateCache = {
  currentScreen: 'home',
  activeFolderId: null,
  activeBookId: null,
  activeChapterId: null,
  cursorPosition: { start: 0, end: 0 },
  scrollOffset: 0,
  lastVisitedTimestamp: Date.now(),
};

// Funções de Persistência Segura e Rápida
export const StorageManager = {
  // --- Pastas ---
  async getFolders(): Promise<Folder[]> {
    try {
      const data = await AsyncStorage.getItem(STORAGE_KEYS.FOLDERS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  async saveFolders(folders: Folder[]): Promise<void> {
    try {
      await AsyncStorage.setItem(STORAGE_KEYS.FOLDERS, JSON.stringify(folders));
    } catch (err) {
      console.warn('Erro ao salvar pastas:', err);
    }
  },

  // --- Livros ---
  async getBooks(): Promise<Book[]> {
    try {
      const data = await AsyncStorage.getItem(STORAGE_KEYS.BOOKS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  async saveBooks(books: Book[]): Promise<void> {
    try {
      await AsyncStorage.setItem(STORAGE_KEYS.BOOKS, JSON.stringify(books));
    } catch (err) {
      console.warn('Erro ao salvar livros:', err);
    }
  },

  // --- Cache de Sessão (Retoma exatamente onde parou) ---
  async getCachedState(): Promise<AppStateCache> {
    try {
      const data = await AsyncStorage.getItem(STORAGE_KEYS.CACHE_STATE);
      if (data) {
        return { ...defaultCacheState, ...JSON.parse(data) };
      }
      return defaultCacheState;
    } catch {
      return defaultCacheState;
    }
  },

  async saveCachedState(partialState: Partial<AppStateCache>): Promise<void> {
    try {
      const current = await this.getCachedState();
      const updated = {
        ...current,
        ...partialState,
        lastVisitedTimestamp: Date.now(),
      };
      await AsyncStorage.setItem(STORAGE_KEYS.CACHE_STATE, JSON.stringify(updated));
    } catch (err) {
      console.warn('Erro ao salvar cache de estado:', err);
    }
  },

  // --- Configurações de Escrita ---
  async getSettings(): Promise<WriterSettings> {
    try {
      const data = await AsyncStorage.getItem(STORAGE_KEYS.SETTINGS);
      return data ? { ...defaultSettings, ...JSON.parse(data) } : defaultSettings;
    } catch {
      return defaultSettings;
    }
  },

  async saveSettings(settings: WriterSettings): Promise<void> {
    try {
      await AsyncStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(settings));
    } catch (err) {
      console.warn('Erro ao salvar configurações:', err);
    }
  },

  // Método auxiliar para criar amostra inicial caso o app esteja completamente limpo
  async seedInitialSampleIfEmpty(): Promise<{ folders: Folder[]; books: Book[] }> {
    const folders = await this.getFolders();
    const books = await this.getBooks();

    if (folders.length === 0 && books.length === 0) {
      const initialFolder: Folder = {
        id: 'folder-sample-1',
        name: 'Trilogia das Sombras',
        description: 'Primeiro rascunho de ficção especulativa',
        color: '#2a2d34',
        createdAt: Date.now(),
        updatedAt: Date.now(),
      };

      const initialBook: Book = {
        id: 'book-sample-1',
        folderId: 'folder-sample-1',
        title: 'O Silêncio dos Vagalumes',
        subtitle: 'Livro I - O Chamado Noturno',
        author: 'Autor Anônimo',
        genre: 'Fantasia Sombria',
        coverColor: '#202227',
        coverIcon: 'feather',
        synopsis: 'Nas colinas cinzentas, onde a luz do sol nunca toca as raízes antigas...',
        createdAt: Date.now(),
        updatedAt: Date.now(),
        chapters: [
          {
            id: 'chap-sample-1',
            bookId: 'book-sample-1',
            title: 'Capítulo 1: A Névoa Espessa',
            content: 'A cidade dormia sob uma cortina de cinzas. Nenhuma lanterna brilhava nos postes de ferro retorcido.\n\nElias ajustou o casaco grosso e observou o relógio da torre. Os ponteiros pareciam hesitar a cada segundo, como se o próprio tempo temesse avançar naquela madrugada fria.\n\n— Você não deveria estar aqui — sussurrou a voz familiar atrás dele.',
            order: 1,
            wordCount: 52,
            updatedAt: Date.now(),
            createdAt: Date.now(),
          },
          {
            id: 'chap-sample-2',
            bookId: 'book-sample-1',
            title: 'Capítulo 2: Pegadas no Asfalto',
            content: 'Os rastros conduziam até a margem do canal esquecido. Pegadas irregulares marcavam o solo orvalhado.\n\nNenhum som vinha dos galpões abandonados, apenas o murmúrio incessante da água escura fluindo para o mar.',
            order: 2,
            wordCount: 29,
            updatedAt: Date.now(),
            createdAt: Date.now(),
          }
        ],
        currentChapterId: 'chap-sample-1',
      };

      const initialStandaloneBook: Book = {
        id: 'book-sample-2',
        folderId: null,
        title: 'Caderno de Crônicas',
        subtitle: 'Pensamentos soltos e aforismos',
        author: 'Notas Pessoais',
        genre: 'Ensaios',
        coverColor: '#1d2126',
        coverIcon: 'book-open',
        synopsis: 'Fragmentos de ideias coletados nas caminhadas noturnas.',
        createdAt: Date.now(),
        updatedAt: Date.now(),
        chapters: [
          {
            id: 'chap-sample-standalone-1',
            bookId: 'book-sample-2',
            title: 'Fragmento I: O Peso das Palavras',
            content: 'Escrever no escuro é como esculpir sombras. As palavras não precisam de brilho, apenas de precisão e quietude.',
            order: 1,
            wordCount: 19,
            updatedAt: Date.now(),
            createdAt: Date.now(),
          }
        ],
        currentChapterId: 'chap-sample-standalone-1',
      };

      const newFolders = [initialFolder];
      const newBooks = [initialBook, initialStandaloneBook];

      await this.saveFolders(newFolders);
      await this.saveBooks(newBooks);
      return { folders: newFolders, books: newBooks };
    }

    return { folders, books };
  }
};
