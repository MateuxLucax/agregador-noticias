package utils;

import java.awt.*;
import java.net.URI;

public class BrowserUtil {

    static public void openUrl(String url) {
        try {
            URI link = new URI(url);
            Desktop.getDesktop().browse(link);
        } catch (Exception e) {
            System.out.println("ERRO: não foi possível abrir a URL - " + url);
        }
    }
}
