import models.Jornal;
import models.Noticia;
import parsers.BBCParser;
import parsers.FSPParser;
import parsers.G1Parser;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class Application {

    public static final Jornal[] jornais = {
        new Jornal("G1", "https://g1.globo.com/", new G1Parser()),
        new Jornal("Folha de São Paulo", "https://www.folha.uol.com.br/", new FSPParser()),
        new Jornal("BBC", "https://www.bbc.com/portuguese/", new BBCParser())
    };

    public static EstatisticasPorRegiao estatisticas = EstatisticasPorRegiao.getInstance();
    public static DadosUsuario          dadosUsuario = DadosUsuario.getInstance();



    public static ArrayList<Noticia> noticiasSalvas;

    public static void lerMaisTarde(Noticia n) {
        if (!noticiasSalvas.contains(n))
            noticiasSalvas.add(n);
    }

    public static void removerLerMaisTarde(Noticia n) {
        noticiasSalvas.remove(n);
    }



    public static void main(String[] args) {

        try {

            noticiasSalvas = dadosUsuario.loadNoticias();
            if (noticiasSalvas.size() > 0) {
                System.out.println("Notícias que você salvou para ler mais tarde: ");
                for (Noticia n : noticiasSalvas)
                    System.out.println(n);
            }

            dadosUsuario.loadJornaisSeguidos(jornais);
            for (Jornal jornal : jornais) {
                if (jornal.seguido()) {
                    Instant now = Instant.now();
                    Date yesterday = Date.from(now.minus(1, ChronoUnit.DAYS));
                    Date today     = Date.from(now.truncatedTo(ChronoUnit.DAYS));

                    System.out.println(jornal.toString());
                }
            }

            estatisticas.printTabela();

            dadosUsuario.saveJornaisSeguidos(jornais);
            dadosUsuario.saveNoticias(noticiasSalvas);

        } catch (IOException e) {  // FileNotFoundException extends IOException
            System.out.println("Algum(ns) arquivo(s) estão faltando.");
            System.out.println("Baixe-os em https://github.com/MateuxLucax/agregador-noticias");
            System.out.println("e coloque-os em " + dadosUsuario.getDiretorio());
        }
    }
}
