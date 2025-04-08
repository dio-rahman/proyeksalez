import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.salez.kasir.ui.theme.BackgroundEnd
import  com.salez.kasir.ui.theme.Typography
import com.salez.kasir.ui.theme.BackgroundStart
import com.salez.kasir.ui.theme.CardBackground
import com.salez.kasir.ui.theme.PrimaryPeach
import com.salez.kasir.ui.theme.PrimaryVariant
import com.salez.kasir.ui.theme.Secondary
import com.salez.kasir.ui.theme.SecondaryVariant
import com.salez.kasir.ui.theme.Shapes
import com.salez.kasir.ui.theme.TextPrimary

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPeach,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    secondaryContainer = SecondaryVariant,
    background = BackgroundStart,
    surface = CardBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPeach,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    secondaryContainer = SecondaryVariant,
    background = BackgroundStart,
    surface = CardBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun CafeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundStart, BackgroundEnd)
                    )
                )
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                shapes = Shapes,
                content = content
            )
        }
    }
}