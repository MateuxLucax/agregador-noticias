package models;

import java.util.Date;

public class Contagem
{
	private final Regiao regiao;
	private final Date   dataColeta;
	private       int    casos;
	private       int    recuperados;
	private       int    mortos;

	public Contagem(Regiao regiao, Date dataColeta) {
		this.regiao = regiao;
		this.dataColeta = dataColeta;
		preencherDados();
	}

	private void preencherDados() {
		// a partir da data e da regiao,
		// busca em algum site o número de casos, recuperados e mortos
		// e carrega os números nos atributos correspondentes
	}

	public int    getCasos()       { return casos; }
	public int    getRecuperados() { return recuperados; }
	public int    getMortos()      { return mortos; }
	public Regiao getRegiao()      { return regiao; }
	public Date getDataColeta()    { return dataColeta; }
}
