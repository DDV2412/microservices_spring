package com.ipmugo.userservice.controller;

import com.ipmugo.userservice.dto.ResponseData;
import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.UserRole;
import com.ipmugo.userservice.service.RoleService;
import com.ipmugo.userservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;


    /**
     * All Role
     * */
    @GetMapping()
    public ResponseEntity<ResponseData<Iterable<Role>>> getAllRole(){
        ResponseData<Iterable<Role>> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(roleService.getAllRole());
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

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
    public ResponseEntity<ResponseData<Role>> updateRole(@PathVariable("name") UserRole name, @RequestBody Role role){
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
    public ResponseEntity<ResponseData<String>> deleteRole(@PathVariable("name") UserRole name){
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
