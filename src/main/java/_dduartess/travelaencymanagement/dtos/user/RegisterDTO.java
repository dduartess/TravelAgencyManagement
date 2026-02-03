package _dduartess.travelaencymanagement.dtos.user;

import _dduartess.travelaencymanagement.entities.user.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
