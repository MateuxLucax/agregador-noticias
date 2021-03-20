import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

public class Arquivos {

    public static String diretorio;
    public static File   arquivoJornaisSeguidos;

    public static void initArquivos() {
        try {
            diretorio              = System.getProperty("user.dir");
            arquivoJornaisSeguidos = new File(diretorio + "/jornais-seguidos.txt");
        } catch (NullPointerException e) {
            System.out.println("ERRO: Não foi possível obter o diretório do projeto.");
        }
    }


    public static void loadJornaisSeguidos(boolean[] jornalSeguido) throws FileNotFoundException {
        Scanner scn = new Scanner(arquivoJornaisSeguidos);
        scn.useDelimiter(";");

        // Formato do arquivo jornais-seguidos.txt:
        // Por exemplo, "true;false;true"
        // carrega jornalSeguido[0] = true, jornalSeguido[1] = false, jornalSeguido[2] = true

        int n = jornalSeguido.length;
        int i;
        for (i=0; scn.hasNext() && i < n; i++)
            jornalSeguido[i] = Boolean.parseBoolean( scn.next() );

        if (i < n) {
            System.out.println("AVISO: jornais-seguidos.txt incompleto, faltam jornais.");
            // Mas não precisa retornar false; só seguir o resto dos jornais por padrão
            for (; i < n; i++)
                jornalSeguido[i] = true;
        }
    }

    public static void saveJornaisSeguidos(boolean[] jornalSeguido) throws FileNotFoundException {
        Formatter fmt = new Formatter(arquivoJornaisSeguidos);
        int n = jornalSeguido.length;
        for (int i=0; i < n-1; i++)
            fmt.format("%b;", jornalSeguido[i]);
        fmt.format("%b", jornalSeguido[n-1]);
        fmt.close();
    }
}
