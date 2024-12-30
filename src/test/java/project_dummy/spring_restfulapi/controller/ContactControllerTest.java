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

import project_dummy.spring_restfulapi.entity.Contact;
import project_dummy.spring_restfulapi.entity.User;
import project_dummy.spring_restfulapi.model.ContactResponse;
import project_dummy.spring_restfulapi.model.CreateContactRequest;
import project_dummy.spring_restfulapi.model.UpdateContactRequest;
import project_dummy.spring_restfulapi.model.WebResponse;
import project_dummy.spring_restfulapi.repository.ContactRepository;
import project_dummy.spring_restfulapi.repository.UserRepository;
import project_dummy.spring_restfulapi.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {    

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @BeforeEach
    void setUp(){
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setName("Test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        userRepository.save(user);
    }

    @Test
    void createContactBadRequest() throws Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setEmail("salah");

        mockMvc.perform(
            post("/api/contacts")
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
    void createContactSuccess() throws Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("tubagus");
        request.setLastName("dylan");
        request.setEmail("tb@email.com");
        request.setPhone("0888888888");

        mockMvc.perform(
            post("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals("tubagus", response.getData().getFirstName());
            assertEquals("dylan", response.getData().getLastName());
            assertEquals("tb@email.com", response.getData().getEmail());
            assertEquals("0888888888", response.getData().getPhone());
            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void getContactNotFound() throws Exception{
        mockMvc.perform(
            get("/api/contacts/asdjhasda")
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
    void getContactSuccess() throws Exception{
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("ucup");
        contact.setLastName("surucup");
        contact.setEmail("ucup@mail.com");
        contact.setPhone("007304972");
        contactRepository.save(contact);

        mockMvc.perform(
            get("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(contact.getId(), response.getData().getId());
            assertEquals(contact.getFirstName(), response.getData().getFirstName());
            assertEquals(contact.getLastName(), response.getData().getLastName());
            assertEquals(contact.getEmail(), response.getData().getEmail());
            assertEquals(contact.getPhone(), response.getData().getPhone());
        });
    }

    @Test
    void updateContactBadRequest() throws Exception{
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("");
        request.setEmail("salah");

        mockMvc.perform(
            put("/api/contacts/1234")
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
    void updateContactSuccess() throws Exception{

        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("ucup");
        contact.setLastName("surucup");
        contact.setEmail("ucup@mail.com");
        contact.setPhone("007304972");
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("tubagus");
        request.setLastName("dylan");
        request.setEmail("tb@email.com");
        request.setPhone("0888888888");

        mockMvc.perform(
            put("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals("tubagus", response.getData().getFirstName());
            assertEquals("dylan", response.getData().getLastName());
            assertEquals("tb@email.com", response.getData().getEmail());
            assertEquals("0888888888", response.getData().getPhone());
            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void deleteContactNotFound() throws Exception{
        mockMvc.perform(
            delete("/api/contacts/asdjhasda")
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
    void deleteContactSuccess() throws Exception{
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("ucup");
        contact.setLastName("surucup");
        contact.setEmail("ucup@mail.com");
        contact.setPhone("007304972");
        contactRepository.save(contact);

        mockMvc.perform(
            delete("/api/contacts/" + contact.getId())
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
    void searchContactNotFound() throws Exception{
        mockMvc.perform(
            get("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(0, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }
    
    @Test
    void searchSuccess() throws Exception{
        User user = userRepository.findById("test").orElseThrow();

        for (int i = 0; i < 100; i++) {     
            Contact contact = new Contact();
            contact.setUser(user);
            contact.setId(UUID.randomUUID().toString());
            contact.setFirstName("ucup" + i);
            contact.setLastName("surucup" + i);
            contact.setEmail("ucup@mail.com" + i);
            contact.setPhone("007304972");
            contactRepository.save(contact);
        }
        // search by name
        mockMvc.perform(
            get("/api/contacts")
            .queryParam("name", "ucup")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
        // search by name, in lastname
        mockMvc.perform(
            get("/api/contacts")
            .queryParam("name", "surucup")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
        // search by email
        mockMvc.perform(
            get("/api/contacts")
            .queryParam("email", "mail")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
        // search by phone
        mockMvc.perform(
            get("/api/contacts")
            .queryParam("phone", "972")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
        // page kelebihan
        mockMvc.perform(
            get("/api/contacts")
            .queryParam("phone", "972")
            .queryParam("page", "1000")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "token")
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(1000, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

    }
}
