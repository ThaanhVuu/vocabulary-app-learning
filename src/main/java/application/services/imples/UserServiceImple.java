package application.services.imples;

import application.mapper.UserMapper;
import application.models.dtos.requests.UserRequest;
import application.models.entities.User;
import application.repositories.UserRepository;
import application.services.UserService;
import core.base.AppException;
import core.base.BaseService;
import core.constants.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImple extends BaseService<User, UserRequest, Long, UserRepository> implements UserDetailsService, UserService {

    private final PasswordEncoder passwordEncoder;

    public UserServiceImple(UserRepository repository, UserMapper mapper, PasswordEncoder passwordEncoder) {
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

        // Mã hóa mật khẩu khi tạo mới
        if (dto.getPassword() != null) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }

    @Override
    protected void beforeUpdate(UserRequest dto, User entity, User oldData) {
        super.beforeUpdate(dto, entity, oldData);

        // 1. Xử lý Password: Nếu Frontend có gửi password mới lên thì mã hóa,
        // ngược lại thì giữ nguyên password cũ trong DB.
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            entity.setPassword(oldData.getPassword());
        }

        // 2. Bảo mật Email: Không cho phép thay đổi email sau khi đã tạo.
        // Ép lại email cũ từ DB, đề phòng Frontend cố tình truyền email mới lên để đổi.
        entity.setEmail(oldData.getEmail());
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            throw new AppException(ErrorCode.UNAUTH);
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getSubject();

        return repository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTH));
    }
}