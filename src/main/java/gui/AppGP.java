package gui;

import java.util.List;
import java.util.Map;

import algoritmogenetico.AlgoritmoGenetico;
import algoritmogenetico.IAlgoritmo;
import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.dominio.IDominio;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import excepciones.ArgsDistintosFuncionesException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Aplicacion JavaFX para visualizar la evolucion del algoritmo de programacion genetica:
 * grafica de mejor fitness por generacion, mejor expresion y datos vs curva.
 */
public class AppGP extends Application {

	private TextArea areaExpresion;
	private LineChart<Number, Number> chartFitness;
	private XYChart.Series<Number, Number> seriesFitness;
	private LineChart<Number, Number> chartDatos;
	private XYChart.Series<Number, Number> seriesDatos;
	private XYChart.Series<Number, Number> seriesCurva;

	@Override
	public void start(Stage stage) {
		NumberAxis axisGen = new NumberAxis();
		axisGen.setLabel("Generacion");
		NumberAxis axisFitness = new NumberAxis();
		axisFitness.setLabel("Mejor fitness");
		chartFitness = new LineChart<>(axisGen, axisFitness);
		chartFitness.setTitle("Mejor fitness por generacion");
		seriesFitness = new XYChart.Series<>();
		seriesFitness.setName("Fitness");
		chartFitness.getData().add(seriesFitness);

		NumberAxis axisX = new NumberAxis();
		axisX.setLabel("x");
		NumberAxis axisY = new NumberAxis();
		axisY.setLabel("y");
		chartDatos = new LineChart<>(axisX, axisY);
		chartDatos.setTitle("Datos y curva del mejor individuo");
		seriesDatos = new XYChart.Series<>();
		seriesDatos.setName("Datos");
		seriesCurva = new XYChart.Series<>();
		seriesCurva.setName("Curva");
		chartDatos.getData().addAll(seriesDatos, seriesCurva);

		areaExpresion = new TextArea();
		areaExpresion.setEditable(false);
		areaExpresion.setPrefRowCount(4);

		TextField rutaFichero = new TextField("valoresX2.txt");
		Button botonEjecutar = new Button("Ejecutar");
		HBox config = new HBox(10);
		config.getChildren().addAll(new Label("Fichero datos:"), rutaFichero, botonEjecutar);

		VBox centro = new VBox(10);
		centro.getChildren().addAll(chartFitness, new Label("Mejor expresion:"), areaExpresion, chartDatos);

		BorderPane root = new BorderPane();
		root.setTop(config);
		root.setCenter(centro);

		botonEjecutar.setOnAction(e -> {
			String ruta = rutaFichero.getText().trim();
			if (ruta.isEmpty()) return;
			botonEjecutar.setDisable(true);
			seriesFitness.getData().clear();
			seriesDatos.getData().clear();
			seriesCurva.getData().clear();
			areaExpresion.clear();
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					ejecutarAlgoritmo(ruta);
					return null;
				}
			};
			task.setOnSucceeded(ev -> botonEjecutar.setDisable(false));
			task.setOnFailed(ev -> botonEjecutar.setDisable(false));
			new Thread(task).start();
		});

		Scene scene = new Scene(root, 900, 700);
		stage.setTitle("Programacion Genetica - Visualizacion");
		stage.setScene(scene);
		stage.show();
	}

	private void ejecutarAlgoritmo(String rutaFichero) {
		try {
			DominioAritmetico dominio = new DominioAritmetico();
			dominio.definirValoresPrueba(rutaFichero);
			List<Terminal> terminales = dominio.definirConjuntoTerminales("x");
			List<Funcion> funciones = dominio.definirConjuntoFunciones(new int[] { 2, 2, 2, 2 }, "+", "-", "*", "/");
			IAlgoritmo alg = new AlgoritmoGenetico(50, 50, 4, 80, 3, 0.15, 42L);
			alg.defineConjuntoTerminales(terminales);
			alg.defineConjuntoFunciones(funciones);

			final IIndividuo[] ultimoMejor = new IIndividuo[1];
			alg.setGeneracionListener((gen, mejor) -> {
				ultimoMejor[0] = mejor;
				final int g = gen;
				final double fitness = mejor.getFitness();
				final String exp = mejor.getExpresion() != null ? mejor.getExpresion().toString() : "";
				Platform.runLater(() -> {
					seriesFitness.getData().add(new XYChart.Data<>(g, fitness));
					areaExpresion.setText(exp);
				});
			});

			alg.ejecutar(dominio);

			IIndividuo mejorIndiv = ultimoMejor[0];
			if (mejorIndiv != null && mejorIndiv.getExpresion() != null) {
				Map<Double, Double> datos = dominio.getValoresPrueba();
				if (!datos.isEmpty()) {
					double xMin = datos.keySet().stream().min(Double::compareTo).orElse(0.0);
					double xMax = datos.keySet().stream().max(Double::compareTo).orElse(1.0);
					Platform.runLater(() -> {
						for (Map.Entry<Double, Double> entry : datos.entrySet())
							seriesDatos.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
						for (double x = xMin; x <= xMax; x += (xMax - xMin) / 50) {
							setValorTerminales(mejorIndiv.getExpresion(), x);
							double y = mejorIndiv.calcularExpresion();
							seriesCurva.getData().add(new XYChart.Data<>(x, y));
						}
					});
				}
			}
		} catch (ArgsDistintosFuncionesException e) {
			Platform.runLater(() -> areaExpresion.setText("Error config: " + e.getMessage()));
		} catch (Exception e) {
			Platform.runLater(() -> areaExpresion.setText("Error: " + e.getMessage()));
		}
	}

	private void setValorTerminales(INodo nodo, double valor) {
		if (nodo instanceof TerminalAritmetico) {
			((TerminalAritmetico) nodo).setValor(valor);
		} else {
			for (INodo n : nodo.getDescendientes())
				setValorTerminales(n, valor);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
