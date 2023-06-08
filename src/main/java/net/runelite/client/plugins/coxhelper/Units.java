package net.runelite.client.plugins.coxhelper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Units {
   String MILLISECONDS = "ms";
   String MINUTES = " mins";
   String PERCENT = "%";
   String PIXELS = "px";
   String POINTS = "pt";
   String SECONDS = "s";
   String TICKS = " ticks";
   String LEVELS = " lvls";
   String FPS = " fps";
   String GP = " GP";

   String value();
}
