# Genetic Programming in Java

Implementación en Java de **Programación Genética (GP)**: evolución de árboles de expresiones para regresión simbólica, clasificación binaria y síntesis de funciones booleanas. Incluye interfaz gráfica (JavaFX) para configuración, ejecución y visualización de resultados.

---

## Índice

- [Requisitos](#requisitos)
- [Construcción y ejecución](#construcción-y-ejecución)
- [Qué es la Programación Genética](#qué-es-la-programación-genética)
- [Funcionalidad](#funcionalidad)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Tests](#tests)
- [Documentación](#documentación)
- [Licencia](#licencia)

---

## Requisitos

- **JDK 11** o superior
- **Maven 3.6+**

---

## Construcción y ejecución

```bash
# Compilar
mvn compile

# Tests
mvn test
```

**Aplicación con interfaz gráfica (JavaFX):**

```bash
mvn javafx:run
```

Desde el IDE: ejecutar la clase `gui.AppGP` con el classpath de Maven y módulos JavaFX (por ejemplo, en VS Code usar la configuración de lanzamiento que incluye `--module-path` para JavaFX).

**Ejecución por línea de comandos** (sin GUI):

```bash
mvn compile
java -cp target/classes test.TesterAlgoritmoProgramacionGenetica [fichero_datos]
java -cp target/classes test.TesterDemoValores
```

El directorio de trabajo debe ser la raíz del proyecto para que se resuelvan los ficheros de datos (p. ej. `valores.txt`, `valoresX2.txt`).

---

## Qué es la Programación Genética

La **Programación Genética (GP)** es una técnica de optimización y búsqueda inspirada en la evolución natural. Pertenece a la familia de los **algoritmos evolutivos** y fue popularizada por John Koza en los años 90.

### Idea central

En lugar de evolucionar *parámetros* (como en los algoritmos genéticos clásicos), la GP evoluciona **estructuras de programa**: en este proyecto, **árboles de expresiones**. Cada individuo es un árbol cuyos nodos internos son **funciones** (operadores como +, −, ×, ÷, sin, cos, etc.) y cuyas hojas son **terminales** (variables como `x` o constantes). Al evaluar el árbol con valores concretos se obtiene un resultado numérico; la calidad del individuo se mide con una función de **fitness** que depende del problema (p. ej. error cuadrático medio frente a datos observados).

### Flujo típico

1. **Población inicial:** se generan al azar muchos árboles (individuos) con una profundidad y un conjunto de funciones/terminales fijos.
2. **Evaluación:** para cada individuo se calcula su fitness (p. ej. −RMSE en regresión, o precisión en clasificación).
3. **Selección:** se eligen padres (en este proyecto, por **torneo**: se toman varios candidatos al azar y se eligen los mejores).
4. **Variación:**  
   - **Cruce:** se intercambian subárboles entre dos padres para producir dos hijos.  
   - **Mutación:** se sustituye un subárbol por uno generado al azar.
5. **Reemplazo:** la nueva generación se forma con los mejores de la actual (elitismo) y los hijos obtenidos por cruce y mutación.
6. Se repite desde el paso 2 hasta cumplir un criterio de parada (número de generaciones, fitness objetivo o estancamiento).

### Por qué es útil

- **Regresión simbólica:** descubrir fórmulas que se ajusten a datos sin imponer a priori la forma de la ecuación (a diferencia de una regresión lineal o polinómica fija).
- **Clasificación:** evolucionar expresiones que, evaluadas con las entradas, separen clases (p. ej. salida > 0.5 → clase 1).
- **Síntesis de circuitos lógicos:** encontrar expresiones booleanas que reproduzcan una tabla de verdad.

La GP es **estocástica**: distintas ejecuciones (o distintas semillas) pueden dar distintos resultados. Por eso suele usarse con semilla fija para reproducibilidad y múltiples ejecuciones para estudios estadísticos.

---

## Funcionalidad

- **Representación:** individuos como árboles de expresiones en notación prefija. Funciones binarias (+, −, *, / con división protegida) y unarias (sin, cos, neg, abs, exp, log, sqrt, sqr). Terminales: variables y constantes (fijas o efímeras aleatorias).
- **Inicialización:** ramped half-and-half (mitad “full”, mitad “grow”) para diversidad.
- **Operadores:** selección por torneo, cruce por subárbol (soporta cualquier aridad), mutación por sustitución de subárbol, elitismo. Límite de profundidad y de nodos; parada por generaciones sin mejora.
- **Dominios:**  
  - **Regresión:** pares (x, y) desde fichero; fitness = −RMSE − α·nodos.  
  - **Clasificación binaria:** columnas numéricas + etiqueta 0/1; fitness = precisión − α·nodos.  
  - **Booleano:** tabla de verdad; fitness = aciertos − α·nodos.
- **Datos:** carga desde TSV/CSV; detección de cabecera; soporte multivariado en regresión.
- **GUI (JavaFX):** selector de fichero, tipo de problema (Regresión / Clasificación), parámetros del algoritmo, conjunto de funciones y constantes, gráfico de evolución del fitness, mejor expresión, gráfico datos vs curva (regresión) y visualización del árbol. Exportación de la mejor expresión y registro opcional en CSV.
- **Reproducibilidad:** semilla configurable en el algoritmo.

---

## Estructura del proyecto

```
├── pom.xml
├── README.md
├── LICENSE
├── changelog.md
├── valores.txt, valoresX2.txt, valoresLineal.txt, ...   # Datos de ejemplo
├── clasificacionEjemplo.csv, tablaVerdad.txt
├── doc/
│   ├── DOCUMENTACION.md    # Arquitectura y referencia técnica
│   └── ROADMAP.md          # Ideas de evolución
└── src/
    ├── main/java/
    │   ├── algoritmogenetico/   # AlgoritmoGenetico, dominio, individuo, nodos, util
    │   ├── excepciones/
    │   └── gui/                 # AppGP (JavaFX)
    └── test/java/test/          # Tests JUnit y runners (Tester*)
```

---

## Tests

Suite JUnit 5 para individuos, cruce, mutación, dominio (aritmético, booleano, clasificación), integración del algoritmo (población impar, funciones unarias) y logger de evolución.

```bash
mvn test
```

---

## Documentación

- **`doc/DOCUMENTACION.md`** — Arquitectura (interfaces, implementaciones), operadores, dominios y uso.
- **`doc/ROADMAP.md`** — Posibles extensiones y mejoras.
- **`changelog.md`** — Historial de cambios.

---

## Licencia

MIT License. Copyright (c) Eduardo Díaz Sánchez. Ver [LICENSE](LICENSE).
