/*
 * Copyright 2016 Rudy Alex Kohn <s133235@student.dtu.dk>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ui;

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.DefaultListModel;

/**
 * Hostile take-over class for default output stream !.<br>
 * <p>
 * How to use :<br>
 <code>java.awt.EventQueue.invokeLater(() -> {<br>
            output = new Output();<br>
            output.setVisible(true);<br>
            Output.redirectSystemStreams(true, output);<br>
        });<br>
 </code>
 *
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
@SuppressWarnings("serial")
public class Output extends java.awt.Frame {

    /**
     * Creates new form Output
     */
    public Output() {
        initComponents();
        listModel = new DefaultListModel();
        listModel.addElement(String.format("%sStart.", getTimeString()));
        jList1.setModel(listModel);
        jList1.setPreferredSize(new Dimension(400, 300));
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        setAlwaysOnTop(true);
        setResizable(false);
        setTitle("Output takeover - af s133235");
        setType(java.awt.Window.Type.UTILITY);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setFont(new java.awt.Font("Courier New", 0, 10)); // NOI18N

        jScrollPane1.setViewportView(jList1);

        add(jScrollPane1, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    public DefaultListModel listModel;
    private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss", Locale.US);

    public void addToList(final String text) {
        try {
            listModel.addElement(String.format("%s %s", getTimeString(), text));
            int lastIndex = listModel.getSize() - 1;
            if (lastIndex >= 0) {
                jList1.setSelectedIndex(lastIndex);
                jList1.ensureIndexIsVisible(lastIndex);
            }
        } catch (final OutOfMemoryError e) {
            listModel.clear();
        }
    }

    private String getTimeString() {
        Calendar cal = Calendar.getInstance();
        return SDF.format(cal.getTime());
    }

    public static void redirectSystemStreams(final boolean enableRedirect, final Output output) {
        if (enableRedirect) {
            final OutputStream out = new OutputStream() {
                @Override
                public void write(final int b) throws IOException {
                    output.addToList(String.valueOf((char) b));
                }

                @Override
                public void write(final byte[] b, final int off, final int len) throws IOException {
                    output.addToList(new String(b, off, len));
                }

                @Override
                public void write(final byte[] b) throws IOException {
                    write(b, 0, b.length);
                }
            };
            System.setOut(new PrintStream(out, true));
            System.setErr(new PrintStream(out, true));
        } else {
            System.setOut(System.out);
            System.setErr(System.err);
        }
    }

}
