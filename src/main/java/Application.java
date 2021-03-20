import parsers.G1Parser;
import parsers.Parser;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Application {

    public static void main(String[] args) {
        Parser g1Parser = new G1Parser();

        Instant now = Instant.now();
        Instant yesterday = now.minus(1, ChronoUnit.DAYS);
        Instant today = now.truncatedTo(ChronoUnit.DAYS);

        System.out.println(g1Parser.getNoticias(Date.from(yesterday), Date.from(today)));
    }

}




/*
public class Jornal {
    ...

    public void preencherNoticias(<args>) {
        this.noticias = parser.getNoticias(<args>);
    }
    // cada método sobrecarregado getNoticias em Parser
    // tem um método correspondente preencherNoticias que chama esse getNoticias 
}

public class Application {
    private DadosUsuario ds;
    ...
    public static void main(String[] args) {
        for (Jornal j : ds.jornaisSeguidos()) {
            j.preencherNoticias();
        }

        // mostrar notícias na interface etc.
    }
}

*/