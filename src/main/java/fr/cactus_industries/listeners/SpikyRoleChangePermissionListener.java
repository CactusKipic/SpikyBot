package fr.cactus_industries.listeners;

import fr.cactus_industries.commands.Commands;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.server.role.RoleChangePermissionsEvent;
import org.javacord.api.listener.server.role.RoleChangePermissionsListener;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SpikyRoleChangePermissionListener implements RoleChangePermissionsListener {
    
    /*private static final SpikyRoleChangePermissionListener listener = new SpikyRoleChangePermissionListener();
    
    public static SpikyRoleChangePermissionListener getInstance() {
        return listener;
    }*/
    
    public SpikyRoleChangePermissionListener() {
    }
    
    @Override
    public void onRoleChangePermissions(RoleChangePermissionsEvent event) {
        Collection<PermissionType> allowedPermission = event.getNewPermissions().getAllowedPermission();
        Collection<PermissionType> oldAllowedPermission = event.getNewPermissions().getAllowedPermission();
        // Le rôle a reçu la permission d'Administrateur
        if (allowedPermission.contains(PermissionType.ADMINISTRATOR) && !oldAllowedPermission.contains(PermissionType.ADMINISTRATOR)) {
            //Commands.updatePermissionOfRole(event.getRole(), true);
        }
        // Le rôle a perdu la permission d'Administrateur
        if (oldAllowedPermission.contains(PermissionType.ADMINISTRATOR) && !allowedPermission.contains(PermissionType.ADMINISTRATOR)) {
            //Commands.updatePermissionOfRole(event.getRole(), false);
        }
    }
}
