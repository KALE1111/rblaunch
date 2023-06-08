package net.runelite.client.plugins.coxhelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.NonNull;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

public class TableComponent implements LayoutableRenderableEntity {
   private static final TableElement EMPTY_ELEMENT = TableElement.builder().build();
   private final List<TableElement> columns = new ArrayList();
   private final List<TableRow> rows = new ArrayList();
   private final Rectangle bounds = new Rectangle();
   private TableAlignment defaultAlignment;
   private Color defaultColor;
   private Dimension gutter;
   private Point preferredLocation;
   private Dimension preferredSize;

   public TableComponent() {
      this.defaultAlignment = TableAlignment.LEFT;
      this.defaultColor = Color.WHITE;
      this.gutter = new Dimension(3, 0);
      this.preferredLocation = new Point();
      this.preferredSize = new Dimension(129, 0);
   }

   public Dimension render(Graphics2D graphics) {
      FontMetrics metrics = graphics.getFontMetrics();
      TableRow colRow = TableRow.builder().elements(this.columns).build();
      int[] columnWidths = this.getColumnWidths(metrics, colRow);
      graphics.translate(this.preferredLocation.x, this.preferredLocation.y);
      int height = this.displayRow(graphics, colRow, 0, columnWidths, metrics);

      TableRow row;
      for(Iterator var6 = this.rows.iterator(); var6.hasNext(); height = this.displayRow(graphics, row, height, columnWidths, metrics)) {
         row = (TableRow)var6.next();
      }

      graphics.translate(-this.preferredLocation.x, -this.preferredLocation.y);
      Dimension dimension = new Dimension(this.preferredSize.width, height);
      this.bounds.setLocation(this.preferredLocation);
      this.bounds.setSize(dimension);
      return dimension;
   }

   private int displayRow(Graphics2D graphics, TableRow row, int height, int[] columnWidths, FontMetrics metrics) {
      int x = 0;
      int startingRowHeight = height;
      List<TableElement> elements = row.getElements();

      for(int i = 0; i < elements.size(); ++i) {
         int y = startingRowHeight;
         TableElement cell = (TableElement)elements.get(i);
         String content = cell.getContent();
         if (content != null) {
            String[] lines = lineBreakText(content, columnWidths[i], metrics);
            TableAlignment alignment = this.getCellAlignment(row, i);
            Color color = this.getCellColor(row, i);
            String[] var16 = lines;
            int var17 = lines.length;

            for(int var18 = 0; var18 < var17; ++var18) {
               String line = var16[var18];
               int alignmentOffset = getAlignedPosition(line, alignment, columnWidths[i], metrics);
               TextComponent leftLineComponent = new TextComponent();
               y += metrics.getHeight();
               leftLineComponent.setPosition(new Point(x + alignmentOffset, y));
               leftLineComponent.setText(line);
               leftLineComponent.setColor(color);
               leftLineComponent.render(graphics);
            }

            height = Math.max(height, y);
            x += columnWidths[i] + this.gutter.width;
         }
      }

      return height + this.gutter.height;
   }

   private int[] getColumnWidths(FontMetrics metrics, TableRow columnRow) {
      int numCols = this.columns.size();

      TableRow r;
      for(Iterator var4 = this.rows.iterator(); var4.hasNext(); numCols = Math.max(r.getElements().size(), numCols)) {
         r = (TableRow)var4.next();
      }

      int[] maxtextw = new int[numCols];
      int[] maxwordw = new int[numCols];
      boolean[] flex = new boolean[numCols];
      boolean[] wrap = new boolean[numCols];
      int[] finalcolw = new int[numCols];
      List<TableRow> rows = new ArrayList(this.rows);
      rows.add(columnRow);
      Iterator var10 = rows.iterator();

      int col;
      while(var10.hasNext()) {
         r = (TableRow) var10.next();
         List<TableElement> elements = r.getElements();

         for(col = 0; col < elements.size(); ++col) {
            TableElement ele = (TableElement)elements.get(col);
            String cell = ele.getContent();
            if (cell != null) {
               col = getTextWidth(metrics, cell);
               maxtextw[col] = Math.max(maxtextw[col], col);
               String[] var17 = cell.split(" ");
               int var18 = var17.length;

               for(int var19 = 0; var19 < var18; ++var19) {
                  String word = var17[var19];
                  maxwordw[col] = Math.max(maxwordw[col], getTextWidth(metrics, word));
               }

               if (maxtextw[col] == col) {
                  wrap[col] = cell.contains(" ");
               }
            }
         }
      }

      int left = this.preferredSize.width - (numCols - 1) * this.gutter.width;
      double avg = (double)(left / numCols);
      col = 0;

      int tot;
      for(tot = 0; tot < numCols; ++tot) {
         double maxNonFlexLimit = 1.5D * avg;
         flex[tot] = (double)maxtextw[tot] > maxNonFlexLimit;
         if (flex[tot]) {
            ++col;
         } else {
            finalcolw[tot] = maxtextw[tot];
            left -= finalcolw[tot];
         }
      }

      if ((double)left < (double)col * avg) {
         for(tot = 0; tot < numCols; ++tot) {
            if (!flex[tot] && wrap[tot]) {
               left += finalcolw[tot];
               finalcolw[tot] = 0;
               flex[tot] = true;
               ++col;
            }
         }
      }

      tot = 0;

      int extraPerCol;
      for(extraPerCol = 0; extraPerCol < numCols; ++extraPerCol) {
         if (flex[extraPerCol]) {
            maxtextw[extraPerCol] = Math.min(maxtextw[extraPerCol], this.preferredSize.width);
            tot += maxtextw[extraPerCol];
         }
      }

      for(extraPerCol = 0; extraPerCol < numCols; ++extraPerCol) {
         if (flex[extraPerCol]) {
            finalcolw[extraPerCol] = left * maxtextw[extraPerCol] / tot;
            finalcolw[extraPerCol] = Math.max(finalcolw[extraPerCol], maxwordw[extraPerCol]);
            left -= finalcolw[extraPerCol];
         }
      }

      extraPerCol = left / numCols;

      for(col = 0; col < numCols; ++col) {
         finalcolw[col] += extraPerCol;
         left -= extraPerCol;
      }

      finalcolw[finalcolw.length - 1] += left;
      return finalcolw;
   }

   private static int getTextWidth(FontMetrics metrics, String cell) {
      return metrics.stringWidth(Text.removeTags(cell));
   }

   private static String[] lineBreakText(String text, int maxWidth, FontMetrics metrics) {
      String[] words = text.split(" ");
      if (words.length == 0) {
         return new String[0];
      } else {
         StringBuilder wrapped = new StringBuilder(words[0]);
         int spaceLeft = maxWidth - getTextWidth(metrics, wrapped.toString());

         for(int i = 1; i < words.length; ++i) {
            String word = words[i];
            int wordLen = getTextWidth(metrics, word);
            int spaceWidth = metrics.stringWidth(" ");
            if (wordLen + spaceWidth > spaceLeft) {
               wrapped.append("\n").append(word);
               spaceLeft = maxWidth - wordLen;
            } else {
               wrapped.append(" ").append(word);
               spaceLeft -= spaceWidth + wordLen;
            }
         }

         return wrapped.toString().split("\n");
      }
   }

   public boolean isEmpty() {
      return this.columns.size() == 0 || this.rows.size() == 0;
   }

   private void ensureColumnSize(int size) {
      while(size > this.columns.size()) {
         this.columns.add(TableElement.builder().build());
      }

   }

   private static int getAlignedPosition(String str, TableAlignment alignment, int columnWidth, FontMetrics metrics) {
      int stringWidth = getTextWidth(metrics, str);
      int offset = 0;
      switch(alignment) {
      case LEFT:
      default:
         break;
      case CENTER:
         offset = columnWidth / 2 - stringWidth / 2;
         break;
      case RIGHT:
         offset = columnWidth - stringWidth;
      }

      return offset;
   }

   private Color getCellColor(TableRow row, int colIndex) {
      List<TableElement> rowElements = row.getElements();
      TableElement cell = colIndex < rowElements.size() ? (TableElement)rowElements.get(colIndex) : EMPTY_ELEMENT;
      TableElement column = colIndex < this.columns.size() ? (TableElement)this.columns.get(colIndex) : EMPTY_ELEMENT;
      return (Color)firstNonNull(cell.getColor(), row.getRowColor(), column.getColor(), this.defaultColor);
   }

   private void setColumnAlignment(int col, TableAlignment alignment) {
      assert this.columns.size() > col;

      ((TableElement)this.columns.get(col)).setAlignment(alignment);
   }

   public void setColumnAlignments(@Nonnull TableAlignment... alignments) {
      this.ensureColumnSize(alignments.length);

      for(int i = 0; i < alignments.length; ++i) {
         this.setColumnAlignment(i, alignments[i]);
      }

   }

   private TableAlignment getCellAlignment(TableRow row, int colIndex) {
      List<TableElement> rowElements = row.getElements();
      TableElement cell = colIndex < rowElements.size() ? (TableElement)rowElements.get(colIndex) : EMPTY_ELEMENT;
      TableElement column = colIndex < this.columns.size() ? (TableElement)this.columns.get(colIndex) : EMPTY_ELEMENT;
      return (TableAlignment)firstNonNull(cell.getAlignment(), row.getRowAlignment(), column.getAlignment(), this.defaultAlignment);
   }

   @SafeVarargs
   private static <T> T firstNonNull(@Nullable T... elements) {
      if (elements != null && elements.length != 0) {
         int i = 0;

         Object cur;
         for(cur = elements[0]; cur == null && i < elements.length; ++i) {
            cur = elements[i];
         }

         return (T) cur;
      } else {
         return null;
      }
   }

   public void addRow(@Nonnull String... cells) {
      List<TableElement> elements = new ArrayList();
      String[] var3 = cells;
      int var4 = cells.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String cell = var3[var5];
         elements.add(TableElement.builder().content(cell).build());
      }

      TableRow row = TableRow.builder().build();
      row.setElements(elements);
      this.rows.add(row);
   }

   private void addRows(@Nonnull String[]... rows) {
      String[][] var2 = rows;
      int var3 = rows.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String[] row = var2[var4];
         this.addRow(row);
      }

   }

   public void addRows(@NonNull TableRow... rows) {
      if (rows == null) {
         throw new NullPointerException("rows is marked non-null but is null");
      } else {
         this.rows.addAll(Arrays.asList(rows));
      }
   }

   public void setRows(@Nonnull String[]... elements) {
      this.rows.clear();
      this.addRows(elements);
   }

   public void setRows(@Nonnull TableRow... elements) {
      this.rows.clear();
      this.rows.addAll(Arrays.asList(elements));
   }

   private void addColumn(@Nonnull String col) {
      this.columns.add(TableElement.builder().content(col).build());
   }

   public void addColumns(@NonNull TableElement... columns) {
      if (columns == null) {
         throw new NullPointerException("columns is marked non-null but is null");
      } else {
         this.columns.addAll(Arrays.asList(columns));
      }
   }

   public void setColumns(@Nonnull TableElement... elements) {
      this.columns.clear();
      this.columns.addAll(Arrays.asList(elements));
   }

   public void setColumns(@Nonnull String... columns) {
      this.columns.clear();
      String[] var2 = columns;
      int var3 = columns.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String col = var2[var4];
         this.addColumn(col);
      }

   }

   public void setDefaultAlignment(TableAlignment defaultAlignment) {
      this.defaultAlignment = defaultAlignment;
   }

   public void setDefaultColor(Color defaultColor) {
      this.defaultColor = defaultColor;
   }

   public void setGutter(Dimension gutter) {
      this.gutter = gutter;
   }

   public void setPreferredLocation(Point preferredLocation) {
      this.preferredLocation = preferredLocation;
   }

   public void setPreferredSize(Dimension preferredSize) {
      this.preferredSize = preferredSize;
   }

   public List<TableElement> getColumns() {
      return this.columns;
   }

   public List<TableRow> getRows() {
      return this.rows;
   }

   public Rectangle getBounds() {
      return this.bounds;
   }
}
