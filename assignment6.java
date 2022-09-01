
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author adam botens
 */
public class assignment6 {

    static int count;
    static int maxProfit;
    static List bestSet;

    public static void main(String args[]) throws ClassNotFoundException, Exception {

        List<int[]> weights = new ArrayList<>();
        List<int[]> profits = new ArrayList<>();
        Scanner myObj = new Scanner(System.in);

        int[] maxWeights = new int[100];
        int[] numOperations = new int[100];

        List include = new LinkedList();

        //create arrays
        weights = generateWeights(weights);
        profits = generateProfits(profits);
        maxWeights = generateMaxWeights(maxWeights);

        //sort the randomly generated arrays
        for (int i = 0; i < 100; i++) {
            weights.set(i, bubbleSortWeight(weights.get(i), profits.get(i)));
            profits.set(i, bubbleSortProfit(weights.get(i), profits.get(i)));
        }

        //switch statement for controlling UI
        int option = 0;
        while (option != 4) {
            count = 0;
            maxProfit = 0;
            System.out.println("1. Breadth First");
            System.out.println("2. Depth First");
            System.out.println("3. Best First");
            System.out.println("4. Exit");
            System.out.println("Make a selection: ");
            option = myObj.nextInt();
            switch (option) {
                case 1:
                    for (int i = 0; i < 100; i++) {
                        assignment6.knapBreadth(5, weights.get(i), profits.get(i), maxWeights[i]);
                        System.out.println(i + ": " + count);
                        numOperations[i] = count;
                        count = 0;
                        maxProfit = 0;
                    }
                    quickSort(numOperations, 0, 99);
                    System.out.println("Minimum operations: " + numOperations[0]);
                    System.out.println("Maximum operations: " + numOperations[99]);
                    System.out.println("Average operations: " + average(numOperations));
                    System.out.println("Standard Deviation: " + standardDeviation(numOperations));
                    break;
                case 2:
                    for (int i = 0; i < 100; i++) {
                        assignment6.knapDepth(0, weights.get(i), profits.get(i), maxWeights[i], 0, 0, include);
                        System.out.println(i + ": " + count);
                        numOperations[i] = count;
                        count = 0;
                        maxProfit = 0;
                    }
                    quickSort(numOperations, 0, 99);
                    System.out.println("Minimum operations: " + numOperations[0]);
                    System.out.println("Maximum operations: " + numOperations[99]);
                    System.out.println("Average operations: " + average(numOperations));
                    System.out.println("Standard Deviation: " + standardDeviation(numOperations));
                    break;
                case 3:
                    for (int i = 0; i < 100; i++) {
                        assignment6.knapBest(5, weights.get(i), profits.get(i), maxWeights[i]);
                        System.out.println(i + ": " + count);
                        numOperations[i] = count;
                        count = 0;
                        maxProfit = 0;
                    }
                    quickSort(numOperations, 0, 99);
                    System.out.println("Minimum operations: " + numOperations[0]);
                    System.out.println("Maximum operations: " + numOperations[99]);
                    System.out.println("Average operations: " + average(numOperations));
                    System.out.println("Standard Deviation: " + standardDeviation(numOperations));
                    break;
            }
        }


        /*
        int[] w = {1, 2, 4, 2};
        int[] p = {10, 18, 32, 14};

        knapDepth(0, weights.get(i), profits.get(i), maxWeights[i], 0, 0, include);
        System.out.println(count);
        System.out.println(maxProfit);
        
        System.out.println(knapBreadth(4, w, p, 6));
        System.out.println(knapBest(4, w, p, 6));
         */
        myObj.close();
    }

    public static int knapBreadth(int n, int[] w, int[] p, int W) {
        Queue<Node> Q = new LinkedList();

        int maxProfit = 0;

        Q.add(new Node(-1, 0, 0));
        count++;

        Node u = new Node(0, 0, 0);

        while (!Q.isEmpty()) {
            Node v = Q.poll();
            count++;

            u.level = v.level + 1;
            u.weight = v.weight + w[u.level];
            u.profit = v.profit + p[u.level];

            if (u.weight <= W && u.profit > maxProfit) {
                maxProfit = u.profit;
            }
            if (bound(u, n, w, p, W) > maxProfit) {
                Q.add(new Node(u.level, u.profit, u.weight));
            }
            u.weight = v.weight;
            u.profit = v.profit;
            count++;
            if (bound(u, n, w, p, W) > maxProfit) {
                Q.add(new Node(u.level, u.profit, u.weight));

            }

        }
        return maxProfit;
    }

    public static float bound(Node u, int n, int[] w, int[] p, int W) {
        int j, k = 0;
        int totweight = 0;
        float result = 0;

        if (u.weight > W) {
            return 0;
        } else {
            result = u.profit;
            j = u.level + 1;
            totweight = u.weight;
            while (j < n && totweight + w[j] <= W) {
                totweight = totweight + w[j];
                result = result + p[j];
                j++;
            }
            k = j;
            if (k < n) {
                result = result + (W - totweight) * p[k] / w[k];
            }
        }
        return result;
    }

    public static void knapDepth(int i, int[] w, int[] p, int W, int weight, int profit, List include) {

        count++;
        if (weight <= W && profit > maxProfit) {
            maxProfit = profit;
            bestSet = new LinkedList(include);
        }
        if (promising(i, w, p, W, weight, profit)) {
            List left = new LinkedList(include);
            left.add(i + 1);
            knapDepth(i + 1, w, p, W, weight + w[i + 1], profit + p[i + 1], left);
            List right = new LinkedList(include);
            knapDepth(i + 1, w, p, W, weight, profit, right);
        }
    }

    public static boolean promising(int i, int[] w, int[] p, int W, int weight, int profit) {
        int j, k = 0;
        int totweight = 0;
        float bound;

        if (weight > W) {
            return false;
        } else {
            j = i + 1;
            bound = profit;
            while (j < w.length && totweight + w[j] <= W) {
                totweight = totweight + w[j];
                bound = bound + p[j];
                j++;
            }
            k = j;
            if (k < w.length) {
                bound = bound + (W - totweight) * p[k] / w[k];
            }
        }
        return bound > maxProfit;
    }

    public static int knapBest(int n, int[] w, int[] p, int W) {
        Queue<Node> Q = new LinkedList();

        Node v = new Node(-1, 0, 0);
        Node u = new Node(0, 0, 0);

        int maxProfit = 0;

        v.bound = bound(v, n, w, p, W);
        count++;
        Q.offer(v);
        while (!Q.isEmpty()) {
            v = Q.poll();

            if (v.bound > maxProfit) {
                u.level = v.level + 1;
                u.weight = v.weight + w[u.level];
                u.profit = v.profit + p[u.level];
                count++;
                if (u.weight <= W && u.profit > maxProfit) {
                    maxProfit = u.profit;
                }
                u.bound = bound(u, n, w, p, W);

                if (u.bound > maxProfit) {
                    Q.offer(u);
                    count++;
                }
                u.weight = v.weight;
                u.profit = v.profit;
                u.bound = bound(u, n, w, p, W);

                if (u.bound > maxProfit) {
                    Q.offer(u);
                    count++;
                }
            }
        }
        return maxProfit;
    }

    public static List<int[]> generateWeights(List<int[]> weights) throws Exception {

        try {
            FileInputStream fileInputStream = new FileInputStream("weights.dat");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            weights = (List) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        if (weights.isEmpty()) {
            int[] w = new int[5];

            for (int k = 0; k < 100; k++) {
                for (int i = 0; i < 5; i++) {
                    w[i] = (int) (Math.random() * 10 + 1);
                }
                weights.add(k, w.clone());
            }

            try {
                FileOutputStream fos = new FileOutputStream("weights.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(weights);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        }

        return weights;
    }

    public static List<int[]> generateProfits(List<int[]> profits) throws Exception {

        try {
            FileInputStream fileInputStream = new FileInputStream("profits.dat");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            profits = (List) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        if (profits.isEmpty()) {
            int[] p = new int[5];

            for (int k = 0; k < 100; k++) {
                for (int i = 0; i < p.length; i++) {
                    p[i] = (int) (Math.random() * 50 + 1);
                }
                profits.add(k, p.clone());
            }

            try {
                FileOutputStream fos = new FileOutputStream("profits.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(profits);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        }

        return profits;
    }

    public static int[] generateMaxWeights(int[] maxWeights) throws Exception {
        try {
            FileInputStream fileInputStream = new FileInputStream("maxWeights.dat");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            maxWeights = (int[]) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        if (maxWeights[0] == 0) {

            for (int i = 0; i < 100; i++) {
                maxWeights[i] = (int) ((Math.random() + 1) * 10);
            }

            try {
                FileOutputStream fos = new FileOutputStream("maxWeights.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(maxWeights);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        }

        return maxWeights;
    }

    public static int[] bubbleSortWeight(int[] weights, int[] profits) {
        int n = weights.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if ((profits[j] / weights[j]) < (profits[j + 1] / weights[j + 1])) {
                    int temp = weights[j];
                    weights[j] = weights[j + 1];
                    weights[j + 1] = temp;
                }
            }
        }
        return weights;
    }

    public static int[] bubbleSortProfit(int[] weights, int[] profits) {
        int n = weights.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if ((profits[j] / weights[j]) < (profits[j + 1] / weights[j + 1])) {
                    int temp = profits[j];
                    profits[j] = profits[j + 1];
                    profits[j + 1] = temp;
                }
            }
        }
        return profits;
    }

    public static void quickSort(int[] array, int left, int right) {

        if (left < right) {

            double pivot = partition(array, left, right);

            quickSort(array, left, (int) (pivot - 1));
            quickSort(array, (int) (pivot + 1), right);
        }
    }

    public static double partition(int[] array, int left, int right) {
        int pivot = array[left];
        int i = left;
        int j = right;

        while (i < j) {
            while (i < j && array[j] >= pivot) {
                j--;
                count++;
            }
            if (i < j) {
                array[i++] = array[j];
            }
            while (i < j && array[i] <= pivot) {
                i++;
                count++;
            }
            if (i < j) {
                array[j--] = array[i];
            }
        }

        array[i] = pivot;
        return i;
    }

    public static int standardDeviation(int[] numOperations) {
        int sum = 0, sd = 0;

        for (int i = 0; i < numOperations.length; i++) {
            sum += numOperations[i];
        }

        int mean = sum / numOperations.length;

        for (int i = 0; i < numOperations.length; i++) {
            sd += Math.pow((numOperations[i] - mean), 2);
        }

        return (int) Math.sqrt(sd / numOperations.length);

    }

    public static int average(int[] numOperations) {
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            sum += numOperations[i];
        }
        return sum / 100;
    }
}

class Node {

    public int level;
    public float bound;
    public int profit;
    public int weight;

    public Node(int i, int p, int w) {
        level = i;
        profit = p;
        weight = w;

    }
}

/*
 public static int knapDepth2(int n, int[] w, int[] p, int W) {
        Stack<Node> Q = new Stack();

        int maxProfit = 0;

        Q.push(new Node(-1, 0, 0));
        count++;

        Node u = new Node(0, 0, 0);

        while (!Q.isEmpty()) {
            Node v = Q.peek();

            if (v.level < n - 1) {
                u.level = v.level + 1;
                u.weight = v.weight + w[u.level];
                u.profit = v.profit + p[u.level];
                count++;
            } else {
                Q.pop();
            }
            if (bound(u, n, w, p, W) > maxProfit) {
                Q.push(new Node(u.level, u.profit, u.weight));
                if (u.profit > maxProfit) {
                    maxProfit = u.profit;
                }
            } else {
                if (Q.isEmpty()) {
                    return maxProfit;
                }
                Q.peek().level++;
                u.level = Q.peek().level;
                u.weight = Q.peek().weight;
                u.profit = Q.peek().profit;
                if (bound(u, n, w, p, W) > maxProfit) {
                    Q.push(new Node(u.level, u.profit, u.weight));
                    if (u.profit > maxProfit) {
                        maxProfit = u.profit;
                    }
                }
            }
        }
        return maxProfit;
    }
 */
