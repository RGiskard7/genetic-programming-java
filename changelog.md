# Changelog

### 2026-03-07 — Demo con datos y = x² para probar capacidades

**Archivos nuevos:**
- `valoresX2.txt`: 7 puntos (x, x²) con x desde -3 a 3 (tab-separado). Sirve para comprobar si el GP encuentra una expresión como `( * x x )`.
- `src/test/java/test/TesterDemoValores.java`: runner que carga `valoresX2.txt`, imprime un encabezado explicando el objetivo y ejecuta el algoritmo. Ejecutar desde la raíz del proyecto.

**Archivos modificados:** `README.md` (sección de ejecución y estructura: mención a valoresX2.txt y TesterDemoValores).

**Motivo:** Tener un caso de prueba con función conocida (parábola) para validar el comportamiento del algoritmo sin depender solo de valores.txt (polinomio más complejo).

---

### 2026-03-07 — Plan de mejoras post-revisión (implementación completa)

**Resumen:**
- **1. Unificar cruce:** TesterCruce usa ahora `IAlgoritmo.cruce()` (AlgoritmoGenetico con terminales/funciones y semilla); eliminada la clase PruebaCruce (duplicaba la lógica de cruce).
- **2. Datos y README:** Añadida en README nota explícita: directorio de trabajo = raíz del proyecto para localizar valores.txt / valoresReducido.txt.
- **3. Configuración por argumentos:** TesterAlgoritmoProgramacionGenetica acepta un argumento opcional: ruta del fichero de datos; por defecto "valores.txt". README actualizado con ejemplo de uso.
- **4. Parada temprana:** En AlgoritmoGenetico.ejecutar(), al alcanzar el fitness objetivo se imprime "Objetivo de fitness alcanzado." antes del break.
- **5. Javadoc API:** Mejorado Javadoc de clase en AlgoritmoGenetico, Individuo y DominioAritmetico; corregido typo en IIndividuo (funciones/terminales) y en IDominio (distinas → distintas); corregido param en Individuo.crearIndividuoAleatorioRec (funciones).
- **6. Tests de borde:** CruceIntegracionTest: nuevo test cruce_unProgenitorUnNodo_completaConReintentos (un progenitor con un solo nodo, otro con varios; reintentos hasta éxito). DominioFitnessTest: nuevo test ficheroVacio_fitnessBuscadoCero_calcularFitnessDevuelveCero (fichero vacío, fitnessBuscado 0, calcularFitness devuelve 0).

**Archivos modificados:** TesterCruce.java, TesterAlgoritmoProgramacionGenetica.java, README.md, AlgoritmoGenetico.java, IIndividuo.java, IDominio.java, Individuo.java, DominioAritmetico.java, CruceIntegracionTest.java, DominioFitnessTest.java. **Eliminado:** PruebaCruce.java.

---

### 2026-03-07 — Revisión de todos los tests y correcciones menores

**Resumen de la revisión:**
- **IndividuoYArbolTest:** Correctos (creación aleatoria, número de nodos, crearSubarbolAleatorio, reemplazarNodo raíz/hijo, calcularExpresion). Coherentes con `Individuo`.
- **MutacionTest:** Correctos (mutar devuelve individuo válido, semilla, original inalterado).
- **CruceIntegracionTest:** Correctos (dos descendientes, progenitores no modificados, crearNuevaPoblacion con reintentos).
- **AlgoritmoGeneticoIntegracionTest:** Correctos (crearPoblacion, crearNuevaPoblacion con/sin mutación, ejecutar con fichero temporal).
- **DominioFitnessTest:** Correctos (terminales, funciones, excepción longitud, definirValoresPrueba, fitnessBuscado, calcularFitness y asignación al individuo; valor 2.0 para "x" con (0,0) y (1,1) coincide con la fórmula del dominio).
- **TesterCruce:** Reintento ante CruceNuloException ya añadido; flujo correcto.
- **TesterIndividuos:** Correcto (árbol manual, writeIndividuo, crearIndividuoAleatorio).
- **TesterLecturaYFitness:** Corregido `throws IOException, IOException` → `throws IOException`.
- **TesterAlgoritmoProgramacionGenetica:** Añadido `return` tras capturar `ArgsDistintosFuncionesException` para no ejecutar con estado indefinido.
- **PruebaCruce:** Lógica de cruce por subárbol correcta (intercambio de referencias, sin doble uso de nodos).

**Archivos modificados:** `TesterLecturaYFitness.java`, `TesterAlgoritmoProgramacionGenetica.java`.

---

### 2026-03-07 — TesterCruce: reintento ante CruceNuloException

**Archivos modificados:** `src/test/java/test/TesterCruce.java`

**Resumen:** Cuando los dos puntos de cruce salen en la raíz (etiqueta 1), `PruebaCruce.cruce` lanza `CruceNuloException` (cruce considerado nulo). El main de TesterCruce ahora reintenta hasta 20 veces; solo si todos fallan se muestra el error. Así se evita que una ejecución aleatoria acabe en excepción por mala suerte.

---

### 2026-03-07 — Estructura Maven estándar (classpath)

**Cambios:**
- Código principal movido a `src/main/java/` (paquetes `algoritmogenetico`, `excepciones`).
- Tests y runners movidos a `src/test/java/test/`.
- `pom.xml`: eliminados `sourceDirectory`, `testSourceDirectory` y `excludes`; se usan los valores por defecto de Maven.

**Archivos afectados:** `pom.xml`, toda la estructura bajo `src/`.

**Motivo:** Con todo bajo `src/` y `src/test/`, el IDE y algunos runners no resolvían bien el classpath (JUnit en scope test, separación main/test). La estructura estándar (`src/main/java`, `src/test/java`) hace que Maven y el IDE (Java Language Server / Red Hat Java) asignen correctamente dependencias de test y ejecuten JUnit y los Tester* sin errores de classpath.

**Qué hacer en el IDE:** Recargar el proyecto Maven (por ejemplo: "Java: Clean Java Language Server Workspace" y reabrir, o desde paleta "Maven: Update project") para que tome las nuevas carpetas de código.

---

### 2026-03-07 — README profesional y académico

**Archivos nuevos:**
- `README.md`: presentación del proyecto (requisitos, construcción, estructura, funcionalidad, tests, documentación, historial). Pensado para ampliarse en cada iteración.

---

### 2026-03-07 — Documentación unificada y tests JUnit

**Archivos nuevos:**
- `doc/DOCUMENTACION.md`: documentación unificada (visión general, arquitectura, operadores, dominio, ejecución, tests). Pensada para ampliarse en cada iteración.
- `pom.xml`: Maven con JUnit 5 para compilar y ejecutar tests (`mvn test`).
- `src/test/IndividuoYArbolTest.java`: tests de Individuo y árbol (creación aleatoria, número de nodos, crearSubarbolAleatorio, reemplazarNodo, calcularExpresion).
- `src/test/MutacionTest.java`: tests del operador de mutación (individuo válido, semilla, original no modificado).
- `src/test/CruceIntegracionTest.java`: tests del cruce (dos descendientes, progenitores inalterados, crearNuevaPoblacion).
- `src/test/AlgoritmoGeneticoIntegracionTest.java`: tests de integración (crearPoblacion, crearNuevaPoblacion con/sin mutación, ejecutar una generación con fichero temporal; 80 % cruce para evitar bucle infinito).
- `src/test/DominioFitnessTest.java`: tests del dominio aritmético (terminales, funciones, definirValoresPrueba, calcularFitness).

**Resumen:**
- Documentación en `doc/DOCUMENTACION.md` cubre base y mutación; nota sobre paridad (tamanioPoblacion - referencia) para que crearNuevaPoblacion termine.
- Tests comprueban lo implementado (individuo, mutación, cruce, algoritmo, dominio) y se pueden ampliar con cada nueva funcionalidad.
- En los tests de integración se usa probabilidad de cruce 80 % para evitar bucle infinito cuando los individuos a añadir son impares.

**Motivo:** Tener una base documental y de pruebas que crezca con el proyecto.

---

### 2026-03-07 — Operador de mutación

**Archivos modificados:**
- `src/algoritmogenetico/individuo/Individuo.java`
- `src/algoritmogenetico/IAlgoritmo.java`
- `src/algoritmogenetico/AlgoritmoGenetico.java`
- `src/test/TesterAlgoritmoProgramacionGenetica.java`

**Resumen:**
- **Mutación:** Nuevo operador que elige un nodo al azar en el árbol y sustituye su subárbol por uno aleatorio de profundidad máxima 2 (constante `PROFUNDIDAD_SUBARBOL_MUTACION`). Se aplica a cada descendiente tras el cruce con probabilidad configurable.
- **Individuo:** `crearSubarbolAleatorio(profundidadMax, terminales, funciones, rng)` reutiliza la lógica de creación de árboles; `reemplazarNodo(etiqueta, nuevoSubarbol)` sustituye el nodo por etiqueta (raíz o hijo).
- **IAlgoritmo:** Método `IIndividuo mutar(IIndividuo individuo)`.
- **AlgoritmoGenetico:** Campo `probabilidadMutacion` (por defecto 0 en el constructor de 5 parámetros). Constructores de 6 y 7 parámetros para fijar probabilidad de mutación y semilla. Integración en `crearNuevaPoblacion()`: tras cada cruce, cada descendiente se muta con probabilidad `probabilidadMutacion` antes de añadirse a la nueva población.
- **Tester:** Usa `probabilidadMutacion = 0.15` para ejercitar la mutación.

**Motivo:** Completar el esquema GP estándar (selección + cruce + mutación) y permitir exploración adicional de la búsqueda.

---

### 2026-03-07 — Mejoras de robustez y estilo

**Archivos modificados:**
- `src/algoritmogenetico/dominio/DominioAritmetico.java`
- `src/algoritmogenetico/AlgoritmoGenetico.java`
- `src/algoritmogenetico/individuo/Individuo.java`
- `src/algoritmogenetico/individuo/nodo/INodo.java`
- `src/algoritmogenetico/individuo/nodo/Nodo.java`
- `src/algoritmogenetico/individuo/nodo/terminales/TerminalAritmetico.java`
- `src/test/PruebaCruce.java`

**Resumen:**
1. **Recursos**: `definirValoresPrueba` usa try-with-resources para cerrar `BufferedReader` automáticamente.
2. **Comparación de fitness**: Uso de `Comparator.comparingDouble(IIndividuo::getFitness)` en torneo y bestFitness para evitar overflow y seguir buenas prácticas.
3. **CruceNuloException**: Reintentos (hasta 50) en `crearNuevaPoblacion`; si falla todo, se añaden copias de los ganadores del torneo para mantener el tamaño de la población.
4. **Random**: Un único `Random` en `AlgoritmoGenetico` (constructor con `Long semilla` opcional para reproducibilidad); se pasa a la creación de individuos.
5. **Nomenclatura**: `INodo.getRaiz()` renombrado a `getSimbolo()`; `Individuo.getRaiz(INodo)` renombrado a `getPadre(INodo)`.
6. **Nodo.copy()**: Método declarado abstracto en `Nodo` para forzar implementación en subclases concretas.
7. **TerminalAritmetico**: Constructor `TerminalAritmetico(String simbolo, double valor)` y `@Override` en `calcular()`.

**Motivo:** Preparar el proyecto para GitHub y futuras extensiones; mejorar robustez y claridad sin cambiar el comportamiento observable.
