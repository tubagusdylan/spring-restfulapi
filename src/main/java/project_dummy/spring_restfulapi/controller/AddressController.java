package project_dummy.spring_restfulapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import project_dummy.spring_restfulapi.entity.User;
import project_dummy.spring_restfulapi.model.AddressResponse;
import project_dummy.spring_restfulapi.model.CreateAddressRequest;
import project_dummy.spring_restfulapi.model.UpdateAddressRequest;
import project_dummy.spring_restfulapi.model.WebResponse;
import project_dummy.spring_restfulapi.service.AddressService;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(
        path = "/api/contacts/{contactId}/addresses",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(User user, @RequestBody CreateAddressRequest request, @PathVariable("contactId") String contactId){
        request.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @GetMapping(
        path = "/api/contacts/{contactId}/addresses/{addressId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(User user, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId){
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @PutMapping(
        path = "/api/contacts/{contactId}/addresses/{addressId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(User user, @RequestBody UpdateAddressRequest request, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId){
        request.setContactId(contactId);
        request.setId(addressId);
        AddressResponse addressResponse = addressService.update(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }
    
    @DeleteMapping(
        path = "/api/contacts/{contactId}/addresses/{addressId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> remove(User user, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId){
        addressService.remove(user, contactId, addressId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
        path = "/api/contacts/{contactId}/addresses",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> list(User user, @PathVariable("contactId") String contactId){
        List<AddressResponse> listResponse = addressService.list(user, contactId);
        return WebResponse.<List<AddressResponse>>builder().data(listResponse).build();
    }

}
