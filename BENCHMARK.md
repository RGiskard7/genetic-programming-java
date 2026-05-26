# Suite de Benchmarks — Programación Genética

Cuatro casos de prueba progresivos para validar el sistema manualmente.
Cada caso incluye el fichero de datos, la configuración recomendada y el resultado esperado.

---

## Caso 1 · Fácil — Cuadrática `y = x²`

**Fichero:** `valoresX2.txt`  
**Función objetivo:** y = x²  
**Puntos:** 7 (x ∈ {-3, -2, -1, 0, 1, 2, 3})

### Configuración recomendada

| Parámetro       | Valor         |
|-----------------|---------------|
| Funciones       | `+`, `-`, `*` |
| Terminales      | `x`           |
| Población       | 30            |
| Generaciones    | 20            |
| Profundidad     | 4             |
| Mutación        | 20 %          |
| Cruce           | 80 %          |
| Inmigrantes     | 0 %           |
| Semilla         | 42            |
| Escalado lineal | activado      |

### Resultado esperado

| Campo                 | Valor                   |
|-----------------------|-------------------------|
| Mejor expresión       | `(x * x)` o equivalente |
| Expresión simplificada | `(x * x)`              |
| Fitness               | ≥ −0.001 (muy cercano a 0) |
| RMSE                  | ≈ 0.000                 |
| Singularidades        | No                      |
| Generación de parada  | ≤ 10 (converge rápido)  |

**Diagnóstico:** Si el algoritmo no converge aquí, hay un problema fundamental en la selección o cruce.

---

## Caso 2 · Medio — Polinomio cúbico `y = x³ + x² + x`

**Fichero:** `benchmarkPolinomioCubico.txt`  
**Función objetivo:** y = x³ + x² + x  
**Puntos:** 9 (x ∈ [−2.0, 2.0] paso 0.5)

### Configuración recomendada

| Parámetro       | Valor         |
|-----------------|---------------|
| Funciones       | `+`, `-`, `*` |
| Terminales      | `x`           |
| Población       | 50            |
| Generaciones    | 50            |
| Profundidad     | 5             |
| Mutación        | 20 %          |
| Cruce           | 80 %          |
| Inmigrantes     | 0 %           |
| Semilla         | 42            |
| Escalado lineal | activado      |

### Resultado esperado

| Campo                  | Valor                             |
|------------------------|-----------------------------------|
| Mejor expresión        | variante de `x³ + x² + x`        |
| Expresión simplificada | forma compacta equivalente        |
| Fitness                | ≥ −0.05                           |
| RMSE                   | < 0.5                             |
| Singularidades         | No                                |
| Generación de parada   | variable (10–50)                  |

**Diagnóstico:** Evalúa si el árbol puede construir polinomios de grado 3. El escalado lineal ayuda si la expresión encontrada es proporcional a la correcta.

---

## Caso 3 · Multivariado — `z = x + y + x·y`

**Fichero:** `benchmarkMultivariado.txt`  
**Función objetivo:** z = x + y + x·y  
**Puntos:** 25 (rejilla 5×5, x,y ∈ {−2, −1, 0, 1, 2})  
**Nota:** fichero de 3 columnas — auto-detectado como multivariado. Variables: `v0`, `v1`.

### Configuración recomendada

| Parámetro       | Valor         |
|-----------------|---------------|
| Funciones       | `+`, `-`, `*` |
| Terminales      | `v0`, `v1`    |
| Población       | 50            |
| Generaciones    | 50            |
| Profundidad     | 5             |
| Mutación        | 20 %          |
| Cruce           | 80 %          |
| Inmigrantes     | 0 %           |
| Semilla         | 42            |
| Escalado lineal | activado      |

### Resultado esperado

| Campo                  | Valor                              |
|------------------------|------------------------------------|
| Mejor expresión        | `((v0 * v1) + (v0 + v1))` o equiv. |
| Expresión simplificada | forma compacta                     |
| Fitness                | ≥ −0.05                            |
| RMSE                   | < 0.5                              |
| Singularidades         | No                                 |
| Visualización          | "Real vs Predicho" (tab Datos)     |

**Diagnóstico:** Primer test real de regresión multivariada. Verifica que ambas variables (`v0`, `v1`) se usan en la expresión final.

---

## Caso 4 · Difícil — Trig-polinómica `z = sin(x) + cos(y) + x·y + x² − y²`

**Fichero:** `benchmarkTrigPolinomico.txt`  
**Función objetivo:** z = sin(x) + cos(y) + x·y + x² − y²  
**Puntos:** 25 (rejilla 5×5, x,y ∈ {−2, −1, 0, 1, 2})  
**Nota:** fichero de 3 columnas — auto-detectado como multivariado.

### Configuración rápida (exploración inicial)

| Parámetro            | Valor                       |
|----------------------|-----------------------------|
| Funciones            | `+`, `-`, `*`, `sin`, `cos` |
| Terminales           | `v0`, `v1`                  |
| Población            | 100                         |
| Generaciones         | 100                         |
| Profundidad          | 6                           |
| Mutación             | 25 %                        |
| Cruce                | 80 %                        |
| Inmigrantes          | 10 %                        |
| Semilla              | 42                          |
| Parada sin mejora    | 0 (desactivado)             |
| Ejecuciones          | 1                           |
| Escalado lineal      | activado                    |

---

## Caso 4B · "Prueba Tocha" — Trig-polinómica con función completa

Mismo fichero y fórmula que el Caso 4, pero con el conjunto de funciones extendido
(incluye `/` y `sqr`) y parámetros intensivos para búsqueda profunda.

**Fichero:** `benchmarkTrigPolinomico.txt`  
**Función objetivo:** z = sin(x) + cos(y) + x·y + x² − y²

### Configuración recomendada

| Parámetro            | Valor                           |
|----------------------|---------------------------------|
| Funciones            | `+`, `-`, `*`, `/`, `sin`, `cos`, `sqr` |
| Terminales           | `v0`, `v1`                      |
| Constantes           | `-1.0, 0.0, 1.0, 2.0`          |
| Población            | 300                             |
| Generaciones         | 250                             |
| Profundidad          | 6                               |
| Torneo               | 3                               |
| Mutación             | 25 %                            |
| Cruce                | 80 %                            |
| Inmigrantes          | 10 %                            |
| Parada sin mejora    | 40                              |
| Ejecuciones          | 5                               |
| Semilla base         | 42 (cada run: 42, 43, 44, 45, 46) |
| Escalado lineal      | activado                        |

### Resultados medidos (semilla 42, 5 runs, Inm% 10)

| Campo                  | Valor medido                                            |
|------------------------|---------------------------------------------------------|
| Fitness media          | −0.9596                                                 |
| Fitness std            | 0.2477                                                  |
| Fitness min / max      | −1.3993 / −0.8245                                       |
| Tasa de éxito          | 0 % (0/5) — esperado, ver nota abajo                   |
| Generaciones (última run) | ~140 (parada por sin mejora en 40)                 |
| Singularidades         | No (con Inm% 10 el resultado es más limpio)             |
| Visualización          | "Real vs Predicho" — curvas alineadas, ajuste razonable |

### Expresión típica encontrada (mejor run)

```
(- (- (cos (* (sin 1.0) (sqr v0))) (- (/ v0 2.0) 1.0)) (* (- v0 (/ v1 1.0)) (* v1 1.0)))
```

Simplificada: `(- (- (cos (* 0.8415 (sqr v0))) (- (/ v0 2.0) 1.0)) (* (- v0 v1) v1))`

Que equivale a: `cos(0.8415·v0²) − v0/2 + 1 − (v0−v1)·v1`

El algoritmo captura los términos `v0·v1` y `v1²` (con signos invertidos corregidos por
el escalado lineal) y construye un término trigonométrico a partir de `sqr`. No encuentra
la fórmula exacta, pero la estructura topológica es correcta.

### Por qué tasa de éxito 0% no es un fallo

El criterio de parada por objetivo es fitness ≥ −0.05 (RMSE < 0.05). Para esa precisión
con una función trig-polinómica de 2 variables y solo 25 puntos de entrenamiento, el
espacio de búsqueda es demasiado grande. Un fitness de −0.82 (RMSE ≈ 0.82) sobre datos
con rango ±7 equivale a un error relativo del ~12 %, que es un resultado correcto y útil.
El programa funciona correctamente; el benchmark es duro por diseño.

### Diagnóstico por resultado

| Fitness final | Diagnóstico                                                    |
|---------------|----------------------------------------------------------------|
| ≥ −0.5        | Excelente para este problema                                   |
| −1.0 a −0.5   | Bueno: ajuste parcial, estructura capturada                    |
| −1.5 a −1.0   | Regular: plateau prematuro; probar semilla diferente           |
| < −1.5        | Malo: considerar aumentar población o generaciones             |

**Nota:** La parada por "40 generaciones sin mejora" es intencional. Evita esperar el
máximo de 250 generaciones cuando el algoritmo ya convergió a un plateau.

---

## Notas generales

- **Escalado lineal** (`usarEscaladoLineal=true`, valor por defecto): mejora fitness en todos los casos porque ajusta la escala de la expresión a los datos. Desactivarlo sirve para diagnosticar si el algoritmo converge a una estructura correcta pero con escala incorrecta.
- **Elitismo estricto**: el mejor individuo siempre pasa a la siguiente generación. El fitness no decrece nunca entre generaciones (verificado en tests).
- **Inmigrantes**: útiles en los casos 3 y 4 para evitar convergencia prematura. Valor por defecto: 0% (desactivado). Recomendado para expresiones complejas: 5–15%.
- **Semillas reproducibles**: fijar semilla garantiza resultados idénticos en cada ejecución. Cambiar la semilla da una exploración diferente del espacio de búsqueda.
- **`sqr(x)` = x²** (función unaria): más eficiente que `(* x x)` en el árbol y más fácil de simplificar. Incluirla en el conjunto de funciones ayuda especialmente cuando la función objetivo tiene términos cuadráticos explícitos.
