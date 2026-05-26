# Documentación técnica — Programación Genética

Referencia de arquitectura, operadores y dominios del sistema.

---

## 1. Visión general

El proyecto implementa **Programación Genética (GP)** en Java: los individuos son **árboles de expresiones** (terminales y funciones), se evalúan con un **dominio** (regresión, clasificación o tabla de verdad), y la población evoluciona mediante **selección por torneo**, **cruce por subárbol** y **mutación**.

- **Regresión simbólica:** encontrar una fórmula que minimice el error (RMSE) sobre pares (x, y).
- **Clasificación binaria:** expresiones que maximicen la precisión sobre datos etiquetados 0/1.
- **Síntesis booleana:** expresiones que reproduzcan una tabla de verdad.

Salida: mejor individuo por generación (expresión en notación prefija) y su fitness.

---

## 2. Arquitectura

### 2.1 Interfaces

| Interfaz | Rol |
|----------|-----|
| **IAlgoritmo** | Contrato del algoritmo: definir terminales/funciones, crear población, cruce, mutación, crear nueva población, ejecutar. |
| **IIndividuo** | Individuo = expresión (árbol). get/set expresión, fitness, crear aleatorio, calcular expresión, número de nodos, profundidad, etiquetar nodos. |
| **INodo** | Nodo del árbol: getSimbolo(), getDescendientes(), incluirDescendiente(), calcular(), copy(). |
| **IDominio** | Terminales, funciones, valores de prueba (p. ej. desde fichero), cálculo de fitness y fitness objetivo. |

### 2.2 Implementaciones

- **AlgoritmoGenetico:** selección por **torneo** o por **ranking** (configurable); cruce por subárbol (cualquier aridad); mutación en tres modalidades (subárbol, punto, contracción) con probabilidades configurables; elitismo. Límite de profundidad y de nodos; parada por generaciones sin mejora.
- **Individuo:** mapa de nodos etiquetados para cruce/mutación; inicialización full y grow (ramped half-and-half).
- **Nodo** (abstracto) → **Funcion** / **Terminal**. Funciones: +, −, *, / (protegida), sin, cos, neg, abs, exp, log, sqrt, sqr. Terminales: **TerminalAritmetico** (variable o valor inyectado), **TerminalConstante** (valor fijo), **TerminalBooleano** (dominio booleano).
- **DominioAritmetico:** regresión sobre pares (x, y); **escalado lineal** opcional (ajuste a·f(x)+b por mínimos cuadrados; fitness = −RMSE(a·p+b, y) − α·nodos); carga TSV/CSV con detección de cabecera; soporte multivariado.
- **DominioClasificacion:** N columnas numéricas + etiqueta 0/1; fitness = precisión − α·nodos.
- **DominioBooleano:** tabla de verdad TSV; fitness = aciertos − α·nodos; funciones AND, OR, NOT, XOR.

### 2.3 Flujo de ejecución

1. Crear algoritmo (población, generaciones, profundidad, % cruce, torneo, probabilidad mutación, semilla opcional).
2. Definir terminales y funciones (desde el dominio).
3. Cargar valores de prueba en el dominio (`definirValoresPrueba(ruta)`).
4. `ejecutar(dominio)`: población inicial (ramped half-and-half); en cada generación: evaluar fitness, registrar mejor, parar si se alcanza objetivo o por estancamiento; si no, crear nueva población (elitismo + selección de padres por torneo o ranking + cruce + mutación).

---

## 3. Operadores

### 3.1 Selección de padres

- **Torneo (por defecto):** Se eligen al azar `valorTorneo` candidatos; se toman los dos mejores por fitness para cruzarlos.
- **Ranking:** La población se ordena por fitness (ascendente). Se asigna peso proporcional al rango (rango² por defecto), se normalizan las probabilidades y se muestrean dos padres sin reemplazo. Configurable con `setTipoSeleccion(TipoSeleccion.RANKING)`.

### 3.2 Cruce por subárbol

Dos puntos de cruce (uno por progenitor), en [1, número de nodos]. Si ambos son 1 (raíz) se lanza **CruceNuloException** y el algoritmo reintenta o copia a los ganadores. En caso contrario se copian ambos progenitores y se sustituye en cada copia el subárbol del punto de cruce por el subárbol del otro progenitor. El cruce soporta funciones de cualquier aridad (unarias y binarias).

### 3.3 Mutación

En cada descendiente se aplica con probabilidad `probabilidadMutacion` **un solo** tipo de mutación, elegido según las probabilidades configuradas (subárbol / punto / contracción; por defecto solo subárbol = 1.0). Sobre una copia del individuo se etiquetan nodos y se actúa según el tipo:

- **Subárbol:** Se elige un nodo al azar y se reemplaza su subárbol por un subárbol aleatorio de profundidad máxima 2 (comportamiento clásico).
- **Punto:** Se elige un nodo al azar. Si es **terminal:** si es constante se perturba (`valor + delta` en [-0.5, 0.5]); si no, se sustituye por otro terminal elegido al azar. Si es **función:** se sustituye por otra función de la misma aridad, manteniendo los mismos hijos.
- **Contracción (shrink):** Se elige un nodo **no terminal** al azar y se sustituye todo su subárbol por un terminal elegido al azar.

Tras cualquier mutación se comprueban profundidad y máximo de nodos; si se superan, se devuelve la copia del original. Configuración: `setProbabilidadesMutacion(subarbol, punto, contraccion)`.

### 3.4 Elitismo e inmigrantes

El mejor individuo siempre se copia de forma defensiva a la posición 0 de la nueva población, independientemente de `probabilidadCruce`. Esto garantiza que el fitness no decrece nunca entre generaciones.

Opcionalmente, al final de cada generación se pueden inyectar **inmigrantes**: los últimos M individuos de la nueva población se sustituyen por árboles aleatorios nuevos (`M = porcentaje * tamanioPoblacion`). El elite (posición 0) nunca se reemplaza. Activar con `setPorcentajeInmigrantes(double)` (por defecto 0, desactivado).

---

## 4. Dominios

### 4.1 DominioAritmetico (regresión)

- **Terminales:** variables por nombre (p. ej. `"x"`) y constantes (fijas o efímeras aleatorias). El dominio asigna el valor a cada variable antes de evaluar.
- **Funciones:** +, −, *, / (división protegida), sin, cos, neg, abs, exp, log, sqrt, sqr.
- **Datos:** fichero TSV o CSV; detección de cabecera; 2 columnas (x, y) o multivariado (varias columnas + objetivo).
- **Fitness:** Con **escalado lineal** (por defecto activado): para cada punto se obtiene la predicción del árbol p_i = f(x_i); se ajustan a y b por mínimos cuadrados de forma que a·p_i + b minimice el error frente a y_i; entonces error_i = y_i − (a·p_i + b), RMSE = √(media(error_i²)), fitness = −RMSE − α·nodos. Sin escalado: fitness = −RMSE(f, y) − α·nodos. Objetivo: 0 (RMSE ≈ 0). Activar/desactivar: `setUsarEscaladoLineal(boolean)`.

### 4.2 DominioClasificacion

- **Terminales:** variables (nombres desde cabecera o v0, v1, …) y constantes opcionales.
- **Mismas funciones** que DominioAritmetico (el individuo evalúa a un número; clase predicha = salida > 0.5).
- **Fitness:** precisión − α·nodos. Objetivo: 1.

### 4.3 DominioBooleano

- **Terminales:** TerminalBooleano por nombre de variable.
- **Funciones:** AND, OR, NOT, XOR.
- **Datos:** TSV tabla de verdad; última columna = objetivo 0/1.
- **Fitness:** número de filas correctas − α·nodos.

---

## 5. Ficheros de datos de ejemplo

En la raíz del proyecto hay ficheros TSV/CSV para probar regresión, clasificación y booleano:

- **Regresión univariada:** `valores.txt`, `valoresX2.txt`, `valoresCubica.txt`, `valoresPolinomio4.txt` (y = x⁴−2x²), `valoresTrigComplejo.txt` (sin+0.5·cos(2x)), `valoresExpGauss.txt` (exp(−x²)), `valoresSeno.txt`, `valoresX2Ruido.txt` (x² con ruido).
- **Regresión multivariada:** `valoresMultiVar.txt` (z ≈ x²+y²), `valoresMultivariadoComplejo.txt` (z = x+y+xy), `valoresMultivariadoTrigPolinomico.txt` (z = sin(x)+cos(y)+xy+x²−y², rejilla 8×8).
- **Clasificación:** `clasificacionEjemplo.csv` (AND), `clasificacionXOR.csv` (XOR), `clasificacionCirculo.csv` (dentro/fuera de círculo).
- **Booleano:** `tablaVerdad.txt` (mayoría 3 bits), `tablaVerdad4vars.txt` (parity 4 bits), `tablaVerdadMayoria4.txt` (≥3 de 4).

**Benchmarks:** Suite de 4 casos documentados (y=x², y=x³+x²+x, z=x+y+xy, z=sin(x)+cos(y)+xy+x²−y²) con parámetros recomendados y resultados medidos. Ver **[BENCHMARK.md](../BENCHMARK.md)**. Ficheros: `benchmarkPolinomioCubico.txt`, `benchmarkMultivariado.txt`, `benchmarkTrigPolinomico.txt`, `benchmark_nguyen1.txt`.

---

## 6. Utilidades

- **GpLogger:** Logger global con niveles `SILENT` (por defecto), `INFO` y `DEBUG`. SILENT no produce ninguna salida; INFO muestra progreso por generación; DEBUG añade detalles de simplificación. Uso: `GpLogger.setNivel(GpLogger.Nivel.INFO)`.
- **ResultadoEjecucion:** Resultado inmutable de una ejecución: mejor individuo, generación final y si se alcanzó el objetivo. Devuelto por `AlgoritmoGenetico.getUltimoResultado()` tras `ejecutar()`.
- **SimplificadorExpresion:** Simplifica algebraicamente un árbol de expresión (x+0→x, x*1→x, plegado de constantes…) sin mutar el original. Las divisiones con denominador ≈ 0 no se pliegan para preservar la singularidad visible.
- **ExportadorExpresion:** Serializa un árbol a notación prefija, LaTeX o Python (`toPrefija`, `toLatex`, `toPythonDef`).
- **EvolucionLogger:** Registra el mejor fitness e individuo de cada generación en un CSV.

---

## 7. Ejecución

- **Compilar:** `mvn compile`. **Tests:** `mvn test`.
- **GUI:** `mvn javafx:run` (clase `gui.AppGP`). Configuración de fichero, tipo de problema, parámetros, funciones y visualización de evolución, mejor expresión y árbol.
- **Línea de comandos:** `test.TesterAlgoritmoProgramacionGenetica [fichero]`, `test.TesterDemoValores`, `test.TesterCruce`, `test.TesterIndividuos`, `test.TesterLecturaYFitness`. Directorio de trabajo: raíz del proyecto.

Parámetros del algoritmo: tamaño población, generaciones, profundidad inicial, probabilidad de cruce, tamaño torneo, probabilidad de mutación, semilla (opcional), profundidad máxima tras cruce/mutación, máximo de nodos por individuo, generaciones sin mejora para parar.

---

## 8. Tests

JUnit 5 cubre: creación de individuos (full/grow), número de nodos y profundidad, `crearSubarbolAleatorio`, `reemplazarNodo`, `calcularExpresion`; mutación (resultado válido, original inalterado); cruce (dos descendientes, progenitores inalterados, CruceNuloException, cruce con funciones unarias y mixtas); algoritmo (creación de población, nueva población con/sin mutación, poblaciones de tamaño impar, ejecución con funciones unarias); dominios (aritmético, clasificación, booleano: terminales, funciones, carga de datos, fitness); EvolucionLogger.

---

## 9. Referencias

- Historial de cambios: **changelog.md** (raíz).
- Extensiones posibles: **doc/ROADMAP.md**.
