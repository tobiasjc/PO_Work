import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class App {

    private static double[] v;
    private static double[] p;
    private static double[] l;

    private static int n;
    private static int k;
    private static int cc;
    private static int cv;
    private static int b;

    public static void main(String[] args) {
        HashMap<String, String> hashArgs = getArgsHash(args);

        readEnvironment(hashArgs.get("--envPath"));

        v = new double[n];
        p = new double[n];
        l = new double[n];

        readProducts(hashArgs.get("--prodPath"));

        try {
            IloCplex model = new IloCplex();

            model.setParam(IloCplex.IntParam.TimeLimit, 10.00);

            // create variables
            IloIntVar[][] vars = new IloIntVar[n][k];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < k; j++) {
                    vars[i][j] = model.intVar(0, Integer.MAX_VALUE, "X[" + i + "," + j + "]");
                }
            }

            // create objective function
            IloLinearNumExpr z = model.linearNumExpr();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < k; j++) {
                    z.addTerm(l[i], vars[i][j]);
                }
            }
            model.addMaximize(z);

            // container v(cv) and p(p) restrictions
            for (int j = 0; j < k; j++) {
                IloLinearNumExpr vr = model.linearNumExpr();
                IloLinearNumExpr wr = model.linearNumExpr();
                for (int i = 0; i < n; i++) {
                    vr.addTerm(v[i], vars[i][j]);
                    wr.addTerm(p[i], vars[i][j]);
                }
                model.addLe(vr, cv);
                model.addLe(wr, cc);
            }

            // prod quantity(q) restriction
//            for (int i = 0; i < n; i++) {
//                IloLinearNumExpr qr = model.linearNumExpr();
//                for (int j = 0; j < k; j++) {
//                    qr.addTerm(1, vars[i][j]);
//                }
//                model.addLe(qr, b);
//            }

            // prod quantity(q) restriction in [0,1] on Z+
            for (int i = 0; i < n; i++) {
                IloLinearIntExpr qr = model.linearIntExpr();
                for (int j = 0; j < k; j++) {
                    qr.addTerm(1, vars[i][j]);
                }
                model.addGe(qr, 0);
                model.addLe(qr, 1);
            }

            // check model
            System.out.println(model.getModel());

            if (model.solve()) {
                // status of found solution and objective v
                System.out.println("Model status: " + model.getStatus());
                System.out.println("Z = " + model.getObjValue());

                // results explained
                for (int j = 0; j < k; j++) {
                    System.out.println("For container " + j + ": ");
                    for (int i = 0; i < n; i++) {
                        if (model.getValue(vars[i][j]) != 0) {
                            System.out.println("\tProd " + i + ", quantity: " + model.getValue(vars[i][j]));
                        }
                    }
                }
            } else {
                System.err.println("Surprisingly enough, your model could not be solved.");
                System.exit(1);
            }
        } catch (IloException e) {
            System.out.println(e.getMessage());
        }
    }

    private static HashMap<String, String> getArgsHash(String[] args) {
        if (args.length % 2 != 0) {
            System.err.println("Wrong number of parameters used. Parameters should be passed as <--key, v>.");
            System.exit(1);
        }

        HashMap<String, String> tmp = new HashMap<>();
        for (int i = 0; i < args.length; i += 2)
            tmp.put(args[i], args[i + 1]);

        return tmp;
    }

    private static void readProducts(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            br.readLine(); // header (column names) discard
            String line;
            for (int i = 0; (i < n) && ((line = br.readLine()) != null); i++) {
                String[] parts = line.split(",");
                v[i] = Double.parseDouble(parts[1]);
                p[i] = Double.parseDouble(parts[2]);
                l[i] = Double.parseDouble(parts[3]);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void readEnvironment(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            br.readLine(); // header (column names) discard
            String[] parts = br.readLine().split(",");
            n = Integer.parseInt(parts[0]);
            k = Integer.parseInt(parts[1]);
            cc = Integer.parseInt(parts[2]);
            cv = Integer.parseInt(parts[3]);
            b = Integer.parseInt(parts[4]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
