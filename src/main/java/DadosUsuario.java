import models.Jornal;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

public class DadosUsuario {

    private String diretorio;
    private File   arquivoJornaisSeguidos;
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
        } catch (NullPointerException e) {
            System.out.println("ERRO: Não foi possível obter o diretório do projeto.");
        }
    }


    public String getDiretorio() {
        return diretorio;
    }

    public void loadJornaisSeguidos(Jornal[] jornais) throws FileNotFoundException {
        int n = jornais.length;

        Scanner scn = new Scanner(arquivoJornaisSeguidos);
        scn.useDelimiter(";");

        int i;
        for (i=0; scn.hasNext() && i < n; i++) {
            boolean seguir = Boolean.parseBoolean( scn.next() );
            if (seguir) jornais[i].seguir();
            else        jornais[i].naoSeguir();
        }

        if (i < n) {
            System.out.println("AVISO: faltam jornais em jornais-seguidos.txt. O resto será seguido por padrão.");
            for (; i < n; i++)
                jornais[i].seguir();
        }
        scn.close();
    }

    public void saveJornaisSeguidos(Jornal[] jornais) throws FileNotFoundException {
        int n = jornais.length;

        Formatter fmt = new Formatter(arquivoJornaisSeguidos);

        for (int i=0; i < n-1; i++)
            fmt.format("%b;", jornais[i].seguido());
        fmt.format("%b", jornais[n-1].seguido());

        fmt.close();
    }
}
