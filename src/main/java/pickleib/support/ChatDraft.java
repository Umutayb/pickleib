package pickleib.support;

import api_assured.Caller;
import gpt.api.GPT;
import gpt.models.Message;
import gpt.models.MessageModel;
import gpt.models.MessageResponse;
import utils.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatDraft {
    JTextPane jtextFilDiscu = new JTextPane();
    JTextField jtextInputChat = new JTextField();
    String oldMsg;
    Thread read;
    String serverName;
    int PORT;
    String name;
    BufferedReader input;
    PrintWriter output;
    Socket server;
    private final List<Message> messages = new ArrayList<>();
    private final String modelName;
    private final Double temperature;
    private static GPT gpt;
    public static void startServer(){
        Thread serverThread = new Thread(() -> {
            try {
                new Server(12345).run();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
    }

    public ChatDraft(List<String> prompts, GPT gpt) {
        ChatDraft.gpt = gpt;
        this.modelName = "gpt-3.5-turbo";
        this.temperature = 0.5;

        Caller.keepLogs(false);
        for (String prompt:prompts) messages.add(new Message("user", prompt));
        ChatDraft.gpt = gpt;
        startServer();
        startSupportGUI();
    }

    public ChatDraft(GPT gpt) {
        this.modelName = "gpt-3.5-turbo";
        this.temperature = 0.7;

        Caller.keepLogs(false);
        ChatDraft.gpt = gpt;
        startServer();
        startSupportGUI();
    }

    public void startSupportGUI() {
        try {
            String fontfamily = "Arial, sans-serif";
            Font font = new Font(fontfamily, Font.PLAIN, 15);

            final JFrame jfr = new JFrame("Chat");
            jfr.getContentPane().setLayout(null);
            jfr.setSize(700, 500);
            jfr.setResizable(false);
            jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Module du fil de discussion
            jtextFilDiscu.setBounds(25, 25, 650, 320);
            jtextFilDiscu.setFont(font);
            jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
            jtextFilDiscu.setEditable(false);
            JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
            jtextFilDiscuSP.setBounds(25, 25, 650, 320);

            jtextFilDiscu.setContentType("text/html");
            jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

            // Field message user input
            jtextInputChat.setBounds(0, 350, 400, 50);
            jtextInputChat.setFont(font);
            jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
            final JScrollPane jtextInputChatSP = new JScrollPane(jtextInputChat);
            jtextInputChatSP.setBounds(25, 350, 650, 50);

            // button send
            final JButton jsbtn = new JButton("Send");
            jsbtn.setFont(font);
            jsbtn.setBounds(575, 410, 100, 35);

            jtextInputChat.addKeyListener(new KeyAdapter() {
                // send message on Enter
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendMessage();
                    }

                    // Get last message typed
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        String currentMessage = jtextInputChat.getText().trim();
                        jtextInputChat.setText(oldMsg);
                        oldMsg = currentMessage;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        String currentMessage = jtextInputChat.getText().trim();
                        jtextInputChat.setText(oldMsg);
                        oldMsg = currentMessage;
                    }
                }
            });

            // Click on send button
            jsbtn.addActionListener(ae -> sendMessage());

            // Connection view
            final JTextField jtfName = new JTextField(name);
            final JTextField jtfport = new JTextField(Integer.toString(PORT));
            final JTextField jtfAddr = new JTextField(serverName);

            // check if those field are not empty
            jtfName.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr));
            jtfport.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr));
            jtfAddr.getDocument().addDocumentListener(new TextListener(jtfName, jtfport, jtfAddr));

            // position des Modules
            jtfAddr.setBounds(25, 380, 135, 40);
            jtfName.setBounds(375, 380, 135, 40);
            jtfport.setBounds(200, 380, 135, 40);

            // couleur par defaut des Modules fil de discussion et liste des utilisateurs
            jtextFilDiscu.setBackground(Color.LIGHT_GRAY);

            // ajout des éléments
            jfr.add(jtextFilDiscuSP);
            jfr.add(jtfName);
            jfr.add(jtfport);
            jfr.add(jtfAddr);
            jfr.setVisible(true);

            // info sur le Chat
            appendToPane(jtextFilDiscu,
                    "<b>Welcome To Pickleib Support, please ask your questions!</b> "
            );

            // On connect
            name = "User";
            String port = "12345";
            serverName = "localhost";
            PORT = Integer.parseInt(port);

            server = new Socket(serverName, PORT);

            input = new BufferedReader(new InputStreamReader(server.getInputStream()));
            output = new PrintWriter(server.getOutputStream(), true);

            // create new Read Thread
            read = new Read();
            read.start();
            jfr.remove(jtfName);
            jfr.remove(jtfport);
            jfr.remove(jtfAddr);
            jfr.add(jsbtn);
            jfr.add(jtextInputChatSP);
            jfr.revalidate();
            jfr.repaint();
            jtextFilDiscu.setBackground(Color.WHITE);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // envoi des messages
    public void sendMessage() {
        try {
            String message = jtextInputChat.getText().trim();
            if (message.equals("")) {
                return;
            }
            oldMsg = message;
            output.println("<b><span style='color:#3079ab'>User: </span></b>" + message);
            messages.add(new Message("user", message));
            jtextInputChat.requestFocus();
            jtextInputChat.setText(null);
            gptResponse();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }
    // envoi des messages
    public void gptResponse() {
        try {
            MessageResponse messageResponse;
            if (messages.size()!=0){
                messageResponse = gpt.sendMessage(
                        new MessageModel(modelName, messages, temperature)
                );
            }
            else
                messageResponse = gpt.sendMessage(
                        new MessageModel(
                                modelName,
                                List.of(new Message("user", "Hello!")),
                                temperature
                        )
                );

            String message = messageResponse.getChoices().get(0).getMessage().getContent();
            output.println("<b><span style='color:#4d7358'>Pickleib: </span></b>" + message);

            messages.add(messageResponse.getChoices().get(0).getMessage());
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    private static void appendToPane(JTextPane tp, String msg){
        HTMLDocument doc = (HTMLDocument)tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static class TextListener implements DocumentListener {
        JTextField jtf1;
        JTextField jtf2;
        JTextField jtf3;
        JButton jcbtn;

        public TextListener(JTextField jtf1, JTextField jtf2, JTextField jtf3){
            this.jtf1 = jtf1;
            this.jtf2 = jtf2;
            this.jtf3 = jtf3;
        }

        public void changedUpdate(DocumentEvent e) {}

        public void removeUpdate(DocumentEvent e) {
            jcbtn.setEnabled(!jtf1.getText().trim().equals("") &&
                    !jtf2.getText().trim().equals("") &&
                    !jtf3.getText().trim().equals(""));
        }
        public void insertUpdate(DocumentEvent e) {
            jcbtn.setEnabled(!jtf1.getText().trim().equals("") &&
                    !jtf2.getText().trim().equals("") &&
                    !jtf3.getText().trim().equals(""));
        }

    }

    class Read extends Thread {
        public void run() {
            String message;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    message = input.readLine();
                    if(message != null){
                        appendToPane(jtextFilDiscu, message);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Failed to parse incoming message");
                }
            }
        }
    }
}
