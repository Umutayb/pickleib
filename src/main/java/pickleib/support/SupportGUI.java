package pickleib.support;

import api_assured.Caller;
import gpt.api.GPT;
import gpt.models.Message;
import gpt.models.MessageModel;
import gpt.models.MessageResponse;
import javax.swing.*;
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

public class SupportGUI {
    JTextPane chatOverviewPanel = new JTextPane();
    JTextField messageInputPanel = new JTextField();
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
            try {new Server(12345).run();}
            catch (IOException e) {throw new RuntimeException(e);}
        });
        serverThread.start();
    }

    public SupportGUI(List<String> prompts, GPT gpt) {
        SupportGUI.gpt = gpt;
        this.modelName = "gpt-3.5-turbo";
        this.temperature = 0.5;

        Caller.keepLogs(false);
        for (String prompt:prompts) messages.add(new Message("user", prompt));
        SupportGUI.gpt = gpt;
        startServer();
        startSupportGUI();
    }

    public SupportGUI(GPT gpt) {
        this.modelName = "gpt-3.5-turbo";
        this.temperature = 0.7;

        Caller.keepLogs(false);
        SupportGUI.gpt = gpt;
        startServer();
        startSupportGUI();
    }

    public void startSupportGUI() {
        try {
            //Font
            String fontfamily = "Arial, sans-serif";
            Font font = new Font(fontfamily, Font.PLAIN, 15);

            final JFrame supportPanel = new JFrame("Pickleib Support");
            supportPanel.getContentPane().setLayout(null);
            supportPanel.setSize(700, 500);
            supportPanel.setResizable(false);
            supportPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Chat panel
            chatOverviewPanel.setBounds(25, 25, 650, 320);
            chatOverviewPanel.setFont(font);
            chatOverviewPanel.setMargin(new Insets(6, 6, 6, 6));
            chatOverviewPanel.setEditable(false);
            JScrollPane chatOverviewScrollPanel = new JScrollPane(chatOverviewPanel);
            chatOverviewScrollPanel.setBounds(25, 25, 650, 320);

            chatOverviewPanel.setContentType("text/html");
            chatOverviewPanel.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

            // Field message user input
            messageInputPanel.setBounds(0, 350, 400, 50);
            messageInputPanel.setFont(font);
            messageInputPanel.setMargin(new Insets(6, 6, 6, 6));
            final JScrollPane messageInputScrollPanel = new JScrollPane(messageInputPanel);
            messageInputScrollPanel.setBounds(25, 350, 650, 50);

            // Send button
            final JButton sendButton = new JButton("Send");
            sendButton.setFont(font);
            sendButton.setBounds(575, 410, 100, 35);

            messageInputPanel.addKeyListener(new KeyAdapter() {
                // Send message on Enter
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        sendMessage();

                    // Get last message typed
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        String currentMessage = messageInputPanel.getText().trim();
                        messageInputPanel.setText(oldMsg);
                        oldMsg = currentMessage;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        String currentMessage = messageInputPanel.getText().trim();
                        messageInputPanel.setText(oldMsg);
                        oldMsg = currentMessage;
                    }
                }
            });

            // Send button click action
            sendButton.addActionListener(ae -> sendMessage());

            // Chat overview background color
            chatOverviewPanel.setBackground(Color.LIGHT_GRAY);

            supportPanel.add(chatOverviewScrollPanel);
            supportPanel.setVisible(true);

            // Chat panel initial message
            appendToPane(chatOverviewPanel,
                    "<b>Welcome To Pickleib Support, please ask your questions!</b> "
            );

            // Default server specifications
            name = "User";
            String port = "12345";
            serverName = "localhost";
            PORT = Integer.parseInt(port);

            server = new Socket(serverName, PORT);

            input = new BufferedReader(new InputStreamReader(server.getInputStream()));
            output = new PrintWriter(server.getOutputStream(), true);

            // Create new read thread
            read = new Read();
            read.start();
            supportPanel.add(sendButton);
            supportPanel.add(messageInputScrollPanel);
            supportPanel.revalidate();
            supportPanel.repaint();
            chatOverviewPanel.setBackground(Color.WHITE);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage() {
        try {
            String message = messageInputPanel.getText().trim();
            if (message.equals("")) return;
            oldMsg = message;
            output.println("<b><span style='color:#3079ab'>User: </span></b>" + message);
            messages.add(new Message("user", message));
            messageInputPanel.requestFocus();
            messageInputPanel.setText(null);
            gptResponse();
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    public void gptResponse() {
        try {
            MessageResponse messageResponse;
            if (messages.size()!=0)
                messageResponse = gpt.sendMessage(new MessageModel(modelName, messages, temperature));
            else
                messageResponse = gpt.sendMessage(
                        new MessageModel(
                                modelName,
                                List.of(new Message("user", "Hello!")),
                                temperature
                        )
                );
            messages.add(messageResponse.getChoices().get(0).getMessage());
            String message = messageResponse.getChoices().get(0).getMessage().getContent();
            output.println("<b><span style='color:#4d7358'>Pickleib: </span></b>" + message);
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
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    class Read extends Thread {
        public void run() {
            String message;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    message = input.readLine();
                    if(message != null) appendToPane(chatOverviewPanel, message);
                }
                catch (IOException ex) {
                    System.err.println("Failed to parse incoming message");
                }
            }
        }
    }
}
