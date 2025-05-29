package gov.iti.jet.ewd.ecom.util;

import gov.iti.jet.ewd.ecom.entity.CartItem;

import java.util.List;

public class CostCalculator {
    public static double calculateTotalCost(List<CartItem> items) {
        if (items == null) {
            throw new IllegalStateException("Items cannot be null");
        }

        double totalCost = 0;
        for (CartItem item : items) {
            totalCost += item.getProduct().getPrice() * item.getQuantity();
        }
        return totalCost;
    }
}
