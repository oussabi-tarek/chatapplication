package chatclientudpapplication;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
/*    ce class fait la connexion et l inscription des clients dans l application c est l interface avec laquelle
 les clients peuvent acceder a l interface */
public class ClientRegister extends JFrame{
    private DatagramSocket socket;
    private  InetAddress address ;
    private  DatagramPacket packet;
    // constructeur pour initialiser les composants de l interface graphique et l inetadresse  du serveur
    public ClientRegister() throws UnknownHostException {
        initComponents();
        address=InetAddress.getByName("localhost");
    }
    // initialisation des composants de l interface graphique
    private void initComponents() {

        jPanel1 = new JPanel();
        idText = new JTextField();
        jButton1 = new JButton();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jPasswordField1 = new JPasswordField();
        jLabel4 = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        idText.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton1.setBackground(new java.awt.Color(204, 204, 204));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setText("Connexion");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Inscription et connexion du Client");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Ton ID :");

        jPasswordField1.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Password :");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(266, 266, 266)
                                                .addComponent(jButton1))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(88, 88, 88)
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel4))
                                                .addGap(28, 28, 28)
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jPasswordField1, GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(idText, GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(198, 198, 198)
                                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 257, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(185, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(idText, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jPasswordField1, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
                                .addGap(17, 17, 17)
                                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(50, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {
    }
    // ici c est le bouton connexion qui permet de se connecter a la base de donnee et de verifier si l id et le mot de passe sont corrects
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        try{
            int inscrit = 0;
            // on recupere l id et le mot de passe
            String idclient=idText.getText();
            String password=new String(jPasswordField1.getPassword());
            DatabaseConnection db=new DatabaseConnection();
            String query="SELECT * FROM client";
            ResultSet rs=db.getStatement().executeQuery(query);
            // on verifie si l id et le mot de passe sont corrects
            while(rs.next())
            {
                if(idclient.equals(rs.getString(2))){
                    // si l id est correct on verifie le mot de passe
                    if(!password.equals(rs.getString(3))){
                        inscrit=2;
                        // si le mot de passe est incorrect on affiche un message d erreur
                        JOptionPane.showMessageDialog(null, "Mot de passe incorrect");
                        jPasswordField1.setText("");
                        break;
                    }else {
                        inscrit = 1;
                        break;
                    }
                }
            }
            // si l id et le mot de passe sont corrects on affiche un message de connexion reussie
            if(inscrit==1){
                JOptionPane.showMessageDialog(null,"connexion réussie");
                String id=idclient+": register";
                socket = new DatagramSocket();
                byte[] buf = id.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, 2020);
                socket.send(packet);
                new MyClient(idclient,socket).setVisible(true);
                this.dispose();
            }
            // si le nom d utilisateur n existe pas on ajoute le client a la base de donnee avec son mot de passe et on affcihe un message de "Inscription reussie"
            else if(inscrit==0){
                String sql="INSERT INTO Client (nom,password) VALUES ('"+idclient+"','"+password+"')";
                db.getStatement().executeUpdate(sql);
                JOptionPane.showMessageDialog(null,"Inscription réussie");
                String id=idclient+": register";
                socket = new DatagramSocket();
                byte[] buf = id.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, 2020);
                socket.send(packet);
                new MyClient(idclient,socket).setVisible(true);
                this.dispose();
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ClientRegister().setVisible(true);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Variables declaration - do not modify
    private JTextField idText;
    private JButton jButton1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JPanel jPanel1;
    private JPasswordField jPasswordField1;
}
