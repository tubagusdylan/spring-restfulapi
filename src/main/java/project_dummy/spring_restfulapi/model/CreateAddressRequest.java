package project_dummy.spring_restfulapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAddressRequest {

    @JsonIgnore
    @NotBlank
    private String contactId;

    @Size(max = 200)
    private String street;
    
    @Size(max = 100)
    private String city;
    
    @Size(max = 100)
    private String province;
    
    @NotBlank
    @Size(max = 100)
    private String country;
    
    @Size(max = 10)
    private String postalCode;
}
