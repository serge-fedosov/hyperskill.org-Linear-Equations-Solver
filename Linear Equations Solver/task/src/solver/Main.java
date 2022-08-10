package solver;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Scanner;

import static solver.Complex.isOne;
import static solver.Complex.isZero;

public class Main {

    public static void changeRows(Complex[][] m, int i, int j) {
        for (int k = 0; k < m[0].length; k++) {
            Complex t = m[i][k];
            m[i][k] = m[j][k];
            m[j][k] = t;
        }
    }

    public static void changeCols(Complex[][] m, int i, int j) {
        for (int k = 0; k < m.length; k++) {
            Complex t = m[k][i];
            m[k][i] = m[k][j];
            m[k][j] = t;
        }
    }

    public static boolean findNotZero(Complex[][] m, int i) {
        // check the lines below
        for (int j = i + 1; j < m.length; j++) {
            if (!isZero(m[j][i])) { // нашли замену
                changeRows(m, i, j);
                return true;
            }
        }

        // check the columns on the right
        for (int j = i + 1; j < m[0].length - 1; j++) {
            if (!isZero(m[i][j])) { // нашли замену
                changeCols(m, i, j);
                return true;
            }
        }

        // looking under the main diagonal
        // (columns to the right and below the current one)
        for (int j = i + 1; j < m.length; j++) {
            for (int k = i + 1; k < m[0].length - 1; k++) {
                if (!isZero(m[j][i])) { // found a replacement
                    // m[i][-] <--> m[j][-] lines
                    changeRows(m, i, j);

                    // m[-][i] <--> m[-][k] columns
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
        scanner.nextLine();

        Complex[][] m = new Complex[numberOfEquations][n + 1];
        for (int i = 0; i < numberOfEquations; i++) {
            String[] items = scanner.nextLine().strip().split("\\s++");
            for (int j = 0; j < n + 1; j++) {
                String[] parts = items[j].split("(?=[+-])");
                if (parts.length == 2) {
                    double image = 0;
                    if (parts[1].equals("i") || parts[1].equals("+i")) {
                        image = 1;
                    } else if (parts[0].equals("-i")) {
                        image = -1;
                    } else {
                        image = Double.parseDouble(parts[1].substring(0, parts[1].length() - 1));
                    }
                    m[i][j] = new Complex(Double.parseDouble(parts[0]), image);
                } else {
                    if (parts[0].contains("i")) {
                        double image = 0;
                        if (parts[0].equals("i") || parts[0].equals("+i")) {
                            image = 1;
                        } else if (parts[0].equals("-i")) {
                            image = -1;
                        } else {
                            image = Double.parseDouble(parts[0].substring(0, parts[0].length() - 1));
                        }
                        m[i][j] = new Complex(0, image);
                    } else {
                        m[i][j] = new Complex(Double.parseDouble(parts[0]),0);
                    }
                }
            }
        }

        scanner.close();

        for (int i = 0; i < numberOfEquations; i++) {

            // if the number on the main diagonal is 0, we try to change
            if (isZero(m[i][i])) {
                if (!findNotZero(m, i)) {
                    break;
                }
            }

            // convert the first value in the string to one
            if (!isZero(m[i][i]) && !isOne(m[i][i])) {
                Complex q = m[i][i];
                for (int j = 0; j < n + 1; j++) {
                    if (!isZero(m[i][j])) { // not to form -0.0
                        m[i][j] = m[i][j].div(q);
                    }
                }
            }

            // set to 0 all values below it
            for (int j = i + 1; j < numberOfEquations; j++) {
                if (!isZero(m[j][i])) {
                    Complex q = m[j][i];
                    for (int k = i; k < n + 1; k++) {
                        m[j][k] = m[j][k].sub(q.mul(m[i][k]));
                    }
                }
            }
        }

        // checking for the absence of solutions
        int zeroCount = 0; // number of null lines
        boolean noSolutions = false;
        for (int i = 0; i < numberOfEquations; i++) {
            boolean rowZero = true;
            for (int j = 0; j < n; j++) {
                rowZero = rowZero && isZero(m[i][j]);
            }
            if (rowZero) {
                if (isZero(m[i][n])) {
                    zeroCount++;
                } else { // no solutions
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
        } else if (numberOfEquations - zeroCount < n) { // the number of significant equations is less than the number of significant variables
            // so there is an infinite number of solutions
            PrintWriter writer = new PrintWriter(new FileWriter(fileOutput, false));
            writer.print("Infinitely many solutions");
            writer.close();
            return;
        }

        // there is only one solution, keep solving
        for (int i = n - 1; i > 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (!isZero(m[j][i])) {
                    Complex q = m[j][i];
                    for (int k = n; k > i; k--) {
                        m[j][k] = m[j][k].sub(q.mul(m[i][k]));
                    }
                }
            }
        }

        // save the result
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
