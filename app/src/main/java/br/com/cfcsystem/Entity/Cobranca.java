package br.com.cfcsystem.Entity;

/**
 * Created by user on 14/10/2014.
 */
public class Cobranca {
    private Integer codigo;
    private Integer codCliente;
    private String cliente;
    private Double vlAnterior;
    private Double vlPeriodo;
    private Double vlPago;
    private String dtPagamento;
    private Double latitude;
    private Double longitude;
    private String status;
    private String cidade;
    private String uf;
    private String dtProxima;
    private Integer ordem;
    private String fone1;
    private String fone2;
    private Double vlNegociar;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(Integer codCliente) {
        this.codCliente = codCliente;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Double getVlAnterior() {
        return vlAnterior;
    }

    public void setVlAnterior(Double vlAnterior) {
        this.vlAnterior = vlAnterior;
    }

    public Double getVlPeriodo() {
        return vlPeriodo;
    }

    public void setVlPeriodo(Double vlPeriodo) {
        this.vlPeriodo = vlPeriodo;
    }

    public Double getVlPago() {
        return vlPago;
    }

    public void setVlPago(Double vlPago) {
        this.vlPago = vlPago;
    }

    public String getDtPagamento() {
        return dtPagamento;
    }

    public void setDtPagamento(String dtPagamento) {
        this.dtPagamento = dtPagamento;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getDtProxima() {
        return dtProxima;
    }

    public void setDtProxima(String dtProxima) {
        this.dtProxima = dtProxima;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getFone1() {
        return fone1;
    }

    public void setFone1(String fone1) {
        this.fone1 = fone1;
    }

    public String getFone2() {
        return fone2;
    }

    public void setFone2(String fone2) {
        this.fone2 = fone2;
    }

    public Double getVlNegociar() {
        return vlNegociar;
    }

    public void setVlNegociar(Double vlNegociar) {
        this.vlNegociar = vlNegociar;
    }
}
