import models.Jornal;
import models.Noticia;
import parsers.FSPParser;
import parsers.G1Parser;
import parsers.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;

public class Application {

    public static Jornal[] jornais = {
        new Jornal("G1", "https://g1.globo.com/", new G1Parser()),
        new Jornal("Folha de São Paulo", "https://www.folha.uol.com.br/", new FSPParser())
    };

    // se jornalSeguido[i] então mostramos as notícias de jornais[i]
    public static boolean[] jornalSeguido = new boolean[jornais.length];

    public static String diretorio;
    public static File   arquivoJornaisSeguidos;


    public static void initArquivos() {
        try {
            diretorio = System.getProperty("user.dir");
            arquivoJornaisSeguidos = new File(diretorio + "/jornais-seguidos.txt");
        } catch (NullPointerException e) {
            System.out.println("ERRO: Não foi possível obter o diretório do projeto.");
        }
    }

    public static void seguirJornal(int i) {
        if (i >= 0 && i < jornalSeguido.length)
            jornalSeguido[i] = true;
    }

    public static void naoSeguirJornal(int i) {
        if (i >= 0 && i < jornalSeguido.length)
            jornalSeguido[i] = false;
    }

    public static void loadJornaisSeguidos() throws FileNotFoundException {
        Scanner scn = new Scanner(arquivoJornaisSeguidos);
        scn.useDelimiter(";");

        // Formato do arquivo jornais-seguidos.txt:
        // Por exemplo, "true;false;true"
        // carrega jornalSeguido[0] = true, jornalSeguido[1] = false, jornalSeguido[2] = true

        int i;
        for (i=0; scn.hasNext() && i < jornais.length; i++)
            jornalSeguido[i] = Boolean.parseBoolean( scn.next() );

        if (i < jornais.length) {
            System.out.println("AVISO: jornais-seguidos.txt incompleto, faltam jornais.");
            // Mas não precisa retornar false; só seguir o resto dos jornais por padrão
            for (; i < jornais.length; i++)
                jornalSeguido[i] = true;
        }
    }

    public static void saveJornaisSeguidos() throws FileNotFoundException {
        Formatter fmt = new Formatter(arquivoJornaisSeguidos);
        int n = jornalSeguido.length;
        for (int i=0; i < n-1; i++)
            fmt.format("%b;", jornalSeguido[i]);
        fmt.format("%b", jornalSeguido[n-1]);
        fmt.close();
    }

    public static void main(String[] args) {

        initArquivos();

        try {
            loadJornaisSeguidos();

            for (int i = 0; i < jornais.length; i++) {
                if (jornalSeguido[i]) {
                    Parser p = jornais[i].getParser();

                    Instant now = Instant.now();
                    Instant yesterday = now.minus(1, ChronoUnit.DAYS);
                    Instant today = now.truncatedTo(ChronoUnit.DAYS);

                    System.out.println(jornais[i].getNome());
                    ArrayList<Noticia> ns = p.getNoticias(Date.from(yesterday), Date.from(today));
                    for (Noticia n : ns)
                        System.out.println(n);
                }
            }

            saveJornaisSeguidos();
        } catch (FileNotFoundException e) {
            System.out.println("Algum(ns) arquivo(s) estão faltando.");
            System.out.println("Baixe-os em https://github.com/MateuxLucax/agregador-noticias");
            System.out.println("e coloque-os em " + diretorio);
        }
    }

}
