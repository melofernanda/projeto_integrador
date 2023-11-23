package com.example.mercadodealimentosjava.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mercadodealimentosjava.R;
import com.example.mercadodealimentosjava.helper.ConfiguracaoFirebase;
import com.example.mercadodealimentosjava.helper.UsuarioFirebase;
import com.example.mercadodealimentosjava.model.Produto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao,
            editProdutoPreco, editProdutoValidade;
    private ImageView imageProduto;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;

    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    private Produto produtoSelecionado; // Para armazenar o produto selecionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        // Configurações iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // Configuração toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recebe o produto selecionado da intent (se houver)
        produtoSelecionado = (Produto) getIntent().getSerializableExtra("produtoSelecionado");

        if (produtoSelecionado != null) {
            // Preenche os campos de edição com os valores do produto selecionado
            editProdutoNome.setText(produtoSelecionado.getNome());
            editProdutoDescricao.setText(produtoSelecionado.getDescricao());
            editProdutoPreco.setText(String.valueOf(produtoSelecionado.getPreco()));
            editProdutoValidade.setText(produtoSelecionado.getValidade());

            // Carrega a imagem do produto, se houver uma URL de imagem
            String urlImagem = produtoSelecionado.getUrlImagem();
            if (urlImagem != null && !urlImagem.isEmpty()) {
                // Carrega a imagem da URL usando uma biblioteca como Picasso ou Glide
                Picasso.get().load(urlImagem).into(imageProduto);
            }
        }

        imageProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    public void validarDadosProduto(View view) {
        // Valida se os campos foram preenchidos
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String validade = editProdutoValidade.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if (!nome.isEmpty() && !descricao.isEmpty() && !validade.isEmpty() && !preco.isEmpty()) {
            Produto produto = new Produto();
            produto.setIdUsuario(idUsuarioLogado);
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setValidade(validade);
            produto.setPreco(Double.parseDouble(preco));
            produto.setUrlImagem(urlImagemSelecionada);

            // Se há um produto selecionado, atualize-o; caso contrário, salve um novo produto
            if (produtoSelecionado != null) {
                produto.setIdProduto(produtoSelecionado.getIdProduto());
                produto.atualizar();
            } else {
                produto.salvar();
            }

            finish();
            exibirMensagem("Produto salvo com sucesso!");
        } else {
            exibirMensagem("Preencha todos os campos antes de salvar.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                        break;
                }

                if (imagem != null) {
                    imageProduto.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("produtos")
                            .child(idUsuarioLogado + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Erro ao carregar a imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            urlImagemSelecionada = taskSnapshot.getStorage().getDownloadUrl().toString();
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Sucesso ao carregar a imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes() {
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
        editProdutoValidade = findViewById(R.id.editProdutoValidade);
        imageProduto = findViewById(R.id.imageProduto);
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}