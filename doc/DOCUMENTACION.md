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

- **AlgoritmoGenetico:** torneo, cruce por subárbol (cualquier aridad), mutación, elitismo. Límite de profundidad y de nodos; parada por generaciones sin mejora.
- **Individuo:** mapa de nodos etiquetados para cruce/mutación; inicialización full y grow (ramped half-and-half).
- **Nodo** (abstracto) → **Funcion** / **Terminal**. Funciones: +, −, *, / (protegida), sin, cos, neg, abs, exp, log, sqrt, sqr. Terminales: **TerminalAritmetico** (variable o valor inyectado), **TerminalConstante** (valor fijo), **TerminalBooleano** (dominio booleano).
- **DominioAritmetico:** regresión sobre pares (x, y); fitness = −RMSE − α·nodos; carga TSV/CSV con detección de cabecera; soporte multivariado.
- **DominioClasificacion:** N columnas numéricas + etiqueta 0/1; fitness = precisión − α·nodos.
- **DominioBooleano:** tabla de verdad TSV; fitness = aciertos − α·nodos; funciones AND, OR, NOT, XOR.

### 2.3 Flujo de ejecución

1. Crear algoritmo (población, generaciones, profundidad, % cruce, torneo, probabilidad mutación, semilla opcional).
2. Definir terminales y funciones (desde el dominio).
3. Cargar valores de prueba en el dominio (`definirValoresPrueba(ruta)`).
4. `ejecutar(dominio)`: población inicial (ramped half-and-half); en cada generación: evaluar fitness, registrar mejor, parar si se alcanza objetivo o por estancamiento; si no, crear nueva población (elitismo + torneos + cruce + mutación).

---

## 3. Operadores

### 3.1 Selección por torneo

Se eligen al azar `valorTorneo` candidatos; se toman los dos mejores por fitness para cruzarlos.

### 3.2 Cruce por subárbol

Dos puntos de cruce (uno por progenitor), en [1, número de nodos]. Si ambos son 1 (raíz) se lanza **CruceNuloException** y el algoritmo reintenta o copia a los ganadores. En caso contrario se copian ambos progenitores y se sustituye en cada copia el subárbol del punto de cruce por el subárbol del otro progenitor. El cruce soporta funciones de cualquier aridad (unarias y binarias).

### 3.3 Mutación

Sobre una copia del individuo: se etiquetan nodos, se elige uno al azar y se reemplaza su subárbol por un subárbol aleatorio de profundidad máxima 2. Si el resultado supera el límite de profundidad o de nodos, se devuelve el original. Cada descendiente se muta con probabilidad `probabilidadMutacion`.

### 3.4 Elitismo

El mejor individuo se coloca en posición 0 y se copia a la nueva población junto con los no cruzados según `probabilidadCruce`.

---

## 4. Dominios

### 4.1 DominioAritmetico (regresión)

- **Terminales:** variables por nombre (p. ej. `"x"`) y constantes (fijas o efímeras aleatorias). El dominio asigna el valor a cada variable antes de evaluar.
- **Funciones:** +, −, *, / (división protegida), sin, cos, neg, abs, exp, log, sqrt, sqr.
- **Datos:** fichero TSV o CSV; detección de cabecera; 2 columnas (x, y) o multivariado (varias columnas + objetivo).
- **Fitness:** −RMSE − α·nodos. Objetivo: 0 (RMSE ≈ 0).

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

## 5. Ejecución

- **Compilar:** `mvn compile`. **Tests:** `mvn test`.
- **GUI:** `mvn javafx:run` (clase `gui.AppGP`). Configuración de fichero, tipo de problema, parámetros, funciones y visualización de evolución, mejor expresión y árbol.
- **Línea de comandos:** `test.TesterAlgoritmoProgramacionGenetica [fichero]`, `test.TesterDemoValores`, `test.TesterCruce`, `test.TesterIndividuos`, `test.TesterLecturaYFitness`. Directorio de trabajo: raíz del proyecto.

Parámetros del algoritmo: tamaño población, generaciones, profundidad inicial, probabilidad de cruce, tamaño torneo, probabilidad de mutación, semilla (opcional), profundidad máxima tras cruce/mutación, máximo de nodos por individuo, generaciones sin mejora para parar.

---

## 6. Tests

JUnit 5 cubre: creación de individuos (full/grow), número de nodos y profundidad, `crearSubarbolAleatorio`, `reemplazarNodo`, `calcularExpresion`; mutación (resultado válido, original inalterado); cruce (dos descendientes, progenitores inalterados, CruceNuloException, cruce con funciones unarias y mixtas); algoritmo (creación de población, nueva población con/sin mutación, poblaciones de tamaño impar, ejecución con funciones unarias); dominios (aritmético, clasificación, booleano: terminales, funciones, carga de datos, fitness); EvolucionLogger.

---

## 7. Referencias

- Historial de cambios: **changelog.md** (raíz).
- Extensiones posibles: **doc/ROADMAP.md**.
