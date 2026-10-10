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
import { Book, Folder, Chapter, WriterSettings, AppStateCache } from './src/types';
import { StorageManager, defaultSettings } from './src/storage/storage';

export default function App() {
  const [loading, setLoading] = useState<boolean>(true);
  const [folders, setFolders] = useState<Folder[]>([]);
  const [books, setBooks] = useState<Book[]>([]);
  const [currentFolderId, setCurrentFolderId] = useState<string | null>(null);
  const [settings, setSettings] = useState<WriterSettings>(defaultSettings);

  // Navegação
  const [activeScreen, setActiveScreen] = useState<'home' | 'book-detail' | 'chapter-editor'>('home');
  const [selectedBookId, setSelectedBookId] = useState<string | null>(null);
  const [selectedChapterId, setSelectedChapterId] = useState<string | null>(null);

  // Inicialização e recuperação de sessão
  useEffect(() => {
    async function initializeApp() {
      try {
        const [savedFolders, savedBooks, savedSettings, stateCache] = await Promise.all([
          StorageManager.getFolders(),
          StorageManager.getBooks(),
          StorageManager.getSettings(),
          StorageManager.getCachedState(),
        ]);

        setFolders(savedFolders);
        setBooks(savedBooks);
        if (savedSettings) {
          setSettings(savedSettings);
        }

        // Recupera sessão anterior se existir
        if (stateCache) {
          if (stateCache.activeFolderId) {
            setCurrentFolderId(stateCache.activeFolderId);
          }

          if (stateCache.activeBookId) {
            const bookExists = savedBooks.find((b) => b.id === stateCache.activeBookId);
            if (bookExists) {
              setSelectedBookId(stateCache.activeBookId);

              if (stateCache.activeChapterId) {
                const chapterExists = bookExists.chapters.find((c) => c.id === stateCache.activeChapterId);
                if (chapterExists) {
                  setSelectedChapterId(stateCache.activeChapterId);
                  setActiveScreen('chapter-editor');
                  return;
                }
              }
              setActiveScreen('book-detail');
              return;
            }
          }
        }
      } catch (error) {
        console.error('Erro ao inicializar aplicação:', error);
      } finally {
        setLoading(false);
      }
    }

    initializeApp();
  }, []);

  // Persiste navegação
  const persistNavigationState = useCallback(async (cache: Partial<AppStateCache>) => {
    try {
      await StorageManager.saveCachedState(cache);
    } catch (err) {
      console.error('Erro ao persistir estado de navegação:', err);
    }
  }, []);

  // Handlers de Pastas
  const handleSelectFolder = async (folderId: string | null) => {
    setCurrentFolderId(folderId);
    await persistNavigationState({ activeFolderId: folderId });
  };

  const handleCreateFolder = async (folderData: { name: string; color: string }) => {
    const newFolder: Folder = {
      id: Date.now().toString(),
      name: folderData.name,
      color: folderData.color || '#6366F1',
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };
    const updated = [...folders, newFolder];
    setFolders(updated);
    await StorageManager.saveFolders(updated);
  };

  const handleUpdateFolder = async (folderId: string, folderData: { name: string; color: string }) => {
    const updated = folders.map((f) =>
      f.id === folderId
        ? { ...f, name: folderData.name, color: folderData.color, updatedAt: Date.now() }
        : f
    );
    setFolders(updated);
    await StorageManager.saveFolders(updated);
  };

  const handleDeleteFolder = async (folderId: string) => {
    const updatedFolders = folders.filter((f) => f.id !== folderId);
    setFolders(updatedFolders);
    await StorageManager.saveFolders(updatedFolders);

    // Desvincula livros da pasta removida
    const updatedBooks = books.map((b) =>
      b.folderId === folderId ? { ...b, folderId: undefined, updatedAt: Date.now() } : b
    );
    setBooks(updatedBooks);
    await StorageManager.saveBooks(updatedBooks);

    if (currentFolderId === folderId) {
      setCurrentFolderId(null);
      await persistNavigationState({ activeFolderId: null });
    }
  };

  // Handlers de Livros
  const handleCreateBook = async (bookData: {
    title: string;
    synopsis?: string;
    genre?: string;
    folderId?: string;
    coverColor?: string;
  }) => {
    const newBook: Book = {
      id: Date.now().toString(),
      title: bookData.title,
      synopsis: bookData.synopsis,
      genre: bookData.genre,
      folderId: bookData.folderId || currentFolderId || undefined,
      coverColor: bookData.coverColor || '#3B82F6',
      chapters: [],
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };

    const updated = [newBook, ...books];
    setBooks(updated);
    await StorageManager.saveBooks(updated);

    // Abre direto o livro criado
    setSelectedBookId(newBook.id);
    setSelectedChapterId(null);
    setActiveScreen('book-detail');
    await persistNavigationState({
      currentScreen: 'book-detail',
      activeBookId: newBook.id,
      activeChapterId: null,
    });
  };

  const handleOpenBook = async (bookId: string) => {
    setSelectedBookId(bookId);
    setSelectedChapterId(null);
    setActiveScreen('book-detail');
    await persistNavigationState({
      currentScreen: 'book-detail',
      activeBookId: bookId,
      activeChapterId: null,
    });
  };

  const handleUpdateBook = async (
    bookId: string,
    bookData: {
      title: string;
      synopsis?: string;
      genre?: string;
      folderId?: string;
      coverColor?: string;
    }
  ) => {
    const updated = books.map((b) =>
      b.id === bookId
        ? {
            ...b,
            title: bookData.title,
            synopsis: bookData.synopsis,
            genre: bookData.genre,
            folderId: bookData.folderId,
            coverColor: bookData.coverColor,
            updatedAt: Date.now(),
          }
        : b
    );
    setBooks(updated);
    await StorageManager.saveBooks(updated);
  };

  const handleDeleteBook = async (bookId: string) => {
    const updated = books.filter((b) => b.id !== bookId);
    setBooks(updated);
    await StorageManager.saveBooks(updated);

    if (selectedBookId === bookId) {
      setSelectedBookId(null);
      setSelectedChapterId(null);
      setActiveScreen('home');
      await persistNavigationState({
        currentScreen: 'home',
        activeBookId: null,
        activeChapterId: null,
      });
    }
  };

  // Handlers de Capítulos
  const handleOpenChapter = async (chapterId: string) => {
    setSelectedChapterId(chapterId);
    setActiveScreen('chapter-editor');
    await persistNavigationState({
      currentScreen: 'chapter-editor',
      activeBookId: selectedBookId,
      activeChapterId: chapterId,
    });
  };

  const handleCreateChapter = async (title: string, synopsis?: string) => {
    if (!selectedBookId) return;

    const book = books.find((b) => b.id === selectedBookId);
    if (!book) return;

    const newChapter: Chapter = {
      id: Date.now().toString(),
      bookId: selectedBookId,
      title,
      synopsis,
      content: '',
      order: book.chapters.length + 1,
      wordCount: 0,
      updatedAt: Date.now(),
    };

    const updatedChapters = [...book.chapters, newChapter];
    const updatedBooks = books.map((b) =>
      b.id === selectedBookId
        ? { ...b, chapters: updatedChapters, updatedAt: Date.now() }
        : b
    );

    setBooks(updatedBooks);
    await StorageManager.saveBooks(updatedBooks);

    // Abre direto o capítulo recém-criado
    setSelectedChapterId(newChapter.id);
    setActiveScreen('chapter-editor');
    await persistNavigationState({
      currentScreen: 'chapter-editor',
      activeBookId: selectedBookId,
      activeChapterId: newChapter.id,
    });
  };

  const handleUpdateChapter = async (chapterId: string, data: { title: string; synopsis?: string }) => {
    if (!selectedBookId) return;

    const book = books.find((b) => b.id === selectedBookId);
    if (!book) return;

    const updatedChapters = book.chapters.map((c) =>
      c.id === chapterId
        ? { ...c, title: data.title, synopsis: data.synopsis, updatedAt: Date.now() }
        : c
    );

    const updatedBooks = books.map((b) =>
      b.id === selectedBookId
        ? { ...b, chapters: updatedChapters, updatedAt: Date.now() }
        : b
    );

    setBooks(updatedBooks);
    await StorageManager.saveBooks(updatedBooks);
  };

  const handleDeleteChapter = async (chapterId: string) => {
    if (!selectedBookId) return;

    const book = books.find((b) => b.id === selectedBookId);
    if (!book) return;

    const updatedChapters = book.chapters.filter((c) => c.id !== chapterId);
    const updatedBooks = books.map((b) =>
      b.id === selectedBookId
        ? { ...b, chapters: updatedChapters, updatedAt: Date.now() }
        : b
    );

    setBooks(updatedBooks);
    await StorageManager.saveBooks(updatedBooks);

    if (selectedChapterId === chapterId) {
      setSelectedChapterId(null);
      setActiveScreen('book-detail');
      await persistNavigationState({
        currentScreen: 'book-detail',
        activeBookId: selectedBookId,
        activeChapterId: null,
      });
    }
  };

  const handleReorderChapters = async (reorderedChapters: Chapter[]) => {
    if (!selectedBookId) return;

    const updatedBooks = books.map((b) =>
      b.id === selectedBookId
        ? { ...b, chapters: reorderedChapters, updatedAt: Date.now() }
        : b
    );

    setBooks(updatedBooks);
    await StorageManager.saveBooks(updatedBooks);
  };

  const handleSaveChapterContent = async (
    chapterId: string,
    content: string,
    wordCount: number,
    cursorPos?: { start: number; end: number },
    scrollOffset?: number
  ) => {
    if (!selectedBookId) return;

    setBooks((prevBooks) =>
      prevBooks.map((b) => {
        if (b.id !== selectedBookId) return b;
        return {
          ...b,
          updatedAt: Date.now(),
          chapters: b.chapters.map((c) =>
            c.id === chapterId
              ? {
                  ...c,
                  content,
                  wordCount,
                  lastSelectionStart: cursorPos?.start,
                  lastSelectionEnd: cursorPos?.end,
                  lastScrollPosition: scrollOffset,
                  updatedAt: Date.now(),
                }
              : c
          ),
        };
      })
    );

    await StorageManager.saveChapterContent(
      selectedBookId,
      chapterId,
      content,
      wordCount,
      cursorPos,
      scrollOffset
    );
  };

  // Configurações
  const handleUpdateSettings = async (newSettings: WriterSettings) => {
    setSettings(newSettings);
    await StorageManager.saveSettings(newSettings);
  };

  // Navegação reversa
  const handleBackToHome = async () => {
    setActiveScreen('home');
    setSelectedChapterId(null);
    setSelectedBookId(null);
    await persistNavigationState({
      currentScreen: 'home',
      activeBookId: null,
      activeChapterId: null,
    });
  };

  const handleBackToBookDetail = async () => {
    setActiveScreen('book-detail');
    setSelectedChapterId(null);
    await persistNavigationState({
      currentScreen: 'book-detail',
      activeBookId: selectedBookId,
      activeChapterId: null,
    });
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#4B5563" />
      </View>
    );
  }

  const currentBook = books.find((b) => b.id === selectedBookId);
  const currentChapter = currentBook?.chapters.find((c) => c.id === selectedChapterId);

  return (
    <SafeAreaProvider>
      <KeyboardProvider>
        <SafeAreaView style={styles.container} edges={['top', 'left', 'right']}>
          <StatusBar barStyle="light-content" backgroundColor="#0D0F12" />

          {activeScreen === 'home' && (
            <HomeScreen
              folders={folders}
              books={books}
              currentFolderId={currentFolderId}
              onSelectFolder={handleSelectFolder}
              onSelectBook={handleOpenBook}
              onCreateBook={handleCreateBook}
              onUpdateBook={handleUpdateBook}
              onDeleteBook={handleDeleteBook}
              onCreateFolder={handleCreateFolder}
              onUpdateFolder={handleUpdateFolder}
              onDeleteFolder={handleDeleteFolder}
            />
          )}

          {activeScreen === 'book-detail' && currentBook && (
            <BookDetailScreen
              book={currentBook}
              folders={folders}
              onBack={handleBackToHome}
              onSelectChapter={handleOpenChapter}
              onCreateChapter={handleCreateChapter}
              onUpdateChapter={handleUpdateChapter}
              onDeleteChapter={handleDeleteChapter}
              onReorderChapters={handleReorderChapters}
              onUpdateBook={handleUpdateBook}
              onDeleteBook={handleDeleteBook}
            />
          )}

          {activeScreen === 'chapter-editor' && currentBook && currentChapter && (
            <ChapterEditorScreen
              book={currentBook}
              chapter={currentChapter}
              initialSettings={settings}
              onBack={handleBackToBookDetail}
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
  container: {
    flex: 1,
    backgroundColor: '#0D0F12',
  },
  loadingContainer: {
    flex: 1,
    backgroundColor: '#0D0F12',
    justifyContent: 'center',
    alignItems: 'center',
  },
});
