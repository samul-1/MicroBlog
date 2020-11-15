import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SocialNetwork {
    // REQUIRES: ps ≠ null ∧ (∀ p ∈ ps . p ≠ null)
    // THROWS: NullPointerException se ps è null ∨ (∃ p ∈ ps . p è null) (unchecked exception)
    // EFFECTS: restituisce una map che associa a ogni utente inferito dalla lista ps gli utenti da esso seguiti
    //          Formalmente: se indichiamo con u ~ v la relazione per la quale u segue v, allora l'output è
    //          una funzione f: String ↦ {String, ..., String} t.c. ∀ u utente . ∀ v utente . v ∈ f(u) ⟺ u ~ v
    public static Map<String, Set<String>> guessFollowers(List<Post> ps) throws NullPointerException {
        throw new UnsupportedOperationException(); // implementazione di default del metodo statico all'interno dell'interfaccia (richiesto dalla specifica di Java)
    }

    // REQUIRES: followers ≠ null ∧ (∀ (k, v) ∈ followers . v ≠ null ∧ (∀ s ∈ v . s ≠ null ∧ s ∉ /^\s+$/ ∧ s.length > 0))
    // THROWS: NullPointerException se followers è null ∨ (∃ (k, v) ∈ followers . v è null) (unchecked exception),
    //         IllegalArgumentException se (∃ (k, v) ∈ followers . (∃ s ∈ v . s è null ∨ s.length = 0 ∨ s ∈ /^\s+$/)) (unchecked exception)
    // EFFECTS: restituise una lista contenente tutti e soli gli utenti che, in base alla map fornita, hanno un numero maggiore di follower
    //          rispetto al numero di utenti che essi seguono
    //          Formalmente: Sia g(u) il numero di utenti che u segue. Allora l'output è: {u | u.numberOfFollowers() > g(u)}
    public static List<String> influencers(Map<String, Set<String>> followers) throws NullPointerException, IllegalArgumentException {
        throw new UnsupportedOperationException(); // implementazione di default del metodo statico all'interno dell'interfaccia (richiesto dalla specifica di Java)
    }

    // EFFECTS: restituisce una lista contenente tutti e soli gli utenti che, all'interno dell'istanza (this), hanno un numero maggiore di follower
    //          rispetto al numero di utenti che essi seguono
    //          Formalmente: Sia g(u) il numero di utenti che u segue. Allora l'output è: {u | u.numberOfFollowers() > g(u)}
    public List<String> influencers();

    // EFFECTS: restituisce un insieme di stringhe che identificano gli utenti che hanno almeno un post a essi associato all'interno dell'istanza (this)
    //          Formalmente: l'output è {this.getAllPosts().get(i).getAuthor() | 0 ≤ i < this.getAllPosts().size()}
    public Set<String> getMentionedUsers();

    // REQUIRES: ps ≠ null ∧ (∀ p ∈ ps . p ≠ null)
    // THROWS: NullPointerException se ps è null ∨ (∃ p ∈ ps . p è null) (unchecked exception)
    // EFFECTS: restituisce un insieme di stringhe che identificano gli utenti che hanno almeno un post a essi associato all'interno della lista ps
    //          Formalmente: l'output è {ps.get(i).getAuthor() | 0 ≤ i < ps.size()}
    public static Set<String> getMentionedUsers(List<Post> ps) throws NullPointerException {
        throw new UnsupportedOperationException(); // implementazione di default del metodo statico all'interno dell'interfaccia (richiesto dalla specifica di Java)
    }

    // REQUIRES: username ≠ null ∧ username ∉ /^\s+$/ ∧ username.length > 0
    // THROWS: NullPointerException se username è null (unchecked exception),
    //         IllegalArgumentException se username.length = 0 ∨ username ∈ /^\s+$/ (unchecked exception)
    // EFFECTS: restituisce una lista di Post che identifica un sottoinsieme dei post presenti nella rete (this) contenente tutti e soli
    //          quelli appartenenti all'utente identificato dal parametro username
    //          Formalmente: l'output contiene tutti i post p t.c. p ∈ this.getAllPosts() ∧ p.getAuthor = username
    public List<Post> writtenBy(String username) throws NullPointerException, IllegalArgumentException;

    // REQUIRES: ps ≠ null ∧ (∀ p ∈ ps . p ≠ null) ∧
    //           username ≠ null ∧ username ∉ /^\s+$/ ∧ username.length > 0
    // THROWS: NullPointerException se ps è null ∨ (∃ p ∈ ps . p è null) ∨ username è null (unchecked exception),
    //         IllegalArgumentException se username.length = 0 ∨ username ∈ /^\s+$/ (unchecked exception)
    // EFFECTS: restituisce una lista di Post che identifica un sottoinsieme dei post presenti nella lista ps contenente tutti e soli
    //          quelli appartenenti all'utente identificato dal parametro username
    //          Formalmente: l'output contiene tutti i post p t.c. p ∈ ps ∧ p.getAuthor = username
    public static List<Post> writtenBy(List<Post> ps, String username) throws NullPointerException, IllegalArgumentException {
        throw new UnsupportedOperationException(); // implementazione di default del metodo statico all'interno dell'interfaccia (richiesto dalla specifica di Java)
    }

    // REQUIRES: words ≠ null ∧ (∀ w ∈ words . w ≠ null ∧ w ∉ /^\s+$/ ∧ w.length > 0)
    // THROWS: NullPointerException se words è null ∨ (∃ w ∈ words . w è null) (unchecked exception),
    //         IllegalArgumentException se ∃ w ∈ words . w.length = 0 ∨ w ∈ /^\s+$/ (unchecked exception)
    // EFFECTS: restituisce una lista di Post che identifica un sottoinsieme dei post presenti nella rete (this) contenente tutti e soli
    //          quelli tali che ogni stringa contenuta nel parametro word è una sottostringa di essi
    //          Formalmente: l'output contiene tutti i post p t.c. ∀ w ∈ words . w ⊆ p.getText()
    public List<Post> containing(List<String> words) throws NullPointerException, IllegalArgumentException;
}
