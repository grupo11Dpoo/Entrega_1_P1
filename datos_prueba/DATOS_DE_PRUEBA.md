# Datos de prueba — Club Deportivo Séneca

Este proyecto incluye `datos-prueba/cds-inicial.dat`, un archivo de datos serializado generado y comprobado con las clases Java de este proyecto.

## Usuarios de ejemplo

| Rol | Login | Contraseña |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Socia/jugadora | `ana` | `1234` |
| Socio/jugador | `juan` | `1234` |
| Entrenador | `entrenador` | `1234` |

El archivo incluye el equipo **Séneca Fútbol** (categoría Mayores) y a Ana y Juan inscritos en él.

## Cómo usar los datos de prueba

La aplicación guarda los datos de trabajo en una ruta local del usuario (`~/.clubDeportivoSeneca/cds.dat` en macOS/Linux). El archivo `datos-prueba/cds-inicial.dat` es la copia inicial que se distribuye con el proyecto; no ejecutes la aplicación apuntando directamente a esa copia si quieres conservarla como plantilla.

### En macOS

1. Ejecuta `ConsolaPrincipal` una vez desde Eclipse y luego elige salir. Esto crea la carpeta local de datos si aún no existe.
2. En Finder, abre **Ir → Ir a la carpeta…** (atajo `Mayúsculas + Comando + G`).
3. Escribe `~/.clubDeportivoSeneca/` y pulsa Enter.
4. Antes de reemplazar nada, puedes guardar una copia de tu `cds.dat` actual en otra carpeta. Para empezar con los datos de ejemplo, copia `datos-prueba/cds-inicial.dat` desde la carpeta del proyecto a esta carpeta local y cámbiale el nombre a `cds.dat`, aceptando reemplazar el archivo si te lo pregunta.
5. Ejecuta otra vez `ConsolaPrincipal` desde Eclipse e inicia sesión con uno de los usuarios de la tabla.

Si Finder no abre la carpeta porque no existe, ejecuta la aplicación una vez y sal; debería crearla. Si aún no existe, crea una carpeta llamada `.clubDeportivoSeneca` dentro de tu carpeta de usuario.

### En otros computadores

Cada persona hace la copia del archivo de ejemplo a su propia carpeta local. Desde ese momento, los cambios se guardan en el computador de esa persona, no en GitHub ni en los computadores de los demás. Para volver al estado inicial, copia de nuevo `cds-inicial.dat` como `cds.dat`.

**Importante:** reemplazar `cds.dat` reinicia los datos guardados localmente. Conserva una copia si quieres mantenerlos.
