# Hacia dónde llevar este proyecto: ideas para hacerlo “con esteroides”

Este documento recoge ideas para convertir la práctica de Programación Genética en un proyecto más completo, reutilizable y didáctico. No es un plan cerrado: se puede ir implementando por fases.

---

## 1. Mejoras en el núcleo del algoritmo

### 1.1 Mutación
- **Qué:** Operador que altera un nodo (cambiar un terminal por otro, una función por otra, o sustituir un subárbol por uno aleatorio).
- **Por qué:** La práctica solo tiene cruce; en GP real la mutación ayuda a explorar y evita estancamiento.
- **Dónde:** `IAlgoritmo` + `AlgoritmoGenetico` (probabilidad de mutación, método `mutar(IIndividuo)`).

### 1.2 Límite de profundidad / tamaño
- **Qué:** Tras cruce o mutación, limitar la profundidad o el número de nodos del árbol (bloat control).
- **Por qué:** Evitar que los árboles crezcan sin mejorar el fitness.
- **Dónde:** Parámetros en el algoritmo y comprobación en cruce/mutación.

### 1.3 Más operadores de selección
- **Qué:** Además del torneo, ofrecer selección por ruleta, ranking, o (μ,λ).
- **Por qué:** Hace el código más flexible y didáctico para comparar estrategias.
- **Dónde:** Interfaz `ISeleccion` o estrategia inyectada en `AlgoritmoGenetico`.

### 1.4 Parada por convergencia / estancamiento
- **Qué:** Detener si el mejor fitness no mejora en N generaciones o si la diversidad cae por debajo de un umbral.
- **Por qué:** No depender solo de “max generaciones” o “fitness perfecto”.
- **Dónde:** Dentro de `ejecutar()` en `AlgoritmoGenetico`, con parámetros configurables.

---

## 2. Dominios y problemas

### 2.1 Más funciones y terminales
- **Qué:** División protegida (/), constantes ephemeral (p.ej. un random en [0,1] por nodo), `sin`, `cos`, `exp`, `log` protegido.
- **Por qué:** Regresión simbólica más expresiva y problemas tipo “descubrir fórmula”.
- **Dónde:** Nuevas clases en `nodo.funciones` y `nodo.terminales`; registro en `IDominio` o en un “conjunto de primitivas” configurable.

### 2.2 Varias variables (x, y, t, …)
- **Qué:** Terminales con nombres distintos (x, y, etc.) y asignación de valores por nombre en el dominio.
- **Por qué:** Problemas multivariados y datos reales (varias columnas).
- **Dónde:** `DominioAritmetico` (o un `DominioMultivariado`) con mapa nombre → valor; terminales que lleven el nombre y el dominio que haga `setValor` por nombre.

### 2.3 Otros dominios
- **Qué:** Dominios que no sean solo regresión: clasificación (fitness = precisión/auc), predicción de series temporales, “symbolic regression” con otras métricas (RMSE, R², etc.).
- **Por qué:** Un mismo motor GP sirve para distintos tipos de problema.
- **Dónde:** Nuevas implementaciones de `IDominio` y, si hace falta, variantes de fitness (minimizar error vs maximizar aciertos).

### 2.4 Datos desde CSV / URL
- **Qué:** Cargar pares (entrada, salida) o tablas desde CSV o desde URL, con cabeceras opcionales.
- **Por qué:** Más realista y reutilizable que solo un .txt con tabulaciones.
- **Dónde:** Utilidades de lectura + `definirValoresPrueba` (o `cargarDatos(String ruta)` en el dominio).

---

## 3. Experimentos y medición

### 3.1 Semilla y reproducibilidad
- **Qué:** Ya se ha añadido semilla opcional en el constructor; documentar que con la misma semilla se repiten los mismos resultados.
- **Extensión:** Fichero de configuración (JSON/YAML) con semilla, tamaño de población, probabilidades, etc., para “experimentos reproducibles”.

### 3.2 Logging de evolución
- **Qué:** Por generación: mejor fitness, fitness medio, peor fitness, tamaño/profundidad del mejor individuo.
- **Por qué:** Analizar convergencia y comparar configuraciones.
- **Dónde:** Listener/callback `onGeneracion(int gen, IIndividuo mejor, EstadisticasPoblacion)` o un `Logger` inyectado en `AlgoritmoGenetico`.

### 3.3 Ejecuciones múltiples y estadísticas
- **Qué:** Ejecutar N veces con distintas semillas y calcular media/desv. típica del mejor fitness y del número de generaciones hasta éxito.
- **Por qué:** GP es estocástico; una sola ejecución no es representativa.
- **Dónde:** Clase `Experimento` o script que llame a `ejecutar` N veces y agregue resultados (a CSV o a un informe).

### 3.4 Exportar mejor individuo (texto / imagen)
- **Qué:** Guardar la expresión del mejor en notación infija o en LaTeX; opcionalmente dibujar el árbol (p.ej. export a DOT/Graphviz o un pequeño visor).
- **Por qué:** Documentar soluciones y hacer el proyecto más presentable.
- **Dónde:** Método en `IIndividuo` o en un `IndividuoFormatter`; visor opcional en otro módulo.

---

## 4. Calidad de código y uso

### 4.1 Tests unitarios (JUnit)
- **Qué:** Tests automáticos para: creación de individuos, cruce (estructura y que no se pierdan nodos), cálculo de expresión, fitness para casos conocidos, mutación cuando exista.
- **Por qué:** Refactorizar y añadir features con seguridad.
- **Dónde:** `src/test` con JUnit 4 o 5; mantener los “Tester*” como runners manuales si se quieren, o sustituirlos por tests.

### 4.2 README y documentación
- **Qué:** README con descripción, requisitos, cómo compilar/ejecutar, estructura de paquetes, y enlace a Javadoc; opcional: un “Tutorial” con un problema de ejemplo de inicio a fin.
- **Por qué:** Cualquiera que clone el repo puede entender y ejecutar.
- **Dónde:** `README.md` en la raíz; `doc/` para tutorial o diseño.

### 4.3 Build con Maven o Gradle
- **Qué:** `pom.xml` o `build.gradle` para compilar, tests, empaquetar y generar Javadoc.
- **Por qué:** Estándar en proyectos Java y fácil para CI (GitHub Actions).
- **Dónde:** Raíz del proyecto.

### 4.4 CLI y parámetros
- **Qué:** Poder ejecutar desde línea de comandos indicando: fichero de datos, tamaño de población, generaciones, semilla, probabilidad de cruce/mutación, fichero de salida para el mejor individuo.
- **Por qué:** Experimentos sin recompilar; scripts y automatización.
- **Dónde:** Clase `Main` o uso de algo tipo picocli/commons-cli para parsear argumentos.

---

## 5. Resumen de prioridades sugeridas

| Prioridad | Tema                    | Esfuerzo | Impacto didáctico / utilidad |
|----------|--------------------------|----------|------------------------------|
| Alta     | Mutación                 | Medio    | Muy alto (GP completo)       |
| Alta     | README + cómo ejecutar   | Bajo     | Alto                         |
| Alta     | Tests JUnit básicos      | Medio    | Alto                         |
| Media    | Varias variables (x,y)   | Medio    | Alto                         |
| Media    | Logging por generación   | Bajo     | Alto                         |
| Media    | Maven/Gradle             | Bajo     | Medio                        |
| Media    | Límite profundidad       | Bajo     | Medio                        |
| Baja     | Más funciones (sin, cos) | Bajo     | Medio                        |
| Baja     | CSV / config JSON        | Medio    | Medio                        |
| Baja     | Visualización del árbol  | Alto     | Muy alto visual              |

Si quieres, el siguiente paso puede ser implementar una de estas fases (por ejemplo mutación + README, o tests + límite de profundidad) y lo vamos haciendo por pasos en el código.
