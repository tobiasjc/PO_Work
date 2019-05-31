public class Item {
    private int index;
    private int maximumQuantity;
    private double profit;
    private double weight;
    private double volume;
    private double container;

    public Item(int index, double volume, double weight, double profit, int maximumQuantity) {
        this.index = index;
        this.volume = volume;
        this.weight = weight;
        this.profit = profit;
        this.maximumQuantity = maximumQuantity;
    }

    public int getMaximumQuantity() {
        return maximumQuantity;
    }

    public int getIndex() {
        return this.index;
    }

    public double getProfit() {
        return this.profit;
    }

    public double getWeight() {
        return this.weight;
    }

    public double getVolume() {
        return this.volume;
    }

    public double getContainer() {
        return this.container;
    }

    public void setContainer(double container) {
        this.container = container;
    }
}
