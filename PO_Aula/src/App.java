import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * @author 2016.1.08.007
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int n = 10;
        double[] q = new double[n];
        double[] l = new double[n];
        double[] p = new double[n];
        double[] v = new double[n];


        try {
            IloCplex model = new IloCplex();
            // create x1 and x2 restrictions
            IloNumVar x1 = model.numVar(0, Double.MAX_VALUE);
            IloNumVar x2 = model.numVar(0, Double.MAX_VALUE);

            // create objective function
            IloLinearNumExpr z = model.linearNumExpr();
            z.addTerm(1.0, x1);
            z.addTerm(1.0, x2);

            // create restrictions
            IloLinearNumExpr r1l = model.linearNumExpr();
            IloLinearNumExpr r2l = model.linearNumExpr();
            r1l.addTerm(2, x1);
            r1l.addTerm(2, x2);
            r2l.addTerm(5, x1);
            r2l.addTerm(3, x2);

            // add restriction to the model
            model.addLe(r1l, 8);
            model.addLe(r2l, 15);

            // objective restriction
            model.addMaximize(z);
            System.out.println(model.getModel());
            if (model.solve()) {
//                System.out.println(model.getValue(x1));
//                System.out.println(model.getValue(x2));
                System.out.println("Solution = " + model.getObjValue());
            }
        } catch (IloException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
