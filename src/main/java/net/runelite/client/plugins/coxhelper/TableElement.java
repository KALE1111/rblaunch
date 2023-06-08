package net.runelite.client.plugins.coxhelper;

import java.awt.Color;

public class TableElement {
   TableAlignment alignment;
   Color color;
   String content;

   TableElement(TableAlignment alignment, Color color, String content) {
      this.alignment = alignment;
      this.color = color;
      this.content = content;
   }

   public static TableElementBuilder builder() {
      return new TableElementBuilder();
   }

   public TableAlignment getAlignment() {
      return this.alignment;
   }

   public Color getColor() {
      return this.color;
   }

   public String getContent() {
      return this.content;
   }

   public void setAlignment(TableAlignment alignment) {
      this.alignment = alignment;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TableElement)) {
         return false;
      } else {
         TableElement other = (TableElement)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            label47: {
               Object this$alignment = this.getAlignment();
               Object other$alignment = other.getAlignment();
               if (this$alignment == null) {
                  if (other$alignment == null) {
                     break label47;
                  }
               } else if (this$alignment.equals(other$alignment)) {
                  break label47;
               }

               return false;
            }

            Object this$color = this.getColor();
            Object other$color = other.getColor();
            if (this$color == null) {
               if (other$color != null) {
                  return false;
               }
            } else if (!this$color.equals(other$color)) {
               return false;
            }

            Object this$content = this.getContent();
            Object other$content = other.getContent();
            if (this$content == null) {
               if (other$content != null) {
                  return false;
               }
            } else if (!this$content.equals(other$content)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof TableElement;
   }

   public int hashCode() {
      boolean PRIME = true;
      int result = 1;
      Object $alignment = this.getAlignment();
      result = result * 59 + ($alignment == null ? 43 : $alignment.hashCode());
      Object $color = this.getColor();
      result = result * 59 + ($color == null ? 43 : $color.hashCode());
      Object $content = this.getContent();
      result = result * 59 + ($content == null ? 43 : $content.hashCode());
      return result;
   }

   public String toString() {
      return "TableElement(alignment=" + this.getAlignment() + ", color=" + this.getColor() + ", content=" + this.getContent() + ")";
   }

   public static class TableElementBuilder {
      private TableAlignment alignment;
      private Color color;
      private String content;

      TableElementBuilder() {
      }

      public TableElementBuilder alignment(TableAlignment alignment) {
         this.alignment = alignment;
         return this;
      }

      public TableElementBuilder color(Color color) {
         this.color = color;
         return this;
      }

      public TableElementBuilder content(String content) {
         this.content = content;
         return this;
      }

      public TableElement build() {
         return new TableElement(this.alignment, this.color, this.content);
      }

      public String toString() {
         return "TableElement.TableElementBuilder(alignment=" + this.alignment + ", color=" + this.color + ", content=" + this.content + ")";
      }
   }
}
