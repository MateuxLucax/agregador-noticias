import models.Jornal;
import models.Noticia;
import parsers.*;

import java.io.FileNotFoundException;
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



    public static void main(String[] args) {

        try {
            dadosUsuario.loadJornaisSeguidos(jornais);

            for (Jornal jornal : jornais) {
                if (jornal.seguido()) {
                    Parser par = jornal.getParser();

                    Instant now = Instant.now();
                    Date yesterday = Date.from(now.minus(1, ChronoUnit.DAYS));
                    Date today     = Date.from(now.truncatedTo(ChronoUnit.DAYS));

                    System.out.println(jornal.getNome());
                    ArrayList<Noticia> ns = par.getNoticiasRecentes();
                    for (Noticia n : ns)
                        System.out.println(n);
                }
            }

            estatisticas.printTabela();

            dadosUsuario.saveJornaisSeguidos(jornais);
        } catch (FileNotFoundException e) {
            System.out.println("Algum(ns) arquivo(s) estão faltando.");
            System.out.println("Baixe-os em https://github.com/MateuxLucax/agregador-noticias");
            System.out.println("e coloque-os em " + dadosUsuario.getDiretorio());
        }
    }
}
