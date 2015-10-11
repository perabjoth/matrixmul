import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by perabjoth on 10/3/15.
 */
public class MatrixMul {


    public static void main(String[] args) {
        double matrix1[][];
        double matrix2[][];
        double matrixR[][];
        BufferedReader br = null;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter location of input file: ");
        String file = input.next();
        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(file));
            int columns1 = 0;
            int rows1 = 0;
            int columns2 = 0;
            int rows2 = 0;
            while ((sCurrentLine = br.readLine()) != null && !sCurrentLine.isEmpty()) {
                if (rows1 == 0) {
                    columns1 = sCurrentLine.split(",").length;
                } else if (columns1 != sCurrentLine.split(",").length) {
                    System.out.println("Invalid matrix");
                    return;
                }
                rows1++;
            }

            while ((sCurrentLine = br.readLine()) != null && !sCurrentLine.isEmpty()) {
                if (rows2 == 0) {
                    columns2 = sCurrentLine.split(",").length;
                } else if (columns2 != sCurrentLine.split(",").length) {
                    System.out.println("Invalid matrix");
                    return;
                }
                rows2++;
            }
            int max = 100;
            if (rows1 >= max || rows2 >= max || columns1 >= max || columns2 >= max) {
                System.out.println("Matrix too large");
                return;
            }
            matrix1 = new double[rows1][columns1];
            matrix2 = new double[rows2][columns2];
            matrixR = new double[rows1][columns2];
            br = new BufferedReader(new FileReader(file));
            int counter = 0;
            while ((sCurrentLine = br.readLine()) != null && !sCurrentLine.isEmpty()) {
                matrix1[counter] = StringToDoubleArray(sCurrentLine.split(","));
                counter++;
            }
            counter = 0;
            while ((sCurrentLine = br.readLine()) != null && !sCurrentLine.isEmpty()) {
                matrix2[counter] = StringToDoubleArray(sCurrentLine.split(","));
                counter++;
            }
            System.out.println("matrix 1 is " + rows1 + "X" + columns1 + ":");
            for (double x[] : matrix1) {
                System.out.println(Arrays.toString(x));
            }

            System.out.println("\nmatrix 2 is " + rows2 + "X" + columns2 + ":");

            for (double x[] : matrix2) {
                System.out.println(Arrays.toString(x));
            }
            System.out.println("\nmatrix resultant is " + matrixR.length + "X" + matrixR[0].length);

            int cores = Runtime.getRuntime().availableProcessors();
            System.out.println(cores + " cores are available");
            Scanner scanner = new Scanner(System.in);
            System.out.println("Press 1 to run sequentially, otherwise a positive number to run in parallel:");
            int choice = 999999999;
            while (choice == 999999999 || choice < 1) {
                try {
                    if (choice != 999999999) {
                        System.out.println("Must enter an integer greater than 1.  Try Again.");
                    }
                    choice = scanner.nextInt();


                } catch (InputMismatchException e) {
                    System.err.println("Input error.  Must enter an integer greater than 1.  Try Again.");
                    scanner = new Scanner(System.in);
                }
            }
            System.out.println();

            ExecutorService executor = Executors.newFixedThreadPool(1);
            int numThreads = 0;
            if (choice == 1) {
                numThreads = 1;
                System.out.println("Since choice was 1, tasks will run sequentially.Only 1 processor was allocated");
            } else {
                executor = Executors.newFixedThreadPool(cores);
                numThreads = cores;
                System.out.println(cores + " processors were allocated");
            }
            List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
            int tasksAdded = 0;
            for (int i = 0; i < rows1; i++) {

                tasksAdded++;

                for (int j = 0; j < columns2; j++) {
                    int position[] = {i, j};
                    MatrixThread thread = new MatrixThread(matrix1[i], column(matrix2, j), position, matrixR);
                    tasks.add(Executors.callable(thread));

                    if (tasksAdded == numThreads || (((rows1 - 1) == i) && (columns2 - 1) == j)) {
                        tasksAdded = 0;
                        try {
                            executor.invokeAll(tasks);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tasks.clear();
                    }

                }

            }
            executor.shutdown();

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("Output.txt"), "utf-8"))) {
                for (int i = 0; i < matrixR.length; i++) {
                    for (int j = 0; j < matrixR[i].length; j++) {
                        if (j != matrixR[i].length - 1) {
                            writer.write(String.valueOf(matrixR[i][j]) + ", ");
                        } else {
                            writer.write(String.valueOf(matrixR[i][j]));
                        }
                    }
                    writer.write("\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static double[] column(double matrix[][], int x) {
        int elements = matrix.length;
        double array[] = new double[elements];
        for (int i = 0; i < elements; i++) {
            array[i] = matrix[i][x];
        }
        return array;
    }

    public static double[] StringToDoubleArray(String array[]) {
        double doubles[] = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            doubles[i] = Double.parseDouble(array[i]);
        }
        return doubles;
    }
}
