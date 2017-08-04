package no.imr.barmar.ajax.pojo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserInfo implements UserDetails {
	private static final long serialVersionUID = -2303905893347299327L;
	
	private String username;
	private String password;

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
             final Set<GrantedAuthority> _grntdAuths = new HashSet<GrantedAuthority>();
             _grntdAuths.add( new SimpleGrantedAuthority("IMR_USER") );
             return _grntdAuths;
    }
    
    public void setPassword( String password ) {
    	this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }
    
    public void setUsername( String username ) {
    	this.username = username;
    }
    
    @Override
    public String getUsername() {
        return username;
    }

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

    @Override
    public String toString() {
            return "CustomUserDetails [user=" + username + "]";
    }
}
