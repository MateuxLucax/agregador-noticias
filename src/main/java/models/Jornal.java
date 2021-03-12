package models;

import java.util.ArrayList;

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
  public ArrayList<Noticia> getNoticias() { return noticias; }

  public boolean setNome(String nome) {
		if (nome.isBlank()) return false; 
    this.nome = nome;
		return true;
  }

  public boolean setUrl(String url) {
		if (url.isBlank()) return false; 
    this.url = url;
		return true;
  }

  public boolean addNoticia(Noticia noticia) {
    if (!noticias.contains(noticia)) return false;
    noticias.add(noticias);
    return true;
  }

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
