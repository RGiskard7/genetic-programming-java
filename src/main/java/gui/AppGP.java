package gui;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import algoritmogenetico.AlgoritmoGenetico;
import algoritmogenetico.IAlgoritmo;
import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import algoritmogenetico.util.EvolucionLogger;
import excepciones.ArgsDistintosFuncionesException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Aplicacion JavaFX para el proyecto de Programacion Genetica.
 * Permite configurar el algoritmo desde la interfaz, visualizar la evolucion
 * del mejor fitness por generacion, la mejor expresion encontrada, el ajuste
 * de la curva a los datos y el arbol de la mejor expresion.
 *
 * <p>Ejecutar con: {@code mvn javafx:run} desde la raiz del proyecto.</p>
 */
public class AppGP extends Application {

	// --- controles de configuracion ---
	private Stage stagePrincipal;
	private TextField rutaFichero;
	private Button botonExaminar;
	private ComboBox<String> comboTipoProblema;
	private CheckBox cbConstantes;
	private TextField tfConstantes;
	private CheckBox cbConstAleat;
	private Spinner<Integer> spinConstAleatN, spinConstAleatMin, spinConstAleatMax;
	private TextField tfSemilla;
	private Spinner<Integer> spinPob, spinGen, spinProf, spinTorneo, spinCruce, spinMutPct;
	private Spinner<Integer> spinMaxNodos, spinGenSinMejora;
	private CheckBox cbSuma, cbResta, cbMult, cbDiv, cbSeno, cbCoseno, cbNeg, cbAbs;
	private CheckBox cbExp, cbLog, cbSqrt, cbSqr;
	private CheckBox cbLogger;
	private TextField tfLoggerPath;
	private CheckBox cbExportarExp;
	private TextField tfExportarExpPath;
	private Button botonEjecutar;

	// --- paneles de resultado ---
	private LineChart<Number, Number> chartFitness;
	private XYChart.Series<Number, Number> seriesFitness;
	private LineChart<Number, Number> chartDatos;
	private XYChart.Series<Number, Number> seriesDatos;
	private XYChart.Series<Number, Number> seriesCurva;
	private TextArea areaExpresion;
	private Canvas canvasArbol;

	@Override
	public void start(Stage stage) {
		stagePrincipal = stage;
		BorderPane root = new BorderPane();
		root.setTop(crearPanelConfig());
		root.setCenter(crearPanelResultados());
		root.setPadding(new Insets(8));

		stage.setTitle("Programacion Genetica — Regresion y Clasificacion");
		stage.setScene(new Scene(root, 1150, 800));
		stage.show();
	}

	// -------------------------------------------------------------------------
	// Panel de configuracion (parte superior)
	// -------------------------------------------------------------------------

	private ScrollPane crearPanelConfig() {
		VBox config = new VBox(6);
		config.setPadding(new Insets(6, 8, 6, 8));

		// Fila 1: fichero de datos + examinar + tipo problema
		rutaFichero = new TextField("valoresX2.txt");
		rutaFichero.setPrefWidth(220);
		botonExaminar = new Button("Examinar...");
		botonExaminar.setOnAction(e -> elegirFichero());
		comboTipoProblema = new ComboBox<>();
		comboTipoProblema.getItems().addAll("Regresión", "Clasificación");
		comboTipoProblema.setValue("Regresión");
		comboTipoProblema.setPrefWidth(120);
		HBox fila1 = hbox("Fichero:", rutaFichero, botonExaminar, "Tipo:", comboTipoProblema);

		// Fila 2: constantes fijas y/o aleatorias + semilla
		cbConstantes = new CheckBox("Constantes:");
		cbConstantes.setSelected(true);
		tfConstantes = new TextField("-1.0,0.0,1.0,2.0");
		tfConstantes.setPrefWidth(140);
		tfConstantes.disableProperty().bind(cbConstantes.selectedProperty().not());
		cbConstAleat = new CheckBox("Const. aleat. N:");
		spinConstAleatN = new Spinner<>(0, 20, 0, 1);
		spinConstAleatMin = new Spinner<>(-10, 10, -2, 1);
		spinConstAleatMax = new Spinner<>(-10, 10, 2, 1);
		spinConstAleatN.setPrefWidth(50);
		spinConstAleatMin.setPrefWidth(55);
		spinConstAleatMax.setPrefWidth(55);
		Label lblSemilla = new Label("Semilla:");
		tfSemilla = new TextField("42");
		tfSemilla.setPrefWidth(70);
		HBox fila2 = hbox(cbConstantes, tfConstantes, cbConstAleat, spinConstAleatN, "min:", spinConstAleatMin, "max:", spinConstAleatMax, lblSemilla, tfSemilla);

		// Fila 3: parametros del algoritmo
		spinPob = new Spinner<>(4, 500, 50, 10);
		spinGen = new Spinner<>(1, 1000, 100, 10);
		spinProf = new Spinner<>(2, 10, 4, 1);
		spinTorneo = new Spinner<>(2, 20, 3, 1);
		spinCruce = new Spinner<>(50, 100, 80, 5);
		spinMutPct = new Spinner<>(0, 100, 15, 5);
		spinMaxNodos = new Spinner<>(0, 200, 0, 10);
		spinGenSinMejora = new Spinner<>(0, 100, 0, 5);
		for (Spinner<?> s : new Spinner<?>[]{ spinPob, spinGen, spinProf, spinTorneo, spinCruce, spinMutPct })
			s.setPrefWidth(72);
		spinMaxNodos.setPrefWidth(60);
		spinGenSinMejora.setPrefWidth(60);
		HBox fila3 = hbox("Población:", spinPob, "Gen.:", spinGen, "Prof.:", spinProf, "Torneo:", spinTorneo,
				"Cruce %:", spinCruce, "Mut %:", spinMutPct, "Max nodos (0=no):", spinMaxNodos, "Parar sin mejora (0=no):", spinGenSinMejora);

		// Fila 4: funciones
		cbSuma  = check("+", true); cbResta = check("-", true);
		cbMult  = check("*", true); cbDiv   = check("/", true);
		cbSeno  = check("sin", false); cbCoseno = check("cos", false);
		cbNeg   = check("neg", false); cbAbs   = check("abs", false);
		cbExp   = check("exp", false); cbLog   = check("log", false);
		cbSqrt  = check("sqrt", false); cbSqr  = check("sqr", false);
		HBox fila4 = hbox("Funciones:", cbSuma, cbResta, cbMult, cbDiv, cbSeno, cbCoseno, cbNeg, cbAbs, cbExp, cbLog, cbSqrt, cbSqr);

		// Fila 5: logger, exportar expresion, ejecutar
		cbLogger = new CheckBox("CSV:");
		tfLoggerPath = new TextField("evolucion.csv");
		tfLoggerPath.setPrefWidth(120);
		tfLoggerPath.disableProperty().bind(cbLogger.selectedProperty().not());
		cbExportarExp = new CheckBox("Exportar expr.:");
		tfExportarExpPath = new TextField("mejor_expresion.txt");
		tfExportarExpPath.setPrefWidth(140);
		tfExportarExpPath.disableProperty().bind(cbExportarExp.selectedProperty().not());
		botonEjecutar = new Button("▶ Ejecutar");
		botonEjecutar.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");
		botonEjecutar.setOnAction(e -> lanzarAlgoritmo());
		HBox fila5 = hbox(cbLogger, tfLoggerPath, cbExportarExp, tfExportarExpPath, botonEjecutar);

		config.getChildren().addAll(fila1, fila2, fila3, fila4, fila5);
		ScrollPane sp = new ScrollPane(config);
		sp.setFitToWidth(true);
		sp.setMaxHeight(200);
		return sp;
	}

	private Long parseSemilla() {
		try {
			String txt = tfSemilla.getText().trim();
			if (txt.isEmpty()) return null;
			return Long.parseLong(txt);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void elegirFichero() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Seleccionar fichero de datos");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Datos (TSV/CSV)", "*.txt", "*.csv", "*"));
		java.io.File f = fc.showOpenDialog(stagePrincipal);
		if (f != null)
			rutaFichero.setText(f.getAbsolutePath());
	}

	private HBox hbox(Object... items) {
		HBox box = new HBox(6);
		box.setPadding(new Insets(2));
		for (Object item : items) {
			if (item instanceof String) box.getChildren().add(new Label((String) item));
			else if (item instanceof javafx.scene.Node) box.getChildren().add((javafx.scene.Node) item);
		}
		return box;
	}

	private CheckBox check(String texto, boolean selected) {
		CheckBox cb = new CheckBox(texto);
		cb.setSelected(selected);
		return cb;
	}

	// -------------------------------------------------------------------------
	// Paneles de resultado (centro)
	// -------------------------------------------------------------------------

	private TabPane crearPanelResultados() {
		TabPane tabs = new TabPane();

		// Tab 1: evolucion
		seriesFitness = new XYChart.Series<>(); seriesFitness.setName("Mejor fitness");
		NumberAxis xFit = new NumberAxis(); xFit.setLabel("Generación");
		NumberAxis yFit = new NumberAxis(); yFit.setLabel("Fitness (−RMSE)");
		chartFitness = new LineChart<>(xFit, yFit);
		chartFitness.setTitle("Evolución del mejor fitness");
		chartFitness.getData().add(seriesFitness);
		chartFitness.setAnimated(false);

		areaExpresion = new TextArea();
		areaExpresion.setEditable(false);
		areaExpresion.setPrefRowCount(3);
		VBox tabEvol = new VBox(6, chartFitness, new Label("Mejor expresión:"), areaExpresion);
		tabEvol.setPadding(new Insets(6));

		// Tab 2: datos vs curva
		seriesDatos = new XYChart.Series<>(); seriesDatos.setName("Datos");
		seriesCurva = new XYChart.Series<>(); seriesCurva.setName("Curva");
		NumberAxis xD = new NumberAxis(); xD.setLabel("x");
		NumberAxis yD = new NumberAxis(); yD.setLabel("y");
		chartDatos = new LineChart<>(xD, yD);
		chartDatos.setTitle("Datos vs Curva del mejor individuo");
		chartDatos.getData().add(seriesDatos);
		chartDatos.getData().add(seriesCurva);
		chartDatos.setAnimated(false);

		// Tab 3: arbol
		canvasArbol = new Canvas(800, 380);
		VBox tabArbol = new VBox(new Label("Árbol de la mejor expresión:"), canvasArbol);
		tabArbol.setPadding(new Insets(6));

		tabs.getTabs().addAll(
				tab("Evolución", tabEvol),
				tab("Datos vs Curva", chartDatos),
				tab("Árbol", tabArbol));
		return tabs;
	}

	private Tab tab(String titulo, javafx.scene.Node contenido) {
		Tab t = new Tab(titulo, contenido);
		t.setClosable(false);
		return t;
	}

	// -------------------------------------------------------------------------
	// Ejecucion del algoritmo
	// -------------------------------------------------------------------------

	private void lanzarAlgoritmo() {
		botonEjecutar.setDisable(true);
		seriesFitness.getData().clear();
		seriesDatos.getData().clear();
		seriesCurva.getData().clear();
		areaExpresion.clear();
		limpiarCanvas();

		Task<Void> task = new Task<>() {
			@Override protected Void call() throws Exception {
				ejecutarAlgoritmo();
				return null;
			}
		};
		task.setOnSucceeded(e -> botonEjecutar.setDisable(false));
		task.setOnFailed(e -> {
			botonEjecutar.setDisable(false);
			Throwable ex = task.getException();
			Platform.runLater(() -> areaExpresion.setText("Error: " + (ex != null ? ex.getMessage() : "desconocido")));
		});
		new Thread(task).start();
	}

	private void ejecutarAlgoritmo() {
		try {
			String ruta = rutaFichero.getText().trim();
			boolean esRegresion = "Regresión".equals(comboTipoProblema.getValue());

			List<Terminal> terminales;
			List<Funcion> funciones = construirFunciones();
			if (funciones == null) {
				Platform.runLater(() -> areaExpresion.setText("Error: ninguna función válida seleccionada."));
				return;
			}

			Long semilla = parseSemilla();
			int pop     = spinPob.getValue();
			int gen     = spinGen.getValue();
			int prof    = spinProf.getValue();
			int torneo  = spinTorneo.getValue();
			int cruce   = spinCruce.getValue();
			double mut  = spinMutPct.getValue() / 100.0;

			AlgoritmoGenetico alg = new AlgoritmoGenetico(pop, gen, prof, cruce, torneo, mut, semilla);
			if (spinMaxNodos.getValue() > 0)
				alg.setMaxNodosIndividuo(spinMaxNodos.getValue());
			if (spinGenSinMejora.getValue() > 0)
				alg.setGeneracionesSinMejoraParaParar(spinGenSinMejora.getValue());

			if (esRegresion) {
				DominioAritmetico dominio = new DominioAritmetico();
				dominio.definirValoresPrueba(ruta);
				terminales = construirTerminalesRegresion(dominio);
				alg.defineConjuntoTerminales(terminales);
				alg.defineConjuntoFunciones(funciones);
				ejecutarConDominio(alg, dominio, true, ruta);
			} else {
				algoritmogenetico.dominio.DominioClasificacion dominioClasif = new algoritmogenetico.dominio.DominioClasificacion();
				dominioClasif.definirValoresPrueba(ruta);
				terminales = construirTerminalesClasificacion(dominioClasif);
				alg.defineConjuntoTerminales(terminales);
				alg.defineConjuntoFunciones(funciones);
				ejecutarConDominio(alg, dominioClasif, false, ruta);
			}
		} catch (Exception e) {
			Platform.runLater(() -> areaExpresion.setText("Error: " + e.getMessage()));
		}
	}

	private void ejecutarConDominio(IAlgoritmo alg, algoritmogenetico.dominio.IDominio dominio, boolean esRegresion, String ruta) {
		try {

			// Logger CSV opcional
			EvolucionLogger logger = null;
			if (cbLogger.isSelected()) {
				try {
					logger = new EvolucionLogger(Paths.get(tfLoggerPath.getText().trim()));
					alg.setLogger(logger);
				} catch (Exception e) {
					System.err.println("No se pudo crear el logger CSV: " + e.getMessage());
				}
			}

			final IIndividuo[] ultimoMejor = { null };
			alg.setGeneracionListener((genNum, mejor) -> {
				// Copiar la expresion para evitar race conditions con el hilo del algoritmo
				final INodo expCopia = mejor.getExpresion() != null ? mejor.getExpresion().copy() : null;
				final double fit = mejor.getFitness();
				ultimoMejor[0] = mejor;
				Platform.runLater(() -> {
					seriesFitness.getData().add(new XYChart.Data<>(genNum, fit));
					if (expCopia != null) {
						areaExpresion.setText(expCopia.toString());
						dibujarArbol(expCopia);
					}
				});
			});

			alg.ejecutar(dominio);

			IIndividuo mejorFinal = ultimoMejor[0];
			if (mejorFinal != null && mejorFinal.getExpresion() != null) {
				// Exportar expresion a fichero si esta activado
				if (cbExportarExp.isSelected()) {
					String pathExp = tfExportarExpPath.getText().trim();
					if (!pathExp.isEmpty()) {
						final String expr = mejorFinal.getExpresion().toString();
						Platform.runLater(() -> {
							try {
								java.nio.file.Files.writeString(java.nio.file.Path.of(pathExp), expr);
							} catch (Exception ex) {
								areaExpresion.setText(areaExpresion.getText() + "\n[No se pudo guardar: " + ex.getMessage() + "]");
							}
						});
					}
				}
				// Solo para regresion: dibujar datos vs curva
				if (esRegresion && dominio instanceof DominioAritmetico) {
					Map<Double, Double> datos = ((DominioAritmetico) dominio).getValoresPrueba();
					if (!datos.isEmpty()) {
						double xMin = datos.keySet().stream().min(Double::compareTo).orElse(0.0);
						double xMax = datos.keySet().stream().max(Double::compareTo).orElse(1.0);
						INodo expFinal = mejorFinal.getExpresion().copy();
						Platform.runLater(() -> {
							for (Map.Entry<Double, Double> e : datos.entrySet())
								seriesDatos.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
							double paso = (xMax - xMin) / 60.0;
							for (double x = xMin; x <= xMax + paso * 0.01; x += paso) {
								setValorTerminales(expFinal, x);
								double y = expFinal.calcular();
								if (Double.isFinite(y))
									seriesCurva.getData().add(new XYChart.Data<>(x, y));
							}
						});
					}
				}
			}
		} catch (Exception e) {
			Platform.runLater(() -> areaExpresion.setText("Error: " + e.getMessage()));
		}
	}

	private List<Terminal> construirTerminalesRegresion(DominioAritmetico dominio) {
		double[] constantes = construirArrayConstantes();
		if (constantes == null || constantes.length == 0)
			return dominio.definirConjuntoTerminales("x");
		return dominio.definirConjuntoTerminalesConConstantes(new String[]{"x"}, constantes);
	}

	private List<Terminal> construirTerminalesClasificacion(algoritmogenetico.dominio.DominioClasificacion dominio) {
		double[] constantes = construirArrayConstantes();
		String[] vars = dominio.getNombresVariables();
		if (constantes != null && constantes.length > 0)
			return dominio.definirConjuntoTerminalesConConstantes(vars, constantes);
		return dominio.definirConjuntoTerminales(vars);
	}

	/** Construye el array de constantes: fijas (lista), aleatorias (N en [min,max]) o ambas. */
	private double[] construirArrayConstantes() {
		java.util.List<Double> list = new ArrayList<>();
		if (cbConstantes.isSelected()) {
			for (String s : tfConstantes.getText().split(",")) {
				try { list.add(Double.parseDouble(s.trim())); }
				catch (NumberFormatException e) { }
			}
		}
		if (cbConstAleat.isSelected() && spinConstAleatN.getValue() > 0) {
			int n = spinConstAleatN.getValue();
			double min = spinConstAleatMin.getValue();
			double max = spinConstAleatMax.getValue();
			java.util.Random r = new java.util.Random(parseSemilla() != null ? parseSemilla() : 42L);
			for (int i = 0; i < n; i++)
				list.add(min + (max - min) * r.nextDouble());
		}
		if (list.isEmpty()) return null;
		return list.stream().mapToDouble(Double::doubleValue).toArray();
	}

	private List<Funcion> construirFunciones() throws ArgsDistintosFuncionesException {
		DominioAritmetico dominio = new DominioAritmetico();
		List<String> nombres = new ArrayList<>();
		List<Integer> aridades = new ArrayList<>();
		if (cbSuma.isSelected())   { nombres.add("+");   aridades.add(2); }
		if (cbResta.isSelected())  { nombres.add("-");   aridades.add(2); }
		if (cbMult.isSelected())   { nombres.add("*");   aridades.add(2); }
		if (cbDiv.isSelected())    { nombres.add("/");   aridades.add(2); }
		if (cbSeno.isSelected())   { nombres.add("sin"); aridades.add(1); }
		if (cbCoseno.isSelected()) { nombres.add("cos"); aridades.add(1); }
		if (cbNeg.isSelected())    { nombres.add("neg"); aridades.add(1); }
		if (cbAbs.isSelected())    { nombres.add("abs"); aridades.add(1); }
		if (cbExp.isSelected())    { nombres.add("exp"); aridades.add(1); }
		if (cbLog.isSelected())    { nombres.add("log"); aridades.add(1); }
		if (cbSqrt.isSelected())   { nombres.add("sqrt"); aridades.add(1); }
		if (cbSqr.isSelected())     { nombres.add("sqr"); aridades.add(1); }
		if (nombres.isEmpty()) {
			nombres.add("+"); nombres.add("-"); nombres.add("*");
			aridades.add(2); aridades.add(2); aridades.add(2);
		}
		int[] arr = aridades.stream().mapToInt(Integer::intValue).toArray();
		return dominio.definirConjuntoFunciones(arr, nombres.toArray(new String[0]));
	}

	private void setValorTerminales(INodo nodo, double valor) {
		if (nodo instanceof TerminalAritmetico) {
			((TerminalAritmetico) nodo).setValor(valor);
		} else {
			for (INodo n : nodo.getDescendientes())
				setValorTerminales(n, valor);
		}
	}

	// -------------------------------------------------------------------------
	// Dibujo del arbol en Canvas
	// -------------------------------------------------------------------------

	private void limpiarCanvas() {
		GraphicsContext gc = canvasArbol.getGraphicsContext2D();
		gc.clearRect(0, 0, canvasArbol.getWidth(), canvasArbol.getHeight());
	}

	/**
	 * Dibuja el arbol de la expresion en el Canvas usando BFS para calcular
	 * posiciones: cada nivel ocupa la misma altura, y los nodos se distribuyen
	 * equidistantemente en cada nivel.
	 */
	private void dibujarArbol(INodo raiz) {
		GraphicsContext gc = canvasArbol.getGraphicsContext2D();
		double w = canvasArbol.getWidth();
		double h = canvasArbol.getHeight();
		gc.clearRect(0, 0, w, h);
		if (raiz == null) return;

		// BFS: recoger nodos por nivel
		List<List<INodo>> niveles = new ArrayList<>();
		Queue<INodo> cola = new LinkedList<>();
		cola.add(raiz);
		while (!cola.isEmpty()) {
			int tam = cola.size();
			List<INodo> nivel = new ArrayList<>();
			for (int i = 0; i < tam; i++) {
				INodo n = cola.poll();
				nivel.add(n);
				cola.addAll(n.getDescendientes());
			}
			niveles.add(nivel);
		}

		int prof = niveles.size();
		double altoNivel = h / (prof + 1);
		double radio = Math.min(20, Math.max(8, altoNivel / 2.5));

		// Asignar posiciones (x, y) a cada nodo
		Map<INodo, double[]> pos = new LinkedHashMap<>();
		for (int lv = 0; lv < prof; lv++) {
			List<INodo> nivel = niveles.get(lv);
			double anchoNodo = w / (nivel.size() + 1);
			double y = altoNivel * (lv + 1);
			for (int j = 0; j < nivel.size(); j++) {
				pos.put(nivel.get(j), new double[]{ anchoNodo * (j + 1), y });
			}
		}

		// Dibujar aristas
		gc.setStroke(Color.SLATEGRAY);
		gc.setLineWidth(1.2);
		for (Map.Entry<INodo, double[]> e : pos.entrySet()) {
			double[] pPadre = e.getValue();
			for (INodo hijo : e.getKey().getDescendientes()) {
				double[] pHijo = pos.get(hijo);
				if (pHijo != null) gc.strokeLine(pPadre[0], pPadre[1], pHijo[0], pHijo[1]);
			}
		}

		// Dibujar nodos
		Font font = Font.font(Math.max(9, radio * 0.75));
		gc.setFont(font);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		for (Map.Entry<INodo, double[]> e : pos.entrySet()) {
			INodo nodo = e.getKey();
			double[] p = e.getValue();
			boolean esHoja = nodo.getDescendientes().isEmpty();
			gc.setFill(esHoja ? Color.LIGHTGREEN : Color.LIGHTSKYBLUE);
			gc.fillOval(p[0] - radio, p[1] - radio, 2 * radio, 2 * radio);
			gc.setStroke(Color.STEELBLUE);
			gc.setLineWidth(1.5);
			gc.strokeOval(p[0] - radio, p[1] - radio, 2 * radio, 2 * radio);
			gc.setFill(Color.BLACK);
			gc.fillText(nodo.getSimbolo(), p[0], p[1]);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
