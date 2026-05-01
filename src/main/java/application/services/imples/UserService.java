package application.services.imples;

import application.mapper.UserMapper;
import application.models.dtos.requests.UserRequest;
import application.models.entities.User;
import application.repositories.UserRepository;
import core.base.AppException;
import core.base.BaseService;
import core.constants.ErrorCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User, UserRequest, Long, UserRepository> implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        super(repository, mapper);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found: " + email));
    }

    @Override
    protected void beforeCreate(UserRequest dto, User entity) {
        super.beforeCreate(dto, entity);

        if (dto.getPassword() != null) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }

    @Override
    protected void beforeUpdate(UserRequest dto, User entity) {
        super.beforeUpdate(dto, entity);

        User oldUser = repository.findById(dto.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            entity.setPassword(oldUser.getPassword());
        }

         entity.setEmail(oldUser.getEmail());
    }
}
