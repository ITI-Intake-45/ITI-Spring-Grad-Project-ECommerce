package gov.iti.jet.ewd.ecom.security;

import com.google.common.collect.Sets;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum AuthRole {
    USER(Sets.newHashSet(
            AuthPermission.ADMIN_ADD_PRODUCT
            ,AuthPermission.ADMIN_REMOVE_PRODUCT
            ,AuthPermission.ADMIN_UPDATE_PRODUCT
            ,AuthPermission.ADMIN_GET_ORDER
            ,AuthPermission.ADMIN_GET_ALL_ORDERS
            ,AuthPermission.ADMIN_GET_ALL_USER
    ))
    ,ADMIN(Sets.newHashSet(
            AuthPermission.USER_UPDATE_PROFILE
            ,AuthPermission.USER_GET_ALL_ORDERS_FOR_USER
            ,AuthPermission.USER_GET_ORDER_FOR_USER
            ,AuthPermission.USER_GET_CART
            ,AuthPermission.USER_CHECKOUT
    ));

    private final Set<AuthPermission> permissions;


    AuthRole(Set<AuthPermission> permissions) {
        this.permissions = permissions;
    }


    public Set<AuthPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> grantedAuthorityDefaults(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
