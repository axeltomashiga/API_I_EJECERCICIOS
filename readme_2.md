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


