# Documentación del proyecto: Programación Genética

Documentación unificada del sistema (base y extensiones). Se irá ampliando con cada iteración.

---

## 1. Visión general

El proyecto implementa **Programación Genética (GP)** en Java: los individuos son **árboles de expresiones** (terminales y funciones), se evalúan con un **dominio** (p. ej. regresión sobre pares (x, f(x))), y la población evoluciona mediante **selección por torneo**, **cruce por subárbol** y **mutación**.

- **Objetivo típico:** encontrar una fórmula que se ajuste a datos de entrada/salida (regresión simbólica).
- **Salida:** mejor individuo por generación (expresión en notación prefija) y su fitness.

---

## 2. Arquitectura

### 2.1 Interfaces principales

| Interfaz | Rol |
|----------|-----|
| **IAlgoritmo** | Define el contrato del algoritmo: definir terminales/funciones, crear población, cruce, mutación, crear nueva población, ejecutar. |
| **IIndividuo** | Individuo = expresión (árbol). Métodos: get/set expresión, fitness, crear aleatorio, calcular expresión, número de nodos, etiquetar nodos. |
| **INodo** | Nodo del árbol: getSimbolo(), getDescendientes(), incluirDescendiente(), calcular(), copy(). |
| **IDominio** | Define terminales y funciones del problema, valores de prueba (p. ej. desde fichero), cálculo de fitness y fitness objetivo. |

### 2.2 Implementaciones actuales

- **AlgoritmoGenetico** implementa IAlgoritmo (torneo, cruce, mutación, elitismo).
- **Individuo** implementa IIndividuo; mantiene un mapa de nodos etiquetados para cruce/mutación.
- **Nodo** (abstracto) → **Funcion** / **Terminal**; funciones concretas: FuncionSuma, FuncionResta, FuncionMultiplicacion; terminal: TerminalAritmetico.
- **DominioAritmetico** implementa IDominio: regresión sobre pares (x, y) desde fichero; fitness = número de puntos en los que el error cuadrático ≤ 1.

### 2.3 Flujo de una ejecución

1. Se crea el algoritmo (tamaño población, generaciones, profundidad, % cruce, tamaño torneo, probabilidad mutación, opcional semilla).
2. Se definen terminales y funciones (desde el dominio).
3. Se definen valores de prueba en el dominio (p. ej. `definirValoresPrueba("valores.txt")`).
4. `ejecutar(dominio)`: crear población inicial; en cada generación: calcular fitness de todos, mostrar mejor, si se alcanza fitness objetivo terminar, si no crear nueva población (elitismo + torneos + cruce + mutación con probabilidad dada).

---

## 3. Operadores

### 3.1 Selección por torneo

- Se eligen al azar `valorTorneo` candidatos de la población.
- Se ordenan por fitness y se toman los **dos mejores** para cruzarlos.

### 3.2 Cruce por subárbol

- Se obtienen dos puntos de cruce (uno por progenitor), en el rango [1, número de nodos].
- Si ambos son 1 (raíz de ambos) se lanza **CruceNuloException**; el algoritmo reintenta hasta un límite o, en último caso, copia a los ganadores del torneo.
- En caso contrario: se copian ambos progenitores; en cada copia se sustituye el subárbol del punto de cruce por el subárbol del otro progenitor en su punto de cruce. Se devuelven los dos descendientes.

### 3.3 Mutación (añadido reciente)

- **Entrada:** un individuo.
- **Proceso:** se trabaja sobre una **copia**. Se etiquetan los nodos; se elige un nodo al azar (etiqueta 1..N). Se genera un **subárbol aleatorio** de profundidad máxima 2 (constante `PROFUNDIDAD_SUBARBOL_MUTACION`) con los mismos terminales y funciones del algoritmo. Se **reemplaza** ese nodo por el nuevo subárbol (si es la raíz, se sustituye toda la expresión).
- **Salida:** nuevo individuo mutado (el original no se modifica).
- **Uso en el algoritmo:** tras cada cruce, cada descendiente se muta con probabilidad `probabilidadMutacion` antes de añadirse a la nueva población. Con probabilidad 0 el comportamiento es el mismo que antes de introducir la mutación.

### 3.4 Elitismo

- El mejor individuo de la población actual se coloca en la posición 0 y se copia tal cual a la nueva población (junto con los no cruzados según `probabilidadCruce`).

---

## 4. Dominio aritmético

- **Terminales:** nombres (p. ej. `"x"`); internamente son `TerminalAritmetico` con un valor numérico que el dominio asigna antes de evaluar (p. ej. para cada x del fichero).
- **Funciones:** `+`, `-`, `*` (cada una con 2 argumentos).
- **Valores de prueba:** fichero de texto con líneas `x\ty` (tabulación). El dominio carga pares (x, y) y `fitnessBuscado` pasa a ser el número de puntos.
- **Fitness:** para cada (x, y) se asigna x a los terminales, se evalúa la expresión y se compara con y; si (valorEstimado - y)² ≤ 1 se suma 1 al fitness. El fitness total es la suma sobre todos los puntos.

---

## 5. Cómo ejecutar

- **Compilación (Maven):** `mvn compile`. **Tests:** `mvn test`.
- **Runner principal:** clase `test.TesterAlgoritmoProgramacionGenetica`: define dominio, algoritmo (p. ej. 100 individuos, 100 generaciones, profundidad 4, 90% cruce, torneo 4, probabilidad mutación 0.15), carga `valores.txt` y ejecuta.
- **Otros runners:** `TesterIndividuos`, `TesterLecturaYFitness`, `TesterCruce` para probar individuos, fitness y cruce por separado.

Parámetros del algoritmo (constructores):

- Tamaño población, máximo de generaciones, profundidad del árbol inicial.
- Probabilidad de cruce (porcentaje 0–100).
- Valor del torneo (número de candidatos).
- Probabilidad de mutación (0.0–1.0); opcional, por defecto 0.
- Semilla (opcional) para reproducibilidad.

**Importante:** El número de individuos que se añaden por cruce es siempre par (dos descendientes). Por tanto, `(tamanioPoblacion - referencia)` debe ser par; si no, el bucle de `crearNuevaPoblacion` no termina. Por ejemplo: probabilidad de cruce 50 % con población 10 da referencia 5 y faltarían 5 individuos (impar); es preferible usar, p. ej., 80 % (referencia 2, faltan 8).

---

## 6. Tests

Los tests (JUnit 5) cubren:

- **Individuo y árbol:** creación aleatoria, número de nodos, `crearSubarbolAleatorio`, `reemplazarNodo` (raíz e interno), `calcularExpresion`.
- **Mutación:** que `mutar` devuelve un individuo distinto (con semilla fija), que el árbol resultante es válido y que el original no cambia.
- **Cruce:** que se obtienen dos descendientes, que no se modifica a los progenitores, y comportamiento ante CruceNuloException (reintentos).
- **Algoritmo (integración):** tamaño de población tras crear y tras una generación, que con probabilidad de mutación 0 no se aplica mutación y que con probabilidad > 0 la nueva población puede contener individuos mutados.
- **Dominio y fitness:** definición de terminales/funciones, carga de datos desde fichero, cálculo de fitness para una expresión conocida.

Al añadir nuevas funcionalidades, se deben añadir o ampliar tests que las verifiquen y sigan probando las anteriores en conjunto.

---

## 7. Changelog y ampliaciones

- Ver **changelog.md** en la raíz para el historial de cambios.
- Ideas de evolución: **doc/EVOLUCION_PROYECTO.md** (límite de profundidad, más funciones/terminales, otros dominios, experimentos, etc.).

*Este documento se ampliará en futuras iteraciones (nuevos operadores, dominios, configuración, etc.).*
