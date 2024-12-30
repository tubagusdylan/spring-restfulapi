package project_dummy.spring_restfulapi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project_dummy.spring_restfulapi.entity.Address;
import project_dummy.spring_restfulapi.entity.Contact;
import project_dummy.spring_restfulapi.entity.User;
import project_dummy.spring_restfulapi.model.AddressResponse;
import project_dummy.spring_restfulapi.model.CreateAddressRequest;
import project_dummy.spring_restfulapi.model.UpdateAddressRequest;
import project_dummy.spring_restfulapi.model.WebResponse;
import project_dummy.spring_restfulapi.repository.AddressRepository;
import project_dummy.spring_restfulapi.repository.ContactRepository;
import project_dummy.spring_restfulapi.repository.UserRepository;
import project_dummy.spring_restfulapi.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired 
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp(){
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setName("Test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId("test");
        contact.setFirstName("ucup");
        contact.setLastName("surucup");
        contact.setEmail("ucup@mail.com");
        contact.setPhone("007304972");
        contactRepository.save(contact);
    }

    @Test
    void createAddressBadRequest() throws Exception{
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
            post("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createAddressSuccess() throws Exception{
        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("jalan");
        request.setCity("kota");
        request.setProvince("jawa barat");
        request.setCountry("indonesia");
        request.setPostalCode("1717");

        mockMvc.perform(
            post("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void getAddressNotFound() throws Exception{
        mockMvc.perform(
            get("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet("jalan");
        address.setCity("kota");
        address.setProvince("jawa barat");
        address.setCountry("indonesia");
        address.setPostalCode("1717");
        addressRepository.save(address);

        mockMvc.perform(
            get("/api/contacts/test/addresses/" + address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(address.getStreet(), response.getData().getStreet());
            assertEquals(address.getCity(), response.getData().getCity());
            assertEquals(address.getProvince(), response.getData().getProvince());
            assertEquals(address.getCountry(), response.getData().getCountry());
            assertEquals(address.getPostalCode(), response.getData().getPostalCode());
        });
    }

    @Test
    void updateAddressBadRequest() throws Exception{
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
            put("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateAddressSuccess() throws Exception{
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet("jalan2");
        address.setCity("kota2");
        address.setProvince("jawa barat2");
        address.setCountry("indonesia2");
        address.setPostalCode("17172");
        addressRepository.save(address);

        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("jalan");
        request.setCity("kota");
        request.setProvince("jawa barat");
        request.setCountry("indonesia");
        request.setPostalCode("1717");

        mockMvc.perform(
            put("/api/contacts/test/addresses/" + address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void deleteAddressNotFound() throws Exception{
        mockMvc.perform(
            delete("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }
    
    @Test
    void deleteAddressSuccess() throws Exception{
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet("jalan2");
        address.setCity("kota2");
        address.setProvince("jawa barat2");
        address.setCountry("indonesia2");
        address.setPostalCode("17172");
        addressRepository.save(address);

        mockMvc.perform(
            delete("/api/contacts/test/addresses/" + address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals("OK", response.getData());
        });
    }

    @Test
    void listAddressNotFound() throws Exception{
        mockMvc.perform(
            get("/api/contacts/salah/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void listAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        for (int i = 0; i < 5; i++) {  
            Address address = new Address();
            address.setId("test" + i);
            address.setContact(contact);
            address.setStreet("jalan");
            address.setCity("kota");
            address.setProvince("jawa barat");
            address.setCountry("indonesia" + i);
            address.setPostalCode("1717");
            addressRepository.save(address);
        }


        mockMvc.perform(
            get("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(5, response.getData().size());
        });
    }
}
