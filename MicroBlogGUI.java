import java.awt.Component;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.naming.LimitExceededException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

/**
 *
 * @author Samuele Bonini
 */
public class MicroBlogGUI extends javax.swing.JPanel {
    class LikeButtonCellRenderer extends DefaultListCellRenderer {
        public static final String HTML_1 = "<html><body><div style='width: 85px; text-align: center; margin: 2px; padding: 3px; border:1px solid gray; background-color:#303030; color:white; padding-left: 4px; padding-right: 4px' id='";
        public static final String HTML_2 = "'>";
        public static final String HTML_3 = "</div></body></html>";
      
        public LikeButtonCellRenderer() {
        }
      
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
          String text = HTML_1 + value.toString()
              + HTML_2 + "👍 Like" + HTML_3;
          return super.getListCellRendererComponent(list, text, index, isSelected,
              cellHasFocus);
        }
      
    }
    
    class PostCellRenderer extends DefaultListCellRenderer {
        public static final String HTML_1 = "<html><body><div style='border:1px solid #c0c0c0; font-weight: 100; background-color: #f5f5f5; padding: 3px; margin: 2px; border-radius: 5px;'>";
        public static final String HTML_3 = "</div></body></html>";
      
        public PostCellRenderer() {
        }
      
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
          String text = HTML_1 + value.toString() + HTML_3;
          return super.getListCellRendererComponent(list, text, index, isSelected,
              cellHasFocus);
        }
      
    }
    
    private MicroBlog network;
    private DefaultListModel postsModel;
    private DefaultListModel usersModel;
    private DefaultListModel influencersModel;
    private DefaultListModel idsModel;
    private LinkedList<Integer> postIds;
    private LikeButtonCellRenderer likeCellRenderer;
    private PostCellRenderer postCellRenderer;
    private LinkedList<Integer> storedIds;

    public MicroBlogGUI() {
        network = new MicroBlog();
        postsModel = new DefaultListModel();
        usersModel = new DefaultListModel();
        idsModel = new DefaultListModel();
        influencersModel = new DefaultListModel();
        postIds = new LinkedList<Integer>();
        storedIds = new LinkedList<Integer>();
        likeCellRenderer = new LikeButtonCellRenderer();
        postCellRenderer = new PostCellRenderer();
        initComponents();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MicroBlogGUI panel = new MicroBlogGUI();
                JFrame frame = new JFrame("Frame");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                frame.add(panel);
                frame.setVisible(true);
                frame.setTitle("MicroBlog");
                frame.setSize(800, 520);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        filterButtonGroup = new javax.swing.ButtonGroup();
        submitButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        postContentTextArea = new javax.swing.JTextArea();
        usernameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        postsList = new javax.swing.JList<Post>(postsModel);
        postsList.setCellRenderer(postCellRenderer);
        postsLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        usersList = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        influencersList = new javax.swing.JList<>();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        likeButtonsList = new javax.swing.JList<>();
        likeButtonsList.setCellRenderer(likeCellRenderer);
        jLabel5 = new javax.swing.JLabel();
        allPostsRadioButton = new javax.swing.JRadioButton();
        filterAuthorRadioButton = new javax.swing.JRadioButton();
        filterKeywordsRadioButton = new javax.swing.JRadioButton();

        filterButtonGroup.add(allPostsRadioButton);
        filterButtonGroup.add(filterAuthorRadioButton);
        filterButtonGroup.add(filterKeywordsRadioButton);

        submitButton.setText("Crea post");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        postContentTextArea.setColumns(20);
        postContentTextArea.setLineWrap(true);
        postContentTextArea.setRows(5);
        jScrollPane1.setViewportView(postContentTextArea);

        jLabel1.setText("Nome utente");

        jLabel2.setText("Contenuto del post");

        postsList.setModel(postsModel);

        jScrollPane2.setViewportView(postsList);

        postsLabel.setText("Post della rete");

        usersList.setModel(usersModel);
        usersList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usersListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(usersList);

        jLabel3.setText("Utenti");

        influencersList.setModel(influencersModel);
        jScrollPane4.setViewportView(influencersList);

        jLabel4.setText("Influencer");

        likeButtonsList.setModel(idsModel);
        likeButtonsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                likeButtonsListMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(likeButtonsList);

        jLabel5.setFont(new java.awt.Font("Ubuntu", 2, 11)); // NOI18N
        jLabel5.setText("(Clicca su un utente per vedere chi segue)");

        allPostsRadioButton.setText("Mostra tutti i post");
        allPostsRadioButton.setSelected(true);
        allPostsRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                allPostsRadioButtonItemStateChanged(evt);
            }
        });

        filterAuthorRadioButton.setText("Filtra per autore");
        filterAuthorRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterAuthorRadioButtonItemStateChanged(evt);
            }
        });


        filterKeywordsRadioButton.setText("Filtra per parole chiave");
        filterKeywordsRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterKeywordsRadioButtonItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1)
                        .addComponent(submitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(usernameTextField))
                    .addComponent(jLabel2)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(allPostsRadioButton)
                    .addComponent(filterAuthorRadioButton)
                    .addComponent(filterKeywordsRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(postsLabel)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(3, 3, 3)
                                .addComponent(jLabel5)
                                .addGap(38, 38, 38)
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(240, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(postsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                                    .addComponent(jScrollPane5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(submitButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(allPostsRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filterAuthorRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filterKeywordsRadioButton)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>                        

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        try {
            int newId = network.createPost(usernameTextField.getText(), postContentTextArea.getText());
            usernameTextField.setText("");
            postContentTextArea.setText("");
            postIds.add(newId);
            storedIds.add(newId);
            updateLists();
            showAllIds();
        } catch(LimitExceededException exc) {
            JOptionPane.showMessageDialog(null,
                "La lunghezza massima del post è di 140 caratteri.");
        } catch(IllegalArgumentException exc) {
            JOptionPane.showMessageDialog(null,
                "Il nome utente e il contenuto del post non possono essere vuoti o composti da soli spazi.");
        }
    }

    private void likeButtonsListMouseClicked(java.awt.event.MouseEvent evt) {                                             
        String likingUser = JOptionPane.showInputDialog("Nome dell'utente che mette like: ");
        try {
            network.likePost(this.likeButtonsList.getSelectedValue(), likingUser);
            JOptionPane.showMessageDialog(null, "Like aggiunto");
            updateLists();
            allPostsRadioButton.setSelected(true);
            showAllIds();
        } catch(IllegalStateException exc) {
            JOptionPane.showMessageDialog(null, "Non puoi mettere like al tuo post");
        } catch(IllegalArgumentException exc) {
            JOptionPane.showMessageDialog(null, "Il nome utente non può essere vuoto o composto da soli spazi");
        }
    }

    private void usersListMouseClicked(java.awt.event.MouseEvent evt) {                                       
        if(this.usersList.getSelectedValue() == null) return;
        String str = "";
        
        Set<String> followers = network.getFollowRelations().get(this.usersList.getSelectedValue());
        for(String follower : followers) {
            str += follower + ", ";
        }
        if(str.length() > 0) {
            str = str.substring(0, str.length() - 2);
            str = this.usersList.getSelectedValue() + " segue: " + str;
        } else str = this.usersList.getSelectedValue() + " non segue alcun utente";
        
        JOptionPane.showMessageDialog(null, str);
    }

    private void filterAuthorRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {                                                         
        JRadioButton source = (JRadioButton) evt.getSource();
                if(source.isSelected()) {
                   String author = JOptionPane.showInputDialog("Mostra i post scritti da: ");
                   try {
                    List<Post> iterator = network.writtenBy(author);
                    postsModel.clear();
                    postsModel.addAll(iterator);
                    alterShownIds(iterator);
                   } catch(IllegalArgumentException exc) {
                       JOptionPane.showMessageDialog(null, "Il nome utente non può essere vuoto o composto da soli spazi");
                       allPostsRadioButton.setSelected(true);
                   }
                }
    }                                                        

    private void allPostsRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {                                                     
            JRadioButton source = (JRadioButton) evt.getSource();
                if(source.isSelected()) {
                    postsModel.clear();
                    postsModel.addAll(network.getAllPosts());
                    showAllIds();
                }
    }                                                    

    private void filterKeywordsRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {                                                           
                JRadioButton source = (JRadioButton) evt.getSource();
                if(source.isSelected()) {
                   String keywordsStr = JOptionPane.showInputDialog("Parole da cercare (separate da virgola): ");
                   try {
                    postsModel.clear();
                    String[] keywordsAsArray = keywordsStr.split(",\\s*");
                    List<String> keywords = new LinkedList<>(Arrays.asList(keywordsAsArray));
                    List<Post> iterator = network.containing(keywords);
                    postsModel.addAll(iterator);
                    alterShownIds(iterator);
                   } catch(IllegalArgumentException exc) {
                       JOptionPane.showMessageDialog(null, "Le parole non possono essere composte da soli spazi");
                       allPostsRadioButton.setSelected(true);
                   }
                }
    }                                                          

    private void updateLists() {
        postsModel.clear();
        postsModel.addAll(network.getAllPosts());

        usersModel.clear();
        usersModel.addAll(network.getMentionedUsers());
        
        influencersModel.clear();
        influencersModel.addAll(network.influencers());
        
        idsModel.clear();
        idsModel.addAll(postIds);
    }
    
    private void alterShownIds(List<Post> shownPosts) {        
        this.postIds.clear();
        postIds.addAll(shownPosts.stream().map(p -> p.getId()).collect(Collectors.toList()));
        
        idsModel.clear();
        idsModel.addAll(postIds);
    }
    
    private void showAllIds() {
        postIds.clear();
        postIds.addAll(storedIds);
        
        idsModel.clear();
        idsModel.addAll(postIds);
    }

    private javax.swing.JRadioButton allPostsRadioButton;
    private javax.swing.JRadioButton filterAuthorRadioButton;
    private javax.swing.ButtonGroup filterButtonGroup;
    private javax.swing.JRadioButton filterKeywordsRadioButton;
    private javax.swing.JList<String> influencersList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JList<Integer> likeButtonsList;
    private javax.swing.JTextArea postContentTextArea;
    private javax.swing.JLabel postsLabel;
    private javax.swing.JList<Post> postsList;
    private javax.swing.JButton submitButton;
    private javax.swing.JTextField usernameTextField;
    private javax.swing.JList<String> usersList;
}
