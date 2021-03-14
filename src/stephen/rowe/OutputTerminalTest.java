package stephen.rowe;


import javax.swing.*;

class OutputTerminalTest {

    public static void main(String [] args) {
        SwingUtilities.invokeLater(() ->
                new OutputTerminal().setVisible(true)
        );
    }

}