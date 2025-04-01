package com.telerikacademy.web.cryptocurrency_trading_platform.helpers;

import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import org.springframework.web.multipart.MultipartFile;

public class PermissionHelper {

    private static final String AUTHORIZATION_PERMISSION_ERROR = "You don't have the permission to do this.";
    private static final String BLOCKED_USER_ERROR = "You are blocked and cannot perform that operation";

    public static void checkIfAdmin(User user) {
        if (!user.isAdmin()) {
            throw new UnauthorizedOperationException(AUTHORIZATION_PERMISSION_ERROR);
        }
    }

    public static void checkIfCreatorOrAdminForUser(User user, User userToUpdate) {
        if (!(user.getId().equals(userToUpdate.getId()) || user.isAdmin())) {
            throw new UnauthorizedOperationException(AUTHORIZATION_PERMISSION_ERROR);
        }
    }

    public static void checkIfBlocked(User user) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_USER_ERROR);
        }
    }

    public static boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg")
                || contentType.equals("image/png") || contentType.equals("image/gif"));
    }
}

