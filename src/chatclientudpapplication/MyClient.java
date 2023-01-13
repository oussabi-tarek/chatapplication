package chatclientudpapplication;

import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
// ce classe permet a  un client UDP d envoyer des messages au serveur UDP et recevoir des messages du serveur UDP
public class MyClient extends JFrame{
    // iD c est l identifiant du client , il est unique ,clientID c est le nom du client qu il a choisi iD pour lui envoyer des messages
    private  String iD,clientID="";
    private DatagramSocket s;
    InetAddress address;
    private  DatagramPacket datagramPacket;
    // chemin du fichier de reception, si le client a recevu un fichier il sera stocke dans ce fichier
    String FILE_TO_RECEIVED = "C:\\Users\\DELL\\Downloads\\L.";
    File myfile;
    // liste des clients connectes au serveur
    DefaultListModel dlm;


    // initialisation des composants graphiques  et de la socket UDP du client  et de l'inetadresse du serveur et l ID du client
    public MyClient(String id, DatagramSocket s) {
        this.s=s;
        this.iD=id;
        try{
            address = InetAddress.getByName("localhost");
            initComponents();
            dlm=new DefaultListModel();
            UL.setModel(dlm);
            // affichage de l ID du client
            idlabel.setText(iD);
            // lancer le thread qui permet de recevoir les messages du serveur
            new Read().start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
     // cette classe interne qui est un Thread permet de lire les messages envoyés par le serveur UDP et de les afficher dans la liste UL
    class Read extends  Thread{
        public void run(){
            while(true){
                try{
                       byte b[]=new byte[20002];
                       datagramPacket=new DatagramPacket(b,b.length);
                       s.receive(datagramPacket);
                       String m=new String(datagramPacket.getData(),0,datagramPacket.getLength());
                   // System.out.println("I am "+iD+" and I received "+m);

                    /* cette condition juste parfois quand un client envoie un message a un autre client et le client qui recoit le message apres reception il a quitté le chat
                        donc le serveur va informer tous les clients que ce client a quitté le chat et le client qui a envoyé le message va recevoir un message de ce type mais parfois
                        le client qui lui envoie le message recoie un autre message que les autres ce message contient "envoie a un client" c est l expression qu on utilise pour dire au
                        server que je vais envoyer un message a un client specifique , donc j avais fais cette condition pour qu a chaque fois que le client recoit un message de ce type il
                        devra la transformer en un message "idclient A quitté le chat"  (idclient on le recuppere du message qu on recoit qui contient cette expression "envoie a un client").
                      */
                    if (m.contains("envoie a un client")){
                        m=m.substring(18);
                        StringTokenizer st=new StringTokenizer(m,":");
                        String idClient=st.nextToken();
                        m=idClient+" A quitté le chat";
                    }
                    // si le message contient le mot "a t envoye un fichier" c'est que le serveur a envoyé un fichier à un client en particulier qui est moi
                    if(m.contains("a t envoye un fichier")){
                        m=m.substring(21);
                        StringTokenizer st = new StringTokenizer(m, ":");
                        // extraire l'ID du client qui a envoyé le fichier et l extension du fichier et la longueur du fichier
                        String idclient= st.nextToken();
                        String extension =st.nextToken();
                        String sizefile =st.nextToken();

                        System.out.println("idclient : "+idclient);
                        System.out.println("extension : "+extension);
                        System.out.println("sizefile : "+sizefile);
                        // le fichier sera stocké dans le chemin FILE_TO_RECEIVED
                        FILE_TO_RECEIVED=FILE_TO_RECEIVED+extension;
                        byte[] tampon = new byte[Integer.parseInt(sizefile)];
                        datagramPacket = new DatagramPacket(tampon, tampon.length);
                        s.receive(datagramPacket);
                        File file=new File(FILE_TO_RECEIVED);
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        bufferedOutputStream.write(tampon, 0 , tampon.length );
                        bufferedOutputStream.flush();
                        // afficher ce message dans l interface graphique
                        msgBox.append("<"+idclient+" a t envoye un fichier>\n");
                    }
                    // c est que le client expediteur a envoye un fichier a  tous les clients
                    else if(m.contains("envoye un fichier a tous")){
                        m=m.substring(24);
                        StringTokenizer st = new StringTokenizer(m, ":");
                        // extraire l'ID du client qui a envoyé le fichier et l extension du fichier et la longueur du fichier
                        String idclient= st.nextToken();
                        String extension =st.nextToken();
                        String sizefile =st.nextToken();

                        // le fichier sera stocké dans le chemin FILE_TO_RECEIVED
                        FILE_TO_RECEIVED=FILE_TO_RECEIVED+extension;
                        byte[] tampon = new byte[Integer.parseInt(sizefile)];
                        datagramPacket = new DatagramPacket(tampon, tampon.length);
                        s.receive(datagramPacket);
                        File file=new File(FILE_TO_RECEIVED);
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        bufferedOutputStream.write(tampon, 0 , tampon.length );
                        bufferedOutputStream.flush();
                        // afficher ce message dans l interface graphique
                        msgBox.append("<"+idclient+" a envoye a tous  un fichier>\n");
                    }
                    else{
                        // le cas ou le message est une liste des clients connectés au serveur UDP  on va afficher cette liste dans la liste UL
                        if(m.contains("listprepare")){
                            m=m.substring(11);
                            if(m.length()>0 ) {
                                System.out.println("hhhhhhhhh");
                                dlm.clear();

                                StringTokenizer st = new StringTokenizer(m, ",");
                                while (st.hasMoreTokens()) {
                                    String u = st.nextToken();
                                    System.out.println("voila : " + u);
                                    while (u.contains("listprepare")) {
                                        u = u.substring(11);
                                    }
                                    if (!iD.equals(u)) {
                                        dlm.addElement(u);
                                    }
                                }
                            }

                        }
                        // la cas ou le message est une annonce par le serveur au client pour dire que le client x a quitte le chat
                        else if(!m.contains("envoie a un client")){
                            // afficher le message dans l interface graphique
                            msgBox.append(""+ m + "\n");
                        }
                    }
                }catch(Exception ex){
                    break;
                }
            }
        }
    }


    // initialisation des composants graphiques
    private void initComponents() {

        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        idlabel = new JLabel();
        selectall = new JButton();
        jScrollPane1 = new JScrollPane();
        msgBox = new JTextArea();
        jSeparator1 = new JSeparator();
        sendButton = new JButton();
        jTextField1 = new JTextField();
        jLabel3 = new JLabel();
        jScrollPane3 = new JScrollPane();
        UL = new JList<>();
        fileupload = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Bonjour :");

        idlabel.setText("................................................");

        selectall.setBackground(new java.awt.Color(242, 242, 242));
        selectall.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        selectall.setText("Selectionner tout");
        selectall.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));
        selectall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectallActionPerformed(evt);
            }
        });

        msgBox.setColumns(20);
        msgBox.setRows(5);
        jScrollPane1.setViewportView(msgBox);

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        sendButton.setBackground(new java.awt.Color(204, 204, 204));
        sendButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        sendButton.setText("Envoyer");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Message");

        UL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ULMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(UL);

        fileupload.setText("Upload File");
        fileupload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileuploadActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(fileupload)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sendButton, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(21, 21, 21))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(35, 35, 35)
                                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(idlabel, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 336, GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)))
                                                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                                                .addGap(16, 16, 16)
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(selectall, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE))))
                                .addGap(19, 19, 19))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(idlabel, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(selectall))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jSeparator1)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(fileupload, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel3)
                                                .addComponent(sendButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(11, 11, 11))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 592, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 425, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    // si le bouton select all est coché, on coche tous les éléments de la liste c est à dire l attribut clientID va etre "" pour dire que c est un message global
    private void selectallActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        clientID="";
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }
    // c est le bouton pour envoyer un message à un client ou à tous les clients connectés au serveur en fonction de la valeur de l attribut clientID
    // et on verifiar si le message est vide ou non avant de l envoyer au serveur pour verifier que le client veut partager un fichier ou non
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here
        try{
            // affectation du text ecrit dans le champ de texte à la variable msg
            String msg=jTextField1.getText(),mm=msg;
            // on affecte clientID choisi dans la liste à la variable CI
            String CI=clientID;
            // si le message est vide on verifie si le client veut partager un fichier ou non
            if(msg.isEmpty() && myfile!=null){
                // lire le fichier choisi par le client
                byte[] tampon = new byte[(int)myfile.length()];
                FileInputStream fileInputStream = new FileInputStream(myfile);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                bufferedInputStream.read(tampon, 0, tampon.length);
                // verifie si le client veut envoyer a tous les clients ou a un seul client
                if(!CI.isEmpty()){
                    // recuperer le nom du fichier choisi par le client
                    String fileName = myfile.getName();
                    // recuperer l extension du fichier choisi par le client
                    int lastDotIndex = fileName.lastIndexOf('.');
                    String extension = fileName.substring(lastDotIndex + 1);
                    // definir le message à envoyer au serveur
                    msg="envoie fichier client"+myfile.length()+":"+CI+":"+extension;
                    // envoyer le message au serveur qui contient la longueur du fichier, l id du client destinataire et l extension du fichier
                    datagramPacket= new DatagramPacket(msg.getBytes(), msg.length(), address, 2020);
                    s.send(datagramPacket);
                    // envoyer le fichier au serveur
                    datagramPacket= new DatagramPacket(tampon, tampon.length, address, 2020);
                    s.send(datagramPacket);

                    System.out.println("Envoi du fichier "+myfile.length());
                    System.out.println("Fait.");
                    // afficher le message dans la zone de texte de l interface
                    msgBox.append("<tu as envoye a "+CI+" un fichier>\n");
                    clientID="";
                }
                // le client veut envoyer un fichier a tous les clients
                else{
                    // recuperer le nom du fichier choisi par le client
                    String fileName = myfile.getName();
                    // ectraction de l extension du fichier
                    int lastDotIndex = fileName.lastIndexOf('.');
                    String extension = fileName.substring(lastDotIndex + 1);
                    // definir le message à envoyer au serveur
                    msg="envoiefile vers tous"+myfile.length()+":"+extension;
                    // envoyer le message au serveur qui contient la longueur du fichier et l extension du fichier
                    datagramPacket= new DatagramPacket(msg.getBytes(), msg.length(), address, 2020);
                    s.send(datagramPacket);
                    // envoyer le fichier au serveur
                    datagramPacket= new DatagramPacket(tampon, tampon.length, address, 2020);
                    s.send(datagramPacket);
                    System.out.println("Envoi du fichier "+myfile.length());
                    System.out.println("Fait.");
                    // afficher un message dans la zone de texte pour indiquer que le fichier a été envoyé
                    msgBox.append("<tu as envoye a tous un fichier>\n");
                }
            }
            // si le client veut envoyer juste un message normale
            else{
                // le client veut envoyer un message à un client en particulier
                if(!clientID.isEmpty()){
                    msg="envoie a un client"+CI+":"+mm+":"+iD;
                    jTextField1.setText("");
                    byte[] tampon = msg.getBytes();
                    datagramPacket = new DatagramPacket(tampon, tampon.length, address, 2020);
                    s.send(datagramPacket);
                    msgBox.append("<tu as envoye a "+CI+" >"+mm+"\n");
                    clientID="";
                }
                // le client veut envoyer a tous un message normale
                else{
                    jTextField1.setText("");
                    byte[] tampon = msg.getBytes();
                    datagramPacket = new DatagramPacket(tampon, tampon.length, address, 2020);
                    s.send(datagramPacket);
                    msgBox.append("<tu as envoye a tous>"+mm+"\n");
                }
            }
        }catch(Exception ex){
            System.out.println(ex);
        }
    }

    // on affecte le client choisi de la liste à l attribut clientID pour pouvoir envoyer un message à un client specifique
    private void ULMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
        clientID=(String)UL.getSelectedValue();
    }

    // si le client ferme la fenetre on envoie un message au serveur pour le supprimer de la liste des clients connectés et informer les autres clients
    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        // TODO add your handling code here:
        String i="formWindowClosing:"+iD;
        try{
            byte[] tampon = i.getBytes();
            datagramPacket = new DatagramPacket(tampon, tampon.length, address, 2020);
            s.send(datagramPacket);
            this.dispose();
        }catch(Exception ex){
             ex.printStackTrace();
        }
    }

    // on verifie si le client veut partager un fichier ou non et on affecte le fichier choisi à l attribut myfile
    private void fileuploadActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        // Show the file chooser and get the user's selection
        int result = fileChooser.showOpenDialog(this);
        // If the user clicked the "Open" button, set the selected image as the icon for the label
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            this.myfile=file;
        }
    }

    private JList<String> UL;
    private JButton fileupload;
    private JLabel idlabel;
    private JLabel jLabel1;
    private JLabel jLabel3;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane3;
    private JSeparator jSeparator1;
    private JTextField jTextField1;
    private JTextArea msgBox;
    private JButton selectall;
    private JButton sendButton;
}
