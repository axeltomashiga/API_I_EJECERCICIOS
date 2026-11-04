# Análisis de Persistencia: Archivo de Texto vs. Bases de Datos

Este documento analiza las limitaciones de utilizar un archivo de texto plano para persistir datos en una aplicación Android, comparándolo conceptualmente con el uso de una base de datos formal (como Room/SQLite).

Se basa en la experiencia de haber implementado el almacenamiento de `Actividad` en un archivo `actividades_db.txt`.

## Consigna: Análisis de Casos de Uso

A continuación, analizamos qué tan cómodo (o incómodo) resulta resolver distintas situaciones utilizando nuestro enfoque actual basado en archivos de texto (`ActividadRepository`).

### 1. Mostrar solamente actividades pendientes
- **Comodidad:** **Baja/Media.**
- **Análisis:** Para mostrar solo las pendientes, tenemos que leer *todo el archivo* de texto, cargarlo en memoria, convertir *todas* las líneas a objetos `Actividad` y luego aplicar un filtro en memoria (ej. `list.filter { !it.completada }`). Si el archivo es pequeño no hay problema, pero es un proceso muy ineficiente a nivel de memoria y procesamiento.

### 2. Modificar una única actividad
- **Comodidad:** **Muy Baja.**
- **Análisis:** Nuestro repositorio no sabe ir a una línea específica y cambiarla. Para modificar *una sola* actividad, actualmente tenemos que:
  1. Leer y cargar *todas* las actividades desde el archivo a la memoria.
  2. Buscar la actividad en la lista y reemplazarla.
  3. Convertir *toda la lista* nuevamente a texto.
  4. Sobrescribir *todo el archivo* completo.
  Esto es extremadamente ineficiente y propenso a errores (ej. si se apaga el celular justo cuando se está sobrescribiendo el archivo).

### 3. Buscar actividades de una materia específica
- **Comodidad:** **Baja.**
- **Análisis:** Al igual que en el caso 1, no hay forma de "preguntarle" al archivo que nos devuelva solo las matemáticas. Debemos leer el archivo completo, convertir todo a objetos y luego filtrar la lista en el ViewModel o Repositorio.

### 4. Manejar miles de registros
- **Comodidad:** **Inviable / Muy Baja.**
- **Análisis:** Si tenemos 10,000 actividades, cargar el archivo entero en la memoria RAM del celular (en la variable `_actividades.value` del ViewModel) podría provocar un `OutOfMemoryError` (cierre forzoso de la app). Además, el tiempo que tardaría la aplicación en arrancar y leer todo el texto congelaría la pantalla, brindando una pésima experiencia de usuario.

### 5. Identificar cada actividad mediante un ID único
- **Comodidad:** **Media.**
- **Análisis:** Lo resolvimos utilizando `UUID.randomUUID().toString()`, lo cual funciona bien para generar IDs únicos. El problema es que en un archivo de texto, este ID es simplemente una parte más del String (`id|titulo|...`). No hay validaciones reales que impidan que por error escribamos dos líneas con el mismo ID, a diferencia de una Base de Datos que tiene "Primary Keys" que garantizan y protegen esta unicidad.

### 6. Agregar nuevas propiedades al modelo
- **Comodidad:** **Baja (Requiere Migraciones Manuales).**
- **Análisis:** Si mañana decidimos agregar una fecha límite (`val fecha: String`) a `Actividad`, nuestro `ActividadMapper` fallará al leer el archivo viejo, porque esperará 6 partes separadas por `|` y solo encontrará 5. Tendríamos que programar lógica compleja en el Mapper para detectar si es un "formato viejo" o "formato nuevo". Es muy frágil.

---

## Puente Conceptual y Evolución Arquitectónica

El diseño actual de nuestra aplicación sigue este flujo:

```text
Compose (Interfaz Gráfica)
   |
estado de la pantalla (ActividadListScreenViewModel)
   |
ActividadRepository
   |
archivo de texto (almacenamiento)
```

La gran ventaja de haber utilizado Arquitectura MVVM y el patrón *Repository* es que **la interfaz de usuario (Compose) y el ViewModel no saben ni les importa cómo se guardan los datos.** Ellos solo piden o envían listas de `Actividad`.

Esto significa que estamos perfectamente preparados para evolucionar hacia una arquitectura profesional usando **Room (SQLite)**:

```text
Compose
   |
estado de la pantalla
   |
repository (Misma interfaz, distinta implementación)
   |
DAO (Data Access Object - Interfaz de Room)
   |
Room
   |
SQLite (Base de datos real)
```

### ¿Por qué esta evolución resolverá los problemas analizados?
Cuando migremos a Room:
1. **Filtros (Casos 1 y 3):** Le pediremos a la base de datos `SELECT * FROM Actividad WHERE completada = false` y solo nos devolverá lo necesario.
2. **Modificaciones (Caso 2):** Usaremos un `UPDATE Actividad SET completada = true WHERE id = X`, modificando solo ese dato en el disco sin tocar el resto.
3. **Escalabilidad (Caso 4):** Room puede usar "Paginación" (Paging 3), trayendo de a 20 registros a la vez en lugar de 10,000.
4. **Seguridad (Caso 5):** Room maneja las `@PrimaryKey` de forma nativa.
5. **Evolución (Caso 6):** Room cuenta con un sistema de "Migraciones" automáticas para cuando agregamos columnas (propiedades) nuevas a nuestras tablas (clases).

La transición será indolora para nuestra capa visual: **la pantalla no tendrá que cambiar por completo solamente porque cambia el mecanismo de almacenamiento.**

---

## Investigación Corta: Conceptos de Room

Preparándonos para la migración a bases de datos, aquí se define qué representan conceptualmente los siguientes términos clave del ecosistema Room:

- **`@Entity`**: Es una anotación que se le pone a una `data class` de Kotlin para indicarle a Room que esa clase representa una **Tabla** dentro de la base de datos SQLite. Cada propiedad de la clase se convertirá en una columna de la tabla.

- **Clave primaria (Primary Key)**: Es un campo (o grupo de campos) único e irrepetible que sirve para identificar de forma exclusiva a un registro específico dentro de una tabla (Entity). En Room se define con la anotación `@PrimaryKey`. Es lo que evita que existan dos actividades idénticas.

- **DAO (Data Access Object)**: Es un patrón de diseño. En Android, es una `interface` anotada con `@Dao` donde definimos **todos los métodos** que necesitamos para interactuar con la base de datos (insertar, borrar, buscar). Room se encarga de escribir el código real que ejecuta estas acciones por detrás.

- **`@Query`**: Es una anotación que se usa dentro del DAO. Permite escribir consultas SQL directas y personalizadas (ej. `@Query("SELECT * FROM actividad WHERE materia = :materia")`). Es extremadamente útil para búsquedas complejas o filtros específicos.

- **`@Insert`**: Anotación usada en el DAO para indicarle a Room que la función debajo de ella sirve para agregar un nuevo registro (o una lista de registros) a la base de datos. No hace falta escribir SQL, Room lo hace por nosotros.

- **`@Delete`**: Similar a la anterior, es una anotación del DAO que le indica a Room que la función sirve para eliminar un objeto específico de la base de datos. Room busca el objeto por su Clave Primaria y lo borra.

- **Clase de Base de Datos (Database class)**: Es una clase abstracta anotada con `@Database` que hereda de `RoomDatabase`. Es el "punto de entrada" principal. Contiene la configuración global de la base de datos (versión, qué entidades la componen) y expone los DAOs disponibles para que la app pueda usarlos.

- **Repository (Repositorio)**: Es un patrón de arquitectura recomendado por Google. Es una clase normal que actúa como un intermediario entre el ViewModel (la lógica de la pantalla) y las fuentes de datos (el DAO de Room, un archivo de texto, o una API en internet). Su trabajo es decidir de dónde sacar los datos para que al ViewModel no le importe el origen.

---

## Desafío Final: Interfaz de Repositorio

El último desafío pedía dividir nuestra clase repositorio concreta en una `interface NotaRepository` y una implementación concreta `NotaRepositoryImpl`.

### ¿Aporta algo en este tamaño de proyecto o solo agrega una capa más?

**En este tamaño de proyecto (Pequeño):**
Técnicamente, **agrega una capa más (sobreingeniería)**. Al ser una aplicación simple donde la única fuente de datos es la base local Room, el ViewModel podría conectarse tranquilamente a la clase concreta `NotaRepository` sin ningún problema de dependencias. 

**En el mundo real (Mediano/Grande):**
Dividirlo en una Interfaz SÍ aporta un inmenso valor (y es casi obligatorio) por dos motivos:

1. **Testabilidad (Unit Tests)**: Para testear un `ViewModel`, no queremos (ni podemos) levantar una base de datos real de SQLite. Al requerir una *interfaz*, podemos inyectarle un "Falso Repositorio" (Fake o Mock) que devuelva listas falsas instantáneas, aislando el testeo solo a la lógica del ViewModel.
2. **Principio de Inversión de Dependencias (SOLID)**: Nuestro `ViewModel` ya no está acoplado al "cómo" se guardan las notas. Solo interactúa con un contrato (interface). Si mañana decidimos que la app tiene que sincronizarse con Firebase en la nube, simplemente programamos una nueva clase `NotaRepositoryFirebaseImpl : NotaRepository`, y en nuestro inyector de dependencias cambiamos qué versión se le entrega al ViewModel. ¡No habría que tocar ni una sola línea de código en el `ViewModel` ni en Jetpack Compose!

### ¿remember implica persistencia?
No. remember permite conservar un estado durante las recomposiciones de Jetpack Compose, pero no guarda los datos permanentemente. Si se cierra la aplicación o se destruye el proceso, los datos se pierden. 

### ¿Qué diferencia hay entre conservar estado ante recomposición y conservar datos al reiniciar el proceso?
- Conservar estado ante recomposición: significa que los datos no se pierden cuando Compose vuelve a dibujar la pantalla. Para eso se utiliza remember.
- Conservar datos al reiniciar el proceso: significa que los datos siguen existiendo aunque se cierre la aplicación y se vuelva a abrir. Para eso se necesita persistencia, por ejemplo, Room, DataStore o archivos. 

### ¿Todos los datos de una pantalla deberían guardarse en una base?
No. Solamente deberían persistirse los datos que necesitamos conservar después de cerrar la aplicación, como tareas, notas o configuraciones del usuario.

Los datos temporales de la interfaz, como si un menú está desplegado o si un diálogo está abierto, generalmente no necesitan guardarse en una base de datos.

### ¿Qué dato de los que probaste sería absurdo persistir?
Sería absurdo persistir el estado de un menú desplegable, por ejemplo, si está abierto o cerrado, porque es un dato temporal de la interfaz que no tiene sentido conservar cuando el usuario cierra y vuelve a abrir la aplicación.

## 1. ¿Por qué `id` puede empezar en 0 aunque las filas guardadas no queden todas con 0?

Porque `id` tiene `@PrimaryKey(autoGenerate = true)`, lo que significa
que Room genera automáticamente un identificador único para cada nota.
El valor `0` indica que todavía no tiene un ID asignado. Al guardarla,
Room le asigna uno automáticamente.

## 2. ¿Qué parte del objeto existe porque la necesita Room y qué parte pertenece al problema que modelamos?

-   **Parte que necesita Room:** `id`, porque permite identificar de
    manera única cada nota dentro de la base de datos.
-   **Parte que pertenece al problema:** `titulo`, `contenido`,
    `importante` y `creadaEn`, porque representan la información propia
    de una nota.

## 3. ¿Guardarías un `Painter`, un `Modifier` o un texto ya formateado dentro de esta entidad? ¿Por qué?

No. `Painter` y `Modifier` pertenecen a la interfaz gráfica de Jetpack
Compose, no a los datos de la nota. Tampoco guardaría un texto ya
formateado, porque debería almacenar el contenido original y darle
formato cuando se muestra en pantalla.

La entidad debería contener únicamente datos que necesitemos persistir.

## 4. Si mañana cambia el nombre de una propiedad, ¿eso puede afectar una base ya instalada?

Sí. Room utiliza los nombres de las propiedades para relacionarlas con
las columnas de la tabla. Si cambiamos el nombre de una propiedad, puede
producirse un conflicto con una base de datos que ya existe.

Para evitar problemas, debemos realizar una migración de la base de
datos o utilizar `@ColumnInfo(name = "nombre_anterior")` para mantener
el nombre de la columna existente.


## ¿Qué parte del método interpreta Room y qué parte interpreta SQLite?
Room interpreta las anotaciones como @Dao, @Insert y @Query, además de los tipos de datos de Kotlin. A partir de eso, genera el código necesario para ejecutar las operaciones.
SQLite interpreta y ejecuta las consultas SQL, como SELECT, DELETE e INSERT, sobre la base de datos.

## ¿Por qué insertar() puede devolver un Long?
Porque Room devuelve el ID de la fila que se acaba de insertar. Ese identificador permite saber qué nota se guardó y encontrarla posteriormente en la base de datos.

## ¿Qué pasa conceptualmente si una consulta por ID no encuentra nada? ¿Qué tipo de retorno sería razonable?
Si no encuentra ninguna nota con ese ID, la consulta no devuelve ningún resultado. Un tipo de retorno razonable sería NotaEntity?, porque permite devolver una nota o null si no existe.

## ¿Por qué estos métodos no deberían ejecutarse bloqueando el hilo principal?
Porque las operaciones de base de datos pueden tardar y bloquear el hilo principal provocaría que la interfaz deje de responder, generando lentitud o incluso errores de tipo Application Not Responding (ANR).
Por eso se utilizan funciones suspend y corrutinas para ejecutar estas operaciones sin bloquear el hilo principal.

## ¿Qué representa version = 1?
Representa la versión actual del esquema de la base de datos. Room utiliza este número para saber qué estructura tiene la base y detectar cuándo se necesitan migraciones si se realizan cambios en las tablas o columnas.

## ¿Qué relación hay entre la clase AppDatabase, el archivo SQLite y el DAO?
- AppDatabase: representa la base de datos y define qué entidades forman parte de ella y qué DAO se pueden utilizar.
- Archivo SQLite: es donde se almacenan físicamente los datos de la aplicación.
- DAO: permite realizar operaciones sobre la base de datos, como insertar, consultar y eliminar notas.
En resumen, AppDatabase organiza el acceso a la base, SQLite almacena los datos y el DAO permite operar sobre ellos.

## ¿Por qué conviene que exista una única instancia de Room dentro del proceso?
Porque evita crear múltiples conexiones a la misma base de datos, mejora el uso de recursos y permite que los distintos componentes de la aplicación trabajen sobre la misma instancia.
Además, evita problemas de rendimiento y es una buena práctica de arquitectura utilizar un patrón Singleton para administrar la base de datos.


## ¿Qué dato de la UI no debería terminar en la tabla?
Los datos temporales de la interfaz, como si un botón está habilitado, si un campo tiene el foco o si un mensaje de confirmación está visible. Estos datos pertenecen a la UI y no necesitan persistirse en la base de datos.

## ¿Dónde se genera creadaEn y por qué?
Se genera al momento de crear la NotaEntity, utilizando por ejemplo System.currentTimeMillis().
Se hace en ese momento porque representa la fecha y hora en la que se creó la nota. Así queda almacenada y no cambia cada vez que se muestra o consulta.

## ¿Qué señal usarías para saber que una inserción terminó correctamente?
Utilizaría el resultado de la operación de inserción. Si Room devuelve un ID válido, significa que la nota se insertó correctamente.
También podría utilizar un StateFlow o un SharedFlow desde el ViewModel para comunicar el resultado a la UI y mostrar un mensaje de éxito o error.

## ¿Qué riesgo hay en lanzar una corrutina nueva en cualquier lugar sólo para "hacer que funcione"?
Se pueden generar múltiples corrutinas sin control, lo que puede provocar problemas de concurrencia, consumo innecesario de recursos y dificultades para manejar errores.
Además, si no se utiliza un ámbito adecuado, las corrutinas pueden seguir ejecutándose aunque la pantalla ya no exista.
Por eso conviene utilizar un CoroutineScope asociado al ciclo de vida correspondiente, como viewModelScope dentro de un ViewModel.

## ¿En qué se diferencia Flow<List<NotaEntity>> de List<NotaEntity>?
- List<NotaEntity>: representa una lista de notas en un momento determinado. Si se agregan o eliminan notas, la lista no se actualiza automáticamente.
- Flow<List<NotaEntity>>: representa un flujo de datos que puede emitir nuevas listas cuando hay cambios en la base de datos.
Por eso, Flow permite que la pantalla se actualice automáticamente sin tener que consultar nuevamente los datos manualmente.

## ¿Room vuelve a ejecutar la consulta cuando detecta cambios en la tabla relevante?
Sí. Room observa los cambios en las tablas involucradas en la consulta y, cuando detecta una modificación, vuelve a ejecutarla y emite una nueva lista a través del Flow.

## ¿Es lo mismo un Flow que un estado de Compose?
No. Tienen responsabilidades diferentes:
- Flow: es un flujo de datos que permite emitir valores a lo largo del tiempo.
- Estado de Compose: es un valor observable por la interfaz que provoca una recomposición cuando cambia.
Generalmente, se utiliza un Flow para obtener los datos desde el DAO y se transforma en un estado de Compose para que la pantalla pueda reaccionar a los cambios.

## ¿Qué responsabilidad tiene collectAsState o collectAsStateWithLifecycle en este recorrido?
Su responsabilidad es recolectar los valores que emite el Flow y convertirlos en un estado observable por Compose.
- collectAsState(): recolecta el flujo y convierte sus emisiones en un estado de Compose.
- collectAsStateWithLifecycle(): hace lo mismo, pero respeta el ciclo de vida de la aplicación, evitando recolectar datos cuando la pantalla no está activa.
Por eso, es recomendable utilizar collectAsStateWithLifecycle() en las pantallas para mejorar el uso de recursos.

## ¿Cómo sabe @Update qué fila debe modificar?
Room utiliza la clave primaria (id) de la entidad para identificar qué fila debe actualizar. Busca el registro que tenga ese mismo ID y modifica sus valores.

## ¿Qué sucede si se construye un objeto editado con otro ID?
Room intentará actualizar la fila que tenga el nuevo ID. Si no existe una fila con ese identificador, no se modificará ningún registro. No se creará una fila nueva porque se está utilizando @Update y no @Insert.

## ¿Conviene borrar apenas se toca un ícono o pedir confirmación? ¿Acá interesa la decisión de producto además del código?
Depende de la decisión de diseño de la aplicación. Lo recomendable es pedir una confirmación antes de eliminar una nota para evitar borrados accidentales.
También es una decisión de producto, porque hay que pensar en la experiencia del usuario y en qué tan fácil queremos que sea realizar esa acción.

## ¿Qué parte de esta pantalla podría sobrevivir en memoria sin persistirse?
Los datos temporales de la interfaz, como el texto que se está escribiendo antes de guardar, si un diálogo de confirmación está abierto o qué nota está seleccionada para editar.
Estos datos pueden mantenerse en memoria utilizando estados de Compose o el ViewModel, sin necesidad de guardarlos en Room.

## ¿Qué ventaja tiene filtrar en SQL en vez de traer todo y usar filter?
La principal ventaja es que el filtrado se realiza directamente en la base de datos, evitando traer todos los registros a la aplicación.
Esto mejora el rendimiento, reduce el uso de memoria y permite trabajar de manera más eficiente cuando hay muchos datos.

## ¿Cómo entra el parámetro :texto en la consulta?
Room utiliza :texto como un parámetro que se reemplaza por el valor recibido en la función buscarPorTitulo(texto: String).
Por ejemplo, si el usuario escribe "Android", Room utiliza ese texto en la consulta para buscar las notas cuyo título contenga esa palabra.

## ¿Qué diferencia hay entre ordenar en SQL y ordenar la lista en memoria?
- Ordenar en SQL: la base de datos devuelve los resultados directamente ordenados mediante ORDER BY. Es más eficiente porque no hace falta ordenar los datos nuevamente en Kotlin.
- Ordenar en memoria: primero se traen los datos y después se ordenan utilizando funciones de Kotlin, como sortedBy o sortedByDescending. Esto consume más memoria y procesamiento de la aplicación. 

## ¿Qué consulta podría beneficiarse de un índice si la tabla creciera mucho?
La búsqueda por título podría beneficiarse de un índice sobre la columna titulo, ya que permitiría localizar registros más rápidamente.
También podría ser útil un índice sobre creadaEn para mejorar el rendimiento de las consultas que ordenan las notas por fecha.
Los índices aceleran las búsquedas y ordenamientos, aunque ocupan espacio adicional y pueden hacer que las inserciones y actualizaciones sean un poco más lentas.

## ¿Qué gana el proyecto con esta separación y qué costo agrega?
Gana organización, mantenibilidad y separación de responsabilidades. Cada capa tiene una función específica: la UI muestra los datos, el ViewModel maneja el estado, el Repository administra el acceso a los datos y el DAO realiza las consultas a Room.
Como costo, agrega más clases y código, lo que puede parecer innecesario en aplicaciones pequeñas.

## ¿El Repository tiene que existir siempre? Justificar para este caso.
No necesariamente. En aplicaciones muy simples, el ViewModel podría comunicarse directamente con el DAO.
Pero en este caso conviene utilizar un Repository porque centraliza las operaciones de datos, evita que el ViewModel conozca los detalles de Room y permite cambiar o agregar otras fuentes de datos en el futuro.

## ¿Qué clase debería decidir si un título vacío es válido?
La validación debería realizarse en el ViewModel o en una capa de dominio, ya que es una regla del problema y no una responsabilidad de la interfaz ni de la base de datos.
La UI puede mostrar un mensaje de error, pero la decisión de si el título es válido debería estar fuera de ella.

## Si mañana los datos vinieran de red y de Room, ¿qué parte de esta estructura sería el mejor lugar para coordinarlos?
El Repository, porque es el encargado de administrar y coordinar las distintas fuentes de datos.
Podría consultar la información de Room y de una API, decidir de dónde obtener los datos y mantenerlos sincronizados sin que el ViewModel o la UI necesiten conocer esos detalles.


## ¿Un índice único es lo mismo que una clave primaria?
No. Ambos garantizan que no haya valores repetidos, pero tienen diferencias:
- Clave primaria (PrimaryKey): identifica de manera única cada fila de la tabla y no puede ser nula.
- Índice único (unique = true): impide que se repitan los valores de una columna, pero no necesariamente identifica la fila como su clave principal.
En este caso, id sigue siendo la clave primaria y codigoExterno tiene un índice único para evitar duplicados.

## ¿Qué problema puede traer REPLACE si se usa por comodidad?
Puede eliminar el registro existente que tiene el mismo valor de clave o índice único y reemplazarlo por uno nuevo.
Esto puede provocar la pérdida de información del registro anterior y generar problemas con relaciones o datos asociados.
Por eso no conviene utilizarlo sin analizar sus consecuencias.

## ¿Los índices mejoran todo gratis?
No. Los índices permiten acelerar las búsquedas, los filtros y algunos ordenamientos, pero tienen un costo:
- Ocupan espacio adicional en la base de datos.
- Hacen que las inserciones, actualizaciones y eliminaciones sean un poco más lentas, porque también hay que actualizar los índices.
Por eso conviene crear índices solamente en las columnas que se utilizan frecuentemente para buscar o filtrar información.

## ¿Cómo comprobarías que una restricción realmente se está cumpliendo?
Intentaría insertar dos notas diferentes con el mismo codigoExterno.
Si el índice único está funcionando correctamente, la segunda inserción debería fallar o ser ignorada, dependiendo de la estrategia de conflicto elegida.
También comprobaría los registros desde Database Inspector para verificar que no existan dos filas con el mismo código externo.

## ¿Por qué no conviene guardar directamente una lista de notas dentro de una columna de categoría?
En bases de datos relacionales (como SQLite/Room), guardar una lista o un arreglo (ej. `List<Nota>`) dentro de una sola celda de una columna viola la **Primera Forma Normal (1NF)**. SQLite no soporta listas nativamente. Para hacerlo funcionar en Room tendrías que convertir la lista a un String largo (ej. formato JSON) usando `TypeConverters`. 
Hacer esto te quitaría todo el poder de las bases de datos: no podrías usar queries como `WHERE titulo = X` o contar las notas de forma eficiente, obligándote a deserializar el JSON en Kotlin constantemente, lo cual es lento e ineficiente.

## ¿Dónde queda la clave foránea?
En una relación "Uno a Muchos" (Una categoría tiene muchas notas), la **clave foránea siempre queda del lado de los "Muchos"**. 
Es decir, la clave foránea es la columna `categoriaId` y queda guardada dentro de la tabla `notas` (en la clase `NotaEntity`).

## ¿Qué debería ocurrir con las notas si se elimina una categoría?
Depende puramente de las reglas de negocio de tu aplicación. 
- Si decides que una nota **no puede** existir sin su categoría, deberías usar la política de borrado en cascada (`CASCADE`). Al borrar la categoría, todas las notas que contenían su ID se destruyen automáticamente.
- Si decides que una nota **sí puede** existir sin categoría (es nuestro caso actual), las notas no deben borrarse. Su campo `categoriaId` debería pasar a valer `null`. En SQL y Room esto se logra configurando la política `SET_NULL`.

## ¿Qué diferencia hay entre una entidad y una clase que sólo representa un resultado relacional?
- Una **Entidad** (anotada con `@Entity`) se traduce directamente a una **Tabla** física real en la base de datos de SQLite.
- Una **clase relacional** (como `CategoriaConNotas`, que usa `@Embedded` y `@Relation`) **no crea ninguna tabla**. Es simplemente un Objeto de Transferencia (POJO) que Room utiliza en la memoria (RAM) para empaquetar de forma cómoda la respuesta obtenida después de "cruzar" (hacer un JOIN/Querys) dos o más tablas reales que ya existen.

### Desafío: Política de Borrado de ForeignKey
Añadimos la regla de `ForeignKey` en el archivo `NotaEntity.kt`.
Elegimos conscientemente la política `onDelete = ForeignKey.SET_NULL`. 
Para que esto sea posible en Kotlin, declaramos la propiedad con un tipo de dato nulable: `val categoriaId: Int? = null`.

**Efecto:**
Si tuvieras 5 notas asociadas a la categoría "Estudios" (id=1), todas tendrían `categoriaId = 1` en la base de datos.
Al ejecutar un `@Delete` sobre la categoría "Estudios", en vez de producirse un error o borrarse las notas, Room irá a la tabla `notas`, buscará esas 5 notas, y cambiará su `categoriaId` a `null`. Las notas sobrevivirán como notas huérfanas (sin categoría).

---

## TypeConverters y Tipos Expresivos

En este ejercicio reemplazamos el campo primitivo `importante: Boolean` por un tipo más expresivo: un enum `Prioridad { BAJA, MEDIA, ALTA }`. Como SQLite no entiende qué es un Enum de Kotlin, tuvimos que usar `@TypeConverter` para traducirlo a `String` al momento de guardarlo.

### 1. ¿Room guarda el enum completo?
No. Room no puede guardar el objeto Enum de Kotlin en la base de datos. Lo que hace es delegar en nuestro `@TypeConverter`, el cual toma el nombre del enum (`valor.name`) y guarda un simple **texto plano (String)** en SQLite, como `"ALTA"`, `"MEDIA"`, etc.

### 2. ¿Qué pasa si en una versión futura se renombra ALTA a URGENTE?
Si solo cambiamos el nombre en el código Kotlin (de `ALTA` a `URGENTE`) pero dejamos las funciones del convertidor tal como están, **la aplicación va a crashear (IllegalArgumentException)** al intentar leer notas viejas de la base de datos.
Esto ocurre porque la base de datos sigue guardando el String `"ALTA"`, y cuando el `@TypeConverter` intenta hacer `Prioridad.valueOf("ALTA")`, Kotlin arrojará un error porque ese valor ya no existe en el Enum.
*Por eso, en nuestro código del TypeConverter agregamos un bloque `try-catch` que devuelve un valor por defecto (Fallback) si falla la conversión, previniendo este error catastrófico.*

### 3. ¿Qué ventaja tiene guardar una fecha como número y qué ventaja tiene guardarla como texto?
- **Como número (Long - Unix Timestamp):** Es extremadamente eficiente para la base de datos. Ocupa menos espacio, y lo más importante: **es fácil de ordenar y comparar matemáticamente**. Consultas como `ORDER BY fecha DESC` o buscar notas entre dos fechas (`WHERE fecha BETWEEN x AND y`) son instántaneas para SQLite.
- **Como texto (String - ISO 8601):** La principal ventaja es la **legibilidad humana**. Si abrimos el archivo SQLite (o el Database Inspector), es mucho más fácil para nosotros leer `"2024-05-10T12:00:00Z"` que intentar descifrar el número `"1715342400000"`. También facilita la depuración si la base de datos se exporta a otros sistemas que solo leen texto.

### 4. ¿Qué parte del problema resuelve el TypeConverter y qué parte no?
- **Qué resuelve:** El TypeConverter actúa como un "traductor automático" bidireccional entre la base de datos (SQLite) y tu código de la aplicación (Kotlin). Te permite usar clases complejas (Enums, Fechas de Java 8 como `LocalDate`, Listas, Objetos anidados) en tu código, sin tener que preocuparte de convertirlos a primitivos (Long, String) cada vez que haces un Insert o un Select.
- **Qué NO resuelve:** No resuelve el problema del **filtrado o búsqueda nativa en SQLite** usando las propiedades internas de esos objetos complejos. Por ejemplo, si tienes un TypeConverter que guarda una lista de etiquetas en formato JSON como `["casa", "trabajo"]`, SQLite no entiende ese JSON, solo ve un texto. No puedes pedirle a SQLite de forma nativa que filtre eficientemente "dame las notas que tengan la etiqueta 'casa' dentro de ese JSON". Para eso se requiere diseño de tablas relacionales.

---
#### 1. ¿Qué significa que una operación sea atómica?
Significa que se ejecuta como una **unidad indivisible**: cumple estrictamente con el principio de "todo o nada" (All or Nothing).
Si una operación atómica consta de varios pasos y alguno de ellos falla, la transacción deshace automáticamente todos los pasos previos mediante un `ROLLBACK`, dejando la base de datos exactamente en el mismo estado en el que estaba antes de iniciar.

#### 2. ¿Una transacción sirve sólo para escrituras?
**No.** Las transacciones también son fundamentales para **lecturas relacionales complejas** (como `@Relation` o consultas compuestas). Sirven para garantizar la propiedad de **Aislamiento (Isolation)** de las propiedades ACID. Aseguran que mientras se ejecutan múltiples consultas para construir un resultado relacional en memoria, ninguna otra escritura modifique la base de datos a mitad de camino, evitando lecturas inconsistentes o desincronizadas.

#### 3. ¿Qué error lógico podría dejar la base en un estado válido para SQLite pero inválido para la app?
Un error lógico que respeta **todas las restricciones técnicas de SQLite** (claves primarias únicas, tipos de datos correctos, claves foráneas válidas), pero que **viola una regla de negocio de la aplicación**.
*Ejemplo:* Dejar una categoría vacía después de una operación de "mover y eliminar", o dejar un saldo contable en negativo, o un carrito de compras confirmado pero sin productos. Para SQLite la estructura relacional es totalmente válida, pero para la lógica de negocio de la aplicación la base quedó en un estado inválido o inconsistente.

#### 4. ¿Qué parte conviene probar forzando un fallo controlado?
Conviene forzar un fallo controlado **justo en el punto medio entre las operaciones individuales que componen la transacción** (después de ejecutar el primer paso y antes de ejecutar el último).
Al inyectar una excepción en ese punto intermedio se puede comprobar e instrumentar mediante pruebas:
1. Que **sin transacción**, los datos sufren una modificación parcial e inconsistente.
2. Que **con `@Transaction`**, Room captura el fallo, ejecuta el `ROLLBACK` y revierte la base de datos a su estado original.

---
#### 1. ¿Por qué cambiar una Entity puede requerir una migración aunque el código compile?
El compilador de Kotlin/Java únicamente verifica la validez del código fuente del proyecto (sintaxis, clases y tipos). Sin embargo, la base de datos SQLite vive en un archivo físico en el almacenamiento del celular con una **estructura de tablas fija (esquema SQL)**.
Al agregar una propiedad a una `@Entity` (por ejemplo `val archivada: Boolean`), el código compila perfectamente, pero cuando Room arranca y compara el esquema guardado en disco contra las entidades en memoria, detecta una falta de coincidencia (falta la columna en SQLite). Si no existe una migración que aplique un `ALTER TABLE`, Room detendrá la ejecución arrojando una `IllegalStateException`.

#### 2. ¿Qué sucede si se sube la versión pero no se registra un camino de migración?
Si se incrementa el atributo `version` en `@Database` (por ejemplo de 1 a 2) sin proporcionar la correspondiente migración con `.addMigrations(...)`:
- **Comportamiento estándar de Room:** La aplicación se **cierra con un crash** en tiempo de ejecución (`IllegalStateException: A migration from 1 to 2 was required but not found`).
- **Si se tiene configurado `fallbackToDestructiveMigration()`:** Room **elimina y recrea todas las tablas** (`DROP TABLE`), causando la **pérdida permanente e irrecuperable de la información** previamente guardada por el usuario.

#### 3. ¿Qué datos deberían tener las filas antiguas en la nueva columna?
Las filas antiguas deben recibir un **valor por defecto coherente con las reglas del negocio** definido en la sentencia SQL mediante `DEFAULT`.
Para el campo `archivada: Boolean` (que en SQLite se almacena como `INTEGER 0` para `false` y `1` para `true`), la instrucción `ALTER TABLE notas ADD COLUMN archivada INTEGER NOT NULL DEFAULT 0` asigna automáticamente el valor `0` (`false`) a todos los registros antiguos.

#### 4. ¿Por qué una migración es parte del diseño de datos y no sólo un detalle de Gradle o Android Studio?
Porque una migración determina **cómo se preserva y transforma la información histórica del usuario** a medida que la aplicación evoluciona.
No se trata de una simple preferencia del entorno de compilación, sino de una decisión fundamental de arquitectura que garantiza la supervivencia de los datos de producción, define valores por defecto coherentes e impide que cambios en las entidades corrompan o borren la información almacenada.

---

## Ejercicio 1 — ¿Quién está haciendo demasiado?

### Análisis y Esquema de Responsabilidades (App de Notas MVVM + Room)

Se ha refactorizado la aplicación de notas para aislar responsabilidades, agrupando las clases en los paquetes `ej1.data`, `ej1.model`, `ej1.view` y `ej1.viewmodel`.

La UI ahora es una "Vista Tonta" (`NotasScreen.kt`) que recibe órdenes y estado del `NotasEj1ViewModel.kt`, quien a su vez delega el guardado persistente en Room al `NotasEj1Repository.kt`.

#### Esquema de Separación Actual

| **View (Capa de Presentación / UI)** | **ViewModel (Lógica y Estado)** | **Model / Repository (Capa de Datos)** |
| :--- | :--- | :--- |
| **Dibujar la interfaz**: Muestra los `TextField`, el `Button`, los mensajes de error, y la lista de notas. | **Validación de datos**: Revisa que el título o contenido no estén en blanco en `onGuardarClick`. | **Interacción con SQLite**: Expone operaciones persistentes mediante el patrón `Dao` de Room. |
| **Capturar y delegar eventos**: Detecta cuando se presiona "Guardar" y avisa al ViewModel. | **Ejecución de Casos de Uso**: Inicia una corrutina y pide al repositorio que guarde la nueva nota. | **Intento de guardado**: Guarda físicamente un `NotaEntity` en la base de datos de manera asíncrona. |
| **Observación reactiva**: Escucha pasivamente los cambios de estado (mediante `collectAsStateWithLifecycle`) y se redibuja. | **Almacenamiento de estado temporal**: Mantiene en memoria el formulario (`NotasFormUiState`) mientras se escribe. | **Exposición de datos crudos**: Provee un `Flow<List<NotaEntity>>` directo desde la base de datos (Room). |

---

### Preguntas

#### ¿Qué trabajo tendría que seguir funcionando aunque mañana cambiemos Compose por otra interfaz (ej. XML o Terminal)?
Todo el trabajo encapsulado en el **ViewModel y en la capa de Datos (Repository/Room)** debería seguir operando idénticamente sin tocar ni una línea de código. Esto incluye:
- Las **validaciones de negocio** (asegurarse de que el título no esté vacío antes de guardar).
- La **persistencia de datos** (insertar registros en SQLite a través del DAO).
- La **emisión de la lista histórica** (el `Flow` emitido desde Room hacia el estado).
Al cambiar de Compose a XML, la única capa a reescribir sería `ej1.view`, que pasaría a observar las mismas variables reactivas (StateFlow) del ViewModel pre-existente.

#### ¿Qué datos existen sólo mientras se está escribiendo un formulario?
El **estado temporal de los campos de texto en edición**.
Esos datos efímeros actualmente viven en el StateFlow `NotasFormUiState` (dentro del ViewModel) y existen únicamente como borrador mientras el usuario escribe en la pantalla. 
Si el usuario navega hacia atrás o la aplicación se cierra de repente antes de que haga clic en "Guardar", esos caracteres parciales no llegan al `Repository` ni se persisten en Room, por lo cual se descartan y se pierden por diseño.

### Desafío: Flujo Unidireccional (UDF)

En nuestro código (MVVM), la UI interactúa con el estado siguiendo este ciclo riguroso de una sola vía:

```text
 1. EVENTO DEL USUARIO: "viewModel.onGuardarClick()"
 ------------------------------------------------------>
|                                                       |
|   VIEW (Compose)                       VIEWMODEL / REPOSITORY (Room)
|                                                       |
 <------------------------------------------------------
 2. ESTADO QUE VUELVE: Emisión reactiva desde StateFlow (Lista Actualizada)
```

---

## Ejercicio 2 — Una pantalla no es su estado

### Análisis e Implementación

En este ejercicio se creó el paquete `ej2` conteniendo:
1. Una *data class* de estado estricto: `NotasEj2UiState`
2. Una clase sellada de eventos: `NotasEj2Event`
3. El `NotasEj2ViewModel` que procesa los eventos y actualiza el estado.
4. La vista `NotasEj2Screen` que se dibuja *exclusivamente* basada en la data class.

El guardado se simula mediante una corrutina y `delay` para observar el ciclo de carga.

#### Tabla "Evento -> Cambio de Estado Esperado" (Desafío)

| Evento (`NotasEj2Event`) | Modificación en `NotasEj2UiState` (Cambio esperado) |
| :--- | :--- |
| `OnTituloChange("H")` | `titulo` pasa a ser `"H"`. `errorValidacion` se vuelve `null` (se borra). |
| `OnContenidoChange("M")` | `contenido` pasa a ser `"M"`. `errorValidacion` se vuelve `null`. |
| `OnGuardarClick` (Vacío) | Si está vacío: `errorValidacion` cambia a "El título/contenido es obligatorio". |
| `OnGuardarClick` (Válido) | `guardando` cambia a `true`. (Esto desactiva los textfields y muestra un loader). |
| *Fin del Delay Interno* | `guardando` a `false`. `titulo` y `contenido` a `""`. `mensajeExito` se llena. |
| `OnMensajeExitoMostrado` | `mensajeExito` vuelve a `null` (para que no se vuelva a mostrar el Toast al rotar la pantalla). |

---

### Preguntas

#### ¿El texto ingresado es lo mismo que la nota ya guardada?
**No.**
El texto que se está ingresando es el "borrador" temporal de la pantalla (actualmente reflejado en `titulo` y `contenido` de la *data class* de estado). Solo se convierte en una "nota guardada" (información persistente y valiosa) después de pasar las validaciones del negocio y de concluir el proceso asíncrono de guardado.

#### ¿Conviene mostrar el mensaje de error como parte del estado?
**Sí, absolutamente.**
En Compose (y en general en UDF), la pantalla debe ser un reflejo puro y exacto del estado en ese milisegundo. Si existe un error (por ejemplo "El título es obligatorio"), este *es* parte de la realidad actual del usuario. Al incluir `val errorValidacion: String?` en la *data class*, nos aseguramos de que Compose redibuje automáticamente el texto en rojo bajo los campos, y que ese error sobreviva si la pantalla se recarga (rotación) hasta que el usuario cambie el texto y el estado lo vuelva a poner en `null`.

---

## Ejercicio 3 — Primer ViewModel, sin magia

### Implementación y Pruebas
Se creó el paquete `ej3` aislando la lógica en una clase `NotasEj3ViewModel` que hereda explícitamente de `androidx.lifecycle.ViewModel`.

#### ¿Qué sucede al rotar el emulador?
Si el usuario escribe "Comprar pan" en el título y se rota la pantalla (lo que destruye y recrea la `Activity`), **el texto "Comprar pan" se mantiene intacto**.
Esto ocurre porque la función `viewModel()` de Compose intercepta la destrucción de la vista, retiene la instancia única del ViewModel en memoria, y se la vuelve a inyectar a la pantalla recién recreada junto con su `StateFlow` intacto.

#### Desafío: El Contador de Vida
Se añadió el atributo `val intentosGuardado: Int = 0` al estado. Cada vez que se intenta guardar una nota, el ViewModel lo incrementa. 
- Si guardamos 3 veces, el contador dice `3`.
- Si rotamos la pantalla, el contador **sigue diciendo `3`**.
- La consola imprime `¡Instancia Creada!` sólo una vez al iniciar. No vuelve a imprimirse al rotar. Esto demuestra visualmente que el ViewModel no muere ante cambios de configuración.

### Preguntas

#### ¿Qué problema resuelve el ViewModel?
El ViewModel resuelve el problema crítico de la **supervivencia del estado de presentación frente a los cambios de configuración** de Android (rotación de pantalla, cambio a modo oscuro, redimensionamiento multi-ventana, cambio de idioma).
Normalmente, todos estos eventos provocan que el sistema operativo destruya y vuelva a crear la `Activity` o `Fragment` desde cero, borrando cualquier variable alojada en ellos. El ViewModel proporciona un refugio seguro (fuera del ciclo de vida de la Activity) para almacenar datos temporales de UI y ejecutar trabajos asíncronos pesados (corrutinas) sin que se interrumpan por una simple rotación.

#### ¿Qué sucederá si el proceso se elimina completamente?
Si el sistema operativo mata el proceso entero por falta de memoria (System-Initiated Process Death), **el ViewModel también será destruido** y su estado en memoria RAM se perderá (se ejecutará su método `onCleared()`). 
En este escenario extremo, al reabrir la app, el ViewModel se instanciará desde cero. Para sobrevivir a este caso particular, se debería utilizar `SavedStateHandle` (para datos pequeños de UI) o una base de datos persistente como Room (para la información valiosa, que es precisamente nuestro objetivo final).

---

## Ejercicio 4 — StateFlow: la pantalla observa

### Implementación
Se creó el paquete `ej4` donde se exponen estrictamente dos variables para manejar el estado en `NotasEj4ViewModel`:
1. `private val _uiState`: Mutable, sólo modificable desde dentro del ViewModel.
2. `val uiState: StateFlow<...>`: Observable, de sólo lectura para la vista.

En la vista (`NotasEj4Screen`), este estado se recolecta utilizando `collectAsStateWithLifecycle()`, lo que asegura que las recolecciones se detengan automáticamente cuando la pantalla pasa a segundo plano, ahorrando recursos de CPU y memoria.

### Preguntas

#### ¿Por qué la pantalla no debería poder modificar directamente `_uiState`?
Para respetar el **Flujo de Datos Unidireccional (UDF)** y mantener el **Single Source of Truth (Única Fuente de Verdad)**. 
Si la pantalla pudiera acceder y mutar libremente a `_uiState.value = nuevoEstado`, estaríamos saltándonos el ViewModel. Esto provocaría que los datos se modifiquen sin pasar por las reglas de validación (lógica de negocio) y dificultaría enormemente rastrear bugs, ya que cualquier componente UI en cualquier lugar podría alterar los datos arbitrariamente. Obligando a la vista a usar métodos como `actualizarTitulo(nuevoTitulo)`, garantizamos que el ViewModel tenga el control total sobre *cuándo* y *cómo* muta el estado.

#### ¿Qué parte se recompone?
Se recomponen **exclusivamente aquellos Composables que leen directamente las propiedades del estado que acaban de cambiar** (Smart Recomposition).
Por ejemplo, si se ejecuta `actualizarTitulo("A")`, el valor de `uiState.titulo` muta. Jetpack Compose evaluará el árbol y *solo* redibujará el `TextField` correspondiente al Título (que usa esa propiedad). El `TextField` del Contenido no se recompondrá porque su estado subyacente no sufrió alteraciones.

### Desafío (Registrar cambios puntuales y comprobar)

El código del ViewModel se implementó utilizando el método `.copy()` de las data classes de Kotlin:

```kotlin
fun actualizarTitulo(nuevoTitulo: String) {
    _uiState.value = _uiState.value.copy(titulo = nuevoTitulo)
}
```

Al utilizar `.copy(titulo = nuevoTitulo)`, Kotlin clona el objeto inmutable actual y genera una nueva instancia exacta, pero **reemplazando únicamente el campo `titulo`** y preservando intacto el valor pre-existente en `contenido`. 
Gracias a esto, podemos escribir en el Título, luego pasar a escribir en el Contenido, y confirmar que el texto del Título **no se borra**, demostrando que el estado global se mantiene consistente y se actualiza de manera aislada (puntual).

---

## Ejercicio 5 — Validar sin esconder la regla en un botón

### Implementación

Se creó el paquete `ej5` y la clase `NotasEj5ViewModel` para alojar estrictamente la lógica de validación de negocio fuera de la Vista. Las reglas que se codificaron en `validarYGuardar()` son:
- **Título no vacío**: `titulo.isNotBlank()`.
- **Contenido de al menos 10 caracteres**: `contenido.trim().length >= 10`.
- **Prohibición de guardar dos veces**: Evaluando `if (_uiState.value.guardando) return`.
 
En `NotasEj5Screen`, aprovechamos el atributo nativo `supportingText` de los `TextField` de Material 3 para desplegar los errores directamente por debajo de los campos de texto, junto a `isError = true` para teñir el componente de rojo, sin utilizar diálogos molestos.

### Preguntas

#### ¿Cuál es una regla de negocio y cuál es una decisión visual?
- **Reglas de Negocio:**
  - Que el contenido deba tener 10 caracteres como mínimo.
  - Que el título no pueda estar vacío o contener puros espacios en blanco.
  - Que el sistema impida una doble solicitud a la base de datos o red.
  *(Todo esto le pertenece al ViewModel y al Modelo).*
- **Decisiones Visuales:**
  - Que el error se muestre debajo de la caja de texto (`supportingText`).
  - Que la caja de texto se tiña de color rojo (`isError = true`).
  - Mostrar un *toast* o una barra de progreso circular mientras se espera.
  *(Todo esto le pertenece estrictamente a la View / Jetpack Compose).*

#### ¿En qué momento conviene limpiar un mensaje de error?
Conviene limpiarlo (establecerlo en `null` en el estado) **en el momento preciso en que el usuario intenta corregirlo**, es decir, durante la pulsación de una nueva tecla (`onValueChange`). 
Si el usuario ve que el campo falló porque estaba vacío, apenas tipea una letra el estado debe reaccionar mediante `onEvent(OnTituloChange("a"))` y limpiar el error (`errorTitulo = null`). Esto mejora enormemente la experiencia de usuario (UX) al darle feedback visual inmediato de que su corrección está siendo procesada, sin obligarlo a presionar el botón "Guardar" de nuevo solo para quitar el color rojo.

---

## Ejercicio 6 — Repository: cambiar datos sin cambiar la pantalla

### Implementación

Se definió el **Contrato** agnóstico (`NotaRepository.kt`), estableciendo las reglas para cualquier fuente de datos (obtenerTodas, guardar, eliminar). Luego se implementó concretamente en memoria mediante `NotaRepositoryInMemoryImpl.kt`, que usa un `MutableStateFlow` interno para almacenar las notas en una lista RAM (sin Room, sin Base de Datos). 

En el `MainActivity.kt`, se inyectó esta versión *falsa* o *en memoria* al `NotasEj6ViewModelFactory`. 

Como el `NotasEj6ViewModel` sólo habla con el contrato (la `interface`), **ignora completamente** que los datos no se están guardando de verdad en el celular, por lo cual todo su código funciona de manera transparente.

### Preguntas

#### ¿Qué conoce el Repository sobre Compose?
**Absolutamente nada.** 
El patrón Repository pertenece a la capa de Datos del proyecto. No conoce (ni debe conocer) absolutamente ningún detalle sobre Material Design, `Scaffold`, composables, colores de error ni sobre la librería de Jetpack Compose en sí. Su único conocimiento está basado en flujos de datos puros de Kotlin (`Flow`, `suspend fun`) y en las entidades o modelos de negocio puros (`Nota`).

#### ¿Por qué esta interfaz ayuda a probar la app?
Al depender de una `interface` (`NotaRepository`) en lugar de depender directamente de una clase que maneja una Base de Datos física (como Room/SQLite), **aplicamos el Principio de Inversión de Dependencias (SOLID)**.
Esto es vital para probar (Testear) la aplicación:
1. **Pruebas Unitarias del ViewModel:** Podemos inyectarle un Repositorio Falso o Simulacro (`FakeRepository` / Mock) al ViewModel que devuelva listas prediseñadas al instante, lo que permite testear las reglas de negocio rápidamente sin inicializar ni tocar el lento disco duro o base de datos.
2. **Prototipado UI Rápido:** Como hicimos en esta entrega, pudimos construir toda la aplicación funcional con un *Repository In-Memory* sin haber escrito ni una sola línea de SQL ni creado las entidades de Room todavía.

#### Decidir si el resultado de "obtenerTodas" se devuelve como valor puntual (`List<Nota>`) o como `Flow<List<Nota>>` y justificarlo:
Se decidió (y se implementó) que **se devuelva como un `Flow<List<Nota>>`**.
**Justificación:**
Si se devolviera como valor puntual (`suspend fun obtenerTodas(): List<Nota>`), la UI requeriría que el desarrollador pida manualmente los datos cada vez que ocurra un cambio (agregado/borrado).
Devolviendo un **Flow**, establecemos una conexión reactiva directa (un *stream* de datos constante) con la base de datos o almacenamiento. Si otra pantalla, o una sincronización de fondo con la red, actualiza, guarda o borra una nota del Repositorio, **el Flow emitirá automáticamente la nueva lista de notas**, y la UI se actualizará al instante por sí sola sin necesidad de invocar ninguna función manual de recarga (ni siquiera en el ViewModel local).

---

## Ejercicio 7 — Leer un archivo de texto desde el Repository

### Implementación
Se creó el paquete `ej7`.
Se implementó `ArchivoTextoRepository.kt` utilizando una clase sellada exhaustiva `ResultadoLectura` (con `Exito`, `NoExiste`, y `ErrorFalla`). El ViewModel invoca el repositorio y publica el estado en la interfaz mediante `estadoLecturaInfo`. 
A su vez, en la pantalla se incluyeron un TextField y un Button extra puramente de "Prueba Manual" (Test) para poder inyectar texto al archivo `borrador.txt`, permitiendo corroborar el funcionamiento real de la lectura/escritura física en el almacenamiento del dispositivo.

### Preguntas

#### Si el archivo todavía no existe, devolver texto vacío o un resultado explícito; dejar por escrito cuál de las dos decisiones se tomó:
Se decidió **devolver un resultado explícito**.
En el repositorio de la aplicación se optó por crear una `sealed class ResultadoLectura` y devolver el caso `ResultadoLectura.NoExiste`.
**Motivo:** Si simplemente se devolviera un `""` (texto vacío), el ViewModel (y por tanto la Vista) no podrían diferenciar matemáticamente si el usuario creó un borrador que casualmente está en blanco o si el archivo literalmente no existe (es la primera vez que se abre la app). Devolver `NoExiste` le permite a la Vista tomar decisiones de UI (ej. mostrar el texto "El archivo borrador.txt aún no existe", como se implementó en este ejercicio).

#### ¿Dónde está físicamente el archivo?
El archivo `borrador.txt` se encuentra guardado físicamente en el almacenamiento **interno y privado** que Android le asigna a esta aplicación específica. 
A nivel de sistema de archivos (en el celular físico o emulador), la ruta suele ser:
`/data/data/ar.edu.uade.axelhiga.ejercicios/files/borrador.txt`.
A esta ruta llegamos instanciando la variable `context.filesDir`.

#### ¿Otra app puede leerlo sin más?
**No.**
Los archivos guardados dentro del directorio `filesDir` (Almacenamiento Interno) son estrictamente privados. El sistema operativo Android (usando sandboxing de Linux basado en UID) prohíbe terminantemente que cualquier otra aplicación acceda, lea o modifique este archivo. Solo la propia aplicación (o un dispositivo con permisos de `root`) puede visualizarlo.

---

## Ejercicio 8 — Guardar texto, cerrar y volver

### Implementación
Se creó el paquete `ej8`. 
En el `BorradorRepository`, se agregó la función `escribirBorrador` que utiliza `archivo.writeText(texto, Charsets.UTF_8)` (sobrescritura total) envolviéndolo en un bloque de `Dispatchers.IO`. 
El `NotasEj8ViewModel` se modificó para que apenas nace, en su bloque `init {}`, lance automáticamente una corrutina para leer el contenido del disco y cargarlo en la UI, garantizando así la recuperación de los datos guardados luego de cerrar o matar la aplicación.

### Preguntas

#### ¿Se sobrescribe todo el archivo o se agrega al final?
**Se sobrescribe todo el archivo.** 
En el Repositorio utilicé la función nativa `writeText()`, la cual destruye el contenido previo del archivo y escribe el nuevo texto desde cero. 
**Justificación (Documentación de la decisión):** Dado que estamos guardando el estado actual de "un borrador", lo que el usuario quiere conservar es exactamente lo que ve en la pantalla en ese instante. Si hubiese optado por agregar texto al final (ej. `appendText()`), cada vez que el usuario clickeara "Guardar" la nota se duplicaría, generando un registro gigante e ilegible del historial de guardados.

#### ¿Qué diferencia hay entre el estado del ViewModel y los datos en disco?
- **El estado del ViewModel (`textoFormulario`)** vive exclusivamente en la **Memoria RAM** del dispositivo (y de forma volátil). Este estado es rapidísimo de leer y escribir (se actualiza instantáneamente con cada pulsación del teclado), pero si la app se cierra o el proceso es destruido, desaparece para siempre.
- **Los datos en disco (`borrador_ej8.txt`)** viven en el **Almacenamiento Físico Secundario** (Flash/SSD del celular). Persisten aunque el celular se apague o se cierre la aplicación (supervivencia a largo plazo), pero escribir allí es una operación "lenta" que requiere mover los datos desde la RAM hacia el sistema de archivos (bloqueos de I/O).
En la aplicación implementada, ambos ecosistemas se sincronizan exclusivamente cuando el usuario oprime voluntariamente el botón "Guardar Físico".

#### ¿Qué pasa con los caracteres acentuados y los saltos de línea?
Se **preservan perfectamente** gracias al uso de la codificación `Charsets.UTF_8` en la lectura y escritura.
`UTF-8` es capaz de mapear y almacenar sin pérdida todos los caracteres latinos (ñ, á, é, í) así como los caracteres de escape como el salto de línea `\n` insertados por el `OutlinedTextField` de Compose al apretar Enter.

### Desafío (Botón "Descartar Cambios")

En la UI de `NotasEj8Screen`, se añadió un botón delineado rojo ("Descartar") a un costado de "Guardar Físico".
Al hacer click, se invoca `viewModel.onDescartarClick()`, función que descarta de raíz el estado volatil/efímero que existía en el `_uiState` del ViewModel, forzando una re-lectura total del repositorio (`repository.leerBorrador()`) sobreescribiendo lo que el usuario tenía tipeado en pantalla por la versión "segura" recuperada desde el disco duro.

---

## Ejercicio 9 — Cargando, listo o falló

### Implementación

Se modeló la clase sellada estricta `EstadoCarga` (`Cargando`, `Listo`, `Fallo(mensaje)`).
En el paquete `ej9`, el ViewModel cuenta con dos estados de carga independientes (uno para la lectura al inicio y otro para la escritura manual).
En `NotasEj9ViewModel.kt` se envuelve cada operación del repositorio dentro de un `try-catch`, de esta forma el hilo de ejecución sigue su curso y actualizamos el valor de la variable de estado apuntando a la rama `EstadoCarga.Fallo` en caso de error para que la UI le avise al usuario, evitando que la app sufra un *crash* (force close).
Para probar las fallas, se implementó una segunda clase `CargaRepositoryErrorSimulado` que reemplaza al repositorio de I/O original en el factory del `MainActivity`, lanzando explícitamente `IllegalStateException`.

### Preguntas

#### ¿Qué excepción no conviene capturar silenciosamente?
No conviene capturar ni comerse de forma general las **Excepciones de Cancelación (`CancellationException`)**.
Cuando utilizamos Corrutinas (`viewModelScope.launch`), el framework maneja el ciclo de vida lanzando esta excepción especial de forma estructurada para avisarle a las corrutinas que el ViewModel (o la Activity) acaba de ser destruido y deben detenerse inmediatamente para liberar recursos (ej. detener el consumo de memoria o red). 
Si capturamos esta excepción con un burdo `catch (e: Exception)` y no la propagamos (haciendo un `throw e`), la corrutina ignorará la solicitud de cancelación y seguirá trabajando en segundo plano o "zombificada", generando severas pérdidas de memoria (*memory leaks*) e inconsistencias impredecibles en el sistema.

#### ¿Por qué un mensaje “Guardado” sólo corresponde después de completar la escritura?
Un mensaje de éxito (como "Guardado") es una afirmación categórica que la aplicación le da al usuario de que su información ahora reside sana y salva en un sistema de persistencia (ej. en el disco duro o en el servidor).
Si emitiéramos este estado *antes* o *durante* el proceso de escritura (`repository.escribirTexto()`), estaríamos asumiendo un escenario ideal donde nada sale mal. Sin embargo, en el mundo real, el dispositivo puede no tener espacio físico en memoria libre disponible en ese milisegundo, la base de datos puede estar corrupta o no haber conexión. Si el mensaje se muestra y simultáneamente ocurre la excepción real por detrás, el usuario perdería los datos pensando que "todo estaba bien". 
El mensaje "Guardado" siempre debe ubicarse en la siguiente línea sincrónica *después* de que la capa de datos retornó la confirmación de éxito.

---

## Ejercicio 10 — De un texto a una lista persistente

### Implementación
Se implementó en el paquete `ej10` la persistencia de una lista entera de notas usando como formato físico nativo un array **JSON** puro (`JSONArray` de `org.json`), guardado en el archivo `notas_persistentes.json` en lugar de utilizar simples separadores por punto y coma (split).

El **Repository (`ListaBorradorRepository`)** maneja las operaciones de lectura (parseando el array), escritura completa y eliminación, cuidando leer todo, modificar la estructura en memoria y sobrescribir un JSON nuevo en un solo bloque para evitar desincronizaciones ("Transacción plana").

El **ViewModel (`NotasEj10ViewModel`)** expone las operaciones CRUD completas y maneja las variables temporales de formulario y un nuevo `idEditando` para permitir que, al hacer click en el lápiz, la nota suba a la interfaz, se edite, y al volver a hacer click en el botón reemplace a su versión anterior sin crear un duplicado.

### Preguntas

#### ¿Cómo se representa una nota vacía?
Una nota vacía se representa instanciando un objeto cuyas propiedades textuales (`titulo` y `contenido`) contienen **Strings vacíos** (`""`) o se establecen como `null` (si el diseño lo permite). 
En nuestro ViewModel `NotasEj10ViewModel`, hemos implementado la regla por consigna de **evitar que el usuario guarde una nota así**: 
`if (titulo.isBlank() && contenido.isBlank()) return` corta el guardado de forma temprana, prohibiendo materializar notas fantasma en la lista.

#### ¿Qué hace el lector si el contenido está incompleto?
Nuestro Repositorio (`ListaBorradorRepository.kt`) envuelve toda la lectura y el *parsing* del `JSONArray` en un bloque `try-catch`. Si el lector de JSON de Android se encuentra con un archivo cortado a la mitad, caracteres corruptos (incompletos) o claves faltantes que no pueden procesarse, arrojará una `JSONException`. Al capturarla y retornar silenciosamente un `emptyList()`, aseguramos que **la aplicación no crashee**, devolviendo al usuario una pizarra limpia para empezar a escribir sin que su aplicación se rompa.

#### ¿Qué se perdería si se reescribe mal el archivo?
Si la aplicación falla justo en el momento físico en que la función `archivo.writeText()` está sobrescribiendo el archivo plano en el disco duro, o si guardamos un *String* mal formateado, **se perdería absolutamente toda la base de datos** (todas las notas creadas hasta el momento).
Este es el mayor riesgo y fragilidad de persistir manualmente en un archivo de texto plano: es un proceso no atómico (todo o nada) carente de mecanismos de protección y transacciones *Rollback* automáticas. Por este motivo en Android es un estándar ineludible migrar a sistemas formales de Base de Datos como SQLite (a través de Room).

---

## Ejercicio 11 — Dependencias: ¿quién crea el Repository?

### Implementación
En ejercicios anteriores, el `Repository` y el `Factory` del `ViewModel` eran creados torpemente dentro de la vista (en `setContent {}` de `MainActivity`), lo cual obligaba a extraer un `Context` de la interfaz (`LocalContext.current`) e instanciar lógica de negocio en la capa gráfica. 

Para este ejercicio creamos un paquete `ej11` que soluciona esto a través de **Inyección de Dependencias Manual**:
1. Se creó `AppContainer.kt` como una fábrica explícita en la capa de aplicación, que posee un atributo `by lazy` para instanciar el Repositorio de forma compartida.
2. Se creó la clase `MyApplication` (registrada en `AndroidManifest.xml`) para instanciar globalmente el `AppContainer` una sola vez en el `onCreate()`.
3. El `MainActivity.kt` extrae el Factory listo para usarse pidiéndoselo a `(application as MyApplication).container`, evitando generar los repositorios y contextos de UI.

### Preguntas

#### ¿Qué se ganarían al reemplazar el Repository sin tocar el ViewModel?
Se gana una total **desvinculación (desacople) entre la capa de Presentación y la de Datos**.
Si el día de mañana queremos que la aplicación deje de guardar en un archivo local y pase a sincronizarse con una base de datos en la nube (ej. Firebase), simplemente escribimos un `NuevoFirebaseRepositoryImpl`, lo registramos en el `AppContainer`, y el ViewModel **no sufre ninguna alteración en su código fuente** (Principio Abierto/Cerrado - OCP). 
El ViewModel sigue confiando en que "alguien" le proveerá el contrato, y al no ser él quien construye la base de datos, queda protegido de los cambios arquitectónicos inferiores.

#### ¿Qué instancia debe compartir la app?
La app debe compartir **una única instancia del `Repository`** (y, si existiese, la conexión al archivo/base de datos subyacente).
Si cada pantalla o ViewModel construyera su propia instancia nueva de `Repository` (haciendo un `val repo = MiRepository()`), tendríamos múltiples objetos intentando leer y escribir en el mismo archivo físico (`borrador.txt`) simultáneamente. Esto provocaría condiciones de carrera (*Race Conditions*), sobreescritura sucia y corrupción de datos. Al tener una instancia compartida global en la clase `Application`, aseguramos consistencia y una "Única Fuente de la Verdad".

---

## Ejercicio 12 — Dos pantallas, una información

### Implementación (Patrón SSOT)
Para este ejercicio dividimos el monolito anterior en dos `Composables` independientes interconectados por la biblioteca **Navigation Compose** (`NavHost`).
1. **La Pantalla de Lista (`ListaEj12Screen`)** que solo muestra los items.
2. **La Pantalla de Editor (`EditorEj12Screen`)** que se abre al tocar una nota y recibe el ID por ruta (`"editor?id=..."`).

Ambas pantallas cuentan con su propio y exclusivo ViewModel (`ListaEj12ViewModel` y `EditorEj12ViewModel`), pero **ambos fueron inyectados con el mismo y único Repositorio (`NotaEj12Repository`)** pre-instanciado en el contenedor global de la `Application`.
Al tener el Repositorio centralizando un `MutableStateFlow` de la lista, cuando el Editor termina de guardar sus cambios en el repositorio, la propiedad reactiva muta, emitiendo automáticamente la nueva lista hacia la pantalla principal apenas regresamos.

### Preguntas

#### ¿Qué ocurre si las dos pantallas guardan una copia distinta de la lista?
Si la Lista tuviera su propia memoria (`List<Nota>`) desconectada del Editor, incurriríamos en el temido problema de la **Doble Fuente de la Verdad**.
Si editamos la "Nota 1" en la pantalla de Editor, al volver hacia atrás la pantalla de Lista no sabría que ocurrió un cambio, seguiría renderizando la copia "vieja" de los datos, desincronizando lo que ve el usuario con la realidad almacenada. Para solucionarlo, tendríamos que programar engorrosas devoluciones de resultados (`SavedStateHandle`, paso de bundles, onResume), pero gracias al patrón de *Single Source Of Truth (SSOT)* centralizado reactivamente en el Repositorio (vía `StateFlow`), este problema desaparece por completo.

#### ¿Qué dato tiene sentido recordar sólo en la navegación?
**El identificador de la entidad (el ID).**
La consigna pedía explícitamente evitar pasar el objeto completo de la Nota serializado entre las rutas de navegación (antipatrón común). 
El único dato que tiene sentido pasar por URL/Arguments en un Navigation Graph es un simple `String` o `Int` que sirva como "clave primaria". La pantalla de destino (en este caso el Editor) toma ese `ID`, va a consultarle a la Fuente de la Verdad central (el Repositorio) y recupera la entidad completa y fresca. Así se evita traficar grandes volúmenes de datos por la memoria de parcelamiento del SO y garantizamos trabajar siempre con la última versión real del dato.

---

## Ejercicio 13 — Probar el ViewModel sin Android ni archivos

### Implementación y Archivos
Para este ejercicio se añadió la dependencia `kotlinx-coroutines-test` en `build.gradle.kts` para permitir manipular el tiempo virtual de las corrutinas durante los tests.
En el directorio `app/src/test/.../ej13` se creó el archivo `NotasEj9ViewModelTest.kt`, conteniendo:

1. **`FakeCargaRepository` (El Repositorio Falso / Mock)**:
  - Una clase que implementa el contrato `CargaRepository` pero no utiliza `File`, `Context` ni librerías de Android. Solo lee y guarda `Strings` en una variable de memoria RAM (`textoGuardado`), permitiendo simular fallas alterando *flags* booleanos (`forzarErrorLectura = true`).

2. **Controladores de Corrutinas (Despachadores)**:
  - Se incorporó un `StandardTestDispatcher()`.
  - **Justificación de su función:** En las pruebas unitarias (JVM nativa, fuera de un celular real), no existe el hilo principal `Dispatchers.Main` de Android. Como el `viewModelScope.launch` utiliza obligatoriamente el hilo `Main`, si ejecutamos la prueba directamente, arrojará una excepción `IllegalStateException`. Utilizando el método `Dispatchers.setMain(testDispatcher)` en el `@Before` y su respectivo `resetMain()` en `@After`, reemplazamos el bucle principal de Android por nuestro propio despachador virtual de tiempo de prueba, permitiendo avanzar el reloj sincrónicamente con `advanceUntilIdle()` y examinar el estado sin tener que poner pausas `Thread.sleep` (lo cual haría los test pesados y defectuosos).

3. **Pruebas Escritas**:
  - `cargaInicial exitosa recupera el texto del repositorio`
  - `cargaInicial fallida muestra EstadoCarga Fallo`
  - `guardado correcto avisa Listo y envia datos al repo`
  - `error de escritura atrapa la excepcion y muestra Fallo`

### Preguntas

#### ¿Qué prueba quedaría innecesariamente difícil si el ViewModel creara su propio Repository?
Probar el **manejo de fallas o respuestas asíncronas lentas**.
Si el ViewModel hiciera `val repo = CargaRepositoryImpl(context)` por sí mismo dentro de su código fuente, nos sería **imposible** inyectarle el `FakeCargaRepository` desde la clase de Test. Por ende, la única forma que tendríamos de probar cómo se comporta el ViewModel cuando "el disco falla", sería forzar mágicamente o alterar el disco real del servidor de integración continua (CI/CD) para que se rompa, lo cual es inestable, muy difícil y técnicamente una prueba de integración ("flaky test"), dejando de ser una prueba unitaria rápida y aislada.

#### ¿Qué sí habría que probar en una prueba de integración con archivo real?
En una prueba de integración o instrumentada (carpeta `androidTest`) utilizando el archivo físico real y un `Context` verdadero de Android, deberíamos probar:
- Si nuestra aplicación posee el **permiso** correcto para escribir en la partición de almacenamiento designada (`SecurityException`).
- El comportamiento bajo **restricciones de espacio** o cuotas de disco alcanzadas (`IOException`).
- Si la **codificación de caracteres** al guardar (ej. `Charsets.UTF_8`) realmente escribe y recupera correctamente caracteres especiales (ñ, á, emojis) desde el archivo binario del sistema operativo Android.

---
## Ejercicio 14

#### ¿Quién transforma Entity en modelo de dominio si son distintos?
**El Repository.**
El repositorio actúa como el "Traductor" de la arquitectura. En nuestro código de `NotaEj14RepositoryRoomImpl.kt`, dentro de la función `map`, tomamos los objetos brutos de base de datos (`NotaEntity`) y construimos manualmente los objetos limpios de la aplicación (`Nota`). De este modo, la UI y el ViewModel jamás llegan a enterarse de que debajo de la app existe SQLite, Room o llaves primarias autogeneradas.

#### ¿Qué gana el ViewModel al no conocer SQL?
Gana una **Cohesión total y Alta Testabilidad**.
Al ViewModel no le importan las columnas, las llaves foráneas ni los tipos de datos de SQLite (`INTEGER`, `TEXT`). Su trabajo es aplicar reglas de presentación y reaccionar a lo que aprieta el usuario. Si lo desacoplamos del SQL:
1. Sus Unit Tests correrán rapidísimo en la JVM.
2. Si el día de mañana migramos la persistencia de SQLite a Firebase Realtime Database (JSON NoSQL), el código del ViewModel no sufrirá ni un solo cambio, cumpliendo el principio Open/Closed de SOLID.

#### ¿Qué sucede si hay dos fuentes de verdad sin política clara?
Se genera la temida **Desincronización de Estado**.
Si un desarrollador decide usar simultáneamente un Repositorio de Archivos y un Repositorio de Room, y permite que distintas pantallas editen la información en distintos repositorios sin una política clara de sincronización (Ej. *Network-bound resource*, donde Room es la fuente única y la red es solo un espejo esclavo), la aplicación mostrará datos distintos o contradictorios dependiendo en qué pantalla se encuentre el usuario, perdiendo datos silenciosamente al sobreescribirse entre sí.