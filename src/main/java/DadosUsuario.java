import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Jornal;
import models.Noticia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

public class DadosUsuario {

    private String diretorio;
    private File   arquivoJornaisSeguidos;
    private File   arquivoNoticias;

    private static DadosUsuario instance;


    public static DadosUsuario getInstance() {
        if (instance == null)
            instance = new DadosUsuario();
        return instance;
    }

    private DadosUsuario() {
        try {
            diretorio              = System.getProperty("user.dir");
            arquivoJornaisSeguidos = new File(diretorio + "/jornais-seguidos.txt");
            arquivoNoticias        = new File(diretorio + "/ler-mais-tarde.txt");
        } catch (NullPointerException e) {
            System.out.println("ERRO: Não foi possível obter o diretório do projeto.");
        }
    }

    public String getDiretorio() {
        return diretorio;
    }

    public void loadJornaisSeguidos(ArrayList<Jornal> jornais) throws FileNotFoundException {
        int n = jornais.size();

        Scanner scn = new Scanner(arquivoJornaisSeguidos);
        scn.useDelimiter(";");

        int i;
        for (i=0; scn.hasNext() && i < n; i++) {
            boolean seguir = Boolean.parseBoolean( scn.next() );
            jornais.get(i).setSeguido(seguir);
        }

        // Seguir resto dos jornais por padrão
        for (; i < n; i++)
            jornais.get(i).setSeguido(true);
        scn.close();
    }

    public void saveJornaisSeguidos(ArrayList<Jornal> jornais) throws FileNotFoundException {
        int n = jornais.size();

        Formatter fmt = new Formatter(arquivoJornaisSeguidos);

        if (n > 0) {
            for (int i = 0; i < n - 1; i++)
                fmt.format("%b;", jornais.get(i).isSeguido());
            fmt.format("%b", jornais.get(n - 1).isSeguido());
        }

        fmt.close();
    }

    public ArrayList<Noticia> loadNoticias() throws FileNotFoundException {
        Scanner scn = new Scanner(arquivoNoticias);
        scn.useDelimiter("\\Z");  // \Z é end of string -- lê arquivo inteiro

        // https://github.com/google/gson/blob/master/UserGuide.md#TOC-Collections-Examples
        Type arrayListNoticias = new TypeToken<ArrayList<Noticia>>(){}.getType();

        Gson gson = new Gson();
        return gson.fromJson(scn.next(), arrayListNoticias);
    }

    public void saveNoticias(ArrayList<Noticia> noticias) throws IOException {
        FileWriter fw = new FileWriter(arquivoNoticias);
        Gson gson = new Gson();
        fw.write(gson.toJson(noticias));
        fw.close();
    }
}
