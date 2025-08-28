package com.example.devdash.controller.cards.typingtest;

import com.example.devdash.helper.data.Session;
import com.example.devdash.model.auth.PreferencesModel;
import com.example.devdash.model.auth.User;
import com.example.devdash.model.typingtest.TypingSession;
import com.example.devdash.model.typingtest.TypingTestModel;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for the statistics pane in the Typing Test card.
 *
 * Author: Alexander Sukhin
 * Version: 28/08/2025
 */
public class StatsController implements TypingTestPaneController {

    @FXML private LineChart<String, Number> wpmDateChart;

    private TypingTestModel typingModel;
    private PreferencesModel prefs;
    private User user;

    /**
     * JavaFX initialization method called after FXML fields are injected.
     *
     * Initializes user and preferences, then loads chart data if user is available.
     */
    @FXML
    public void initialize() {
        user = Session.getInstance().getUser();
        if (user == null) return;

        prefs = PreferencesModel.getInstance();
        typingModel = new TypingTestModel();
        loadChartData();
    }

    /**
     * Loads and populates the WPM line chart data.
     */
    private void loadChartData() {
        wpmDateChart.getData().clear();
        wpmDateChart.setLegendVisible(false);

        List<TypingSession> sessions = getSortedSessions();
        if (sessions.isEmpty()) return;

        List<String> labels = extractLabels(sessions);
        List<Double> wpmValues = extractWpmValues(sessions);

        double[] bounds = calculateIqrBounds(wpmValues);
        double lowerBound = bounds[0];
        double upperBound = bounds[1];

        List<Integer> validIndices = filterValidIndices(wpmValues, lowerBound, upperBound);

        XYChart.Series<String, Number> pointSeries = buildPointSeries(labels, wpmValues, lowerBound, upperBound);

        if (validIndices.size() < 2) {
            wpmDateChart.getData().add(pointSeries);
            return;
        }

        XYChart.Series<String, Number> regressionSeries = buildRegressionSeries(labels, wpmValues, validIndices);

        wpmDateChart.getData().addAll(pointSeries, regressionSeries);

        styleSeries(pointSeries, regressionSeries);
    }

    /**
     * Retrieves all typing sessions for the current user and sorts them by start time ascending.
     *
     * @return Sorted list of TypingSession objects
     */
    private List<TypingSession> getSortedSessions() {
        List<TypingSession> sessions = typingModel.getSessions(user.getID());
        sessions.sort(Comparator.comparing(TypingSession::getStartTime));
        return sessions;
    }


    /**
     * Extracts date/time labels from the session start times for the chart's X axis.
     *
     * @param sessions List of typing sessions
     * @return List of formatted labels in "YYYY-MM-DD\nHH:mm" format
     */
    private List<String> extractLabels(List<TypingSession> sessions) {
        List<String> labels = new ArrayList<>();
        for (TypingSession session : sessions) {
            String[] parts = session.getStartTime().split(" ");
            labels.add(parts[0] + "\n" + parts[1].substring(0, 5));
        }
        return labels;
    }

    /**
     * Extracts the WPM values from the typing sessions.
     *
     * @param sessions List of typing sessions
     * @return List of WPM values as Doubles
     */
    private List<Double> extractWpmValues(List<TypingSession> sessions) {
        List<Double> wpmValues = new ArrayList<>();
        for (TypingSession session : sessions) {
            wpmValues.add(session.getWpm());
        }
        return wpmValues;
    }

    /**
     * Calculates the lower and upper bounds for filtering outliers using the Interquartile Range method.
     *
     * @param values list of WPM values
     * @return Array of two doubles: [lowerBound, upperBound]
     */
    private double[] calculateIqrBounds(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        double q1 = getPercentile(sorted, 25);
        double q3 = getPercentile(sorted, 75);
        double iqr = q3 - q1;

        double lowerBound = Math.max(10, q1 - 1.5 * iqr);  // Clip lower bound at 10
        double upperBound = q3 + 1.5 * iqr;

        System.out.println("Q1: " + q1 + ", Q3: " + q3 + ", IQR: " + iqr);
        System.out.println("Lower Bound: " + lowerBound + ", Upper Bound: " + upperBound);

        return new double[] { lowerBound, upperBound };
    }

    /**
     * Filters indices of WPM values that fall within the given bounds.
     *
     * @param values List of WPM values
     * @param lowerBound Lower threshold
     * @param upperBound Upper threshold
     * @return List of indices of values within bounds (non-outliers)
     */
    private List<Integer> filterValidIndices(List<Double> values, double lowerBound, double upperBound) {
        List<Integer> validIndices = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            double wpm = values.get(i);
            if (wpm >= lowerBound && wpm <= upperBound) {
                System.out.println("Correct WPM: index=" + i + ", wpm=" + wpm);
                validIndices.add(i);
            } else {
                System.out.println("Anomalous WPM: index=" + i + ", wpm=" + wpm);
            }
        }
        return validIndices;
    }

    /**
     * Builds the series of points representing individual WPM measurements.
     *
     * @param labels X-axis labels (dates)
     * @param wpmValues List of WPM values
     * @param lowerBound Lower bound for filtering outliers
     * @param upperBound Upper bound for filtering outliers
     * @return XYChart.Series containing data points
     */
    private XYChart.Series<String, Number> buildPointSeries(List<String> labels, List<Double> wpmValues, double lowerBound, double upperBound) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        //series.setName("WPM Points");

        for (int i = 0; i < wpmValues.size(); i++) {
            String label = labels.get(i);
            double wpm = wpmValues.get(i);

            XYChart.Data<String, Number> point = new XYChart.Data<>(label, wpm);
            series.getData().add(point);

            point.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    boolean isOutlier = (wpm < lowerBound || wpm > upperBound);
                    String color = isOutlier ? "red" : "lightgreen"; // orange or blue
                    newNode.setStyle("-fx-background-color: " + color + ", white;");
                }
            });
        }
        return series;
    }

    /**
     * Builds a linear regression series (trend line) based on filtered WPM values.
     *
     * @param labels X-axis labels
     * @param wpmValues List of all WPM values
     * @param validIndices Indices of values considered valid (non-outliers)
     * @return XYChart.Series representing the trend line (two endpoints)
     */
    private XYChart.Series<String, Number> buildRegressionSeries(List<String> labels, List<Double> wpmValues, List<Integer> validIndices) {
        int n = validIndices.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;

        for (int i = 0; i < n; i++) {
            int x = validIndices.get(i);
            double y = wpmValues.get(x);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        int firstIndex = validIndices.get(0);
        int lastIndex = validIndices.get(n - 1);

        XYChart.Series<String, Number> regressionSeries = new XYChart.Series<>();

        //regressionSeries.setName("Trend Line");

        XYChart.Data<String, Number> firstPoint = new XYChart.Data<>(labels.get(firstIndex), slope * firstIndex + intercept);
        XYChart.Data<String, Number> lastPoint = new XYChart.Data<>(labels.get(lastIndex), slope * lastIndex + intercept);

        regressionSeries.getData().addAll(firstPoint, lastPoint);

        firstPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-background-color: green, white;");
            }
        });

        lastPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-background-color: green, white;");
            }
        });

        return regressionSeries;
    }

    /**
     * Applies styling to the data and regression series, including stroke color based on dark mode preference.
     *
     * @param pointSeries The data points series
     * @param regressionSeries The trend line series
     */
    private void styleSeries(XYChart.Series<String, Number> pointSeries, XYChart.Series<String, Number> regressionSeries) {
        pointSeries.getNode().setStyle("-fx-stroke: transparent;");

        String color;
        if (prefs.getDarkMode(user.getID())) {
            color = "lightgray";
        } else {
            color = "gray";
        }
        regressionSeries.getNode().setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 2px;");
    }

    /**
     * Calculates the specified percentile value from a sorted list.
     *
     * @param sortedList The sorted list of doubles
     * @param percentile The percentile to calculate (e.g., 25 or 75)
     * @return The percentile value
     */
    private double getPercentile(List<Double> sortedList, double percentile) {
        if (sortedList.isEmpty()) return 0;

        double index = percentile / 100.0 * (sortedList.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        if (lower == upper) {
            return sortedList.get(lower);
        }
        double weight = index - lower;
        return sortedList.get(lower) * (1 - weight) + sortedList.get(upper) * weight;
    }

    /**
     * Resets the stats page to the initial state.
     */
    @Override
    public void resetPane() {
        loadChartData();
    }
}

