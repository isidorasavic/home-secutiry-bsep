package com.ftn.MyHousebackend.controller;

import antlr.Token;
import com.ftn.MyHousebackend.dto.ObjectDTO;
import com.ftn.MyHousebackend.dto.ObjectMessageDTO;
import com.ftn.MyHousebackend.dto.UserDTO;
import com.ftn.MyHousebackend.model.ObjectMessage;
import com.ftn.MyHousebackend.security.security.TokenUtils;
import com.ftn.MyHousebackend.service.ObjectMessageService;
import com.ftn.MyHousebackend.service.ObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ObjectController {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectController.class);

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private ObjectService objectService;

    @Autowired
    private ObjectMessageService objectMessageService;

    @ResponseBody
    // @PreAuthorize("hasRole('ADMIN')")
    //izmeniti da se username ni ne salje nego uzima na beku TODO
    @GetMapping(path = "/user/{username}/objects")
    @ResponseStatus(HttpStatus.OK)
    public List<ObjectDTO> getOwnerObjects(@PathVariable("username") String username) {
        return objectService.getOwnerObjects(username);
    }

    @ResponseBody
    @GetMapping(path = "/object/{objectId}/messages")
    @ResponseStatus(HttpStatus.OK)
    public List<ObjectMessageDTO> getObjectMessages(@PathVariable("objectId") long objectId) {
        return objectMessageService.getAllMessagesForObject(objectId);
    }

    @ResponseBody
    @PostMapping(path = "/object/tenant")
    public UserDTO addTenantToObject(@RequestParam("objectId") long objectId, @RequestParam("tenant") String tenant) {
        return objectService.addTennatToObject(objectId, tenant);
    }

    @ResponseBody
    @GetMapping(path = "/object/tenantOptions")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getPotentialTenants(@RequestParam("objectId") long objectId) {
        return objectService.getPotentialTenants(objectId);
    }

//    @ResponseBody
//    @GetMapping(path = "/all/{username}/objects")
//    @ResponseStatus(HttpStatus.OK)
//    public List<UserDTO> getPotentialTenants(@RequestParam("objectId") long objectId) {
//        return objectService.getPotentialTenants(objectId);
//    }
}
