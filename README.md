# Programación Genética en Java

Implementación didáctica de **Programación Genética (GP)** para regresión simbólica: evolución de árboles de expresiones mediante selección por torneo, cruce por subárbol y mutación. Los individuos son fórmulas (terminales y operadores) que se evalúan sobre un conjunto de datos; el algoritmo busca la expresión que mejor se ajuste.

Este documento se irá ampliando con cada iteración del proyecto.

---

## Contenido

- [Requisitos](#requisitos)
- [Construcción y ejecución](#construcción-y-ejecución)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Funcionalidad actual](#funcionalidad-actual)
- [Tests](#tests)
- [Documentación](#documentación)
- [Historial de cambios](#historial-de-cambios)

---

## Requisitos

- **JDK 11** o superior (el proyecto compila con Java 11 en Maven).
- **Maven 3.6+** (recomendado para compilar y ejecutar tests).

---

## Construcción y ejecución

```bash
# Compilar
mvn compile

# Ejecutar tests
mvn test
```

**Ejecución del algoritmo completo** (clase principal de ejemplo):

```bash
mvn compile
java -cp target/classes test.TesterAlgoritmoProgramacionGenetica
# Con fichero de datos distinto:
java -cp target/classes test.TesterAlgoritmoProgramacionGenetica valoresReducido.txt
```

O desde el IDE: ejecutar la clase `test.TesterAlgoritmoProgramacionGenetica`. El fichero `valores.txt` debe estar en la raíz del proyecto (directorio de trabajo).

**Importante:** El directorio de trabajo debe ser la raíz del proyecto (donde está `pom.xml`) para que se encuentren los ficheros de datos (`valores.txt`, `valoresReducido.txt`, `valoresX2.txt`). Al ejecutar desde el IDE, configurar "working directory" en la run configuration si hace falta.

**Demo con datos y = x²** (prueba de capacidades con función conocida):

```bash
java -cp target/classes test.TesterDemoValores
```

Usa `valoresX2.txt` (puntos de la parábola y = x²). Objetivo: que el algoritmo encuentre una expresión como `( * x x )`.

**GUI JavaFX** (visualización de evolución, mejor expresión y curva vs datos):

- **Desde VS Code:** en el panel "Run and Debug" (Ctrl+Shift+D) elige la configuración **"AppGP (JavaFX)"** y pulsa Run (F5) o Run Without Debugging (Ctrl+F5). Alternativa: Terminal → Run Task → **"JavaFX: run AppGP"** (ejecuta `mvn javafx:run`).
- **Desde terminal:** en la raíz del proyecto, `mvn javafx:run`.

En la ventana puedes cambiar el fichero de datos (p. ej. `valoresX2.txt`, `valoresLineal.txt`, `valoresCubica.txt`) y pulsar "Ejecutar"; se actualizan en tiempo real el gráfico de mejor fitness por generación, la mejor expresión y el gráfico de datos + curva del mejor individuo.

**Otros runners** (pruebas manuales de componentes):

- `test.TesterIndividuos` — creación y visualización de individuos aleatorios.
- `test.TesterLecturaYFitness` — carga de datos y cálculo de fitness (usa `valoresReducido.txt`).
- `test.TesterCruce` — cruce entre dos progenitores de ejemplo.

---

## Estructura del proyecto

```
programacion-genetica/
├── pom.xml
├── README.md
├── changelog.md
├── valores.txt              # Datos de ejemplo (entrada/salida para regresión)
├── valoresReducido.txt       # Subconjunto reducido para pruebas
├── valoresX2.txt            # Puntos y = x² para demo (probar capacidades)
├── valoresLineal.txt        # y = 2x + 1 (pruebas con constantes)
├── valoresCubica.txt        # y = x³ − x (pruebas con más profundidad/operadores)
├── doc/
│   ├── DOCUMENTACION.md      # Documentación técnica unificada (ampliable)
│   └── EVOLUCION_PROYECTO.md # Ideas de evolución y mejoras
└── src/
    ├── main/java/
    │   ├── algoritmogenetico/   # Núcleo: AlgoritmoGenetico, dominio, individuo, util (EvolucionLogger)
    │   ├── excepciones/
    │   └── gui/                 # AppGP (JavaFX): gráficos fitness, expresión, datos vs curva
    └── test/java/test/          # Runners (Tester*) y tests JUnit (*Test.java)
```

---

## Funcionalidad actual

- **Representación:** individuos como árboles de expresiones (notación prefija); nodos = funciones (`+`, `-`, `*`, `/` con división protegida) o terminales (variable `x` y constantes efímeras opcionales).
- **Algoritmo evolutivo:** población inicial aleatoria con profundidad máxima configurable; límite de profundidad tras cruce/mutación; en cada generación: evaluación de fitness (con parsimonia: penalización por tamaño), elitismo, selección por torneo, cruce por subárbol, mutación con probabilidad configurable.
- **Dominio:** regresión sobre pares (x, y) leídos de un fichero; fitness = puntos acertados − α·nodos; opción de registrar evolución en CSV (EvolucionLogger).
- **GUI JavaFX:** ventana con fichero de datos configurable, gráfico de mejor fitness por generación, mejor expresión y gráfico datos + curva del mejor individuo; el algoritmo se ejecuta en un `Task` y se actualiza vía listener por generación.
- **Reproducibilidad:** semilla opcional en el constructor del algoritmo.

Parámetros del algoritmo: tamaño de población, generaciones, profundidad, probabilidad de cruce, tamaño del torneo, probabilidad de mutación, semilla, profundidad máxima permitida. Ver `doc/DOCUMENTACION.md` para detalles.

---

## Tests

Los tests (JUnit 5) cubren:

- **Individuo y árbol:** creación aleatoria, número de nodos, `crearSubarbolAleatorio`, `reemplazarNodo`, `calcularExpresion`.
- **Mutación:** resultado válido, no modificación del original.
- **Cruce:** dos descendientes, progenitores inalterados, integración en nueva población.
- **Algoritmo:** creación de población, nueva población con y sin mutación, ejecución con fichero temporal, límite de profundidad.
- **Dominio:** definición de terminales/funciones, carga de datos, cálculo de fitness (parsimonia), división protegida, constantes.
- **EvolucionLogger:** registro CSV por generación.

Ejecución: `mvn test`.

---

## Documentación

- **`doc/DOCUMENTACION.md`** — Visión general, arquitectura (interfaces e implementaciones), operadores (torneo, cruce, mutación, elitismo), dominio aritmético, ejecución y tests. Referencia para ampliar el proyecto.
- **`doc/EVOLUCION_PROYECTO.md`** — Propuestas de extensión (más operadores, límite de profundidad, otros dominios, experimentos, etc.).
- **`changelog.md`** — Historial de cambios (mejoras de robustez, mutación, documentación y tests).

---

## Historial de cambios

El historial detallado se mantiene en **`changelog.md`**. Resumen reciente:

- **Fases 1–4 del plan de escalado:** parsimonia (fitness compuesto), límite de profundidad tras cruce/mutación, división protegida, constantes efímeras (TerminalConstante), EvolucionLogger CSV, GUI JavaFX (AppGP) con gráficos de fitness, expresión y datos vs curva.
- Mejoras de robustez y estilo (recursos, comparadores, reintentos de cruce, Random con semilla, mutación, documentación y tests JUnit 5).

---

## Licencia

Por determinar. Proyecto de carácter académico y didáctico.
