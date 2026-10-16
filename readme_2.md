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