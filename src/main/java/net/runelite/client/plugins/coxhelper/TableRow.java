package net.runelite.client.plugins.coxhelper;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class TableRow {
   Color rowColor;
   TableAlignment rowAlignment;
   List<TableElement> elements;

   private static List<TableElement> $default$elements() {
      return Collections.emptyList();
   }

   TableRow(Color rowColor, TableAlignment rowAlignment, List<TableElement> elements) {
      this.rowColor = rowColor;
      this.rowAlignment = rowAlignment;
      this.elements = elements;
   }

   public static TableRowBuilder builder() {
      return new TableRowBuilder();
   }

   public Color getRowColor() {
      return this.rowColor;
   }

   public TableAlignment getRowAlignment() {
      return this.rowAlignment;
   }

   public List<TableElement> getElements() {
      return this.elements;
   }

   public void setRowColor(Color rowColor) {
      this.rowColor = rowColor;
   }

   public void setRowAlignment(TableAlignment rowAlignment) {
      this.rowAlignment = rowAlignment;
   }

   public void setElements(List<TableElement> elements) {
      this.elements = elements;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TableRow)) {
         return false;
      } else {
         TableRow other = (TableRow)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            label47: {
               Object this$rowColor = this.getRowColor();
               Object other$rowColor = other.getRowColor();
               if (this$rowColor == null) {
                  if (other$rowColor == null) {
                     break label47;
                  }
               } else if (this$rowColor.equals(other$rowColor)) {
                  break label47;
               }

               return false;
            }

            Object this$rowAlignment = this.getRowAlignment();
            Object other$rowAlignment = other.getRowAlignment();
            if (this$rowAlignment == null) {
               if (other$rowAlignment != null) {
                  return false;
               }
            } else if (!this$rowAlignment.equals(other$rowAlignment)) {
               return false;
            }

            Object this$elements = this.getElements();
            Object other$elements = other.getElements();
            if (this$elements == null) {
               if (other$elements != null) {
                  return false;
               }
            } else if (!this$elements.equals(other$elements)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof TableRow;
   }

   public int hashCode() {
      boolean PRIME = true;
      int result = 1;
      Object $rowColor = this.getRowColor();
      result = result * 59 + ($rowColor == null ? 43 : $rowColor.hashCode());
      Object $rowAlignment = this.getRowAlignment();
      result = result * 59 + ($rowAlignment == null ? 43 : $rowAlignment.hashCode());
      Object $elements = this.getElements();
      result = result * 59 + ($elements == null ? 43 : $elements.hashCode());
      return result;
   }

   public String toString() {
      return "TableRow(rowColor=" + this.getRowColor() + ", rowAlignment=" + this.getRowAlignment() + ", elements=" + this.getElements() + ")";
   }

   public static class TableRowBuilder {
      private Color rowColor;
      private TableAlignment rowAlignment;
      private boolean elements$set;
      private List<TableElement> elements$value;

      TableRowBuilder() {
      }

      public TableRowBuilder rowColor(Color rowColor) {
         this.rowColor = rowColor;
         return this;
      }

      public TableRowBuilder rowAlignment(TableAlignment rowAlignment) {
         this.rowAlignment = rowAlignment;
         return this;
      }

      public TableRowBuilder elements(List<TableElement> elements) {
         this.elements$value = elements;
         this.elements$set = true;
         return this;
      }

      public TableRow build() {
         List<TableElement> elements$value = this.elements$value;
         if (!this.elements$set) {
            elements$value = TableRow.$default$elements();
         }

         return new TableRow(this.rowColor, this.rowAlignment, elements$value);
      }

      public String toString() {
         return "TableRow.TableRowBuilder(rowColor=" + this.rowColor + ", rowAlignment=" + this.rowAlignment + ", elements$value=" + this.elements$value + ")";
      }
   }
}
