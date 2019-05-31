import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        HashMap<String, String> hashArgs = getHashArgs(args);

        ModelInfo modelInfo = new ModelInfo(hashArgs);

        try {
            IloCplex model = new IloCplex();

            // create variables
            ArrayList[] vars = new ArrayList[modelInfo.getItemsQuantity()];
            for (int i = 0; i < modelInfo.getItemsQuantity(); i++) {
                vars[i] = new ArrayList<IloNumVar>();
                for (int j = 0; j < modelInfo.getContainerQuantity(); j++) {
                    vars[i].add(model.numVar(0.0, modelInfo.getItems().get(i).getMaximumQuantity(), "X(" + i + "," + j + ")"));
                }
            }

            // create objective function
            IloLinearNumExpr z = model.linearNumExpr();
            for (int i = 0; i < modelInfo.getItemsQuantity(); i++) {
                for (int j = 0; j < modelInfo.getContainerQuantity(); j++) {
                    z.addTerm(modelInfo.getItems().get(i).getProfit(), (IloNumVar) vars[i].get(j));
                }
            }

            // restrictions for weight and volume
            ArrayList<IloLinearNumExpr> rw = new ArrayList<>();
            ArrayList<IloLinearNumExpr> rv = new ArrayList<>();

            for (int j = 0; j < modelInfo.getContainerQuantity(); j++) {
                rw.add(model.linearNumExpr());
                rv.add(model.linearNumExpr());
                for (int i = 0; i < modelInfo.getItemsQuantity(); i++) {
                    rw.get(j).addTerm(modelInfo.getItems().get(i).getWeight(), (IloNumVar) vars[i].get(j));
                    rv.get(j).addTerm(modelInfo.getItems().get(i).getVolume(), (IloNumVar) vars[i].get(j));
                }
                model.addLe(rw.get(j), modelInfo.getContainerCapacity());
                model.addLe(rv.get(j), modelInfo.getContainerVolume());
            }

            model.addMaximize(z);
            System.out.println(model.getModel());
            if (model.solve()) {
                System.out.println("z = " + model.getObjValue());
                for (int i = 0; i < modelInfo.getItemsQuantity(); i++) {
                    for (Object o : vars[i]) {
                        IloNumVar v = (IloNumVar) o;
                        if (model.getValue(v) != 0) {
                            System.out.println(v.getName() + " => " + model.getValue(v));
                        }
                    }
                }
            } else {
                System.out.println("IT'S UNSOVABLE BREH");
            }

//            // create variables and boundaries
//            ArrayList<IloNumVar> vars = new ArrayList<>(modelInfo.getItemsQuantity());
//            for (int i = 0; i < modelInfo.getItemsQuantity(); i++) {
//                vars.add(model.numVar(0.0, modelInfo.getItems().get(i).getMaximumQuantity(), "X" + i));
//            }
//
//            // create objective function
//            IloLinearNumExpr z = model.linearNumExpr();
//            for (int i = 0; i < vars.size(); i++) {
//                z.addTerm(modelInfo.getItems().get(i).getProfit(), vars.get(i));
//            }
//
//            // restrictions weight and volume
//            IloLinearNumExpr rw = model.linearNumExpr();
//            IloLinearNumExpr rv = model.linearNumExpr();
//
//            for (int i = 0; i < modelInfo.getItemsQuantity(); i++) {
//                rw.addTerm(modelInfo.getItems().get(i).getWeight(), vars.get(i));
//                rv.addTerm(modelInfo.getItems().get(i).getVolume(), vars.get(i));
//            }
//
//            model.addLe(rw, modelInfo.getContainerTotalCapacity());
//            model.addLe(rv, modelInfo.getContainerTotalVolume());
//
//            model.addMaximize(z);
//            System.out.println(model.getModel());
//
//            if (model.solve()) {
//                System.out.println("z = " + model.getObjValue());
//                for (IloNumVar v : vars) {
//                    if (model.getValue(v) != 0) {
//                        System.out.println(v.getName() + "=" + model.getValue(v));
//                    }
//                }
//            }
        } catch (IloException e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap<String, String> getHashArgs(String[] args) {
        if (args.length % 2 != 0) {
            System.out.println("Wrong number of arguments. Multiple <--key, value> needed.");
            System.exit(1);
        }

        HashMap<String, String> hashArgs = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            hashArgs.put(args[i], args[i + 1]);
        }

        return hashArgs;
    }
}
