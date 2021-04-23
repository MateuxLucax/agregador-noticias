package gui;

import models.Jornal;

import javax.swing.*;
import java.util.ArrayList;

public class JornaisSeguidosPanel extends JPanel
{
    public JornaisSeguidosPanel(ArrayList<Jornal> jornais)
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        int n = jornais.size();
        JCheckBox[] checkboxes = new JCheckBox[n];
        for (int i = 0; i < n; i++) {
            Jornal jornal = jornais.get(i);
            checkboxes[i] = new JCheckBox(jornal.getNome(), jornal.isSeguido());
            add(checkboxes[i]);
        }

        JButton btSalvar = new JButton("Salvar preferências");
        btSalvar.addActionListener(e -> {
            for (int i = 0; i < n; i++)
                jornais.get(i).setSeguido(checkboxes[i].isSelected());
        });
        add(btSalvar);
    }
}
