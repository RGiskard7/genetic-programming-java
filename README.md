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
├── doc/
│   ├── DOCUMENTACION.md      # Documentación técnica unificada (ampliable)
│   └── EVOLUCION_PROYECTO.md # Ideas de evolución y mejoras
└── src/
    ├── algoritmogenetico/    # Núcleo del algoritmo y dominio
    │   ├── AlgoritmoGenetico.java, IAlgoritmo.java
    │   ├── dominio/          # IDominio, DominioAritmetico
    │   └── individuo/        # IIndividuo, Individuo, nodo (funciones y terminales)
    ├── excepciones/          # CruceNuloException, ArgsDistintosFuncionesException
    └── test/                 # Runners manuales (Tester*) y tests JUnit (*Test.java)
```

---

## Funcionalidad actual

- **Representación:** individuos como árboles de expresiones (notación prefija); nodos = funciones (`+`, `-`, `*`) o terminales (p. ej. variable `x`).
- **Algoritmo evolutivo:** población inicial aleatoria con profundidad máxima configurable; en cada generación: evaluación de fitness, elitismo, selección por torneo, cruce por subárbol (con reintentos ante cruce nulo), mutación con probabilidad configurable.
- **Dominio:** regresión sobre pares (x, y) leídos de un fichero; fitness = número de puntos en los que el error cuadrático no supera un umbral.
- **Reproducibilidad:** semilla opcional en el constructor del algoritmo para fijar el generador aleatorio.

Parámetros del algoritmo: tamaño de población, número máximo de generaciones, profundidad del árbol, probabilidad de cruce, tamaño del torneo, probabilidad de mutación (0 = desactivada), semilla (opcional). Ver `doc/DOCUMENTACION.md` para detalles y la restricción de que el número de individuos a rellenar por cruce debe ser par.

---

## Tests

Los tests (JUnit 5) cubren:

- **Individuo y árbol:** creación aleatoria, número de nodos, `crearSubarbolAleatorio`, `reemplazarNodo`, `calcularExpresion`.
- **Mutación:** resultado válido, no modificación del original.
- **Cruce:** dos descendientes, progenitores inalterados, integración en nueva población.
- **Algoritmo:** creación de población, nueva población con y sin mutación, ejecución de una generación con fichero temporal.
- **Dominio:** definición de terminales/funciones, carga de datos, cálculo de fitness.

Ejecución: `mvn test`.

---

## Documentación

- **`doc/DOCUMENTACION.md`** — Visión general, arquitectura (interfaces e implementaciones), operadores (torneo, cruce, mutación, elitismo), dominio aritmético, ejecución y tests. Referencia para ampliar el proyecto.
- **`doc/EVOLUCION_PROYECTO.md`** — Propuestas de extensión (más operadores, límite de profundidad, otros dominios, experimentos, etc.).
- **`changelog.md`** — Historial de cambios (mejoras de robustez, mutación, documentación y tests).

---

## Historial de cambios

El historial detallado se mantiene en **`changelog.md`**. Resumen reciente:

- Mejoras de robustez y estilo (recursos, comparadores, reintentos de cruce, Random con semilla, nomenclatura, `copy()` abstracto, constructor en `TerminalAritmetico`).
- Operador de mutación (probabilidad configurable, profundidad del subárbol de reemplazo fija).
- Documentación unificada y tests JUnit 5; construcción con Maven.

---

## Licencia

Por determinar. Proyecto de carácter académico y didáctico.
