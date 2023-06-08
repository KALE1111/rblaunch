package net.runelite.client.plugins.coxhelper;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;

public class Text {
   private static final StringBuilder SB = new StringBuilder(64);
   private static final Pattern TAG_REGEXP = Pattern.compile("<[^>]*>");
   public static final JaroWinklerDistance DISTANCE = new JaroWinklerDistance();
   public static final Splitter COMMA_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
   private static final Joiner COMMA_JOINER = Joiner.on(",").skipNulls();
   public static final CharMatcher JAGEX_PRINTABLE_CHAR_MATCHER = new JagexPrintableCharMatcher();

   public static List<String> fromCSV(String input) {
      return COMMA_SPLITTER.splitToList(input);
   }

   public static String toCSV(Collection<String> input) {
      return COMMA_JOINER.join(input);
   }

   public static String removeTags(String str, boolean removeLevels) {
      int strLen;
      if (removeLevels) {
         strLen = StringUtils.lastIndexOf(str, "  (level");
         if (strLen >= 0) {
            str = str.substring(0, strLen);
         }
      }

      strLen = str.length();
      int open;
      int close;
      if ((open = StringUtils.indexOf(str, 60)) != -1 && (close = StringUtils.indexOf(str, 62, open)) != -1) {
         if (open == 0) {
            if ((open = close + 1) >= strLen) {
               return "";
            }

            if ((open = StringUtils.indexOf(str, 60, open)) == -1 || StringUtils.indexOf(str, 62, open) == -1) {
               return StringUtils.substring(str, close + 1);
            }

            open = 0;
         }

         SB.setLength(0);
         int i = 0;

         do {
            while(open != i) {
               SB.append(str.charAt(i++));
            }

            i = close + 1;
         } while((open = StringUtils.indexOf(str, 60, close)) != -1 && (close = StringUtils.indexOf(str, 62, open)) != -1 && i < strLen);

         while(i < strLen) {
            SB.append(str.charAt(i++));
         }

         return SB.toString();
      } else {
         return strLen == str.length() ? str : str.substring(0, strLen - 1);
      }
   }

   public static String removeTags(String str) {
      return removeTags(str, false);
   }

   public static String standardize(String str, boolean removeLevel) {
      return StringUtils.isBlank(str) ? str : removeTags(str, removeLevel).replace(' ', ' ').trim().toLowerCase();
   }

   public static String standardize(String str) {
      return standardize(str, false);
   }

   public static String toJagexName(String str) {
      return CharMatcher.ascii().retainFrom(str.replaceAll("[ _-]", " ")).trim();
   }

   public static String sanitizeMultilineText(String str) {
      return removeTags(str.replaceAll("-<br>", "-").replaceAll("<br>", " ").replaceAll("[ ]+", " "));
   }

   public static String escapeJagex(String str) {
      StringBuilder out = new StringBuilder(str.length());

      for(int i = 0; i < str.length(); ++i) {
         char c = str.charAt(i);
         if (c == '<') {
            out.append("<lt>");
         } else if (c == '>') {
            out.append("<gt>");
         } else if (c == '\n') {
            out.append("<br>");
         } else if (c != '\r') {
            out.append(c);
         }
      }

      return out.toString();
   }

   public static String sanitize(String name) {
      String cleaned = name.contains("<img") ? name.substring(name.lastIndexOf(62) + 1) : name;
      return cleaned.replace(' ', ' ');
   }

   public static String titleCase(Enum o) {
      String toString = o.toString();
      return o.name().equals(toString) ? WordUtils.capitalize(toString.toLowerCase(), new char[]{'_'}).replace('_', ' ') : toString;
   }

   public static boolean matchesSearchTerms(Iterable<String> searchTerms, Collection<String> keywords) {
      Iterator var2 = searchTerms.iterator();

      String term = null;
      String finalTerm = term;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         term = (String)var2.next();
      } while(!keywords.stream().noneMatch((t) -> {
         return t.contains(finalTerm) || DISTANCE.apply(t, finalTerm) > 0.9D;
      }));

      return false;
   }
}
