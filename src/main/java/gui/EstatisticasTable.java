package gui;

import enums.Regiao;
import main.EstatisticasPorRegiao;
import models.Estatisticas;

import javax.swing.*;
import java.text.DecimalFormat;


public class EstatisticasTable
{
    private final JTable tabela;
    private static EstatisticasTable instance;

    public static void inicializar()
    {
        instance = new EstatisticasTable();
    }

    public static JTable get()
    {
        return instance.tabela;
    }

    /* Por que não fazer EstatisticasTable uma subclasse de JTable,
       de forma que se em vez de escrever
           EstatisticasTable.inicializar();
           JTable tabEstatisticas = EstatisticasTable.get();
       se escreve
           JTable tabEstatisticas = new EstatisticasTable();
       ?

       Porque atualmente, para instanciar a JTable, fazemos o seguinte:
           this.tabela = new JTable(dados, colunas);
       Se EstatisticasTable fosse subclasse de JTable, teríamos que fazer
           super(dados, colunas);
       Mas precisamos gerar os dados e as colunas antes,
       e uma chamada super() precisa ser a primeira do construtor.

       Então por que não ler os dados e as colunas como argumentos do construtor:
           JTable tabEstatisticas = new EstatisticasTable(dados, colunas);
       ?
       Porque abstrair a tabela é justamente o propósito da classe EstatisticasTable.
     */

    private EstatisticasTable()
    {
        EstatisticasPorRegiao estats = EstatisticasPorRegiao.getInstance();

        DecimalFormat df = new DecimalFormat();
        df.setGroupingSize(3);

        String[] colunas = {"Região", "Casos", "Óbitos", "Recuperados",
                            "Vacinados", "Segunda dose"};

        Regiao[] regioes = Regiao.values();
        String[][] dados = new String[regioes.length][colunas.length];
        for (int i = 0; i < regioes.length; i++)
        {
            int j = 0;
            Estatisticas e = estats.getEstatisticas(regioes[i]);
            dados[i][j++] = regioes[i].name();
            dados[i][j++] = df.format(e.getCasos());
            dados[i][j++] = df.format(e.getObitos());
            dados[i][j++] = df.format(e.getRecuperados());
            dados[i][j++] = df.format(e.getVacinados());
            dados[i][j  ] = df.format(e.getSegundaDose());
        }

        this.tabela = new JTable(dados, colunas);
        // Para usuário não poder editar a coluna (https://stackoverflow.com/a/36356371)
        this.tabela.setDefaultEditor(Object.class, null);
    }
}
