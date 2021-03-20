import models.Jornal;
import models.Noticia;

import parsers.G1Parser;
import parsers.Parser;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.ArrayList;

public class Application {

    public static Jornal[] jornais = {
            new Jornal("G1", "https://g1.globo.com/", new G1Parser())
    };

    // se jornalSeguido[i] então mostramos as notícias de jornais[i]
    public static boolean[] jornalSeguido = new boolean[jornais.length];


    public static boolean carregarJornaisSeguidos() {
        //
        // Busca arquivo no diretório
        //
        String homepath;
        try {
            homepath = System.getenv("HOMEPATH");  // Geralmente C:/Users/user
        } catch (NullPointerException e) {
            // System.getenv() causa NullPointerException caso a variável não exista
            System.out.println("ERRO: Configure a variável HOMEPATH do seu sistema.");
            return false;
        }
        String path = homepath + "/agregador-noticias/jornais-seguidos.txt";
        File arquivo = new File(path);

        //
        // Lê o arquivo e configura jornalSeguido[]
        //
        try {
            Scanner scn = new Scanner(arquivo);
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

        } catch (FileNotFoundException e) {
            System.out.println("Baixe o arquivo jornais-seguidos.txt de https://github.com/MateuxLucax/agregador-noticias");
            System.out.println("e coloque-o em " + arquivo.getAbsolutePath() + ".");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {

        boolean jornaisSeguidosCarregados = carregarJornaisSeguidos();
        if (!jornaisSeguidosCarregados) {
            System.out.println("Não foi possível carregar os jornais seguidos.");
            return;
        }

        for (int i=0; i<jornais.length; i++) {
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
    }

}
