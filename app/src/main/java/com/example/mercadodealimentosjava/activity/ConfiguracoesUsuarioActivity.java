package com.example.mercadodealimentosjava.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mercadodealimentosjava.R;
import com.example.mercadodealimentosjava.helper.ConfiguracaoFirebase;
import com.example.mercadodealimentosjava.helper.UsuarioFirebase;
import com.example.mercadodealimentosjava.model.Empresa;
import com.example.mercadodealimentosjava.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioEndereco;
    private String idUsuario;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        //Configurações iniciais
        incializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Configuração toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recupera dados do usuário
        recuperarDadosUsuario();



    }
    //OBSERVER: Definir uma dependência entre objetos, de maneira que quando um objeto muda de estado todos os seus dependentes são notificados e atualizados automaticamente.
    //onDataChange: Este método é chamado quando os dados no usuarioRef são alterados.
    //O padrão Observer é utilizado no código para realizar ações em resposta a mudanças nos dados do Firebase...O addListenerForSingleValueEvent é um método do
    // Firebase que registra um ouvinte para uma única alteração nos dados.
    // Isso significa que o método onDataChange será chamado uma vez quando os dados em usuarioRef forem alterados.
    // Essencialmente, o padrão Observer está sendo usado aqui para observar (ouvir) mudanças nos dados do Firebase. Quando os dados mudam, o método onDataChange é chamado automaticamente,
    // permitindo que a atividade ConfiguracoesUsuarioActivity responda e
    // atualize a interface do usuário com os dados mais recentes do usuário.
    //Dessa forma, a aplicação prática do padrão Observer nesse contexto específico envolve a reação a eventos assíncronos (mudanças nos dados do Firebase) por meio dos métodos onDataChange e onCancelled.
    private void recuperarDadosUsuario(){
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioEndereco.setText(usuario.getEndereco());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void validarDadosUsuario(View view){

        //Valida se os campos foram preenchidos
        String nome = editUsuarioNome.getText().toString();
        String endereco = editUsuarioEndereco.getText().toString();


        if ( !nome.isEmpty()){
            if ( !endereco.isEmpty()){


                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario( idUsuario );
                        usuario.setNome( nome );
                        usuario.setEndereco( endereco );
                        usuario.salvar();
                        exibirMensagem("Dados Atualiados com sucesso!");
                        finish();





            }else{
                exibirMensagem("Digite seu endereço");

            }
        }else{
            exibirMensagem("Digite seu nome");

        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto
                , Toast.LENGTH_SHORT).show();
    }

    private void incializarComponentes(){
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);
    }


}
