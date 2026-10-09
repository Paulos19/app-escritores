import React, { useState, useRef, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Platform,
  NativeSyntheticEvent,
  TextInputSelectionChangeEventData,
} from 'react-native';
import { KeyboardAvoidingView, KeyboardStickyView } from 'react-native-keyboard-controller';
import { ArrowLeft, Save, Eye, EyeOff } from 'lucide-react-native';
import { DimColors, DimTypography } from '../theme/colors';
import { WriterToolbar } from '../components/WriterToolbar';
import { Book, Chapter, WriterSettings } from '../types';

interface ChapterEditorScreenProps {
  book: Book;
  chapter: Chapter;
  initialSettings: WriterSettings;
  onBack: () => void;
  onSaveContent: (chapterId: string, content: string, wordCount: number) => void;
  onUpdateSettings: (settings: WriterSettings) => void;
}

export const ChapterEditorScreen: React.FC<ChapterEditorScreenProps> = ({
  book,
  chapter,
  initialSettings,
  onBack,
  onSaveContent,
  onUpdateSettings,
}) => {
  const [content, setContent] = useState(chapter.content || '');
  const [settings, setSettings] = useState<WriterSettings>(initialSettings);
  const [selection, setSelection] = useState({ start: 0, end: 0 });
  const [isZenMode, setZenMode] = useState(false);

  const textInputRef = useRef<TextInput>(null);
  const autoSaveTimerRef = useRef<NodeJS.Timeout | null>(null);

  // Cálculo de palavras
  const calculateWordCount = (text: string) => {
    const trimmed = text.trim();
    if (!trimmed) return 0;
    return trimmed.split(/\s+/).length;
  };

  const wordCount = calculateWordCount(content);

  // Debounced auto-save local contínuo
  useEffect(() => {
    if (autoSaveTimerRef.current) {
      clearTimeout(autoSaveTimerRef.current);
    }
    autoSaveTimerRef.current = setTimeout(() => {
      onSaveContent(chapter.id, content, calculateWordCount(content));
    }, 800);

    return () => {
      if (autoSaveTimerRef.current) clearTimeout(autoSaveTimerRef.current);
    };
  }, [content]);

  // Manipulação de inserções a partir da Toolbar
  const handleInsertText = (insertedText: string) => {
    const { start, end } = selection;
    const newContent =
      content.substring(0, start) + insertedText + content.substring(end);
    setContent(newContent);
    const newCursor = start + insertedText.length;
    setSelection({ start: newCursor, end: newCursor });
  };

  const handleWrapSelection = (prefix: string, suffix: string) => {
    const { start, end } = selection;
    const selectedText = content.substring(start, end);
    const wrapped = `${prefix}${selectedText || 'texto'}${suffix}`;
    const newContent =
      content.substring(0, start) + wrapped + content.substring(end);
    setContent(newContent);
    const newCursor = start + wrapped.length;
    setSelection({ start: newCursor, end: newCursor });
  };

  const handleFormatHeading = (prefix: string) => {
    const { start } = selection;
    // Encontrar o início da linha atual
    const lastNewline = content.lastIndexOf('\n', start - 1);
    const lineStart = lastNewline === -1 ? 0 : lastNewline + 1;
    const newContent =
      content.substring(0, lineStart) +
      prefix +
      ' ' +
      content.substring(lineStart);
    setContent(newContent);
  };

  const handleUpdateSettings = (newSettings: WriterSettings) => {
    setSettings(newSettings);
    onUpdateSettings(newSettings);
  };

  const handleSelectionChange = (
    e: NativeSyntheticEvent<TextInputSelectionChangeEventData>
  ) => {
    setSelection(e.nativeEvent.selection);
  };

  // Font family dinâmica
  const getFontFamily = () => {
    switch (settings.fontFamily) {
      case 'serif':
        return Platform.OS === 'ios' ? 'Georgia' : 'serif';
      case 'mono':
        return Platform.OS === 'ios' ? 'Courier' : 'monospace';
      default:
        return Platform.OS === 'ios' ? 'System' : 'sans-serif';
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      style={styles.container}
    >
      {/* Header superior discreto */}
      {!isZenMode && (
        <View style={styles.topHeader}>
          <TouchableOpacity
            style={styles.backButton}
            onPress={onBack}
            activeOpacity={0.7}
          >
            <ArrowLeft size={18} color={DimColors.textMuted} />
            <Text style={styles.backButtonText} numberOfLines={1}>
              {book.title}
            </Text>
          </TouchableOpacity>

          <View style={styles.headerRight}>
            <TouchableOpacity
              style={styles.zenToggleBtn}
              onPress={() => setZenMode(true)}
              activeOpacity={0.7}
            >
              <EyeOff size={16} color={DimColors.textMuted} />
              <Text style={styles.zenToggleText}>Zen</Text>
            </TouchableOpacity>

            <View style={styles.savedStatusBadge}>
              <View style={styles.savedDot} />
              <Text style={styles.savedText}>Salvo</Text>
            </View>
          </View>
        </View>
      )}

      {/* Título do Capítulo (fixo ou oculto no modo Zen) */}
      {!isZenMode && (
        <View style={styles.chapterHeader}>
          <Text style={styles.chapterTitle}>{chapter.title}</Text>
          <Text style={styles.chapterMeta}>
            {wordCount} palavras • ~{Math.ceil(wordCount / 200)} min de leitura
          </Text>
        </View>
      )}

      {/* Botão flutuante para sair do modo Zen se ativado */}
      {isZenMode && (
        <TouchableOpacity
          style={styles.exitZenBtn}
          onPress={() => setZenMode(false)}
          activeOpacity={0.7}
        >
          <Eye size={16} color={DimColors.textMuted} />
          <Text style={styles.exitZenText}>Sair do Modo Zen</Text>
        </TouchableOpacity>
      )}

      {/* Área Central de Escrita com Proteção Visual */}
      <ScrollView
        style={styles.editorScroll}
        contentContainerStyle={[
          styles.editorScrollContent,
          {
            paddingHorizontal: settings.editorWidth === 'narrow' ? 36 : 20,
          },
        ]}
        keyboardDismissMode="none"
        showsVerticalScrollIndicator={false}
      >
        <TextInput
          ref={textInputRef}
          style={[
            styles.textInput,
            {
              fontSize: settings.fontSize,
              lineHeight: Math.round(settings.fontSize * settings.lineSpacing),
              fontFamily: getFontFamily(),
            },
          ]}
          multiline
          scrollEnabled={false}
          value={content}
          onChangeText={setContent}
          onSelectionChange={handleSelectionChange}
          placeholder="Comece sua história aqui... Sem pressa, sem ruído, apenas palavras."
          placeholderTextColor={DimColors.textGhost}
          textAlignVertical="top"
          autoCorrect={true}
          autoCapitalize="sentences"
        />
        <View style={{ height: 180 }} />
      </ScrollView>

      {/* Toolbar Flutuante que acompanha o teclado no Android/iOS */}
      <KeyboardStickyView offset={{ closed: 0, opened: 0 }}>
        <WriterToolbar
          wordCount={wordCount}
          settings={settings}
          onInsertText={handleInsertText}
          onWrapSelection={handleWrapSelection}
          onFormatHeading={handleFormatHeading}
          onUpdateSettings={handleUpdateSettings}
        />
      </KeyboardStickyView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: DimColors.bgCanvas,
  },
  topHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingTop: 14,
    paddingBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: DimColors.borderSubtle,
    backgroundColor: DimColors.bgCanvas,
  },
  backButton: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    flex: 1,
    paddingRight: 10,
  },
  backButtonText: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.sm,
    fontWeight: '500',
  },
  headerRight: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  zenToggleBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    backgroundColor: DimColors.bgSurface,
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: DimColors.borderSubtle,
  },
  zenToggleText: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
  },
  savedStatusBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
  },
  savedDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: '#3d4b3d',
  },
  savedText: {
    color: DimColors.textGhost,
    fontSize: DimTypography.sizes.xs,
  },
  chapterHeader: {
    paddingHorizontal: 20,
    paddingTop: 14,
    paddingBottom: 6,
  },
  chapterTitle: {
    color: DimColors.textQuiet,
    fontSize: DimTypography.sizes.lg,
    fontWeight: '600',
    letterSpacing: -0.2,
  },
  chapterMeta: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
    marginTop: 3,
  },
  exitZenBtn: {
    position: 'absolute',
    top: 14,
    right: 16,
    zIndex: 10,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    backgroundColor: DimColors.bgElevated,
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: DimColors.borderFocus,
  },
  exitZenText: {
    color: DimColors.textMuted,
    fontSize: DimTypography.sizes.xs,
  },
  editorScroll: {
    flex: 1,
  },
  editorScrollContent: {
    paddingTop: 16,
    minHeight: '100%',
  },
  textInput: {
    color: DimColors.textDimmed,
    letterSpacing: 0.2,
    textAlignVertical: 'top',
    padding: 0,
    margin: 0,
  },
});
