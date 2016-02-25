package eliteapps.SOSBattery.domain;

public class Estabelecimentos {
    private String cabo;

    private String imgURL;

    private String wifi_senha;

    private String bairro;

    private String cidade;

    private String wifi;

    private String estado;

    private String createdAt;

    private String[] hr_open;

    private String id;

    private String[] hr_close;

    private String nome;

    private String end;

    public String getCabo() {
        return cabo;
    }

    public void setCabo(String cabo) {
        this.cabo = cabo;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getWifi_senha() {
        return wifi_senha;
    }

    public void setWifi_senha(String wifi_senha) {
        this.wifi_senha = wifi_senha;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String[] getHr_open() {
        return hr_open;
    }

    public void setHr_open(String[] hr_open) {
        this.hr_open = hr_open;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "ClassPojo [cabo = " + cabo + ", imgURL = " + imgURL + ", wifi_senha = " + wifi_senha + ", bairro = " + bairro + ", cidade = " + cidade + ", wifi = " + wifi + ", estado = " + estado + ", createdAt = " + createdAt + ", hr_open = " + hr_open + ", ID = " + id + ", hr_close = " + hr_close + ", nome = " + nome + ", end = " + end + "]";
    }
}