### 1. ¿Qué archivo contiene el código Kotlin principal?

Normalmente el código principal de la aplicación se encuentra en un archivo **`.kt`** dentro de:
En un proyecto de Jetpack Compose, **`MainActivity.kt`** suele ser el punto de entrada de la aplicación y contiene la función `onCreate()` donde se configura la interfaz.

---

### 2. ¿Qué diferencia observás entre hacer Run y abrir solamente un Preview?

* **Run ▶️:** compila y ejecuta la aplicación completa en un dispositivo físico o en un **emulador (AVD)**. Podés interactuar con la aplicación como si estuviera instalada en un celular.
* **Preview 👁️:** muestra una representación de cómo se verá una función `@Composable` **sin ejecutar toda la aplicación**.

---

### 3. ¿Qué es un AVD? ¿Es lo mismo que el emulador?

**AVD** significa **Android Virtual Device**.

Es una **configuración de un dispositivo Android virtual**, donde se define, por ejemplo:

* Modelo/tamaño del dispositivo.
* Versión de Android.
* Cantidad de memoria.
* Resolución.
* Arquitectura.

El **emulador** es el programa que ejecuta ese dispositivo virtual.

Una forma fácil de recordarlo:

> **AVD = configuración del celular virtual**
> **Emulador = programa que ejecuta ese celular virtual**

Por ejemplo, podés crear un AVD que simule un Pixel con Android 15 y después ejecutarlo mediante el emulador.

---

### 4. ¿Qué API de Android utiliza el dispositivo virtual creado?

La **API Level** indica la versión de las APIs disponibles para que las aplicaciones puedan utilizar las funcionalidades de Android.

#### ⚠️ Importante: API Level no es exactamente lo mismo que el nombre comercial de Android.

### 5. ¿Qué ocurre si el AVD está apagado cuando se presiona Run?

Cuando presionás **Run ▶️**, Android Studio intenta encontrar un dispositivo disponible para ejecutar la aplicación.

Si el AVD está apagado, normalmente **Android Studio inicia automáticamente el emulador** y luego espera a que termine de arrancar.

Una vez que el dispositivo está listo:

**Android Studio → compila la aplicación → instala el APK → inicia la aplicación en el AVD.**

Por eso no es necesario que tengas el emulador abierto antes de presionar Run.

### 6. ¿Qué significa @Composable?
El @Composable indica que Saludo() es una función que puede construir o describir elementos de la interfaz de usuario.

### 7. ¿Por qué puede llamar a Text()?
Porque Text() también es una función @Composable.


### 8. ¿Por qué una función Kotlin común no debería llamar directamente a Text()?
Porque Text() necesita ejecutarse dentro del contexto de Compose.

### 9. ¿Cuándo usarías val?
Usás val cuando una variable no necesita cambiar su valor después de ser creada.

### 10. ¿Cuándo sería necesario var?
Usás var cuando necesitás cambiar el valor de la variable.

### 11. ¿Qué significa que Kotlin "infiera" un tipo?
Significa que Kotlin puede darse cuenta automáticamente de qué tipo de dato estás utilizando, sin que tengas que escribirlo.

### 12. ¿Por qué "20" y 20 no representan lo mismo?
Porque uno es un texto (String) y el otro es un número (Int).

### 13. ¿Cómo detecto que estoy repitiendo código?
Puedes ver código repetido o intuir cuando se escriben bloques similares, cuando el diseño de los componentes son iguales en disposición y comportamiento,
si copias y pegas puede ser signo de que es codigo repetido.

### 14. ¿Cómo decidir qué debe ser un parámetro?
Si el contenido es dinamico, deberia ser un parámetro. Si tus bloques tienen botones que hacen cosas distintas al presionarlos, pasa el comportamiento como un parámetro usando funciones lambda.
El parámetro indispensable (Modifier): Siempre incluye un parámetro modifier: Modifier = Modifier por defecto en tu nuevo Composable Esto permite que, cuando uses el bloque, le puedas inyectar márgenes,
colores de fondo o tamaños distintos desde afuera, sin tener que duplicar el código interno.

### 15. ¿Por qué se encadenan los modificadores?
En Jetpack Compose, los objetos Modifier son inmutables. Cada vez que llamas a una función como .padding() o .background(), no estás editando el modificador original. En su lugar, el sistema crea y devuelve un nuevo modificador que envuelve las instrucciones anteriores junto con la tuya.

Al encadenarlos usando el punto (.), estás construyendo una secuencia ordenada de transformaciones mediante un patrón de diseño conocido como Fluent Builder. Esto permite leer el código de forma declarativa, como si fuera una lista de instrucciones paso a paso.

### 16. ¿Por qué el orden altera el resultado?
El modelo de diseño de Compose funciona envolviendo el contenido capa por capa (como una cebolla o muñecas rusas) evaluando los modificadores de arriba hacia abajo, o de afuera hacia adentro. Cada modificador que encadenas define un límite que afecta estrictamente a los modificadores y al contenido que vienen después de él en la cadena.

### 17. ¿Qué ocurre cuando cambia el estado?
Ocurre un proceso llamado recomposición (recomposition). Compose detecta que un valor observado ha cambiado y vuelve a ejecutar automáticamente las funciones composables que leyeron ese estado para actualizar la pantalla con la nueva información.

### 18. ¿Qué ocurre cuando cambia el estado?
Ocurre un proceso llamado recomposición (recomposition). Compose detecta que un valor observado ha cambiado y vuelve a ejecutar automáticamente las funciones composables que leyeron ese estado para actualizar la pantalla con la nueva información.
