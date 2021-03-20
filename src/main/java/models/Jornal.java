package models;

import parsers.Parser;

import java.util.ArrayList;
import java.util.Objects;

public class Jornal {

    private final String             nome;
    private final String             url;
    private final Parser             parser;
    private       ArrayList<Noticia> noticias;

    public Jornal(String nome, String url, Parser parser) {
        this.nome = nome;
        this.url = url;
        this.parser = parser;
        noticias = new ArrayList<>();
    }

    public String             getNome()     { return nome; }
    public String             getUrl()      { return url; }
    public Parser             getParser()   { return parser; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jornal jornal = (Jornal) o;
        return url.equals(jornal.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "Jornal{" +
                "nome='" + nome + '\'' +
                ", url='" + url + '\'' +
                ", noticias=" + noticias +
                '}';
    }
}
