import React, { useState, useEffect, useCallback } from 'react';
import {
  StyleSheet,
  View,
  ActivityIndicator,
  StatusBar,
} from 'react-native';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import { KeyboardProvider } from 'react-native-keyboard-controller';
import { HomeScreen } from './src/screens/HomeScreen';
import { BookDetailScreen } from './src/screens/BookDetailScreen';
import { ChapterEditorScreen } from './src/screens/ChapterEditorScreen';
import { DimColors } from './src/theme/colors';
import {
  Folder,
  Book,
  Chapter,
  WriterSettings,
  AppStateCache,
} from './src/types';
import {
  StorageManager,
  defaultWriterSettings,
} from './src/storage/storage';

export default function App() {
  const [loading, setLoading] = useState(true);

  // Estados principais
  const [folders, setFolders] = useState<Folder[]>([]);
  const [books, setBooks] = useState<Book[]>([]);
  const [settings, setSettings] = useState<WriterSettings>(defaultWriterSettings);

  // Roteamento de Estado (Cache contínuo)
  const [currentScreen, setCurrentScreen] = useState<'home' | 'book_detail' | 'editor'>('home');
  const [activeBookId, setActiveBookId] = useState<string | null>(null);
  const [activeChapterId, setActiveChapterId] = useState<string | null>(null);

  // Inicialização e Recuperação do Cache de Estado (Abrir exatamente onde parou)
  useEffect(() => {
    async function initializeApp() {
      try {
        const [savedFolders, savedBooks, savedSettings, stateCache] = await Promise.all([
          StorageManager.getFolders(),
          StorageManager.getBooks(),
          StorageManager.getSettings(),
          StorageManager.getStateCache(),
        ]);

        setFolders(savedFolders);
        setBooks(savedBooks);
        setSettings(savedSettings);

        // Restaurar onde parou se houver cache
        if (stateCache) {
          if (stateCache.lastScreen === 'editor' && stateCache.activeBookId && stateCache.activeChapterId) {
            const bookExists = savedBooks.find((b) => b.id === stateCache.activeBookId);
            const chapterExists = bookExists?.chapters.find((c) => c.id === stateCache.activeChapterId);
            if (bookExists && chapterExists) {
              setActiveBookId(stateCache.activeBookId);
              setActiveChapterId(stateCache.activeChapterId);
              setCurrentScreen('editor');
            }
          } else if (stateCache.lastScreen === 'book_detail' && stateCache.activeBookId) {
            const bookExists = savedBooks.find((b) => b.id === stateCache.activeBookId);
            if (bookExists) {
              setActiveBookId(stateCache.activeBookId);
              setCurrentScreen('book_detail');
            }
          }
        }
      } catch (err) {
        console.error('Erro ao inicializar:', err);
      } finally {
        setLoading(false);
      }
    }

    initializeApp();
  }, []);

  // Salvar estado sempre que a navegação mudar
  const persistNavigationState = useCallback(
    (screen: 'home' | 'book_detail' | 'editor', bId: string | null, cId: string | null) => {
      setCurrentScreen(screen);
      setActiveBookId(bId);
      setActiveChapterId(cId);

      const cache: AppStateCache = {
        lastScreen: screen,
        activeBookId: bId || undefined,
        activeChapterId: cId || undefined,
        lastOpenedTimestamp: Date.now(),
      };
      saveStateCache(cache);
    },
    []
  );

  // Ações de Pastas
  const handleCreateFolder = async (name: string) => {
    const newFolder: Folder = {
      id: `folder_${Date.now()}`,
      name,
      createdAt: Date.now(),
    };
    const updated = [newFolder, ...folders];
    setFolders(updated);
    await saveFolders(updated);
  };

  // Ações de Livros
  const handleCreateBook = async (title: string, folderId?: string, synopsis?: string) => {
    const defaultPalette = ['#1e2029', '#242029', '#1d2623', '#27241e', '#23252b'];
    const randomColor = defaultPalette[Math.floor(Math.random() * defaultPalette.length)];

    const firstChapter: Chapter = {
      id: `chap_${Date.now()}`,
      bookId: `book_${Date.now()}`,
      title: 'Capítulo 1',
      content: '',
      order: 1,
      wordCount: 0,
      updatedAt: Date.now(),
    };

    const newBook: Book = {
      id: firstChapter.bookId,
      title,
      synopsis,
      folderId,
      coverColor: randomColor,
      chapters: [firstChapter],
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };

    const updated = [newBook, ...books];
    setBooks(updated);
    await saveBooks(updated);

    // Navegar diretamente para o novo livro
    persistNavigationState('book_detail', newBook.id, null);
  };

  const handleOpenBook = (bookId: string) => {
    persistNavigationState('book_detail', bookId, null);
  };

  // Ações de Capítulos
  const handleCreateChapter = async (title: string) => {
    if (!activeBookId) return;

    const bookIndex = books.findIndex((b) => b.id === activeBookId);
    if (bookIndex === -1) return;

    const targetBook = books[bookIndex];
    const newChapter: Chapter = {
      id: `chap_${Date.now()}`,
      bookId: targetBook.id,
      title,
      content: '',
      order: targetBook.chapters.length + 1,
      wordCount: 0,
      updatedAt: Date.now(),
    };

    const updatedBook: Book = {
      ...targetBook,
      chapters: [...targetBook.chapters, newChapter],
      updatedAt: Date.now(),
    };

    const updatedBooks = [...books];
    updatedBooks[bookIndex] = updatedBook;

    setBooks(updatedBooks);
    await saveBooks(updatedBooks);

    // Abrir o capítulo recém-criado no editor
    persistNavigationState('editor', targetBook.id, newChapter.id);
  };

  const handleOpenChapter = (chapterId: string) => {
    if (!activeBookId) return;
    persistNavigationState('editor', activeBookId, chapterId);
  };

  // Salvar conteúdo do capítulo (Chamado continuamente via debounced auto-save)
  const handleSaveChapterContent = async (chapterId: string, content: string, wordCount: number) => {
    if (!activeBookId) return;

    setBooks((prevBooks) => {
      const bIndex = prevBooks.findIndex((b) => b.id === activeBookId);
      if (bIndex === -1) return prevBooks;

      const currentBook = prevBooks[bIndex];
      const cIndex = currentBook.chapters.findIndex((c) => c.id === chapterId);
      if (cIndex === -1) return prevBooks;

      const updatedChapter = {
        ...currentBook.chapters[cIndex],
        content,
        wordCount,
        updatedAt: Date.now(),
      };

      const updatedChapters = [...currentBook.chapters];
      updatedChapters[cIndex] = updatedChapter;

      const updatedBook = {
        ...currentBook,
        chapters: updatedChapters,
        updatedAt: Date.now(),
      };

      const newBooksList = [...prevBooks];
      newBooksList[bIndex] = updatedBook;

      // Persistir em disco de forma assíncrona
      saveBooks(newBooksList);
      return newBooksList;
    });
  };

  const handleUpdateSettings = async (newSettings: WriterSettings) => {
    setSettings(newSettings);
    await saveSettings(newSettings);
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="small" color={DimColors.textMuted} />
      </View>
    );
  }

  const activeBook = books.find((b) => b.id === activeBookId);
  const activeChapter = activeBook?.chapters.find((c) => c.id === activeChapterId);

  return (
    <SafeAreaProvider>
      <KeyboardProvider>
        <SafeAreaView style={styles.safeArea}>
          <StatusBar barStyle="light-content" backgroundColor={DimColors.bgCanvas} />

          {currentScreen === 'home' && (
            <HomeScreen
              folders={folders}
              books={books}
              onOpenBook={handleOpenBook}
              onCreateFolder={handleCreateFolder}
              onCreateBook={handleCreateBook}
            />
          )}

          {currentScreen === 'book_detail' && activeBook && (
            <BookDetailScreen
              book={activeBook}
              onBack={() => persistNavigationState('home', null, null)}
              onOpenChapter={handleOpenChapter}
              onCreateChapter={handleCreateChapter}
            />
          )}

          {currentScreen === 'editor' && activeBook && activeChapter && (
            <ChapterEditorScreen
              book={activeBook}
              chapter={activeChapter}
              initialSettings={settings}
              onBack={() => persistNavigationState('book_detail', activeBook.id, null)}
              onSaveContent={handleSaveChapterContent}
              onUpdateSettings={handleUpdateSettings}
            />
          )}
        </SafeAreaView>
      </KeyboardProvider>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: DimColors.bgCanvas,
  },
  loadingContainer: {
    flex: 1,
    backgroundColor: DimColors.bgCanvas,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
