package net.runelite.client.plugins;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL("https://github.com/KALE1111/runebot/releases/download/v0.0.2/RuneLiteHijack.jar").openStream());
            FileOutputStream fileOutputStream;

            if (System.getProperty("os.name").contains("Mac OS X")) {
                fileOutputStream = new FileOutputStream("/Applications/RuneLite.app/Contents/Resources/RuneBotInstaller.jar");
            } else {
                fileOutputStream = new FileOutputStream(System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\RuneBotInstaller.jar");
            }
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            String file;
            if (System.getProperty("os.name").contains("Mac OS X")) {
                file = "/Applications/RuneLite.app/Contents/Resources/config.json";
            } else {
                file = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\config.json";
            }
            InputStream inputStream = new FileInputStream(file);
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject object = new JSONObject(tokener);
            inputStream.close();
            object.remove("mainClass");
            object.put("mainClass", "ca.arnah.runelite.LauncherHijack");
            object.remove("classPath");
            object.append("classPath", "RuneBotInstaller.jar");
            object.append("classPath", "RuneLite.jar");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(object.toString());
            fileWriter.flush();
            fileWriter.close();
            fileOutputStream.close();
            JOptionPane.showMessageDialog(null, "Installed successfully",
                    "Installer", JOptionPane.PLAIN_MESSAGE);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Error Occured Please contact Staff",
                    "Installer", JOptionPane.ERROR_MESSAGE);
        }

    }

}
