// Author: Samuele Bonini (mat. 597443)
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.naming.LimitExceededException;

public class Post implements Cloneable {
    private final int id;
    private final String author;
    private String text;
    private Timestamp timestamp;
    private LinkedList<String> likes;

    /*
        Abstraction function:
        α(c) = (c.id, c.author, c.text, System.currentTimeMillis(), {c.likes.get(i) | 0 ≤ i < c.likes.length()})

        Typical element:
        (id, author, text, timestamp, [likedBy1, ..., likedByN])
        un elemento istanza di Post può essere rappresentato da una quintupla formata dagli attributi
        della classe, dove l'ultimo attributo è una lista

        Representation invariant:
        this.id ≥ 0 ∧
        this.author ≠ null ∧ this.author.length > 0 ∧ this.author ∉ /^\s+$/ (dove /^\s+$/ è l'espressione regolare che rappresenta una stringa composta solo da spazi) ∧
        this.text ≠ null ∧ 0 < this.text.length ≤ 140 ∧ this.text ∉ /^\s+$/ ∧
        this.timestamp ≠ null ∧
        this.likes ≠ null ∧
        ∀ 0 ≤ i < this.likes.length() . this.likes.get(i) ≠ null ∧ this.likes.get(i).length > 0 ∧ this.likes.get(i) ∉ /^\s+$/ ∧ this.likes.get(i) ≠ this.author ∧
        ∀ 0 ≤ i < j < this.likes.length() . this.likes.get(i) ≠ this.likes.get(j)
    */

    public Post(int id, String author, String text) throws NullPointerException, LimitExceededException, IllegalArgumentException {
        if(author == null || text == null) {
            throw new NullPointerException();
        }
        if(id < 0 || author.trim().isEmpty() || text.trim().isEmpty()) {
            // l'id non può essere minore di 0; il nome dell'autore e il contenuto del post non possono essere vuoti o contenere solo spazi
            throw new IllegalArgumentException();
        }
        if(text.length() > 140) { // limite di lunghezza del post
            throw new LimitExceededException();
        }

        this.id = id; // l'unicità dell'id viene garantita da SocialNetwork e non dalla classe Post stessa
        this.author = author;
        this.text = text;
        this.timestamp = new Timestamp(System.currentTimeMillis()); // il timestamp del post viene assegnato automaticamente alla creazione del post
        this.likes = new LinkedList<String>();
    }

    // EFFECTS: restituisce l'attributo id dell'oggetto
    public int getId() {
        return this.id;
    }

    // EFFECTS: restituisce l'attributo author dell'oggetto
    public String getAuthor() {
        return this.author;
    }

    // EFFECTS: restituisce l'attributo text dell'oggetto
    public String getText() {
        return this.text;
    }

    // EFFECTS: restituisce l'attibuto timestamp dell'oggetto
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    // REQUIRES: newText ≠ null ∧ 0 < newText.length ≤ 140
    // THROWS: NullPointerException se newText è null (unchecked exception),
    //         IllegalArgumentException se newText.length == 0 (unchecked exception),
    //         LimitExceededException se newText.length > 140 (checked exception)
    // MODIFIES: this
    // EFFECTS: dopo l'esecuzione, this.text sarà uguale a newText
    public void editPost(String newText) throws NullPointerException, IllegalArgumentException, LimitExceededException {
        if(newText == null) {
            throw new NullPointerException();
        }
        if(newText.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        if(newText.length() > 140) {
            throw new LimitExceededException();
        }

        this.text = newText;
    }

    // EFFECTS: restituisce una rappresentazione dell'istanza (this) come stringa
    public String toString() {
        return "\"" +
                this.text +
                "\" - " +
                this.author +
                ", " +
                this.timestamp.toString();
    }

    // REQUIRES: other ≠ null ∧ other è (un sottotipo di) Post
    // THROWS: NullPointerException se other è null (unchecked exception)
    // EFFECTS: restituisce true se e solo se l'id dell'istanza (this) è lo stesso dell'id di other
    //          (come conseguenza del fatto che i Post sono identificati univocamente dal loro id)
    public boolean equals(Post other) throws NullPointerException {
        if(other == null) {
            throw new NullPointerException();
        }
        return this.id == other.id;
    }

    // REQUIRES: other ≠ null ∧ other è (un sottotipo di) Post
    // THROWS: NullPointerException se other è null (unchecked exception)
    // EFFECTS: restituisce un numero maggiore di 0 se this > other,
    //                      0 se i due post sono uguali,
    //                      un numero minore di 0 se this < other,
    //          in base al seguente ordinamento totale:
    //          ∀ x, y istanze di Post . x > y ⟺ x.id > y.id
    public int compareTo(Post other) throws NullPointerException {
        if(other == null) {
            throw new NullPointerException();
        }

        return this.id - other.getId();
    }

    // REQUIRES: user ≠ null ∧ 0 < user.length ∧ user ∉ this.likes, user ≠ this.author
    // THROWS: NullPointerException se user è null (unchecked exception),
    //         IllegalArgumentException se user.length == 0 (unchecked exception),
    //         IllegalStateException se user == this.author (unchecked exception)
    // MODIFIES: this
    // EFFECTS: se user ∈ this.likes, il metodo non fa nulla (non viene lanciata alcuna eccezione in quanto questo comportamento non è considerato un errore)
    //          altrimenti, this.likes' = this.likes ∪ user dove this.likes' è il nuovo stato dell'insieme likes dell'istanza dopo l'esecuzione del metodo
    public void addLike(String user) throws NullPointerException, IllegalArgumentException, IllegalStateException {
        if(user == null) {
            throw new NullPointerException();
        }
        if(user.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        if(user.compareTo(this.author) == 0) {
            throw new IllegalStateException();
        }
        
        int index = this.likes.indexOf(user);
        if(index == -1) { // aggiungo il like solo se non è già presente
            this.likes.add(user);
        }
    }

    // REQUIRES: user ≠ null ∧ 0 < user.length ∧ user ∈ this.likes
    // THROWS: NullPointerException se user è null (unchecked exception),
    //         IllegalArgumentException se user.length == 0 (unchecked exception),
    // MODIFIES: this
    // EFFECTS: se user ∉ this.likes, il metodo non fa nulla (non viene lanciata alcuna eccezione in quanto questo comportamento non è considerato un errore)
    //          altrimenti, this.likes' = this.likes \ user dove this.likes' è il nuovo stato dell'insieme likes dell'istanza dopo l'esecuzione del metodo
    public void removeLike(String user) throws NullPointerException, IllegalArgumentException {
        if(user == null) {
            throw new NullPointerException();
        }
        if(user.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        int index = this.likes.indexOf(user);
        if(index != -1) { // rimuovo il like solo se è già presente
            this.likes.remove(index);
        }
    }

    // EFFECTS: restituisce una copia del campo this.likes dell'istanza
    public List<String> getLikes() {
        List<String> deepCopy = new LinkedList<String>();

        for(String like : this.likes) {
            deepCopy.add(like);
        }

        return deepCopy;
    }

    // EFFECTS: restituisce una deep copy dell'istanza (this), utilizzabile dai metodi di SocialNetwork
    // che devono restituire liste di post senza violare l'information hiding e senza esporre la referenza privata al post
    public Post clone() {
        try { // il try catch è necessario perché il costruttore di Post può lanciare eccezioni checked,
              // sebbene in questo caso nessuna eccezione verrà mai lanciata poiché this è un oggetto valido
            Post clone = new Post(this.id, this.author, this.text);
            clone.setTimestamp(this.timestamp);

            for(String like : this.likes) {
                clone.addLike(like);
            }

            return clone;
        } catch (LimitExceededException exc) {
            return null;
        }
    }

    // REQUIRES: ts ≠ null
    // THROWS: NullPointerException se ts è null (unchecked exception)
    // MODIFIES: this
    // EFFECTS: dopo l'esecuzione del metodo, this.timestamp sarà uguale a ts
    // questo metodo è privato perché deve essere chiamato solo da clone() per garantire che il Post prodotto
    // dal metodo abbia lo stesso timestamp del Post clonato
    private void setTimestamp(Timestamp ts) throws NullPointerException {
        if(timestamp == null) {
            throw new NullPointerException();
        }
        this.timestamp = ts;
    }
}
