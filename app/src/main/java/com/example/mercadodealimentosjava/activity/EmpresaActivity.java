package com.example.mercadodealimentosjava.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.mercadodealimentosjava.R;
import com.example.mercadodealimentosjava.adapter.AdapterProduto;
import com.example.mercadodealimentosjava.helper.ConfiguracaoFirebase;
import com.example.mercadodealimentosjava.helper.UsuarioFirebase;
import com.example.mercadodealimentosjava.listener.RecyclerItemClickListener;
import com.example.mercadodealimentosjava.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    //No método onCreate, a configuração inicial é feita, como a inicialização de componentes, obtenção da instância de autenticação do Firebase e configuração da Toolbar.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        //Configurações iniciais
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutentucacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configuração toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mercado de Alimentos - Empresa");
        setSupportActionBar(toolbar);

        //Configuração recyclerView
        //O RecyclerView é configurado com um LayoutManager, um AdapterProduto é criado e definido no RecyclerView.
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter( adapterProduto );

        //Recuperar produtos para empresa
        //O método recuperarProdutos é chamado para recuperar os produtos da base de dados Firebase e atualizar a lista de produtos no adaptador.
        recuperarProdutos();

        //Evento de clique no recyclerView
        //Um RecyclerItemClickListener é configurado para lidar com eventos de clique e clique longo no RecyclerView.
        recyclerProdutos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProdutos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Produto produtoSelecionado = produtos.get(position);

                                Intent intent = new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class);
                                intent.putExtra("produtoSelecionado", produtoSelecionado);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Produto produtoSelecionado = produtos.get(position);
                                produtoSelecionado.remover();
                                Toast.makeText(EmpresaActivity.this,
                                        "Produto excluído!",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );


    }
    //O método recuperarProdutos é chamado para recuperar os produtos da base de dados Firebase e atualizar a lista de produtos no adaptador.
    private void recuperarProdutos() {
        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idUsuarioLogado);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes(){
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto :
                abrirNovoProduto();
                break;
            case R.id.menuPedidos :
                abrirPedidosEmp();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirPedidosEmp(){
        startActivity(new Intent(EmpresaActivity.this, PedidosEmpActivity.class));
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));
    }

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }
}
