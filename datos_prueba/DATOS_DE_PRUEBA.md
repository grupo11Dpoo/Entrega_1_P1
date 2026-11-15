# Datos de ejemplo — Club Deportivo Séneca

`cds-inicial.dat` es una plantilla serializada, regenerada con el código actual del proyecto. Incluye:

- Administrador: `admin` / `admin123`
- Jugadores: `ana` / `1234` y `juan` / `1234`
- Entrenador: `entrenador` / `1234`
- Fisioterapeuta: `fisio` / `1234`
- Equipo «Séneca Fútbol», categoría Mayores, con Ana y Juan inscritos

**La aplicación no modifica esta plantilla.** Guarda los datos activos en `~/.clubDeportivoSeneca/cds.dat` (o la ruta definida en `cds.archivoDatos`). Para comenzar con el ejemplo, copiar `cds-inicial.dat` a esa ubicación con el nombre `cds.dat`, después de respaldar cualquier archivo previo.

Para regenerarlo: compilar el proyecto y ejecutar `clubDeportivoSeneca.persistencia.GeneradorDatosPrueba` indicando como argumento `datos_prueba/cds-inicial.dat`. Para más instrucciones, consultar `README_CAMBIOS.md` en la raíz del proyecto.
