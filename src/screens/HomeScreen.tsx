import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Modal,
  TextInput,
  Alert,
} from 'react-native';
import {
  Folder as FolderIcon,
  Book as BookIcon,
  Plus,
  ChevronRight,
  FolderPlus,
  BookOpen,
  Layers,
  Sparkles,
} from 'lucide-react-native';
import { DimColors, DimTypography } from '../theme/colors';
import { Book, Folder } from '../types';

interface HomeScreenProps {
  folders: Folder[];
  books: Book[];
  onOpenBook: (bookId: string) => void;
  onCreateFolder: (name: string) => void;
  onCreateBook: (title: string, folderId?: string, synopsis?: string) => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  folders,
  books,
  onOpenBook,
  onCreateFolder,
  onCreateBook,
}) => {
  const [selectedFolderId, setSelectedFolderId] = useState<string | null>(null);

  // Modais de Criação
  const [isFolderModalVisible, setFolderModalVisible] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');

  const [isBookModalVisible, setBookModalVisible] = useState(false);
  const [newBookTitle, setNewBookTitle] = useState('');
  const [newBookSynopsis, setNewBookSynopsis] = useState('');

  const activeFolder = folders.find((f) => f.id === selectedFolderId);

  // Livros a exibir (filtrados por pasta ou todos da raiz se nenhuma pasta selecionada)
  const displayedBooks = selectedFolderId
    ? books.filter((b) => b.folderId === selectedFolderId)
    : books;

  const handleCreateFolder = () => {
    if (!newFolderName.trim()) return;
    onCreateFolder(newFolderName.trim());
    setNewFolderName('');
    setFolderModalVisible(false);
  };

  const handleCreateBook = () => {
    if (!newBookTitle.trim()) return;
    onCreateBook(
      newBookTitle.trim(),
      selectedFolderId || undefined,
      newBookSynopsis.trim() || undefined
    );
    setNewBookTitle('');
    setNewBookSynopsis('');
    setBookModalVisible(false);
  };

  return (
    <View style={styles.container}>
      {/* Header minimalista com tom ultra baixo */}
      <View style={styles.header}>
        <View>
          <Text style={styles.headerTitle}>Ateliê do Escritor</Text>
          <Text style={styles.headerSubtitle}>
            Modo baixa luminância ocular • Offline
          </Text>
        </View>

        <View style={styles.headerActions}>
          <TouchableOpacity
            style={styles.actionIconBtn}
            onPress={() => setFolderModalVisible(true)}
            activeOpacity={0.7}
          >
            <FolderPlus size={18} color={DimColors.textMuted} />
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.actionIconBtn, styles.actionPrimaryBtn]}
            onPress={() => setBookModalVisible(true)}
            activeOpacity={0.7}
          >
            <Plus size={18} color={DimColors.textQuiet} />
          </TouchableOpacity>
        </View>
      </View>

      <ScrollView
        style={styles.scrollArea}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        {/* Seção 1: Filtro de Pastas */}
        <View style={styles.sectionHeader}>
          <View style={styles.sectionTitleRow}>
            <FolderIcon size={14} color={DimColors.textMuted} />
            <Text style={styles.sectionTitle}>PASTAS DE UNIVERSOS</Text>
          </View>
          {selectedFolderId && (
            <TouchableOpacity onPress={() => setSelectedFolderId(null)}>
              <Text style={styles.clearFilterText}>Ver todos</Text>
            </TouchableOpacity>
          )}
        </View>

        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          style={styles.folderRow}
          contentContainerStyle={styles.folderRowContent}
        >
          <TouchableOpacity
            style={[
              styles.folderChip,
              selectedFolderId === null && styles.folderChipActive,
            ]}
            onPress={() => setSelectedFolderId(null)}
          >
            <Text
              style={[
                styles.folderChipText,
                selectedFolderId === null && styles.folderChipTextActive,
              ]}
            >
              Todos ({books.length})
            </Text>
          </TouchableOpacity>

          {folders.map((folder) => {
            const count = books.filter((b) => b.folderId === folder.id).length;
            const isSelected = selectedFolderId === folder.id;
            return (
              <TouchableOpacity
                key={folder.id}
                style={[styles.folderChip, isSelected && styles.folderChipActive]}
                onPress={() => setSelectedFolderId(folder.id)}
              >
                <FolderIcon
                  size={12}
                  color={isSelected ? DimColors.textQuiet : DimColors.textMuted}
                  style={{ marginRight: 6 }}
                />
                <Text
                  style={[
                    styles.folderChipText,
                    isSelected && styles.folderChipTextActive,
                  ]}
                  numberOfLines={1}
                >
                  {folder.name} ({count})
                </Text>
              </TouchableOpacity>
            );
          })}
        </ScrollView>

        {/* Seção 2: Estante de Livros com Capa na Interface Principal */}
        <View style={[styles.sectionHeader, { marginTop: 20 }]}>
          <View style={styles.sectionTitleRow}>
            <BookOpen size={14} color={DimColors.textMuted} />
            <Text style={styles.sectionTitle}>
              {activeFolder ? `LIVROS EM "${activeFolder.name.toUpperCase()}"` : 'TODOS OS LIVROS'}
            </Text>
          </View>
          <Text style={styles.bookCounter}>
            {displayedBooks.length} manuscrito(s)
          </Text>
        </View>

        {displayedBooks.length === 0 ? (
          <View style={styles.emptyContainer}>
            <BookIcon size={32} color={DimColors.borderFocus} />
            <Text style={styles.emptyTitle}>Nenhum livro aqui ainda</Text>
            <Text style={styles.emptySubtitle}>
              Toque no botão + acima para iniciar um novo manuscrito
            </Text>
          </View>
        ) : (
          <View style={styles.booksGrid}>
            {displayedBooks.map((book) => {
              const totalWords = book.chapters.reduce(
                (acc, c) => acc + (c.wordCount || 0),
                0
              );
              return (
                <TouchableOpacity
                  key={book.id}
                  style={styles.bookCard}
                  activeOpacity={0.75}
                  onPress={() => onOpenBook(book.id)}
                >
                  {/* Capa do Livro (Dimmed Stylized Cover) */}
                  <View
                    style={[
                      styles.bookCover,
                      {
                        backgroundColor: book.coverColor || DimColors.bgElevated,
                      },
                    ]}
                  >
                    <View style={styles.bookSpineEffect} />
                    <View style={styles.bookCoverContent}>
                      <Text style={styles.bookCoverTitle} numberOfLines={3}>
                        {book.title}
                      </Text>
                      <View style={styles.coverBadge}>
                        <Text style={styles.coverBadgeText}>
                          {book.chapters.length} cap.
                        </Text>
                      </View>
                    </View>
                  </View>

                  {/* Informações Abaixo da Capa */}
                  <View style={styles.bookMeta}>
                    <Text style={styles.bookTitle} numberOfLines={1}>
                      {book.title}
                    </Text>
                    {book.synopsis ? (
                      <Text style={styles.bookSynopsis} numberOfLines={2}>
                        {book.synopsis}
                      </Text>
                    ) : null}
                    <View style={styles.bookStatsRow}>
                      <Text style={styles.bookStatsText}>
                        {totalWords.toLocaleString()} palavras
                      </Text>
                      <ChevronRight size={13} color={DimColors.textMuted} />
                    </View>
                  </View>
                </TouchableOpacity>
              );
            })}
          </View>
        )}
      </ScrollView>

      {/* Modal: Nova Pasta */}
      <Modal
        visible={isFolderModalVisible}
        transparent
        animationType="fade"
        onRequestClose={() => setFolderModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>Criar Nova Pasta</Text>
            <Text style={styles.modalDescription}>
              Agrupe livros por sagas, antologias ou universos.
            </Text>
            <TextInput
              style={styles.modalInput}
              placeholder="Ex: Trilogia das Sombras, Crônicas 2025"
              placeholderTextColor={DimColors.textGhost}
              value={newFolderName}
              onChangeText={setNewFolderName}
              autoFocus
            />
            <View style={styles.modalButtonsRow}>
              <TouchableOpacity
                style={styles.modalCancelBtn}
                onPress={() => setFolderModalVisible(false)}
              >
                <Text style={styles.modalCancelText}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.modalConfirmBtn}
                onPress={handleCreateFolder}
              >
                <Text style={styles.modalConfirmText}>Criar Pasta</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* Modal: Novo Livro */}
      <Modal
        visible={isBookModalVisible}
        transparent
        animationType="fade"
        onRequestClose={() => setBookModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>Novo Livro</Text>
            <Text style={styles.modalDescription}>
              Inicie um novo manuscrito. Capítulos e capas podem ser refinados a qualquer momento.
            </Text>

            <TextInput
              style={styles.modalInput}
              placeholder="Título do Livro..."
              placeholderTextColor={DimColors.textGhost}
              value={newBookTitle}
              onChangeText={setNewBookTitle}
              autoFocus
            />

            <TextInput
              style={[styles.modalInput, styles.modalTextArea]}
              placeholder="Sinopse breve ou premissa (opcional)..."
              placeholderTextColor={DimColors.textGhost}
              value={newBookSynopsis}
              onChangeText={setNewBookSynopsis}
              multiline
              numberOfLines={3}
            />

            <View style={styles.modalButtonsRow}>
              <TouchableOpacity
                style={styles.modalCancelBtn}
                onPress={() => setBookModalVisible(false)}
              >
                <Text style={styles.modalCancelText}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.modalConfirmBtn}
                onPress={handleCreateBook}
              >
                <Text style={styles.modalConfirmText}>Começar Manuscrito</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: DimColors.bgCanvas,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingTop: 16,
    paddingBottom: 14,
    borderBottomWidth: 1,
    borderBottomColor: DimColors.borderSubtle,
    backgroundColor: DimColors.bgCanvas,
  },
  headerTitle: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.lg,
    fontWeight: '600',
    letterSpacing: -0.3,
  },
  headerSubtitle: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    marginTop: 2,
  },
  headerActions: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  actionIconBtn: {
    width: 38,
    height: 38,
    borderRadius: 8,
    backgroundColor: DimColors.bgSurface,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
    alignItems: 'center',
    justifyContent: 'center',
  },
  actionPrimaryBtn: {
    backgroundColor: DimColors.bgElevated,
    borderColor: DimColors.borderFocus,
  },
  scrollArea: {
    flex: 1,
  },
  scrollContent: {
    paddingHorizontal: 20,
    paddingTop: 18,
    paddingBottom: 40,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  sectionTitleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  sectionTitle: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    fontWeight: '700',
    letterSpacing: 0.8,
  },
  clearFilterText: {
    color: DimColors.textDimmed,
    fontSize: DimTypography.sizes.xs,
  },
  bookCounter: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
  },
  folderRow: {
    flexGrow: 0,
  },
  folderRowContent: {
    paddingVertical: 4,
    gap: 8,
  },
  folderChip: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 12,
    paddingVertical: 7,
    borderRadius: 6,
    backgroundColor: DimColors.bgSurface,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
  },
  folderChipActive: {
    backgroundColor: DimColors.bgElevated,
    borderColor: DimColors.borderFocus,
  },
  folderChipText: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.sm,
  },
  folderChipTextActive: {
    color: DimColors.textQuiet,
    fontWeight: '500',
  },
  booksGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    marginTop: 8,
  },
  bookCard: {
    width: '48%',
    marginBottom: 18,
    backgroundColor: DimColors.bgSurface,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
    overflow: 'hidden',
  },
  bookCover: {
    height: 140,
    width: '100%',
    position: 'relative',
    justifyContent: 'flex-end',
    padding: 12,
  },
  bookSpineEffect: {
    position: 'absolute',
    left: 0,
    top: 0,
    bottom: 0,
    width: 6,
    backgroundColor: 'rgba(0,0,0,0.3)',
  },
  bookCoverContent: {
    zIndex: 2,
  },
  bookCoverTitle: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.base,
    fontWeight: '700',
    lineHeight: 19,
    letterSpacing: -0.2,
  },
  coverBadge: {
    alignSelf: 'flex-start',
    backgroundColor: 'rgba(0,0,0,0.45)',
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
    marginTop: 6,
  },
  coverBadgeText: {
    color: DimColors.textMuted,
    fontSize: 10,
    fontWeight: '500',
  },
  bookMeta: {
    padding: 10,
  },
  bookTitle: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.sm,
    fontWeight: '600',
    marginBottom: 4,
  },
  bookSynopsis: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    lineHeight: 15,
    marginBottom: 6,
  },
  bookStatsRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 4,
  },
  bookStatsText: {
    color: DimColors.textMuted,
    fontSize: 11,
  },
  emptyContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 50,
    backgroundColor: DimColors.bgSurface,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
    borderStyle: 'dashed',
    marginTop: 10,
  },
  emptyTitle: {
    color: DimColors.textDimmed,
    fontSize: DimTypography.sizes.base,
    marginTop: 10,
    fontWeight: '500',
  },
  emptySubtitle: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    marginTop: 4,
    textAlign: 'center',
    paddingHorizontal: 20,
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.75)',
    justifyContent: 'center',
    paddingHorizontal: 20,
  },
  modalBox: {
    backgroundColor: DimColors.bgSurface,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: DimColors.borderFocus,
    padding: 20,
  },
  modalTitle: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.lg,
    fontWeight: '600',
    marginBottom: 4,
  },
  modalDescription: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    lineHeight: 17,
    marginBottom: 16,
  },
  modalInput: {
    backgroundColor: DimColors.bgCanvas,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    color: DimColors.textDimmed,
    fontSize: DimTypography.sizes.base,
    marginBottom: 12,
  },
  modalTextArea: {
    height: 70,
    textAlignVertical: 'top',
  },
  modalButtonsRow: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: 10,
    marginTop: 6,
  },
  modalCancelBtn: {
    paddingVertical: 8,
    paddingHorizontal: 14,
    borderRadius: 6,
  },
  modalCancelText: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.sm,
  },
  modalConfirmBtn: {
    backgroundColor: DimColors.bgElevated,
    borderWidth: 1,
    borderColor: DimColors.borderFocus,
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 6,
  },
  modalConfirmText: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.sm,
    fontWeight: '600',
  },
});
