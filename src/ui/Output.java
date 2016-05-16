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

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Console replacement class for System.out and System.err<br>
 * <p>
 * Example usage :<br>
 * <code>java.awt.EventQueue.invokeLater(() -> {<br>
 * output = new Output();<br>
 * Output.redirectSystemStreams(true, output);<br>
 * output.setVisible(true);<br>
 * });</code>
 *
 * @author Rudy Alex Kohn (s133235@student.dtu.dk)
 */
public class Output {

    private final DefaultListModel listModel;
    private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel();
    private final JScrollPane listScrollPane = new JScrollPane();
    private final JList rowList = new JList();

    public Output() {
        /* configure the listmodel */
        listModel = new DefaultListModel();
        //listModel.addElement(String.format("%sStart.", getTimeString()));
        rowList.setModel(listModel);

        /* configure listrow */
        rowList.setFont(new java.awt.Font("Courier New", 0, 10));
        rowList.setVisibleRowCount(20); // default set to 20!
        listScrollPane.setViewportView(rowList);

        /* configure panel */
        panel.setLayout(new BorderLayout());
        panel.add(listScrollPane);

        /* configure frame (window) */
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationByPlatform(true);
        frame.setTitle("System.out.err.output :: by Rudy Alex Kohn (s133235)");
        frame.pack();
        frame.setSize(800, 450);
        frame.setVisible(true);
    }

    /**
     * Change the visibility of the classes frame
     * @param value If true, frame will be set to visible, otherwise it will be hidden
     */
    public void setVisible(final boolean value) {
        frame.setVisible(value);
    }

    /**
     * Adds a string to the output display, if needed, just cast the output to String
     *
     * @param text The string to add to output
     */
    public void addToList(final String text) {
        if (!"".equals(text.trim())) {
            try {
                listModel.addElement(String.format("%s %s", getTimeString(), text));
                int lastIndex = listModel.getSize() - 1;
                if (lastIndex >= 0) {
                    rowList.setSelectedIndex(lastIndex);
                    rowList.ensureIndexIsVisible(lastIndex);
                }
            } catch (final OutOfMemoryError e) {
                listModel.clear();
            }
        }
    }

    private String getTimeString() {
        Calendar cal = Calendar.getInstance();
        return SDF.format(cal.getTime());
    }

    /**
     * Will effectively take over the System.out and System.err streams.
     *
     * @param enableRedirect If true, takeover thy will be done, false sets
     * default
     * @param output The output object that contains the functionality to
     * display the text
     */
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
