/**
 * Java Project B2-INFO ULB
 * Linear search using Multithreading and Generics
 *
 * @author Mark Diamantino Caribé
 * @version 1.0
 * @since 2018-12
 */

class FindDuplicate<E extends Comparable<E>> {
    private int tofind_index;
    private E[] array;
    private int num_threads;
    private static boolean found = false;
    private Thread[] threads;

    private static boolean get_found() {
        return found;
    }

    private synchronized static void set_found() {
        found = true;
    }

    private class Finder<E extends Comparable<E>> implements Runnable {
        private E[] my_array;
        private int main_index;
        private E comparewith;
        private int start;
        private int end;

        public Finder(E[] array, int tofind_index, int s, int e) {
            my_array = array; // Prevent accessing the same resource
            main_index = tofind_index; // Prevent accessing the same resource
            comparewith = my_array[main_index];
            start = s;
            end = e;
        }

        public void run() {
            int i = start;
            while (!(FindDuplicate.get_found()) && i < end){
                // If index of the currently visited item is different from the given one AND
                // AND the value is the same, THEN a duplicate was found.
                // THEN thread X has finished, others will not enter the conditional loop again.
                if ((i != main_index) && (my_array[i].compareTo(comparewith) == 0)) {
                    FindDuplicate.set_found();
                }
                i++;
            }
        }
    }

    private void findElement() {
        int min_range = array.length / num_threads;
        int max_range = min_range + 1;
        int maxrange_threads = array.length - (min_range * num_threads);
        int dynamic_range = 0; // Could be modified if 'array.lenght' is not divisible by 'num_threads'
        int start = 0;
        int i = 0;
        while (!(found) && i < num_threads) {
            dynamic_range = (i < maxrange_threads ? max_range : min_range);
            int end = start + dynamic_range;
            threads[i] = new Thread(new Finder<E>(array, tofind_index, start, end));
            threads[i].start();
            start = end;
            i++;
        }
        for (int a = 0; a < threads.length; a++) {
            try {
                if (threads[a] != null) {
                    threads[a].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("-> Found : %s\n\n", found);
    }

    public FindDuplicate(E[] arr, int ind, int thr) {
        array = arr;
        tofind_index = ind;
        num_threads = thr;
        threads = new Thread[num_threads]; // In order to iterate on threads and join them.
    }

    public boolean has_twin() {
        findElement();
        return found;
    }

}

public class LinearSearch {
    private static <E extends Comparable<E>> boolean isDuplicate(E[] array, int tofind_index, int num_threads) {
        if (tofind_index >= array.length || tofind_index < 0) {
            throw new IndexOutOfBoundsException("Invalid index of element : Index " + tofind_index + " is out of range.");
        } else if (num_threads > array.length || num_threads < 0) {
            throw new Error("Inavild number of threads: Negative or greater than lenght of array.");
        } else {
            FindDuplicate<E> finder = new FindDuplicate<E>(array, tofind_index, num_threads);
            return finder.has_twin();
        }
    }

    // Version with default parameter "num_threads" = 4, using method overloading
    private static <E extends Comparable<E>> boolean isDuplicate(E[] array, int tofind_index) {
        return isDuplicate(array, tofind_index, 4);
    }

    public static void main(String args[]) {
        Short[] shortArray = {1, 2, 3, 4, 2, 5, 6, 1, 3, 6};
        assert (!isDuplicate(shortArray, 3)); // 4 has NOT duplicate
        assert (isDuplicate(shortArray, 9)); // 6 has duplicate
        assert (isDuplicate(shortArray, 9, 10)); // 6 has duplicate - max num. of
        //isDuplicate(shortArray, 9, 11); // Error - Num. of threads out of range
        //isDuplicate(shortArray, 10); // Error - Index 10 is out of range

        Double[] doubleArray = {1.1, 4.3};
        //isDuplicate(doubleArray, 0); // Error - Lenght of array less than minimum num. of threads
        //isDuplicate(doubleArray, 3, -10); // Error - Invalid nummber of threads.

        Float[] floatArray = {};
        //isDuplicate(floatArray, 3); // Error - Array is empty

        Integer[] intArray = {15, 17, 2, 2, 9, 0, 1, 478, 1, 3, 8, 1};
        assert (isDuplicate(shortArray, 2, 5)); // OK 2 has duplicate
    }
}
