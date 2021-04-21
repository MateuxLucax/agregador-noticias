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

    public static void inicializar() {
        instance = new EstatisticasTable();
    }

    public static JTable get() {
        return instance.tabela;
    }


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

        tabela = new JTable(dados, colunas);
        // Para usuário não poder editar a coluna (https://stackoverflow.com/a/36356371)
        tabela.setDefaultEditor(Object.class, null);
    }
}
