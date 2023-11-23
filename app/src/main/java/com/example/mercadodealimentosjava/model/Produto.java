package com.example.mercadodealimentosjava.model;

import com.example.mercadodealimentosjava.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Produto implements Serializable {

    private String idUsuario;
    private String IdProduto;
    private String urlImagem;
    private String nome;
    private String descricao;
    private String validade;
    private Double preco;

    //Construtor que com base no nó produto retorna o ID do produto
    public Produto() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos");
        setIdProduto(produtoRef.push().getKey());
    }

    //Método para salvar um produto criando o nó "produtos" e o identificando com o Id da Empresa e do Produto
    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        produtoRef.setValue(this);
    }

    //Método para remover o produto usado pelo clique longo na RecyclerView
    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        produtoRef.removeValue();

    }
    public void atualizar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        produtoRef.setValue(this);
    }
    //Getters e Setters

    public String getIdProduto() {
        return IdProduto;
    }

    public void setIdProduto(String idProduto) {
        IdProduto = idProduto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
