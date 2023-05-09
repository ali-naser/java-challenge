package jp.co.axa.apidemo.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jp.co.axa.apidemo.dto.AuthRequest;
import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.entities.User;
import jp.co.axa.apidemo.services.UserService;
import jp.co.axa.apidemo.util.HeaderUtil;
import jp.co.axa.apidemo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

/**
 * Controller to create new user and authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    private static final String ENTITY_NAME = "user";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * POST  /users : Create a new user.
     *
     * @param userDto the User to create
     * @return the ResponseEntity with status 201 (Created) and with body the new User, or with status 400 (Bad Request) if the userName or email already exist
     */
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            if (userService.findUserByName(userDto.getUserName()).isPresent()) {
                return ResponseEntity.badRequest()
                        .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, "userExist", "Username already in use")).body(null);
            }
            if (userService.findUserByEmail(userDto.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, "emailExist", "Email already in use")).body(null);
            }
            User result = userService.createUser(userDto);
            return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationHeaders(ENTITY_NAME, result.getUserName()))
                    .body(result);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, ex.getMessage(), "Cannot create user")).body(null);
        }
    }

    /**
     * POST  /authenticate : authenticate user.
     *
     * @param authRequest the user login info
     * @return the ResponseEntity with status 200 and with body JWTToken, or with status 400 (Bad Request) if login failed
     */
    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtil.generateToken(authentication.getName());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + jwt);
            return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureHeaders(ENTITY_NAME, ex.getMessage(), "Cannot login")).body(null);
        }
    }

    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

}
