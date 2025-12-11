package cl.pasteleriamilsabores.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.pasteleriamilsabores.navigation.Screen
import cl.pasteleriamilsabores.ui.components.BottomNavigationBar
import cl.pasteleriamilsabores.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen(navController: NavController) {
    val articulos = listOf(
        ArticuloBlog(
            "1",
            "5 Técnicas de Decoración para Principiantes",
            "Aprende las técnicas básicas para decorar tus tortas como un profesional",
            "María González",
            "15 Nov 2023",
            R.drawable.tecnicas
        ),
        ArticuloBlog(
            "2",
            "Recetas Chilenas que no Pueden Faltar",
            "Descubre las recetas más tradicionales de la repostería chilena",
            "Carlos Méndez",
            "22 Nov 2023",
            R.drawable.milhojas
        ),
        ArticuloBlog(
            "3",
            "Postres Sin Azúcar: Mitos y Realidades",
            "Exploramos las opciones de postres sin azúcar y cómo mantener el sabor",
            "Ana Silva",
            "30 Nov 2023",
            R.drawable.postres
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Blog de Repostería",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.Blog.route
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "Descubre tips, recetas y técnicas de nuestros estudiantes de gastronomía",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            items(articulos) { articulo ->
                ArticuloBlogItem(
                    articulo = articulo,
                    onArticuloClick = {
                        navController.navigate(Screen.BlogDetail.route)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticuloBlogItem(
    articulo: ArticuloBlog,
    onArticuloClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onArticuloClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (articulo.imagen != 0) {
                Image(
                    painter = painterResource(id = articulo.imagen),
                    contentDescription = articulo.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback si no hay imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📝",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = articulo.titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = articulo.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Autor",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = articulo.autor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Fecha",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = articulo.fecha,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

data class ArticuloBlog(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val autor: String,
    val fecha: String,
    val imagen: Int
)