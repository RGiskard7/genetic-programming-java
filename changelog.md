# Changelog

### 2026-03-07 — Plan "Mejoras GP interesantes" (implementación completa)

**Resumen:** Cinco bloques implementados sin modificar la firma pública de IAlgoritmo.

**1. Resultado de ejecución y estadísticas sobre múltiples ejecuciones**
- **Nuevo:** `algoritmogenetico/util/ResultadoEjecucion.java` — Datos inmutables: mejorIndividuo, generacionFinal, objetivoAlcanzado; constructor y getters.
- **AlgoritmoGenetico:** Campo `ultimoResultado`; al final de `ejecutar(IDominio)` se asigna un `ResultadoEjecucion` con el mejor de la última generación, generación en que se paró y si se paró por objetivo; getter `getUltimoResultado()`.
- **AppGP:** Spinner "N ejecuciones" (1–100, default 1). Si N > 1: bucle N veces con semilla `baseSeed + i`, solo la última run con listener/logger; se acumulan `ResultadoEjecucion`; al final se muestran en un Alert: media/std/min/max fitness, tasa de éxito, media de generaciones cuando éxito.
- **Tests:** `AlgoritmoGeneticoIntegracionTest`: getUltimoResultado coherente y varias ejecuciones con distintas semillas.

**2. Evitar óptimo trivial en clasificación**
- **Nuevo:** `algoritmogenetico/util/UtilExpresion.java` — Método estático `isConstant(INodo raiz)`: true si el árbol solo tiene constantes (TerminalConstante/funciones), false si hay al menos un TerminalAritmetico (variable).
- **AlgoritmoGenetico:** En la condición de "objetivo alcanzado", si el dominio es `DominioClasificacion` y `UtilExpresion.isConstant(mejorIndiv.getExpresion())`, no se hace break (no se considera objetivo alcanzado).
- **Tests:** `UtilExpresionTest`: isConstant con null, TerminalConstante, TerminalAritmetico.

**3. Simplificación de expresiones**
- **Nuevo:** `algoritmogenetico/util/SimplificadorExpresion.java` — Método estático `simplificar(INodo raiz)` que devuelve un **nuevo** árbol. Reglas: x+0→x, 0+x→x, x*1→x, 1*x→x, 0*x→0, x*0→0, x-0→x, x/1→x. Soporte para +, -, *, / y funciones unarias (sin, cos, neg, abs, exp, log, sqrt, sqr) para reconstruir el árbol.
- **AppGP:** Checkbox "Simplificar expresión" (por defecto desactivado). Tras obtener el mejor individuo, si está activo se usa la expresión simplificada para: texto en área, dibujo del árbol, exportar a fichero y curva datos vs curva.
- **Tests:** `SimplificadorExpresionTest`: suma con 0, producto por 1, producto por 0, árbol original no mutado.

**4. Exportar expresión a LaTeX y código**
- **Nuevo:** `algoritmogenetico/util/ExportadorExpresion.java` — Métodos estáticos: `toPrefija(INodo)` (equivalente a toString), `toLatex(INodo)` (infija con \frac, \sin, \cos, etc.), `toPython(INodo)` (expresión infija), `toPythonDef(INodo)` (def f(x): return ... con import math si aplica).
- **AppGP:** ComboBox "Formato" junto a "Exportar expr." con opciones: Prefija (texto), LaTeX, Python. Al guardar se usa el formato elegido; si "Simplificar expresión" está activo se aplica antes de exportar.
- **Tests:** `ExportadorExpresionTest`: toPrefija equivale a toString, toLatex y toPython formato razonable para ( * x x ), toPythonDef contiene def y return, null → cadena vacía.

**5. Benchmarks estándar y documentación**
- **Nuevo:** `benchmark_nguyen1.txt` — Nguyen-1: y = x³ + x² + x, 21 puntos en [-1, 1] (TSV; líneas # omitidas).
- **Nuevo:** `doc/BENCHMARKS.md` — Descripción del problema Nguyen-1, columnas del fichero, parámetros recomendados (población, generaciones, funciones), resultado típico y uso de "N ejecuciones" para reportar estadísticas.
- **doc/DOCUMENTACION.md:** Enlace a la sección de benchmarks (BENCHMARKS.md).
- **README.md:** Enlace a doc/BENCHMARKS.md en Documentación; fila en tabla de ficheros de ejemplo para benchmark_nguyen1.txt.

**Archivos nuevos:** `ResultadoEjecucion.java`, `UtilExpresion.java`, `SimplificadorExpresion.java`, `ExportadorExpresion.java`, `benchmark_nguyen1.txt`, `doc/BENCHMARKS.md`, `UtilExpresionTest.java`, `SimplificadorExpresionTest.java`, `ExportadorExpresionTest.java`.  
**Archivos modificados:** `AlgoritmoGenetico.java`, `AppGP.java`, `doc/DOCUMENTACION.md`, `README.md`, `AlgoritmoGeneticoIntegracionTest.java`, `changelog.md`.

---

### 2026-03-07 — Nuevos ficheros de datos para probar capacidades del proyecto

**Resumen:**

- **Regresión (TSV):** `valoresPolinomio4.txt` (y = x⁴−2x², 17 puntos), `valoresTrigComplejo.txt` (y = sin(x)+0.5·cos(2x)), `valoresExpGauss.txt` (y = exp(−x²)), `valoresX2Ruido.txt` (y ≈ x² con ruido), `valoresMultivariadoComplejo.txt` (z = x+y+xy, 13 filas).
- **Clasificación (CSV):** `clasificacionXOR.csv` (XOR, no lineal), `clasificacionCirculo.csv` (25 puntos: dentro/fuera de círculo x²+y²&lt;1).
- **Booleano (TSV):** `tablaVerdad4vars.txt` (parity de 4 bits, 16 filas), `tablaVerdadMayoria4.txt` (mayoría de 4 bits: salida 1 si ≥3 entradas son 1).
- **Documentación:** README con tabla de ficheros de ejemplo y descripción; DOCUMENTACION.md con nueva sección "Ficheros de datos de ejemplo" y renumeración de secciones (6→Ejecución, 7→Tests, 8→Referencias).

**Archivos nuevos:** `valoresPolinomio4.txt`, `valoresTrigComplejo.txt`, `valoresExpGauss.txt`, `valoresX2Ruido.txt`, `valoresMultivariadoComplejo.txt`, `clasificacionXOR.csv`, `clasificacionCirculo.csv`, `tablaVerdad4vars.txt`, `tablaVerdadMayoria4.txt`. **Modificados:** `README.md`, `doc/DOCUMENTACION.md`, `changelog.md`.

---

### 2026-03-07 — Evolución del algoritmo genético: escalado lineal, mutación punto/contracción, selección por ranking

**Resumen:**

- **Escalado lineal en regresión (DominioAritmetico):** Parámetro `usarEscaladoLineal` (por defecto `true`). En `calcularFitness` se obtienen predicciones p_i del árbol; se ajustan a y b por mínimos cuadrados (a·p_i + b ≈ y_i); fitness = −RMSE(a·p + b, y) − α·nodos. Aplicado en rama univariada y multivariada. Sin cambiar la estructura del árbol se mejora el fitness al permitir escala y desplazamiento.
- **Nuevos operadores de mutación (AlgoritmoGenetico):** Tres tipos con probabilidades configurables (`setProbabilidadesMutacion(subarbol, punto, contraccion)`): (1) **Subárbol** (comportamiento actual: sustituir subárbol por uno aleatorio de profundidad 2). (2) **Punto:** si el nodo es terminal constante → perturbación (valor + delta en [-0.5, 0.5]); si otro terminal → sustituir por terminal aleatorio; si función → sustituir por otra función de la misma aridad manteniendo hijos. (3) **Contracción:** elegir nodo no terminal y sustituir su subárbol por un terminal aleatorio. Por defecto solo subárbol = 1.0 para compatibilidad.
- **Selección por ranking:** Enum `TipoSeleccion { TORNEO, RANKING }`. Método `seleccionarPadres()`: con TORNEO se mantiene la lógica actual; con RANKING se ordena la población por fitness, se asignan pesos por rango², se normalizan y se muestrean dos padres sin reemplazo. Por defecto TORNEO. Configuración: `setTipoSeleccion(TipoSeleccion)`.
- **TerminalConstante:** Añadido getter `getValor()` para lectura del valor en mutación por punto.

**Archivos modificados:** `DominioAritmetico.java`, `TerminalConstante.java`, `AlgoritmoGenetico.java`, `doc/DOCUMENTACION.md`, `changelog.md`.

---

### 2026-03-08 — Revisión de doc/

- **DOCUMENTACION.md:** Reescrito para reflejar el estado actual del código: DominioAritmetico (RMSE, todas las funciones, TSV/CSV, multivariado), DominioClasificacion, DominioBooleano; cruce con cualquier aridad; parámetros de parada y límite de nodos; ejecución (GUI, runners). Eliminadas frases genéricas ("Se irá ampliando...", "Este documento se ampliará...") y la nota obsoleta sobre paridad del tamaño de población.
- **ROADMAP.md:** Título e intro acortados; "Prioridades sugeridas" renombrado a "Prioridades". Sin cambios de contenido técnico.

---

### 2026-03-08 — Branding, licencia MIT y README profesional

**Resumen:**

- **README:** Reescrito con tono profesional y estándar. Incluye sección "Qué es la Programación Genética" explicando idea central, flujo (población, evaluación, selección, cruce, mutación, reemplazo), casos de uso (regresión simbólica, clasificación, síntesis lógica) y carácter estocástico. Sin referencias académicas ni a prácticas.
- **Licencia:** Añadido fichero `LICENSE` con MIT License. Copyright (c) Eduardo Díaz Sánchez.
- **Documentación:** Eliminadas referencias a "práctica" o contexto universitario. `doc/EVOLUCION_PROYECTO.md` sustituido por `doc/ROADMAP.md` (mismo contenido técnico, redacción neutra). `doc/DOCUMENTACION.md` actualizado para enlazar a `ROADMAP.md`.
- **Eliminados:** `Diagrama de clases.pdf`, `doc/EVOLUCION_PROYECTO.md`.
- **Changelog:** Entrada "README profesional y académico" renombrada a "README profesional"; "buenas prácticas" reemplazado por "prácticas habituales" en otra entrada.

**Archivos nuevos:** `LICENSE`, `doc/ROADMAP.md`. **Archivos modificados:** `README.md`, `doc/DOCUMENTACION.md`, `changelog.md`. **Archivos eliminados:** `Diagrama de clases.pdf`, `doc/EVOLUCION_PROYECTO.md`.

---

### 2026-03-08 — Correcciones críticas: cruce con funciones unarias, bucle infinito, limpieza general

**Resumen:**

- **CRÍTICO — Cruce con funciones unarias (sin, cos, neg, abs, exp, log, sqrt, sqr):** El método `cruce()` en `AlgoritmoGenetico` asumía que el padre del punto de cruce siempre tenía 2 hijos (aridad 2). Con funciones unarias, `raiz.getDescendientes().get(1)` lanzaba `IndexOutOfBoundsException`. Se reescribió el cruce usando `List.set()` directo, que funciona con cualquier aridad sin asumir el número de hijos.
- **CRÍTICO — Bucle infinito en `crearNuevaPoblacion()`:** El bucle usaba `while (nuevaPoblacion.size() != tamanioPoblacion)` y añadía 2 descendientes por iteración. Con poblaciones impares, el tamaño se pasaba y el `!=` nunca se cumplía → bucle infinito. Cambiado a `<` con guard de tamaño al añadir descendientes.
- **Imports duplicados eliminados:** `IAlgoritmo.java` (IIndividuo x2), `AppGP.java` (Stage x2), `TesterDemoValores.java` (DominioAritmetico x2).
- **copy() normalizado en 13 funciones:** `FuncionDivision`, `FuncionSeno`, `FuncionCoseno`, `FuncionNegacion`, `FuncionValorAbsoluto`, `FuncionExp`, `FuncionLog`, `FuncionSqrt`, `FuncionCuadrado`, `FuncionAnd`, `FuncionOr`, `FuncionNot`, `FuncionXor` llamaban `incluirDescendiente(nodo.copy())` pero `Nodo.incluirDescendiente()` ya llama `nodo.copy()` internamente → doble copia innecesaria. Ahora todas usan `incluirDescendiente(nodo)`, consistente con FuncionSuma/Resta/Multiplicacion.
- **TerminalBooleano.copy():** No preservaba el valor actual del terminal. Ahora copia el valor con `setValor(this.calcular())`.
- **Warning unchecked en AppGP:** `chartDatos.getData().addAll(s1, s2)` varargs genérico → sustituido por dos llamadas `add()`.
- **5 tests nuevos:** Cruce con funciones solo unarias (50 seeds), cruce con mezcla binarias/unarias (50 seeds), población tamaño impar (5 tamaños), ejecución completa con funciones unarias, ejecución solo con funciones unarias.

**Tests:** 35 tests, 0 fallos (antes 30).

**Archivos modificados:** `AlgoritmoGenetico.java`, `IAlgoritmo.java`, `AppGP.java`, `FuncionDivision.java`, `FuncionSeno.java`, `FuncionCoseno.java`, `FuncionNegacion.java`, `FuncionValorAbsoluto.java`, `FuncionExp.java`, `FuncionLog.java`, `FuncionSqrt.java`, `FuncionCuadrado.java`, `FuncionAnd.java`, `FuncionOr.java`, `FuncionNot.java`, `FuncionXor.java`, `TerminalBooleano.java`, `CruceIntegracionTest.java`, `AlgoritmoGeneticoIntegracionTest.java`, `TesterDemoValores.java`.

---

### 2026-03-08 — Expansión de capacidades: más funciones, clasificación, CSV, GUI flexible

**Resumen:**

- **Nuevas funciones matemáticas:** exp (exponencial acotada), log (log(1+|x|)), sqrt (raíz de |x|), sqr (cuadrado). Registradas en DominioAritmetico y disponibles en la GUI como checkboxes.
- **Carga de datos flexible:** En DominioAritmetico y DominioClasificacion, definirValoresPrueba acepta TSV (tabulador) o CSV (coma). Si la primera línea no es numérica, se considera cabecera y se omite. Permite cargar cualquier archivo de valores deseado.
- **Dominio de clasificación binaria:** Nueva clase DominioClasificacion. Lee ficheros con N columnas numéricas + última columna clase (0.0/1.0). Fitness = precisión − ALPHA*nodos. Mismo algoritmo genético que para regresión. Fichero de ejemplo clasificacionEjemplo.csv (x1, x2, clase).
- **Constantes aleatorias en GUI:** Opción "Const. aleat. N" con spinners N, min, max. Se generan N constantes en [min,max] con la semilla indicada al inicio de la ejecución. Combinable con constantes fijas.
- **AlgoritmoGenetico:** setMaxNodosIndividuo(int): rechaza hijos que superen ese número de nodos (0 = sin límite). setGeneracionesSinMejoraParaParar(int): parada por convergencia (0 = desactivado). Ambos configurables desde la GUI.
- **GUI:** Botón "Examinar..." para elegir el fichero de datos con un diálogo. Combo "Tipo: Regresión | Clasificación". Spinners para semilla, max nodos y parar sin mejora. Checkboxes exp, log, sqrt, sqr. Opción "Exportar expr." para guardar la mejor expresión en un fichero de texto al finalizar.

**Archivos nuevos:** FuncionExp, FuncionLog, FuncionSqrt (raíz cuadrada), FuncionCuadrado (sqr), DominioClasificacion, clasificacionEjemplo.csv.

**Archivos modificados:** DominioAritmetico (exp/log/sqrt/sqr, carga CSV y cabecera), DominioClasificacion (definirConjuntoTerminalesConConstantes), AlgoritmoGenetico (maxNodos, generacionesSinMejora), AppGP (selector fichero, tipo problema, constantes aleatorias, nuevas funciones, exportar expresión).

---

### 2026-03-08 — Iteración v2: robustez, nuevos operadores, GUI completa y dominio booleano

**Fases implementadas:** 0.1, 0.2, 0.3, 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 3.1

**Resumen:**

- **Fase 0.1 (Nodo.java):** `numNodos` estático reemplazado por `AtomicInteger CONTADOR`. Elimina condición de carrera entre el hilo del algoritmo y el Task de JavaFX.
- **Fase 0.2 (DominioAritmetico):** Fitness cambiado de conteo binario a **RMSE negado** (`fitness = -RMSE - ALPHA*nodos`). `fitnessBuscado()` devuelve 0.0. El algoritmo para cuando RMSE < ~0.05. Tests de dominio actualizados.
- **Fase 0.3:** `TesterDemoValores` y `AppGP` usan `definirConjuntoTerminalesConConstantes` con {-1.0, 0.0, 1.0, 2.0}.
- **Fase 1.1 (AlgoritmoGenetico):** Inicialización **ramped half-and-half**: mitad con `full` (profundidad exacta), mitad con `grow` (profundidad variable). Mayor diversidad inicial. `crearIndividuoAleatorioGrow` añadido a `Individuo`.
- **Fase 1.2:** Funciones unarias **sin, cos, neg, abs** (`FuncionSeno`, `FuncionCoseno`, `FuncionNegacion`, `FuncionValorAbsoluto`). `crearIndividuoAleatorioRec` usa aridad dinámica (`f.getNumArgu()`). `getNumArgu()` añadido a `Funcion`. Nuevo fichero `valoresSeno.txt`.
- **Fase 1.3 (DominioAritmetico):** Soporte **multivariado**: `definirValoresPruebaMultiVar(fichero, "x", "y")` carga N+1 columnas; `calcularFitness` inyecta variables por nombre. Nuevo fichero `valoresMultiVar.txt` (z = x²+y²).
- **Fase 2.1 (AppGP):** Panel de configuración completo: spinners para población, generaciones, profundidad, torneo, cruce%, mutación%; checkboxes para cada función matemática; campo para constantes efímeras.
- **Fase 2.2:** `EvolucionLogger` se activa desde la GUI (checkbox + ruta CSV).
- **Fase 2.3:** **Canvas con árbol visual** de la mejor expresión: BFS para asignar posiciones, elipses coloreadas (verde=hoja, azul=función), aristas grises. Se actualiza en cada generación.
- **Fase 3.1:** Nuevo **DominioBooleano** (`DominioBooleano.java`), terminal `TerminalBooleano`, funciones `FuncionAnd/Or/Not/Xor`. Lee tablas de verdad TSV. Nuevo fichero `tablaVerdad.txt` (función mayoría 3 bits). Tests: `DominioBooleanoTest` (6 tests).

**Tests:** 30 tests, 0 fallos.

**Archivos nuevos main:** `FuncionSeno`, `FuncionCoseno`, `FuncionNegacion`, `FuncionValorAbsoluto`, `FuncionAnd`, `FuncionOr`, `FuncionNot`, `FuncionXor`, `TerminalBooleano`, `DominioBooleano`.
**Archivos modificados main:** `Nodo`, `Funcion`, `Individuo`, `AlgoritmoGenetico`, `DominioAritmetico`, `AppGP`.
**Archivos nuevos test:** `DominioBooleanoTest`.
**Archivos modificados test:** `DominioFitnessTest`, `TesterDemoValores`.
**Datos nuevos:** `valoresSeno.txt`, `valoresMultiVar.txt`, `tablaVerdad.txt`.

---

### 2026-03-07 — GUI JavaFX (Fase 4): integración con algoritmo y corrección test

**Resumen:**
- **AppGP reescrita:** ya no usa reflexión ni `calcularFitness(alg)`. Configura dominio (definirValoresPrueba, terminales, funciones con "+", "-", "*", "/"), algoritmo con `setGeneracionListener`; ejecuta `alg.ejecutar(dominio)` dentro de un `Task<Void>`. En cada generación el listener actualiza en el hilo JavaFX el gráfico de fitness y el área de la mejor expresión; al finalizar se dibujan en el otro gráfico los puntos de `getValoresPrueba()` y la curva del mejor individuo evaluada en 50 puntos.
- **DominioFitnessTest:** el test `calcularFitness_asignaFitnessAlIndividuo` fallaba porque con parsimonia el fitness puede ser negativo (0 puntos − α·nodos). Se cambió la aserción a `Double.isFinite(ind.getFitness()) && ind.getFitness() <= 1.0`.
- **README:** descripción de la GUI, nuevos ficheros de datos (valoresLineal, valoresCubica), estructura actualizada (main/java, gui), funcionalidad actual (parsimonia, profundidad, división, constantes, logger, GUI) y tests (EvolucionLogger, constantes).

**Archivos modificados:** `src/main/java/gui/AppGP.java`, `src/test/java/test/DominioFitnessTest.java`, `README.md`.

---

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

### 2026-03-07 — README profesional

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
2. **Comparación de fitness**: Uso de `Comparator.comparingDouble(IIndividuo::getFitness)` en torneo y bestFitness para evitar overflow y seguir prácticas habituales.
3. **CruceNuloException**: Reintentos (hasta 50) en `crearNuevaPoblacion`; si falla todo, se añaden copias de los ganadores del torneo para mantener el tamaño de la población.
4. **Random**: Un único `Random` en `AlgoritmoGenetico` (constructor con `Long semilla` opcional para reproducibilidad); se pasa a la creación de individuos.
5. **Nomenclatura**: `INodo.getRaiz()` renombrado a `getSimbolo()`; `Individuo.getRaiz(INodo)` renombrado a `getPadre(INodo)`.
6. **Nodo.copy()**: Método declarado abstracto en `Nodo` para forzar implementación en subclases concretas.
7. **TerminalAritmetico**: Constructor `TerminalAritmetico(String simbolo, double valor)` y `@Override` en `calcular()`.

**Motivo:** Preparar el proyecto para GitHub y futuras extensiones; mejorar robustez y claridad sin cambiar el comportamiento observable.
