package com.example.mercadodealimentosjava.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.mercadodealimentosjava.R;
import com.example.mercadodealimentosjava.adapter.AdapterPedido;
import com.example.mercadodealimentosjava.adapter.AdapterProduto;
import com.example.mercadodealimentosjava.helper.ConfiguracaoFirebase;
import com.example.mercadodealimentosjava.helper.UsuarioFirebase;
import com.example.mercadodealimentosjava.listener.RecyclerItemClickListener;
import com.example.mercadodealimentosjava.model.Pedido;
import com.example.mercadodealimentosjava.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosEmpActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidosEmp;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_emp);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();



        //Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos Empresa");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuração recyclerview
        recyclerPedidosEmp.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidosEmp.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidosEmp.setAdapter(adapterPedido);

        recuperarPedidos();


        //Adiciona evento de clique no recyclerview
        recyclerPedidosEmp.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPedidosEmp,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Pedido pedido = pedidos.get(position);
                                pedido.setStatus("finalizado");
                                pedido.atualizarStatus();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
               .setCancelable(false)
              .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(idEmpresa);

        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("Confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pedidos.clear();
                if (dataSnapshot.getValue() != null ) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void inicializarComponentes(){
        recyclerPedidosEmp = findViewById(R.id.recyclerPedidosEmp);
    }
}
