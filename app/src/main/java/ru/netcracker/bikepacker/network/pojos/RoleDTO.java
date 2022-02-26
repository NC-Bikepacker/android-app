package ru.netcracker.bikepacker.network.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoleDTO {

//    TODO: remove

    @SerializedName("role_id")
    @Expose
    private long roleId;
    @SerializedName("role_name")
    @Expose
    private String roleName;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}