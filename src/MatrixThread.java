import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * Created by perabjoth on 10/3/15.
 */
public class MatrixThread implements Runnable {
    double row[];
    double column[];
    int position[] = new int[2];
    double sum = 0;
    double result[][];

    public MatrixThread(double row[], double column[], int position[],  double result[][]) {
        this.row = row;
        this.column = column;
        this.position = position;
        this.result = result;
    }


    public void run() {
        try {
//            System.out.println(this.toString() + " computing element [" + position[0] + "," + position[1] + "] of result (" + this.row.length + " multiplications).");
            for (int i = 0; i < this.row.length; i++) {
                this.sum += (this.row[i] * this.column[i]);
            }

//            System.out.println(this.toString() + " found element[" + position[0] + "," + position[1] + "] to be " + this.sum);
            result[position[0]][position[1]] = this.sum;
//            System.out.println(this.toString() + " Released/DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
