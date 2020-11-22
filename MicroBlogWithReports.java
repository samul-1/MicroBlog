import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.naming.LimitExceededException;

public class MicroBlogWithReports extends MicroBlog {
    // classe interna alla rete sociale che contiene una coppia (stringa, int) che rappresenta una segnalazione fatta da un utente a un post
    private class Report {
        private final String reportedBy;
        private final int postId;

        // pre-condizioni non riportate in quanto garantite dalla classe esterna

        public Report(String reportedBy, int postId) {
            this.reportedBy = reportedBy;
            this.postId = postId;
        }

        public String getReporter() {
            return this.reportedBy;
        }
    }
    
    private Map<Integer, Set<Report>> reports; // mappa gli id dei post su insiemi di segnalazioni fatte a quel post
    private byte maxReportCount; // numero massimo di segnalazioni che possono essere fatte a un post prima che questo venga automaticamente censurato

    /*
        Representation invariant:
        IR(MicroBlog) ∧
        ∀ p post ∈ this . reports.get(p.getId()).size() ≥ this.maxReportCount ⇒ p.getText() = '(deleted)'
    */

    public MicroBlogWithReports() {
        super();

        this.reports = new HashMap<Integer, Set<Report>>();
        this.maxReportCount = 5; // valore di default per la variabile
    }

    public MicroBlogWithReports(byte maxReportCount) {
        super();

        this.reports = new HashMap<Integer, Set<Report>>();
        this.maxReportCount = maxReportCount;
    }

    // REQUIRES: user ≠ null ∧ user ∉ /^\s+$/ ∧ user.length > 0 ∧ postId ≥ 0 ∧ ∃ p post ∈ this . p.getId() = postId
    // THROWS: NullPointerException se user è null (unchecked exception).
    //         IllegalArgumentException se user.length = 0 ∨ user ∈ /^\s+$/ (unchecked exception),
    //         NoSuchElementException se ∄ p post ∈ this . p.getId() = postId (checked exception),
    //         IllegalStateException se user = getPostById(postId).getAuthor() ∨ user ∈ this.reports.get(postId) (checked exception)
    // MODIFIES: this
    // EFFECTS: crea una nuova segnalazione (Report) e la inserisce nell'insieme mappato dall'id del post. Se il numero di segnalazioni al post
    //          supera la soglia definita da maxReportCount, il contenuto del post viene sostituito dalla stringa "(deleted)"
    public void reportContent(String user, int postId) throws NullPointerException, IllegalArgumentException, NoSuchElementException, IllegalStateException {
        if(user == null) {
            throw new NullPointerException();
        }
        if(user.trim().isEmpty() || postId < 0) {
            throw new IllegalArgumentException();
        }

        try {
            Post post = this.getPostById(postId); // ottengo il post che ha l'id richiesto
       
        
            if(post == null) {
                throw new NoSuchElementException();
            }
            if(post.getAuthor() == user) { // se colui che sta cercando di segnalare il post è l'autore del post stesso, lancio IllegalStateException
                throw new IllegalStateException();
            }
            if(this.reports.get(postId) == null) { // se è la prima segnalazione al post, creo un nuovo insieme per quel post
                this.reports.put(postId, new HashSet<Report>());
            }
            

            for(Report report : this.reports.get(postId)) { // se l'utente che sta cercando di segnalare il post lo ha già segnalato, lancio IllegalStateException
                if(report.getReporter() == user) {
                    throw new IllegalStateException();
                }
            }

            this.reports.get(postId).add(new Report(user, postId));

            if(reports.get(postId).size() >= this.maxReportCount) { // se il post è stato segnalato un numero sufficientemente grande di volte, nascondo il contenuto del post
                post.editPost("(deleted)");
            }
        } catch(LimitExceededException exc) {}
    }
}
