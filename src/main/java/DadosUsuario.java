import models.Jornal;
import models.Noticia;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;



public class DadosUsuario
{
   	private ArrayList<Jornal>  jornaisSeguidos;
   	private ArrayList<Noticia> noticiasLerMaisTarde;

   	public DadosUsuario() {
   		carregarDados();
   	}

   	private void carregarDados() {

   	}

   	public void escreverDados() {
   		// sobrescreve o documento com os dados nessa instância
   	}

	public boolean addJornal(Jornal jornal) {
		if (this.jornaisSeguidos.contains(jornal)) return false;
		this.jornaisSeguidos.add(jornal);
		return true;
	}

	public boolean verMaisTarde(Noticia noticia) {
		if (this.noticiasLerMaisTarde.contains(noticia)) return false;
		this.noticiasLerMaisTarde.add(noticia);
		return true;
	}

	public void remover(Jornal jornal) {
		this.jornaisSeguidos.remove(jornal);
	}

	public void remover(Noticia noticia) {
		this.noticiasLerMaisTarde.remove(noticia);
	}

}
