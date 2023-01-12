package chatudpserverapplication;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

// Server class permettant de gérer les clients
// utilisation  de la 2eme methode pour creer un Thread car la classe deja il herite du JFrame
public class Server extends javax.swing.JFrame implements Runnable {

    public final static int PORT = 2020;
    // fichier de reception si le client a envoyer un fichier on stocke ce fichier dans ce path afin de le transferer apres a un autre client ou a tous les clients
    String FILE_TO_RECEIVED = "C:\\Users\\DELL\\Downloads\\k.";
    private DatagramSocket socket;
    DatagramPacket packet;
    InetAddress clientAddress;
    // liste des clients connectés avec leurs adresses ip et leurs ports
    HashMap clientColl=new HashMap();

    // initialisation de la fenetre de l'application serveur et de la socket
    public Server() {
        try{
            initComponents();
            socket = new DatagramSocket(PORT);
            this.sStatus.setText("Server Started");
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for request");
                byte[] buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                clientAddress = packet.getAddress();
                int client_port = packet.getPort();
                 // l address ip du client et son port sont stockés dans la liste des clients connectés comme value du key idclient
                String id = clientAddress.toString() + "|" + client_port;

                // si le client a envoyé un message qui ne contient pas "register" c est a dire qu il a envoyé un message normal pas une inscription ou bien une connexion
                if(!message.contains("register")) {
                   if(message.contains("envoie a un client")){
                      message=message.substring(18);
                      StringTokenizer st=new StringTokenizer(message,":");
                        String idClient=st.nextToken();
                        String msg=st.nextToken();
                        String sender=st.nextToken();
                        msg="< "+sender+" a t envoye> "+msg;
                       Set<String> k = clientColl.keySet();
                          for(String key : k){
                              String value = clientColl.get(key).toString();
                            if(key.equals(idClient)){
                                 String[] s=value.split("\\|");
                                clientAddress = InetAddress.getByName(s[0].substring(1));
                                 byte[] buf = msg.getBytes();
                                 packet = new DatagramPacket(buf, buf.length, clientAddress, Integer.parseInt(s[1]));
                                 socket.send(packet);
                            }
                          }
                   }else if(message.contains("envoie fichier client")){
                       // extraire extemsion,recepteurclient et longueur du fichier a envoyer du message recu
                        System.out.println("envoie fichier client here");
                        message=message.substring(21);
                        StringTokenizer st=new StringTokenizer(message,":");
                        String sizefile=st.nextToken();
                        String idClient=st.nextToken();
                        String extension=st.nextToken();
                        // receive file
                       FILE_TO_RECEIVED=FILE_TO_RECEIVED+extension;
                       byte[] tampon = new byte[Integer.parseInt(sizefile)];
                       packet = new DatagramPacket(tampon, tampon.length);
                       socket.receive(packet);
                       File file=new File(FILE_TO_RECEIVED);
                       FileOutputStream fileOutputStream = new FileOutputStream(file);
                       BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                       bufferedOutputStream.write(tampon, 0 , tampon.length );
                       bufferedOutputStream.flush();
                       Thread.sleep(2000);
                       // trouver le sender
                       String sender="";
                       Set k = clientColl.keySet();
                       for (Object key : k) {
                            String value = clientColl.get(key).toString();
                            if(value.equals(id)){
                                 sender=key.toString();
                            }
                       }
                       // envoyer file to client
                            for(Object key : k){
                                String value = clientColl.get(key).toString();
                                if(key.equals(idClient)){
                                    String[] s=value.split("\\|");
                                    clientAddress = InetAddress.getByName(s[0].substring(1));
                                    String msg="a t envoye un fichier:"+sender+":"+extension+":"+sizefile;
                                    packet = new DatagramPacket(msg.getBytes(),msg.length() ,clientAddress, Integer.parseInt(s[1]));
                                    socket.send(packet);
                                    File myFile = new File(FILE_TO_RECEIVED);
                                    byte[] mybytearray = new byte[(int)myFile.length()];
                                    FileInputStream fileInputStream = new FileInputStream(myFile);
                                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                                    bufferedInputStream.read(mybytearray, 0, mybytearray.length);
                                    packet = new DatagramPacket(mybytearray,mybytearray.length, clientAddress, Integer.parseInt(s[1]));
                                    socket.send(packet);
                                    System.out.println("File sent to client! avec succe");
                                }
                            }
                   }else if(message.contains("envoiefile vers tous")){
                       // extraire extemsion et longueur du fichier a envoyer du message recu
                       message=message.substring(20);
                       StringTokenizer st=new StringTokenizer(message,":");
                       String sizefile=st.nextToken();
                       String extension=st.nextToken();
                          // receive file
                       FILE_TO_RECEIVED=FILE_TO_RECEIVED+extension;
                       System.out.println("sizefile: "+sizefile);
                       System.out.println("extension: "+extension);
                       byte[] tampon = new byte[Integer.parseInt(sizefile)];
                       packet = new DatagramPacket(tampon, tampon.length);
                       socket.receive(packet);
                       File file=new File(FILE_TO_RECEIVED);
                       FileOutputStream fileOutputStream = new FileOutputStream(file);
                       BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                       bufferedOutputStream.write(tampon, 0 , tampon.length );
                       bufferedOutputStream.flush();
                       Thread.sleep(2000);
                       // trouver le sender
                       String sender="";
                       Set k = clientColl.keySet();
                       for (Object key : k) {
                           String value = clientColl.get(key).toString();
                           if(value.equals(id)){
                               sender=key.toString();
                           }
                       }
                          // envoyer file vers tous sauf sender
                       for (Object key : k) {
                           String value = clientColl.get(key).toString();
                           if(!value.equals(id)){
                               String[] s=value.split("\\|");
                               clientAddress = InetAddress.getByName(s[0].substring(1));
                               String msg="envoye un fichier a tous:"+sender+":"+extension+":"+sizefile;
                               packet = new DatagramPacket(msg.getBytes(),msg.length() , clientAddress, Integer.parseInt(s[1]));
                               socket.send(packet);

                               File myFile = new File(FILE_TO_RECEIVED);
                               byte[] mybytearray = new byte[(int)myFile.length()];
                               FileInputStream fileInputStream = new FileInputStream(myFile);
                               BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                               bufferedInputStream.read(mybytearray, 0, mybytearray.length);
                               packet = new DatagramPacket(mybytearray,mybytearray.length, clientAddress, Integer.parseInt(s[1]));
                               socket.send(packet);
                           }
                       }
                   }
                   else if(message.contains("formWindowClosing")){
                       // extraire id du client qui a ferme la fenetre
                       StringTokenizer st=new StringTokenizer(message,":");
                       String s=st.nextToken();
                       String idClient=st.nextToken();
                       System.out.println("Client ID: " + idClient);
                       // supprimer le client de la liste
                       clientColl.remove(idClient);
                       msgBox.append(idClient+" removed! \n");
                       System.out.println("my list after remove: "+clientColl.toString());
                       Set<String> k=clientColl.keySet();
                       Iterator itr=k.iterator();
                       // informer les autres clients de la suppression du client
                       while(itr.hasNext()){
                           String key=(String)itr.next();
                               try{
                                      String value = clientColl.get(key).toString();
                                      String[] s1=value.split("\\|");
                                      clientAddress = InetAddress.getByName(s1[0].substring(1));
                                      String msg=idClient+" A quitté le chat";
                                      byte[] buf = msg.getBytes();
                                      packet = new DatagramPacket(buf, buf.length, clientAddress, Integer.parseInt(s1[1]));
                                      socket.send(packet);
                                      System.out.println("I sent < "+msg+"> to "+key);
                                      new PrepareClientList().start();
                               }catch(Exception ex){
                                   clientColl.remove(key);
                                   msgBox.append(key+" removed");
                                   new PrepareClientList().start();
                               }
                       }
                   }
                   else {
                       // envoyer le message a tous les clients
                       String sourceclient="";
                       Set<String> k = clientColl.keySet();
                       Iterator itr = k.iterator();
                       // trouver sender du message
                       while (itr.hasNext()) {
                           String key = (String) itr.next();
                           String value = (String) clientColl.get(key);
                           if (value.equalsIgnoreCase(id)) {
                               sourceclient= key;
                           }
                       }
                       itr = k.iterator();
                       String msgTosend="< "+sourceclient+" à tous >"+message;
                       // envoyer le message a tous les clients sauf sender
                       while (itr.hasNext()) {
                           String key = (String) itr.next();
                           if (!key.equalsIgnoreCase(sourceclient)) {
                               String value = (String) clientColl.get(key);
                               String[] arr = value.split("\\|");
                               clientAddress = InetAddress.getByName(arr[0].substring(1));
                               int port = Integer.parseInt(arr[1]);
                               byte[] tampon = msgTosend.getBytes();
                               packet = new DatagramPacket(tampon, tampon.length, clientAddress, port);
                               socket.send(packet);
                           }
                       }
                   }
                }
                else {
                    // registration du client  dans la liste
                    StringTokenizer st = new StringTokenizer(message, ":");
                    String idclient = st.nextToken();
                    if (!clientColl.containsKey(idclient)) {
                        clientColl.put(idclient, id);
                        new PrepareClientList().start();
                        System.out.println("New Client Registered");
                        msgBox.append(idclient+" Join! \n");
                    } else {
                        System.out.println("Client Already Registered");
                    }
                }
                message = "";

            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    // preparer la liste des clients connectes et l'envoyer a tous les clients
    class PrepareClientList extends Thread{
        public void run(){
            try{
                String ids="";
                Set k=clientColl.keySet();
                Iterator itr=k.iterator();
                while(itr.hasNext()){
                    String key=(String)itr.next();
                    ids+=key+",";
                }

                itr=k.iterator();
                String list="";
                while(itr.hasNext()){
                    String key=(String)itr.next();
                    try{
                        String value=(String)clientColl.get(key);
                        String[] arr=value.split("\\|");
                        clientAddress=InetAddress.getByName(arr[0].substring(1));
                        int port=Integer.parseInt(arr[1]);
                        list="listprepare";
                        ids=list+ids;
                        byte[] tampon=ids.getBytes();
                        DatagramPacket packet=new DatagramPacket(tampon, tampon.length, clientAddress, port);
                        socket.send(packet);
                        System.out.println("Client List Sent");
                        list="";
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgBox = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        sStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MyServer");

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        msgBox.setColumns(20);
        msgBox.setRows(5);
        jScrollPane1.setViewportView(msgBox);

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel1.setText("Server Status :");

        sStatus.setText(".........................................................");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(18, 18, 18)
                                                .addComponent(sStatus)))
                                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(sStatus))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Server server=new Server();
                server.setVisible(true);
                Thread t=new Thread(server);
                t.start();
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea msgBox;
    private javax.swing.JLabel sStatus;

}
