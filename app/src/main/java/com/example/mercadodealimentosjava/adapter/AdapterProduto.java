package com.example.mercadodealimentosjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mercadodealimentosjava.R;
import java.util.List;
import com.example.mercadodealimentosjava.model.Produto;


//O adapter funciona como um intermediário entre os dados (a lista de produtos) e a exibição (o RecyclerView).
// Ele cria as ViewHolders necessárias, preenche os dados nas ViewHolders e gerencia a reciclagem de ViewHolders para uma rolagem eficiente.

//Esta classe estende RecyclerView.Adapter e é usada para ligar os dados da lista de produtos a uma exibição de recycler view.
public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    //O método onCreateViewHolder é chamado quando o RecyclerView precisa de uma nova ViewHolder para representar um item.
    // Ele infla o layout do item da lista (adapter_produto.xml) e retorna uma nova instância de MyViewHolder.
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    //O método onBindViewHolder é chamado para preencher os dados de um item na posição específica.
    // Ele obtém o objeto Produto da lista na posição correspondente e define os valores nos elementos de visualização (TextView) da ViewHolder.
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.validade.setText(produto.getValidade());
        holder.valor.setText("R$ " + produto.getPreco());
    }

    @Override
    //O método getItemCount retorna o número total de itens na lista de produtos.
    public int getItemCount() {
        return produtos.size();
    }

    //Esta classe estende RecyclerView.ViewHolder e representa uma única linha na lista de produtos.
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView validade;
        TextView valor;
        //Os elementos de visualização (TextView) são inicializados no construtor desta classe.

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeProduto);
            descricao = itemView.findViewById(R.id.textDescricaoProduto);
            validade = itemView.findViewById(R.id.textValidadeProduto);
            valor = itemView.findViewById(R.id.textPreco);
        }
    }
}
