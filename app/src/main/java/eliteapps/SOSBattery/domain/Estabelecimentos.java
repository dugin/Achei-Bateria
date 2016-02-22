package eliteapps.SOSBattery.domain;

/**
 * Created by Rodrigo on 16/02/2016.
 */
public class Estabelecimentos {
    private String updatedAt;

    private String g;

    private String wifi;

    private String bairro;

    private String objectId;

    private String[] hr_open;

    private String createdAt;

    private Img img;

    private String[] hr_close;

    private String nome;

    private String[] l;

    private String end;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String[] getHr_open() {
        return hr_open;
    }

    public void setHr_open(String[] hr_open) {
        this.hr_open = hr_open;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Img getImg() {
        return img;
    }

    public void setImg(Img img) {
        this.img = img;
    }

    public String[] getHr_close() {
        return hr_close;
    }

    public void setHr_close(String[] hr_close) {
        this.hr_close = hr_close;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String[] getL() {
        return l;
    }

    public void setL(String[] l) {
        this.l = l;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "ClassPojo [updatedAt = " + updatedAt + ", g = " + g + ", wifi = " + wifi + ", bairro = " + bairro + ", objectId = " + objectId + ", hr_open = " + hr_open + ", createdAt = " + createdAt + ", img = " + img + ", hr_close = " + hr_close + ", nome = " + nome + ", l = " + l + ", end = " + end + "]";
    }
}
