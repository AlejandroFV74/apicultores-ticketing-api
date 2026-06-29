package com.apicultores.backendapicultores.domain.dto.request;
import com.apicultores.backendapicultores.common.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull(message = "El rol es requerido")
    private Role role;
}