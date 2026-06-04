package com.example.proyectopnc.dto.request;
import com.example.proyectopnc.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull(message = "El rol es requerido")
    private Role role;
}