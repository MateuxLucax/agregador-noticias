package gui;

import models.Jornal;

import javax.swing.*;
import java.util.ArrayList;

public class JornaisSeguidosPanel
{
    private JPanel painel;

    private static JornaisSeguidosPanel instance;

    public static void inicializar(ArrayList<Jornal> jornais) {
        instance = new JornaisSeguidosPanel(jornais);
    }

    public static JPanel get() {
        return instance.painel;
    }

    public JornaisSeguidosPanel(ArrayList<Jornal> jornais)
    {
        painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        int n = jornais.size();
        JCheckBox[] cbs = new JCheckBox[n];
        for (int i = 0; i < n; i++)
        {
            Jornal j = jornais.get(i);
            cbs[i] = new JCheckBox(j.getNome(), j.isSeguido());
            painel.add(cbs[i]);
        }

        JButton btSalvar = new JButton("Salvar preferências");
        btSalvar.addActionListener(e -> {
            for (int i = 0; i < n; i++)
                jornais.get(i).setSeguido(cbs[i].isSelected());
        });
        painel.add(btSalvar);
    }
}
