package com.secure.shopbackend.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Column(name = "name")
    private String adminName;

    // UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 관리자는 단일 권한 ROLE_ADMIN을 부여받도록 설정
        return Collections.singleton(() -> "ROLE_ADMIN");
    }

    @Override
    public String getUsername() {
        return email;
    }

    // 아래 메서드들은 모두 true로 설정하여 사용(필요시 추가 로직 구현)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
