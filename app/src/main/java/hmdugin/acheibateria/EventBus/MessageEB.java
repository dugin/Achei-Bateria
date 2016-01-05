package hmdugin.acheibateria.eventBus;

/**
 * Created by Rodrigo on 16/12/2015.
 */
public class MessageEB {

    private String nomeClasse;
    private int pos;

    public MessageEB(String nomeClasse) {
        this.nomeClasse = nomeClasse;
    }

    public String getData() {
        return nomeClasse;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}