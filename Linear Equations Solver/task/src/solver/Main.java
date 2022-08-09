package solver;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void changeRows(double[][] m, int i, int j) {
        for (int k = 0; k < m[0].length; k++) {
            double t = m[i][k];
            m[i][k] = m[j][k];
            m[j][k] = t;
        }
    }

    public static void changeCols(double[][] m, int i, int j) {
        for (int k = 0; k < m.length; k++) {
            double t = m[k][i];
            m[k][i] = m[k][j];
            m[k][j] = t;
        }
    }

    public static boolean findNotZero(double[][] m, int i) {
        // проверяем строки ниже
        for (int j = i + 1; j < m.length; j++) {
            if (m[j][i] != 0) { // нашли замену
                changeRows(m, i, j);
                return true;
            }
        }

        // проверяем столбцы справа
        for (int j = i + 1; j < m[0].length - 1; j++) {
            if (m[i][j] != 0) { // нашли замену
                changeCols(m, i, j);
                return true;
            }
        }

        // ищем под главной диагональю
        // (столбцы правее и ниже текущего)
        for (int j = i + 1; j < m.length; j++) {
            for (int k = i + 1; k < m[0].length - 1; k++) {
                if (m[j][k] != 0) { // нашли замену
                    // m[i][-] <--> m[j][-] строки
                    changeRows(m, i, j);

                    // m[-][i] <--> m[-][k] колонки
                    changeCols(m, i, j);
                    return true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) throws IOException {

        String fileInput = "in.txt";
        String fileOutput = "out.txt";

        if (args.length == 4) {
            if ("-in".equals(args[0])) {
                fileInput = args[1];
            } else if ("-out".equals(args[0])) {
                fileOutput = args[1];
            }

            if ("-in".equals(args[2])) {
                fileInput = args[3];
            } else if ("-out".equals(args[2])) {
                fileOutput = args[3];
            }
        }

        Scanner scanner = new Scanner(Paths.get(fileInput));
        int n = scanner.nextInt();
        int numberOfEquations = scanner.nextInt();

        double[][] m = new double[numberOfEquations][n + 1];
        for (int i = 0; i < numberOfEquations; i++) {
            for (int j = 0; j < n + 1; j++) {
                m[i][j] = scanner.nextDouble();
            }
        }

        scanner.close();
//        for (int i = 0; i < n; i++) {
        for (int i = 0; i < numberOfEquations; i++) {

            // если число на главной диагонали равно 0, пытаемся поменять
            if (m[i][i] == 0) {
                if (!findNotZero(m, i)) {
                    break;
                }
            }

            // приводим к единице первое значение в строке
            if (m[i][i] != 0 && m[i][i] != 1) {
                double q = m[i][i];
                for (int j = 0; j < n + 1; j++) {
                    if (m[i][j] != 0) { // чтобы не образовывался -0.0
                        m[i][j] /= q;
                    }
                }
            }

            // приводим к 0 все значения под ним
            for (int j = i + 1; j < numberOfEquations; j++) {
                if (m[j][i] != 0) {
                    double q = m[j][i];
                    for (int k = i; k < n + 1; k++) {
                        m[j][k] -= q * m[i][k];
                    }
                }
            }
        }

        // проверка отсутствия решений
        int zeroCount = 0; // количество нулевых строк
        boolean noSolutions = false;
        for (int i = 0; i < numberOfEquations; i++) {
            double sum = 0;
            for (int j = 0; j < n; j++) {
                sum += m[i][j];
            }
            if (sum == 0) {
                if (m[i][n] == 0) {
                    zeroCount++;
                } else { // нет решений
                    noSolutions = true;
                    break;
                }
            }
        }

        if (noSolutions) {
            PrintWriter writer = new PrintWriter(new FileWriter(fileOutput, false));
            writer.print("No solutions");
            writer.close();
            return;
        } else if (numberOfEquations - zeroCount < n) { // количество значимых уравнений меньше количества значимых переменных
            // значит существует бесконечное количество решений
            PrintWriter writer = new PrintWriter(new FileWriter(fileOutput, false));
            writer.print("Infinitely many solutions");
            writer.close();
            return;
        }

        // существует одно решение, продолжаем решать
        for (int i = n - 1; i > 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (m[j][i] != 0) {
                    double q = m[j][i];
                    for (int k = n; k > i; k--) {
                        m[j][k] -= q * m[i][k];
                    }
                }
            }
        }

        // сохраняем решение
        PrintWriter writer = new PrintWriter(new FileWriter(fileOutput, false));

        for (int i = 0; i < n; i++) {
            writer.print(m[i][n]);
            if (i != n - 1) {
                writer.println();
            }
        }

        writer.close();

    }
}
