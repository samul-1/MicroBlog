import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.LimitExceededException;

public class MicroBlog implements SocialNetwork {
    // Struttura dati che associa a un utente l'insieme degli utenti da esso seguiti
    private Map<String, Set<String>> followRelations;

    // Struttura dati che associa a un utente l'insieme dei post scritti da esso
    private Map<String, Set<Post>> postRelations;

    // Struttura dati di supporto ottimizzata per la ricerca dei post
    private Map<Integer, Post> postLookup;

    // Prossimo id unico che verrà associato a un nuovo post
    private int nextId;

    /*
        Abstraction function:
        α(c) = {c.posts.get(i) | 0 ≤ i < c.posts.size()}
        * esistono altre possibilità per la funzione di astrazione; le motivazioni di questa scelta sono discusse all'interno della relazione

        Typical element:
        (
            {user_1, ..., user_n},
            {post_1, ..., post_n},
            f: user ↦ {users} | f(user_1) = {user_i, ..., user_j}, ..., f(user_n) = {user_h, ..., user_k}
        )

        Representation invariant:
        IR(Post) ∧
        ∀ u utente . u ≠ null ∧ u ∉ /^\s+$/ ∧ u.length > 0 ∧ (∀ v utente. v ∈ this.followRelations.get(u) ⟺ (∃ p post . p.getAuthor() = v ∧ u ∈ p.getLikes())) ∧
        ∀ (k, v) ∈ this.postRelations . k = v.getAuthor() ∧
        ∀ (k, v) ∈ this.postLookup . k = v.getId() ∧
        this.postRelations.keySet() = this.followRelations.keySet() ∧
        ∀ p ∈ this.getAllPosts() . this.nextId ≠ p.getId()
    */

    public MicroBlog() {
        this.followRelations = new HashMap<String, Set<String>>();
        this.postRelations = new HashMap<String, Set<Post>>();
        this.postLookup = new HashMap<Integer, Post>();
        this.nextId = 0;
    }

    public MicroBlog(List<Post> posts) throws NullPointerException, LimitExceededException {
        this(); // creo una rete sociale vuota

        // verifica delle pre-condizioni
        if(posts == null) {
            throw new NullPointerException();
        }
        for(Post post : posts) {
            if(post == null) {
                throw new NullPointerException();
            }
        }

        List<Integer> references = new LinkedList<Integer>();
        for(Post post : posts) { // itero i post passati come argomento
            // per ogni post, lo aggiungo alla rete a salvo l'id unico del post generato dalla rete
            int reference = this.createPost(post.getAuthor(), post.getText());
            references.add(reference);
        }

        // l'operazione di aggiunta dei like dev'essere fatta in un secondo ciclo per garantire che le chiavi degli autori di tutti i post
        // siano già stati aggiunti alla struttura dati degli "utenti menzionati" (altrimenti le dipendenze tra utenti non vengono create correttamente)
        for(int i = 0; i < references.size(); i++) {
            int currentReference = references.get(i);
            Post currentPost = posts.get(i);
            for(String like : currentPost.getLikes()) { // per ogni like del post originale, li aggiungo al clone del post generato dalla rete
                // le relazioni di follow vengono automaticamente costruite chiamando likePost per ogni like a ogni post
                this.likePost(currentReference, like);
            }
        }
    }

    /*
        Seguono le implementazioni dei 7 metodi definiti dall'interfaccia
        Le clausole REQUIRES, MODIFIES, THROWS, EFFECTS per questi metodi sono riportate nel file
        dell'interfaccia SocialNetwork.java per maggiore leggibilità
    */
    
    public static Map<String, Set<String>> guessFollowers(List<Post> ps) throws NullPointerException {
        if(ps == null) {
            throw new NullPointerException();
        }
        // istanzio un SocialNetwork contenente i post passati come parametro
        try {
            MicroBlog derivedNetwork = new MicroBlog(ps);
            // restituisco le relazioni di follow generate automaticamente dal costruttore in base ai post passatigli
            return derivedNetwork.getFollowRelations();
        } catch (LimitExceededException exc) {
            return null;
        }
    }

    public List<String> influencers() {
        return MicroBlog.influencers(this.getFollowRelations());
    }

    public static List<String> influencers(Map<String, Set<String>> followers) throws NullPointerException {
        if(followers == null) {
            throw new NullPointerException();
        }

        List<String> influencerList = new LinkedList<String>();
        
        // itero la mappa followers per ottenere i nomi degli utenti, verificando uno per uno se rientrano nei canoni di "influencer"
        for(Map.Entry<String,Set<String>> entry : followers.entrySet()) {
            if(entry.getValue().size() < MicroBlog.getNumerOfFollowers(entry.getKey(), followers)) {
                influencerList.add(entry.getKey());
            }
        }

        return influencerList;

    }

    public Set<String> getMentionedUsers() {
        return MicroBlog.getMentionedUsers(this.getAllPosts());
    }

    public static Set<String> getMentionedUsers(List<Post> ps) throws NullPointerException {
        if(ps == null) {
            throw new NullPointerException();
        }
        for(Post post : ps) {
            if(post == null) {
                throw new NullPointerException();
            }
        }

        Set<String> users = new HashSet<String>();

        for(Post post : ps) {
            users.add(post.getAuthor()); // aggiungo l'autore del post all'insieme di ritorno
        }

        return users;
    }

    public List<Post> writtenBy(String username) throws NullPointerException, IllegalArgumentException {
        return MicroBlog.writtenBy(this.getAllPosts(), username);
    }

    public static List<Post> writtenBy(List<Post> ps, String username) throws NullPointerException, IllegalArgumentException {
        if(username == null) {
            throw new NullPointerException();
        }
        if(username.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        LinkedList<Post> outputList = new LinkedList<Post>();
        
        for(Post post : ps) {
            if(post.getAuthor().compareTo(username) == 0) { // se il post è stato scritto dall'utente username, lo aggiungo alla lista
                outputList.add(post.clone());
            }
        }

        return outputList;
    }

    public List<Post> containing(List<String> words) throws NullPointerException, IllegalArgumentException {
        if(words == null) {
            throw new NullPointerException();
        }
        for(String word : words) {
            if(word == null) {
                throw new NullPointerException();
            }
            if(word.trim().isEmpty()) {
                throw new IllegalArgumentException();
            }
        }

        List<Post> outputList = new LinkedList<Post>();

        for(Map.Entry<Integer,Post> entry : this.postLookup.entrySet()) {
            Post post = entry.getValue();
            boolean valid = true;
            for(String word : words) { // per ogni post, controllo che ciascuna delle parole in words sia contenuta nel testo del post
                //System.out.println(post.getText() + " contains or not " + word);
                if(!containsExactly(post.getText(), word)) {
                   // System.out.println("no");
                    valid = false;
                    break; // se trovo una parola non contenuta, posso uscire dal ciclo più interno
                }
                //System.out.println("yes");

            }
            if(valid) {
                outputList.add(post);
            }
        }
        return outputList;
    }

    // Seguono i metodi non facenti parte dell'interfaccia SocialNetwork

    // REQUIRES: author ≠ null ∧ author ∉ /^\s+$/ ∧ author.length > 0 ∧ text ≠ null ∧ text ∉ /^\s+$/ ∧ text.length > 0
    // THROWS: NullPointerException se author è null ∨ text è null (unchecked exception),
    //         IllegalArgumentException se author.length = 0 ∨ author ∈ /^\s+$/ ∨ text.length = 0 ∨ text ∈ /^\s+$/ (unchecked exception),
    // MODIFIES: this
    // EFFECTS: viene aggiunto un nuovo post alla rete sociale, con testo e autore uguali ai parametri forniti
    //          Lo stato di this.postLookup viene modificato aggiungendo la coppia (newPost.getId(), newPost);
    //          se author ∉ this.postRelations.keySet() ⇒ lo stato di this.postRelations viene modificato aggiungendo la coppia (newPost.getAuthor(), {newPost})
    //                                                    e lo stato di this.followRelations viene modificato aggiungendo la coppia (newPost.getAuthor, ∅);
    //          altrimenti ⇒ lo stato di this.postRelations diventa s' dove s' è this.postRelations dove all'elemento this.postRelations.get(author)
    //                       viene sostituito (this.postRelations.get(author) ∪ newPost)
    public int createPost(String author, String text) throws NullPointerException, IllegalArgumentException, LimitExceededException {
        if(author == null || text == null) {
            throw new NullPointerException();
        }
        if(author.trim().isEmpty() || text.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        int newId = this.getUniqueId();
        Post newPost = new Post(newId, author, text);

        // aggiungo il nuovo post alla struttura ottimizzata per la ricerca dei post
        this.postLookup.put(newPost.getId(), newPost);

        if(this.postRelations.containsKey(author)) { // verifico se è il primo post di quest'utente
            this.postRelations.get(author).add(newPost);
        } else {
            // se l'autore non è già presente nella rete, aggiungo il suo nome alla lista utenti delle due strutture interne
            this.postRelations.put(author, new HashSet<Post>());
            this.postRelations.get(author).add(newPost);
            this.followRelations.put(author, new HashSet<String>());
        }

        return newId;
    }

    // REQUIRES: likedByUser ≠ null ∧ likedByUser ∉ /^\s+$/ ∧ likedByUser.length > 0 ∧ postId ≥ 0 ∧ ∃ p post ∈ this . p.getId() = postId
    // THROWS: NullPointerException se likedByUser è null (unchecked exception),
    //         IllegalArgumentException se likedByUser.length = 0 ∨ likedByUser ∈ /^\s+$/ (unchecked exception),
    //         NoSuchElementException se ∄ p post ∈ this . p.getId() = postId (checked exception),
    //         IllegalStateException se getPostById(postId).getAuthor = likedByUser (checked exception)
    // MODIFIES: this
    // EFFECTS: se likedByUser è già presente nella lista dei like del post richiesto, lo stato non viene modificato e la funzione restituisce false,
    //          altrimenti viene aggiunto likedByUser alla lista likes del post, e viene restituito true
    public boolean likePost(int postId, String likedByUser) throws NullPointerException, IllegalArgumentException, NoSuchElementException, IllegalStateException {
        if(likedByUser == null) {
            throw new NullPointerException();
        }
        if(likedByUser.trim().isEmpty() || postId < 0) {
            throw new IllegalArgumentException();
        }

        Post post = this.getPostById(postId);

        if(post == null) {
            throw new NoSuchElementException();
        }

        post.addLike(likedByUser); // aggiungo il like al post -- viene lanciata IllegalStateException se l'autore del post tenta di mettervi like

        // se questo è il primo post di un utente a cui likedByUser ha messo like, quell'utente viene aggiunto alla lista dei seguiti di likedByUser
        if(this.followRelations.get(likedByUser) != null && !this.followRelations.get(likedByUser).contains(post.getAuthor())) {
            this.followRelations.get(likedByUser).add(post.getAuthor());
            return true;
        }
        return false;
    }

    // REQUIRES: unlikedByUser ≠ null ∧ unlikedByUser ∉ /^\s+$/ ∧ unlikedByUser.length > 0 ∧ postId ≥ 0 ∧ ∃ p post ∈ this . p.getId() = postId
    // THROWS: NullPointerException se unlikedByUser è null (unchecked exception),
    //         IllegalArgumentException se unlikedByUser.length = 0 ∨ unlikedByUser ∈ /^\s+$/ (unchecked exception),
    //         NoSuchElementException se ∄ p post ∈ this . p.getId() = postId (checked exception),
    // MODIFIES: this
    // EFFECTS: se unlikedByUser non è già presente nella lista dei like del post richiesto, lo stato non viene modificato e la funzione restituisce false,
    //          altrimenti viene rimosso unlikedByUser alla lista likes del post, e viene restituito true
    public boolean unlikePost(int postId, String unlikedByUser) throws NullPointerException, IllegalArgumentException, NoSuchElementException {
        if(unlikedByUser == null) {
            throw new NullPointerException();
        }
        if(unlikedByUser.trim().isEmpty() || postId < 0) {
            throw new IllegalArgumentException();
        }

        Post post = this.getPostById(postId);
        
        if(post == null) {
            throw new NoSuchElementException();
        }
        
        post.removeLike(unlikedByUser); // rimuovo il like dal post

        // se questo era l'unico post di un utente a cui unlikedByUser aveva messo like, quell'utente viene rimosso dalla lista dei seguiti di unlikedByUser
        if(this.getNumberOfLikedPosts(unlikedByUser, post.getAuthor()) == 0) {
            this.followRelations.get(unlikedByUser).remove(post.getAuthor());
            return true;
        }
        return false;
    }

    // REQUIRES: likedBy ≠ null ∧ likedBy ∉ /^\s+$/ ∧ likedBy.length > 0 ∧ ofAuthor ≠ null ∧ ofAuthor ∉ /^\s+$/ ∧ ofAuthor.length > 0
    // THROWS: NullPointerException se likedBy è null ∨ ofAuthor è null (unchecked exception),
    //         IllegalArgumentException se likedBy.length = 0 ∨ likedBy ∈ /^\s+$/ ∨ ofAuthor.length = 0 ∨ ofAuthor ∈ /^\s+$/ (unchecked exception)
    // EFFECTS: restituisce il numero di post scritti dall'utente identificato da ofAuthor ai quali l'utente identificato da likedBy ha messo like
    private int getNumberOfLikedPosts(String likedBy, String ofAuthor) throws NullPointerException, IllegalArgumentException {
        if(likedBy == null || ofAuthor == null) {
            throw new NullPointerException();
        }
        if(likedBy.trim().isEmpty() || ofAuthor.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        Set<Post> postsByAuthor = this.postRelations.get(ofAuthor);
        int count = 0;

        for(Post post : postsByAuthor) {
            if(post.getLikes().contains(likedBy)) {
                count++;
            }
        }

        return count;
    }

    // REQUIRES: user ≠ null ∧ user ∉ /^\s+$/ ∧ user.length > 0
    // THROWS: NullPointerException se user è null (unchecked exception),
    //         IllegalArgumentException se user.length = 0 ∨ user ∈ /^\s+$/) (unchecked exception)
    // EFFECTS: restituisce il numero di follower che l'utente user ha all'interno della rete (this)
    //          (dove tale numero è uguale alla sommatoria su tutte le chiavi delle occorrenze di user all'interno dei valori di tali chiavi)
    private int getNumerOfFollowers(String user) throws NullPointerException, IllegalArgumentException {
        if(user == null) {
            throw new NullPointerException();
        }
        if(user.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        return MicroBlog.getNumerOfFollowers(user, this.followRelations);
    }

    // REQUIRES: user ≠ null ∧ user ∉ /^\s+$/ ∧ user.length > 0 ∧ followers ≠ null ∧ (∀ (k, v) ∈ followers . v ≠ null ∧ (∀ s ∈ v . s ≠ null ∧ s ∉ /^\s+$/ ∧ s.length > 0))
    // THROWS: NullPointerException se user è null ∨ followers è null ∨ (∃ (k, v) ∈ followers . v è null) (unchecked exception),
    //         IllegalArgumentException se (∃ (k, v) ∈ followers . (∃ s ∈ v . s è null ∨ s.length = 0 ∨ s ∈ /^\s+$/)) (unchecked exception)
    // EFFECTS: restituisce il numero di follower che l'utente user ha all'interno della map followers
    //          (dove tale numero è uguale alla sommatoria su tutte le chiavi delle occorrenze di user all'interno dei valori di tali chiavi)
    private static int getNumerOfFollowers(String user, Map<String, Set<String>> followers) throws NullPointerException, IllegalArgumentException {
        if(user == null) {
            throw new NullPointerException();
        }
        if(user.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        if(followers == null) {
            throw new NullPointerException();
        }

        int count = 0;

        for(Map.Entry<String,Set<String>> entry : followers.entrySet()) {
            if(entry.getValue().contains(user)) {
                count++;
            }
        }

        return count;
    }

    // EFFECTS: restituisce una map che esplicita le relazioni di "follower" all'interno della rete, ovvero ogni key
    //          è associata a un set che contiene tutti e soli gli utenti seguiti da quello individuato dalla key
    public Map<String, Set<String>> getFollowRelations() {
        Map<String, Set<String>> copy = new HashMap<String, Set<String>>();
        
        // itero gli elementi di followRelations
        for(Map.Entry<String, Set<String>> entry : this.followRelations.entrySet()) {
            // aggiungo alla deep copy la chiave originale associata a una deep copy del valore originale (insieme di stringhe)
            copy.put(entry.getKey(), new HashSet<String>(entry.getValue()));
        }

        return copy;
    }

    // REQUIRES: searchWords ≠ null ∧ (∀ w ∈ searchWords . w ≠ null ∧ w ∉ /^\s+$/ ∧ w.length > 0)
    // THROWS: NullPointerException se searchWords è null ∨ (∃ w ∈ searchWords . w è null) (unchecked exception),
    //         IllegalArgumentException se ∃ w ∈ searchWords . w.length = 0 ∨ w ∈ /^\s+$/ (unchecked exception)
    // EFFECTS: restituisce una lista contenente tutti i Post nella rete ordinati per rilevanza, ovvero per numero di parole in searchWords contenute nel testo
    //          Formalmente: sia λ(p) = #{w ∈ searchWords | w ⊆ p.getText()}. Allora l'output è
    //          [p1, p2, ..., pn] dove λ(p1) ≥ λ(p2) ≥ ... ≥ λ(pn)
    public List<Post> sortByRelevance(List<String> searchWords) throws NullPointerException, IllegalArgumentException {
        List<Post> outputList = this.getAllPosts();

        outputList.sort((p, q) -> {
            int count1, count2;
            count1 = count2 = 0;
            for(String word : searchWords) { // conto il numero di parole in searchWords che compaiono nei due generici post della lista
                if(containsExactly(p.getText(), word)) {
                    count1++;
                }
                if(containsExactly(q.getText(), word)) {
                    count2++;
                }
            }
            return count2 - count1; // la funzione lambda restituisce la differenza dei due conteggi, utilizzata poi da sort per l'ordinamento
        });

        return outputList;
    }

    // REQUIRES: str ≠ null ∧ substr ≠ null
    // THROWS: NullPointerException se str è null ∨ substr è null
    // EFFECTS: restituisce true se la stringa substr, trattata come parola, è contenuta in str; false altrimenti.
    //          Utilizza una espressione regolare e il word boundary per verificare che la parola *esatta* sia contenuta nella stringa,
    //          e non sia per esempio prefisso di una parola più lunga
    private boolean containsExactly(String str, String substr) {
        String regex = ".*\\b" + substr + "\\b.*"; // creo un'espressione regolare -- \b è il word boundary, utilizzato per riconoscere l'inizio o la fine di una parola
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str); // cerco la parola substr all'interno della stringa str

        return matcher.matches();
    }

    // EFFECTS: restituisce una lista contenente tutti i post che sono stati aggiunti alla rete
    public List<Post> getAllPosts() {
        List<Post> posts = new LinkedList<Post>();

        for(Map.Entry<Integer, Post> entry : this.postLookup.entrySet()) {
            posts.add(entry.getValue().clone());
        }

        return posts;
    }

    // EFFECTS: restituisce il contenuto della variabile di istanza nextId e la incrementa successivamente, garantendo
    //          che ogni output sia unico
    // MODIFIES: this
    private int getUniqueId() {
        return (this.nextId)++;
    }

    // REQUIRES: id ≥ 0
    // THROWS: IllegalArgumentException se id < 0 (unchecked exception)
    // EFFECTS: restituisce il solo post che ha id uguale al parametro id, o null se non esiste un post con quell'id
    protected Post getPostById(int id) throws IllegalArgumentException {
        if(id < 0) {
            throw new IllegalArgumentException();
        }
        return this.postLookup.get(id);
    }
}
