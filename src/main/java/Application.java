import models.Jornal;
import models.Noticia;
import parsers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Formatter;

public class Application {

    public static final Jornal[] jornais = {
        new Jornal("G1", "https://g1.globo.com/", new G1Parser()),
        new Jornal("Folha de São Paulo", "https://www.folha.uol.com.br/", new FSPParser()),
        new Jornal("BBC", "https://www.bbc.com/portuguese/", new BBCParser())
    };
    public static boolean[] jornalSeguido = new boolean[jornais.length];
    // se jornalSeguido[i] então mostramos as notícias de jornais[i]

    public static EstatisticasPorRegiao estatisticasPorRegiao = new EstatisticasPorRegiao();

    public static void seguirJornal(int i) {
        if (i >= 0 && i < jornalSeguido.length)
            jornalSeguido[i] = true;
    }

    public static void naoSeguirJornal(int i) {
        if (i >= 0 && i < jornalSeguido.length)
            jornalSeguido[i] = false;
    }


    public static void main(String[] args) {

        Arquivos.initArquivos();

        try {
            Arquivos.loadJornaisSeguidos(jornalSeguido);

            for (int i = 0; i < jornais.length; i++) {
                if (jornalSeguido[i]) {
                    Parser p = jornais[i].getParser();

                    Instant now = Instant.now();
                    Date yesterday = Date.from(now.minus(1, ChronoUnit.DAYS));
                    Date today     = Date.from(now.truncatedTo(ChronoUnit.DAYS));

                    System.out.println(jornais[i].getNome());
                    ArrayList<Noticia> ns = p.getNoticiasRecentes();
                    for (Noticia n : ns)
                        System.out.println(n);
                }
            }

            estatisticasPorRegiao.printTabela();

            Arquivos.saveJornaisSeguidos(jornalSeguido);
        } catch (FileNotFoundException e) {
            System.out.println("Algum(ns) arquivo(s) estão faltando.");
            System.out.println("Baixe-os em https://github.com/MateuxLucax/agregador-noticias");
            System.out.println("e coloque-os em " + Arquivos.diretorio);
        }
    }
}
