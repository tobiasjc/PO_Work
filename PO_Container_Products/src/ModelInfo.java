import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelInfo {

    private String itemsPath;
    private String environmentPath;
    private ArrayList<Item> items;
    private double itemsWeightSum;
    private double itemsVolumeSum;
    private double containerCapacity;
    private double containerVolume;
    private int itemsQuantity;
    private int containerQuantity;
    private int maximumItemEach;
    private double containerTotalCapacity;
    private double containerTotalVolume;

    ModelInfo(HashMap<String, String> hashArgs) {
        this.itemsPath = hashArgs.get("--itemsPath");
        this.environmentPath = hashArgs.get("--environmentPath");
        this.items = new ArrayList<>();
        this.itemsWeightSum = 0;
        this.itemsVolumeSum = 0;
        this.itemsQuantity = 0;
        readEnvironment();
        readItems();
    }

    public int getContainerQuantity() {
        return containerQuantity;
    }

    public double getItemsWeightSum() {
        return itemsWeightSum;
    }

    public double getItemsVolumeSum() {
        return itemsVolumeSum;
    }

    public int getItemsQuantity() {
        return itemsQuantity;
    }

    public double getContainerTotalCapacity() {
        return containerTotalCapacity;
    }

    public double getContainerTotalVolume() {
        return containerTotalVolume;
    }

    private void readEnvironment() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(this.environmentPath)))) {
            br.readLine();
            String line = br.readLine();
            String[] environmentInfo = line.split(",");
            this.maximumItemEach = Integer.parseInt(environmentInfo[0]);
            this.containerQuantity = Integer.parseInt(environmentInfo[1]);
            this.containerCapacity = Integer.parseInt(environmentInfo[2]);
            this.containerVolume = Integer.parseInt(environmentInfo[3]);
            this.containerTotalCapacity += this.containerCapacity * this.containerQuantity;
            this.containerTotalVolume += this.containerVolume * this.containerQuantity;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void readItems() {
        // read items
        try (BufferedReader br = new BufferedReader(new FileReader(new File(this.itemsPath)))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] itemInfo = line.split(",");
                int index = Integer.parseInt(itemInfo[0].split("_")[1]);
                double volume = Double.parseDouble(itemInfo[1]);
                double weight = Double.parseDouble(itemInfo[2]);
                double profit = Double.parseDouble(itemInfo[3]);
                this.itemsVolumeSum += volume;
                this.itemsWeightSum += weight;
                this.itemsQuantity += 1;
                this.items.add(new Item(index, volume, weight, profit, this.maximumItemEach));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Item> getItems() {
        return this.items;
    }

    public double getContainerCapacity() {
        return this.containerCapacity;
    }

    public double getContainerVolume() {
        return this.containerVolume;
    }

}
