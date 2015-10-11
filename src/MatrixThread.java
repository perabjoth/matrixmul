/**
 * Created by perabjoth on 10/3/15.
 *      ******
 *    **********
 *   *************
 *  ***************
 *  **   *****  ***
 *  ***************
 *   ****** ******
 *    ***********
 *     *********
 *    ***********
 *   *************
 */
//Runnable class to perform calculations and assign elements at appropriate index
public class MatrixThread implements Runnable {
    double row[];
    double column[];
    int position[] = new int[2];
    double sum = 0;
    double result[][];

    //Constructor takes the row and column that need to be multiplied as well as the position at which
    //The result needs to be stored at and the matrix in which to store it
    public MatrixThread(double row[], double column[], int position[],  double result[][]) {
        this.row = row;
        this.column = column;
        this.position = position;
        this.result = result;
    }

    //Performing calculations in the run method
    public void run() {
        try {
//            System.out.println(this.toString() + " computing element [" + position[0] + "," + position[1] + "] of result (" + this.row.length + " multiplications).");
            for (int i = 0; i < this.row.length; i++) {
                this.sum += (this.row[i] * this.column[i]);
            }

//            System.out.println(this.toString() + " found element[" + position[0] + "," + position[1] + "] to be " + this.sum);
            result[position[0]][position[1]] = this.sum;
//            System.out.println(this.toString() + " /DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
