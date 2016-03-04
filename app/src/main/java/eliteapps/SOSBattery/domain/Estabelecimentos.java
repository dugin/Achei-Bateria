package eliteapps.SOSBattery.domain;

public class Estabelecimentos {
    private Boolean cabo;

    private String imgURL;

    private String wifi_senha;

    private String bairro;

    private String cidade;

    private Boolean wifi;

    private String estado;

    private String createdAt;

    private String[] hr_open;

    private String id;

    private String[] hr_close;

    private String nome;

    private String end;

    private String tipo;

    private String modifiedAt;


    public Estabelecimentos(String modifiedAt, String tipo, Boolean cabo, String imgURL, String wifi_senha, String bairro, String cidade, Boolean wifi, String estado, String createdAt, String[] hr_open, String id, String[] hr_close, String nome, String end) {
        this.cabo = cabo;
        this.imgURL = imgURL;
        this.wifi_senha = wifi_senha;
        this.bairro = bairro;
        this.cidade = cidade;
        this.wifi = wifi;
        this.estado = estado;
        this.createdAt = createdAt;
        this.hr_open = hr_open;
        this.id = id;
        this.hr_close = hr_close;
        this.nome = nome;
        this.end = end;
    }

    public Estabelecimentos() {

    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getCabo() {
        return cabo;
    }

    public void setCabo(Boolean cabo) {
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

    public Boolean getWifi() {
        return wifi;
    }

    public void setWifi(Boolean wifi) {
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