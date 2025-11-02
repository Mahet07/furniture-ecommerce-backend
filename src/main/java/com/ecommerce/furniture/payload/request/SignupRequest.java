package com.ecommerce.furniture.payload.request;

import java.util.Set;
import jakarta.validation.constraints.*;

public class SignupRequest {

  @NotBlank
  @Size(min = 3, max = 50)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private Set<String> role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  @NotBlank
  @Size(min = 6, max = 40)
  private String confirmPassword;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public Set<String> getRole() { return role; }
  public void setRole(Set<String> role) { this.role = role; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getConfirmPassword() { return confirmPassword; }
  public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
