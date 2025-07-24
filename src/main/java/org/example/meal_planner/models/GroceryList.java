package org.example.meal_planner.models;

import java.util.ArrayList;
import java.util.List;

public class GroceryList {
    private List<GroceryItem> items;

    public GroceryList() {
        this.items = new ArrayList<>();
    }

    public GroceryList(List<GroceryItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    // Getters and Setters
    public List<GroceryItem> getItems() {
        return items;
    }

    public void setItems(List<GroceryItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    // Add item methods
    public void addItem(GroceryItem item) {
        if (item == null) return;

        // Check if item already exists
        for (GroceryItem existingItem : items) {
            if (existingItem.isSameItem(item)) {
                existingItem.addAmount(item.getAmount());
                return;
            }
        }
        
        // Add new item if not found
        items.add(item);
    }

    // Remove item methods
    public boolean removeItem(GroceryItem item) {
        return items.remove(item);
    }

    public boolean removeItemByIndex(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            return true;
        }
        return false;
    }

    // Clear methods
    public void clearAll() {
        items.clear();
    }

    public void clearGenerated() {
        items.removeIf(GroceryItem::isGenerated);
    }

    // Statistics
    public int getTotalItemCount() {
        return items.size();
    }

    public int getPurchasedItemCount() {
        return (int) items.stream().filter(GroceryItem::isPurchased).count();
    }

    public double getCompletionPercentage() {
        if (items.isEmpty()) return 0.0;
        return (double) getPurchasedItemCount() / getTotalItemCount() * 100.0;
    }

    // Utility methods
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void toggleItemPurchased(GroceryItem item) {
        item.setPurchased(!item.isPurchased());
    }
}
