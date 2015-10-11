import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by perabjoth on 10/6/15.
 */
public class MatrixMulPerform {

    public static void main(String[] args) {
        double matrix1[][];
        double matrix2[][];
        double matrixR[][];
        BufferedReader br = null;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter location of input file: ");
        String file = input.next();
        BufferedReader br2 = null;
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(cores + " cores are available.");
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
        try {
            br = new BufferedReader(new FileReader(file));
            br2 = new BufferedReader(new FileReader(file));
            br.mark(99999);
            br2.mark(99999);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sCurrentLine;

        try {
            while (br.readLine() != null) {
                br.reset();
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
                int n = rows1;
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

                br.mark(9999);
                matrix1 = new double[rows1][columns1];
                matrix2 = new double[rows2][columns2];
                matrixR = new double[rows1][columns2];
                int counter = 0;
                while ((sCurrentLine = br2.readLine()) != null && !sCurrentLine.isEmpty()) {
                    matrix1[counter] = StringToDoubleArray(sCurrentLine.split(","));
                    counter++;
                }
                counter = 0;
                while ((sCurrentLine = br2.readLine()) != null && !sCurrentLine.isEmpty()) {
                    matrix2[counter] = StringToDoubleArray(sCurrentLine.split(","));
                    counter++;
                }
                br2.mark(99999);
                System.out.println("matrix 1 is " + rows1 + "X" + columns1 + ":");
                for (double x[] : matrix1) {
                    System.out.println(Arrays.toString(x));
                }

                System.out.println("\nmatrix 2 is " + rows2 + "X" + columns2 + ":");

                for (double x[] : matrix2) {
                    System.out.println(Arrays.toString(x));
                }
                System.out.println("\nmatrix resultant is " + matrixR.length + "X" + matrixR[0].length);

                int reps = 0;
                long averageTime = 0;
                while (reps < 100) {
                    int numThreads = 0;
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    if (choice == 1) {
                        numThreads = 1;
//                        System.out.println("Since choice was 1, tasks will run sequentially.Only 1 processor was allocated");
                    } else {
                        numThreads = cores;
                        executor = Executors.newFixedThreadPool(cores);
//                        System.out.println(cores + " processors were allocated");
                    }

                    List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
                    int tasksAdded = 0;
                    long startTime = System.nanoTime();

                    for (int i = 0; i < rows1; i++) {
                        for (int j = 0; j < columns2; j++) {
                            int position[] = {i, j};
                            MatrixThread thread = new MatrixThread(matrix1[i], column(matrix2, j), position, matrixR);
                            tasks.add(Executors.callable(thread));
                            tasksAdded++;

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

                    long endTime = System.nanoTime();
                    long time = (endTime - startTime);
                    averageTime += time;
                    reps++;
                }
                averageTime = averageTime/100;

                try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("Output.txt", true)))) {
                    for (int i = 0; i < matrixR.length; i++) {
                        for (int j = 0; j < matrixR[i].length; j++) {
                            if (j != matrixR[i].length - 1) {
                                writer.print(String.valueOf(matrixR[i][j]) + ", ");
                            } else {
                                writer.print(String.valueOf(matrixR[i][j]));
                            }
                        }
                        writer.println();
                    }
                    writer.println();
                }

                try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("time.csv", true)))) {
                    writer.print(n + ", " + averageTime);
                    writer.println();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            if (br != null) br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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
