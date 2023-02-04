package com.ipmugo.userservice.controller;

import com.ipmugo.userservice.dto.ResponseData;
import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.RoleEnum;
import com.ipmugo.userservice.service.RoleService;
import com.ipmugo.userservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Create Role
     * */
    @PostMapping("/role")
    public ResponseEntity<ResponseData<Role>> createRole(@RequestBody Role role){
        ResponseData<Role> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(roleService.createRole(role));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Update Role
     * */
    @PutMapping("/role/{name}")
    public ResponseEntity<ResponseData<Role>> updateRole(@PathVariable("name") RoleEnum name, @RequestBody Role role){
        ResponseData<Role> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(roleService.updateRole(role, name));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Delete Role
     * */
    @DeleteMapping("/role/{name}")
    public ResponseEntity<ResponseData<String>> deleteRole(@PathVariable("name") RoleEnum name){
        ResponseData<String> responseData = new ResponseData<>();

        try{
            roleService.deleteRole(name);

            responseData.setStatus(true);
            responseData.setData("Deleted role with name " + name + " successfully");
            return ResponseEntity.ok(responseData);

        }catch (CustomException e){
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}
