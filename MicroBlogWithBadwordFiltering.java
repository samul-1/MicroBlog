import java.util.HashSet;
import java.util.Set;

import javax.naming.LimitExceededException;

import java.util.List;

public class MicroBlogWithBadwordFiltering extends MicroBlog {
    private Set<String> badwords; // insieme delle parole offensive

    /*
        Representation invariant:
        IR(MicroBlog) ∧
        ∀ w ∈ badword . w ≠ null ∧ w ∉ /^\s+$/ ∧ w.length > 0 ∧
        ∀ p post ∈ this . ∀ w ∈ badwords . w ⊈ p.getText()
    */

    public MicroBlogWithBadwordFiltering(List<String> badwords) throws NullPointerException, IllegalArgumentException {
        super();

        if(badwords == null) {
            throw new NullPointerException();
        }

        this.badwords = new HashSet<String>();
        for(String word : badwords) {
            if(word == null) {
                throw new NullPointerException();
            }
            if(word.trim().isEmpty()) {
                throw new IllegalArgumentException();
            }
            this.badwords.add(word);
        }
    }

    // REQUIRES: eredita le pre-condizioni del metodo createPost() della superclasse
    // THROWS: stesse eccezioni del metodo createPost() della superclasse
    // MODIFIES: this
    // EFFECTS: chiama il metodo createPost() della superclasse. Dopodiché, sostituisce tutte le occorrenze delle stringhe contenute in
    //          this.badwords all'interno del post con "***". Restituisce l'id del post creato
    @Override
    public int createPost(String author, String text) throws NullPointerException, IllegalArgumentException {
        try {
            int retId = super.createPost(author, text); // effettuo la chiamata al metodo della superclasse, che verificherà le pre-condizioni e creerà il post
            Post createdPost = this.getPostById(retId);

            String filteredText = text;
            for(String badword : this.badwords) {
                filteredText = filteredText.replaceAll(badword, "***"); // per ogni stringa in badwords, sostituisco le occorrenze di quella stringa con "***" nel post
            }

            createdPost.editPost(filteredText); // sostituisco il testo originale del post con quello censurato
            return retId;
        } catch(LimitExceededException exc) { return -1; } // try catch obbligatorio per l'eccezione checked
    }
}
