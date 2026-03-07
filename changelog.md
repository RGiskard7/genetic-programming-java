# Changelog

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
