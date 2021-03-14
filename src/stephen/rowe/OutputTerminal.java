package stephen.rowe;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class OutputTerminal extends JFrame {
    private JTextArea jTextArea;
    private PrintStream reference = System.out;

    public OutputTerminal() {
        super("PROXY INFO");

        jTextArea = new JTextArea(50, 10);
        PrintStream streamToTextArea = new PrintStream(new JTextAreaOutputStream(jTextArea));

        // Change the System.out for both standard-out and error
        // to display on this text box. This is so that the
        // console in the IDE is not clogged up while the user
        // wants to write their regular expressions.
        System.setOut(streamToTextArea);
        System.setErr(streamToTextArea);

        jTextArea.setEditable(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.weightx = 1.0;
        gbConstraints.weighty = 1.0;
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 1;
        gbConstraints.gridwidth = 2;
        gbConstraints.fill = GridBagConstraints.BOTH;

        add(new JScrollPane(jTextArea), gbConstraints);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 500);
        setLocationRelativeTo(null);    // centers on screen

    }

    public class JTextAreaOutputStream extends OutputStream {
        private JTextArea textArea;

        public JTextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.append(String.valueOf((char)b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}