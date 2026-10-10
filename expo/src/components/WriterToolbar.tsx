import React, { useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  Platform,
} from 'react-native';
import {
  Bold,
  Italic,
  Quote,
  Minus,
  Heading1,
  Heading2,
  List,
  Eye,
  Type,
  AlignLeft,
  ChevronRight,
  Maximize2,
  FileText,
  Bookmark,
  CheckCircle2,
} from 'lucide-react-native';
import { DimColors } from '../theme/colors';

export type ToolbarCategory = 'format' | 'dialogue' | 'structure' | 'focus' | 'meta';

interface WriterToolbarProps {
  onInsertText: (prefix: string, suffix?: string) => void;
  onReplaceLine: (transformer: (line: string) => string) => void;
  wordCount: number;
  charCount: number;
  readingTimeMinutes: number;
  fontSize: number;
  onIncreaseFontSize: () => void;
  onDecreaseFontSize: () => void;
  focusMode: boolean;
  onToggleFocusMode: () => void;
  typewriterMode: boolean;
  onToggleTypewriterMode: () => void;
}

export const WriterToolbar: React.FC<WriterToolbarProps> = ({
  onInsertText,
  onReplaceLine,
  wordCount,
  charCount,
  readingTimeMinutes,
  fontSize,
  onIncreaseFontSize,
  onDecreaseFontSize,
  focusMode,
  onToggleFocusMode,
  typewriterMode,
  onToggleTypewriterMode,
}) => {
  const [activeCategory, setActiveCategory] = useState<ToolbarCategory>('dialogue');

  const categories: { id: ToolbarCategory; label: string }[] = [
    { id: 'dialogue', label: 'Diálogo & Ponto' },
    { id: 'format', label: 'Estilo' },
    { id: 'structure', label: 'Estrutura' },
    { id: 'focus', label: 'Modo Foco' },
    { id: 'meta', label: 'Estatísticas' },
  ];

  return (
    <View style={styles.container}>
      {/* Barra de Categorias */}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.categoryScroll}
        style={styles.categoryBar}
      >
        {categories.map((cat) => {
          const isActive = activeCategory === cat.id;
          return (
            <TouchableOpacity
              key={cat.id}
              onPress={() => setActiveCategory(cat.id)}
              style={[styles.categoryTab, isActive && styles.categoryTabActive]}
              activeOpacity={0.7}
            >
              <Text
                style={[
                  styles.categoryLabel,
                  isActive && styles.categoryLabelActive,
                ]}
              >
                {cat.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      {/* Ações Específicas da Categoria Selecionada */}
      <View style={styles.actionsBar}>
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.actionItemsScroll}
        >
          {activeCategory === 'dialogue' && (
            <>
              {/* Diálogos com travessão em-dash sem brilho */}
              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('— ')}
                activeOpacity={0.6}
              >
                <Text style={styles.symbolText}>—</Text>
                <Text style={styles.btnSubtext}>Travessão</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('«', '»')}
                activeOpacity={0.6}
              >
                <Text style={styles.symbolText}>« »</Text>
                <Text style={styles.btnSubtext}>Angulares</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('“', '”')}
                activeOpacity={0.6}
              >
                <Quote size={15} color={DimColors.textMuted} />
                <Text style={styles.btnSubtext}>Aspas</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('… ')}
                activeOpacity={0.6}
              >
                <Text style={styles.symbolText}>…</Text>
                <Text style={styles.btnSubtext}>Reticências</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('\n* * *\n')}
                activeOpacity={0.6}
              >
                <Text style={styles.symbolText}>* * *</Text>
                <Text style={styles.btnSubtext}>Quebra Cena</Text>
              </TouchableOpacity>
            </>
          )}

          {activeCategory === 'format' && (
            <>
              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('*', '*')}
                activeOpacity={0.6}
              >
                <Italic size={16} color={DimColors.textMuted} />
                <Text style={styles.btnSubtext}>Itálico</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('**', '**')}
                activeOpacity={0.6}
              >
                <Bold size={16} color={DimColors.textMuted} />
                <Text style={styles.btnSubtext}>Negrito</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('~', '~')}
                activeOpacity={0.6}
              >
                <Text style={styles.symbolText}>~s~</Text>
                <Text style={styles.btnSubtext}>Tachado</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={onDecreaseFontSize}
                activeOpacity={0.6}
              >
                <Text style={styles.symbolText}>A-</Text>
                <Text style={styles.btnSubtext}>{fontSize}px</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={onIncreaseFontSize}
                activeOpacity={0.6}
              >
                <Text style={styles.symbolText}>A+</Text>
                <Text style={styles.btnSubtext}>Aumentar</Text>
              </TouchableOpacity>
            </>
          )}

          {activeCategory === 'structure' && (
            <>
              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('# ')}
                activeOpacity={0.6}
              >
                <Heading1 size={16} color={DimColors.textMuted} />
                <Text style={styles.btnSubtext}>Título</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('## ')}
                activeOpacity={0.6}
              >
                <Heading2 size={16} color={DimColors.textMuted} />
                <Text style={styles.btnSubtext}>Subtítulo</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('> ')}
                activeOpacity={0.6}
              >
                <Quote size={15} color={DimColors.textMuted} />
                <Text style={styles.btnSubtext}>Citação</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => onInsertText('• ')}
                activeOpacity={0.6}
              >
                <List size={16} color={DimColors.textMuted} />
                <Text style={styles.btnSubtext}>Lista</Text>
              </TouchableOpacity>
            </>
          )}

          {activeCategory === 'focus' && (
            <>
              <TouchableOpacity
                style={[styles.actionBtn, typewriterMode && styles.actionBtnActive]}
                onPress={onToggleTypewriterMode}
                activeOpacity={0.6}
              >
                <AlignLeft size={16} color={typewriterMode ? DimColors.textSecondary : DimColors.textFaint} />
                <Text style={[styles.btnSubtext, typewriterMode && styles.btnSubtextActive]}>
                  {typewriterMode ? 'Máq. Ativa' : 'Máq. Escrever'}
                </Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[styles.actionBtn, focusMode && styles.actionBtnActive]}
                onPress={onToggleFocusMode}
                activeOpacity={0.6}
              >
                <Eye size={16} color={focusMode ? DimColors.textSecondary : DimColors.textFaint} />
                <Text style={[styles.btnSubtext, focusMode && styles.btnSubtextActive]}>
                  {focusMode ? 'Foco Ligado' : 'Foco Zen'}
                </Text>
              </TouchableOpacity>
            </>
          )}

          {activeCategory === 'meta' && (
            <View style={styles.statsContainer}>
              <View style={styles.statPill}>
                <Text style={styles.statValue}>{wordCount}</Text>
                <Text style={styles.statLabel}>palavras</Text>
              </View>
              <View style={styles.statDivider} />
              <View style={styles.statPill}>
                <Text style={styles.statValue}>{charCount}</Text>
                <Text style={styles.statLabel}>caracteres</Text>
              </View>
              <View style={styles.statDivider} />
              <View style={styles.statPill}>
                <Text style={styles.statValue}>~{readingTimeMinutes} min</Text>
                <Text style={styles.statLabel}>leitura</Text>
              </View>
            </View>
          )}
        </ScrollView>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: DimColors.surfaceDim,
    borderTopWidth: 1,
    borderTopColor: DimColors.border,
    paddingVertical: 4,
  },
  categoryBar: {
    maxHeight: 34,
    borderBottomWidth: 1,
    borderBottomColor: DimColors.borderFaint,
  },
  categoryScroll: {
    paddingHorizontal: 10,
    alignItems: 'center',
    gap: 8,
  },
  categoryTab: {
    paddingVertical: 5,
    paddingHorizontal: 10,
    borderRadius: 6,
    backgroundColor: 'transparent',
  },
  categoryTabActive: {
    backgroundColor: DimColors.surfaceElevated,
    borderBottomWidth: 1.5,
    borderBottomColor: DimColors.textMuted,
  },
  categoryLabel: {
    fontSize: 11,
    color: DimColors.textFaint,
    fontWeight: '500',
    letterSpacing: 0.2,
  },
  categoryLabelActive: {
    color: DimColors.textSecondary,
    fontWeight: '600',
  },
  actionsBar: {
    paddingVertical: 4,
    paddingHorizontal: 8,
    minHeight: 46,
    justifyContent: 'center',
  },
  actionItemsScroll: {
    alignItems: 'center',
    gap: 6,
    paddingRight: 16,
  },
  actionBtn: {
    minWidth: 50,
    height: 42,
    backgroundColor: DimColors.surfaceDark,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: DimColors.borderFaint,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 8,
  },
  actionBtnActive: {
    backgroundColor: DimColors.surfaceElevated,
    borderColor: DimColors.border,
  },
  symbolText: {
    fontSize: 15,
    color: DimColors.textSecondary,
    fontWeight: '600',
  },
  btnSubtext: {
    fontSize: 9,
    color: DimColors.textFaint,
    marginTop: 2,
  },
  btnSubtextActive: {
    color: DimColors.textSecondary,
  },
  statsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 10,
    gap: 12,
  },
  statPill: {
    alignItems: 'center',
  },
  statValue: {
    fontSize: 13,
    fontWeight: '600',
    color: DimColors.textSecondary,
  },
  statLabel: {
    fontSize: 9,
    color: DimColors.textFaint,
  },
  statDivider: {
    width: 1,
    height: 20,
    backgroundColor: DimColors.borderFaint,
  },
});
