package com.avilaa.marinecleangs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

data class RelatorioESG(val empresa: String, val categoria: String, val pontuacao: Int, val detalhes: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val relatoriosExemplo = mutableStateListOf(
            RelatorioESG("Empresa A", "Ambiental", 85, "Detalhes do relatório da Empresa A..."),
            RelatorioESG("Empresa B", "Social", 92, "Detalhes do relatório da Empresa B..."),
            RelatorioESG("Empresa C", "Governança", 78, "Detalhes do relatório da Empresa C...")
        )

        setContent {
            AppNavigation(relatoriosExemplo)
        }
    }
}

@Composable
fun AppNavigation(relatoriosExemplo: MutableList<RelatorioESG>) {
    val navController = rememberNavController()
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = if (isLoggedIn) "home" else "login") {
        composable("login") { LoginScreen(navController, onLoginSuccess = { isLoggedIn = true }, relatoriosExemplo) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(relatoriosExemplo, onLogout = { isLoggedIn = false }) }
    }
}


@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit, relatoriosExemplo: MutableList<RelatorioESG>) {
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) } // Estado para mostrar mensagem de erro

    val image = painterResource(id = R.drawable.marineclean)

    Box( // Use um Box para sobrepor a imagem aos campos de entrada
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter // Alinha a imagem ao topo
    ) {
        Image(
            painter = image,
            contentDescription = "Logo", // Descrição da imagem para acessibilidade
            modifier = Modifier
                .fillMaxWidth() // A imagem preenche a largura da tela
                .height(150.dp), // Ajuste a altura conforme necessário
            contentScale = ContentScale.Fit // A imagem se ajusta sem distorção
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onLoginSuccess()
            navController.navigate("home")
        }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Não tem conta? Registre-se",
            modifier = Modifier.clickable { navController.navigate("register") }
        )
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var confirmaSenha by rememberSaveable { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") }
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = confirmaSenha,
            onValueChange = { confirmaSenha = it },
            label = { Text("Confirmar Senha") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (senha == confirmaSenha) {
                showSuccess = true
            } else {
                showError = true
            }
        }) {
            Text("Registrar")
        }
        if (showSuccess) {
            Text(
                "Registro bem-sucedido!",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (showError) {
            Text(
                "As senhas não coincidem",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Já tem conta? Faça login",
            modifier = Modifier.clickable { navController.navigate("login") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(relatorios: MutableList<RelatorioESG>, onLogout: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<RelatorioESG?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    var novaEmpresa by remember { mutableStateOf("") }
    var novaCategoria by remember { mutableStateOf("") }
    var novaPontuacao by remember { mutableStateOf("") }
    var novosDetalhes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatórios ESG") },
                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Adicionar Relatório")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(relatorios) { relatorio ->
                RelatorioItem(relatorio) {
                    selectedReport = relatorio
                    showDialog = true
                }
            }
        }

        if (showDialog && selectedReport != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(selectedReport!!.empresa) },
                text = { Text(selectedReport!!.detalhes) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Fechar")
                    }
                }
            )
        }

        if (showAddDialog) {
            AddReportDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { empresa, categoria, pontuacao, detalhes ->
                    relatorios.add(RelatorioESG(empresa, categoria, pontuacao.toIntOrNull() ?: 0, detalhes))
                    showAddDialog = false
                },
                novaEmpresa = novaEmpresa,
                onNovaEmpresaChange = { novaEmpresa = it },
                novaCategoria = novaCategoria,
                onNovaCategoriaChange = { novaCategoria = it },
                novaPontuacao = novaPontuacao,
                onNovaPontuacaoChange = { novaPontuacao = it },
                novosDetalhes = novosDetalhes,
                onNovosDetalhesChange = { novosDetalhes = it }
            )
        }
    }
}

@Composable
fun AddReportDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit,
    novaEmpresa: String,
    onNovaEmpresaChange: (String) -> Unit,
    novaCategoria: String,
    onNovaCategoriaChange: (String) -> Unit,
    novaPontuacao: String,
    onNovaPontuacaoChange: (String) -> Unit,
    novosDetalhes: String,
    onNovosDetalhesChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Relatório") },
        text = {
            Column {
                OutlinedTextField(
                    value = novaEmpresa,
                    onValueChange = onNovaEmpresaChange,
                    label = { Text("Empresa") }
                )
                OutlinedTextField(
                    value = novaCategoria,
                    onValueChange = onNovaCategoriaChange,
                    label = { Text("Categoria") }
                )
                OutlinedTextField(
                    value = novaPontuacao,
                    onValueChange = onNovaPontuacaoChange,
                    label = { Text("Pontuação") }
                )
                OutlinedTextField(
                    value = novosDetalhes,
                    onValueChange = onNovosDetalhesChange,
                    label = { Text("Detalhes") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(novaEmpresa, novaCategoria, novaPontuacao, novosDetalhes) }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun RelatorioItem(relatorio: RelatorioESG, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = relatorio.empresa, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Categoria: ${relatorio.categoria}")
            Text(text = "Pontuação: ${relatorio.pontuacao}")
            // ... (botões de download/compartilhamento - não funcionais neste protótipo)
        }
    }
}

