import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Modal,
  TextInput,
} from 'react-native';
import {
  ArrowLeft,
  Plus,
  BookOpen,
  FileText,
  ChevronRight,
  Clock,
  Trash2,
} from 'lucide-react-native';
import { DimColors, DimTypography } from '../theme/colors';
import { Book, Chapter } from '../types';

interface BookDetailScreenProps {
  book: Book;
  onBack: () => void;
  onOpenChapter: (chapterId: string) => void;
  onCreateChapter: (title: string) => void;
  onDeleteChapter?: (chapterId: string) => void;
}

export const BookDetailScreen: React.FC<BookDetailScreenProps> = ({
  book,
  onBack,
  onOpenChapter,
  onCreateChapter,
  onDeleteChapter,
}) => {
  const [isChapterModalVisible, setChapterModalVisible] = useState(false);
  const [newChapterTitle, setNewChapterTitle] = useState('');

  const totalWords = book.chapters.reduce(
    (acc, chap) => acc + (chap.wordCount || 0),
    0
  );
  const estimatedReadingMinutes = Math.ceil(totalWords / 200);

  const handleCreateChapter = () => {
    if (!newChapterTitle.trim()) return;
    onCreateChapter(newChapterTitle.trim());
    setNewChapterTitle('');
    setChapterModalVisible(false);
  };

  return (
    <View style={styles.container}>
      {/* Top Header */}
      <View style={styles.header}>
        <TouchableOpacity
          style={styles.backButton}
          onPress={onBack}
          activeOpacity={0.7}
        >
          <ArrowLeft size={18} color={DimColors.textMuted} />
          <Text style={styles.backButtonText}>Estante</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.newChapterBtn}
          onPress={() => setChapterModalVisible(true)}
          activeOpacity={0.7}
        >
          <Plus size={16} color={DimColors.textQuiet} />
          <Text style={styles.newChapterBtnText}>Novo Capítulo</Text>
        </TouchableOpacity>
      </View>

      <ScrollView
        style={styles.scrollArea}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        {/* Banner de Apresentação da Capa */}
        <View
          style={[
            styles.heroCoverCard,
            { backgroundColor: book.coverColor || DimColors.bgElevated },
          ]}
        >
          <View style={styles.heroSpine} />
          <View style={styles.heroCoverDetails}>
            <Text style={styles.heroBookTitle}>{book.title}</Text>
            {book.synopsis ? (
              <Text style={styles.heroBookSynopsis}>{book.synopsis}</Text>
            ) : null}

            <View style={styles.heroStatsRow}>
              <View style={styles.statChip}>
                <BookOpen size={12} color={DimColors.textMuted} />
                <Text style={styles.statChipText}>
                  {book.chapters.length} capítulos
                </Text>
              </View>
              <View style={styles.statChip}>
                <FileText size={12} color={DimColors.textMuted} />
                <Text style={styles.statChipText}>
                  {totalWords.toLocaleString()} palavras
                </Text>
              </View>
              <View style={styles.statChip}>
                <Clock size={12} color={DimColors.textMuted} />
                <Text style={styles.statChipText}>
                  ~{estimatedReadingMinutes} min
                </Text>
              </View>
            </View>
          </View>
        </View>

        {/* Lista de Capítulos */}
        <View style={styles.chaptersHeader}>
          <Text style={styles.chaptersTitle}>ÍNDICE DO MANUSCRITO</Text>
          <Text style={styles.chaptersSubtitle}>
            Toque em um capítulo para abrir a mesa de escrita
          </Text>
        </View>

        {book.chapters.length === 0 ? (
          <View style={styles.emptyChapters}>
            <FileText size={28} color={DimColors.borderFocus} />
            <Text style={styles.emptyChaptersTitle}>Sem capítulos criados</Text>
            <Text style={styles.emptyChaptersSubtitle}>
              Comece pelo Prólogo ou Capítulo 1 tocando no botão acima
            </Text>
          </View>
        ) : (
          <View style={styles.chaptersList}>
            {book.chapters
              .sort((a, b) => a.order - b.order)
              .map((chap, index) => {
                return (
                  <TouchableOpacity
                    key={chap.id}
                    style={styles.chapterItem}
                    activeOpacity={0.7}
                    onPress={() => onOpenChapter(chap.id)}
                  >
                    <View style={styles.chapterOrderBox}>
                      <Text style={styles.chapterOrderText}>{index + 1}</Text>
                    </View>

                    <View style={styles.chapterInfo}>
                      <Text style={styles.chapterTitle} numberOfLines={1}>
                        {chap.title}
                      </Text>
                      <Text style={styles.chapterMeta}>
                        {chap.wordCount.toLocaleString()} palavras •{' '}
                        {chap.content ? 'Em andamento' : 'Em branco'}
                      </Text>
                    </View>

                    <ChevronRight size={16} color={DimColors.textMuted} />
                  </TouchableOpacity>
                );
              })}
          </View>
        )}
      </ScrollView>

      {/* Modal: Novo Capítulo */}
      <Modal
        visible={isChapterModalVisible}
        transparent
        animationType="fade"
        onRequestClose={() => setChapterModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>Novo Capítulo</Text>
            <Text style={styles.modalDescription}>
              Defina o título ou número da nova cena/capítulo.
            </Text>
            <TextInput
              style={styles.modalInput}
              placeholder="Ex: Capítulo 1 - O Despertar, Prólogo"
              placeholderTextColor={DimColors.textGhost}
              value={newChapterTitle}
              onChangeText={setNewChapterTitle}
              autoFocus
            />
            <View style={styles.modalButtonsRow}>
              <TouchableOpacity
                style={styles.modalCancelBtn}
                onPress={() => setChapterModalVisible(false)}
              >
                <Text style={styles.modalCancelText}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.modalConfirmBtn}
                onPress={handleCreateChapter}
              >
                <Text style={styles.modalConfirmText}>Criar e Escrever</Text>
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
  backButton: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    paddingVertical: 6,
    paddingRight: 12,
  },
  backButtonText: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.sm,
    fontWeight: '500',
  },
  newChapterBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    backgroundColor: DimColors.bgElevated,
    borderWidth: 1,
    borderColor: DimColors.borderFocus,
    paddingHorizontal: 12,
    paddingVertical: 7,
    borderRadius: 6,
  },
  newChapterBtnText: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.xs,
    fontWeight: '600',
  },
  scrollArea: {
    flex: 1,
  },
  scrollContent: {
    paddingHorizontal: 20,
    paddingTop: 18,
    paddingBottom: 40,
  },
  heroCoverCard: {
    borderRadius: 12,
    padding: 18,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
    position: 'relative',
    overflow: 'hidden',
    marginBottom: 24,
  },
  heroSpine: {
    position: 'absolute',
    left: 0,
    top: 0,
    bottom: 0,
    width: 6,
    backgroundColor: 'rgba(0,0,0,0.35)',
  },
  heroCoverDetails: {
    marginLeft: 6,
  },
  heroBookTitle: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.xl,
    fontWeight: '700',
    letterSpacing: -0.3,
    marginBottom: 8,
  },
  heroBookSynopsis: {
    color: DimColors.textDimmed,
    fontSize: DimTypography.sizes.sm,
    lineHeight: 20,
    marginBottom: 14,
  },
  heroStatsRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  statChip: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    backgroundColor: 'rgba(0,0,0,0.3)',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 5,
  },
  statChipText: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
  },
  chaptersHeader: {
    marginBottom: 12,
  },
  chaptersTitle: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    fontWeight: '700',
    letterSpacing: 0.8,
  },
  chaptersSubtitle: {
    color: DimColors.textGhost,
    fontSize: DimTypography.sizes.xs,
    marginTop: 2,
  },
  chaptersList: {
    gap: 8,
  },
  chapterItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: DimColors.bgSurface,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
    padding: 12,
  },
  chapterOrderBox: {
    width: 28,
    height: 28,
    borderRadius: 6,
    backgroundColor: DimColors.bgElevated,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 12,
  },
  chapterOrderText: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    fontWeight: '700',
  },
  chapterInfo: {
    flex: 1,
  },
  chapterTitle: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.sm,
    fontWeight: '500',
    marginBottom: 2,
  },
  chapterMeta: {
    color: DimColors.textMuted,
    fontSize: 11,
  },
  emptyChapters: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 40,
    backgroundColor: DimColors.bgSurface,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
    borderStyle: 'dashed',
  },
  emptyChaptersTitle: {
    color: DimColors.textDimmed,
    fontSize: DimTypography.sizes.sm,
    marginTop: 8,
  },
  emptyChaptersSubtitle: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    marginTop: 2,
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
    marginBottom: 14,
  },
  modalButtonsRow: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: 10,
  },
  modalCancelBtn: {
    paddingVertical: 8,
    paddingHorizontal: 14,
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
