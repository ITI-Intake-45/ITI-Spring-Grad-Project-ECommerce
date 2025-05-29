package gov.iti.jet.ewd.ecom.security;

public enum AuthPermission {
    ADMIN_ADD_PRODUCT("add-product")
    ,ADMIN_REMOVE_PRODUCT("remove-product")
    ,ADMIN_UPDATE_PRODUCT("update-product")
    ,ADMIN_GET_ALL_USER("get-all-users")
    ,ADMIN_GET_ALL_ORDERS("get-all-orders")
    ,ADMIN_GET_ORDER("get-order")
    ,USER_UPDATE_PROFILE("edit-profile")
    ,USER_GET_ALL_ORDERS_FOR_USER("get-all-orders-for-user")
    ,USER_GET_ORDER_FOR_USER("get-order-for-user")
    ,USER_GET_CART("get-cart")
    ,USER_CHECKOUT("checkout")
    ;

    private final String permission;

    AuthPermission(String permission){
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
