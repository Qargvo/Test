package com.test.dataBase;

import com.test.model.Message;
import com.test.model.Token;
import com.test.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class Repository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Token token;

    public void newUser(User user) {
        try {
            String sql = """
                    INSERT INTO users(login, password) 
                    values(:login, :password);""";
            jdbcTemplate.update(sql, Map.of("login", user.login(), "password", user.password()));
        } catch (Exception e) {
            log.error("Insert profile error", e);
        }
    }

    public boolean checkUser(@NotNull User user) {
        try {
            String sql = """
                    SELECT count(*) as c 
                    FROM users where login = :login and password = :password 
                    limit 1;""";
            var count = jdbcTemplate.queryForObject(sql, Map.of("login", user.login(), "password", user.password()), Integer.class);
            return count == 1;
        } catch (Exception e) {
            log.error("Check profile error", e, user);
        }
        return false;
    }

    public Optional<Integer> getId(String login) {
        try {
            final String sql = """
                    select id from users 
                    where login = :login 
                    limit 1;""";
            Integer id = jdbcTemplate.queryForObject(sql, Map.of("login", login), Integer.class);
            return Optional.ofNullable(id);
        } catch (Exception e) {
            log.error("Get id error", e);
        }
        return Optional.empty();
    }

    public String newToken(User user) {
        if (this.checkUser(user)) {
            try {
                final String SQL_HAVE_TOKEN = """
                        Select count(*)
                        from tokens left join users on tokens.loginId = users.id
                        where users.login = :login limit 1""";
                Optional<Integer> id = getId(user.login());
                if (id.isEmpty()) return null;
                String tkn = token.generate();
                var count = jdbcTemplate.queryForObject(SQL_HAVE_TOKEN, Map.of("login", user.login()), Integer.class);
                if (count == 1) {
                    final String SQL_UPDATE = """
                            UPDATE tokens 
                            set token = :token, creationdate = :creationdate 
                            where loginId = :loginId""";
                    jdbcTemplate.update(SQL_UPDATE, Map.of("token", tkn, "creationdate", LocalDate.now().plusDays(1), "loginId", id.get()));
                } else {
                    final String SQL_INSERT = """
                            INSERT into tokens (token, loginId, creationdate)
                            values(:token, :loginId, :creationdate)""";
                    jdbcTemplate.update(SQL_INSERT, Map.of("token", tkn, "loginId", id.get(), "creationdate", LocalDate.now().plusDays(1)));
                }


                return tkn;
            } catch (Exception e) {
                log.error("New token error", e);
            }
        }
        return null;
    }

    public boolean checkToken(String token) {
        try {
            final String SQL = """
                    Select count(*) 
                    from tokens 
                    where :nowDate < creationdate and token = :token;""";
            var count = jdbcTemplate.queryForObject(SQL, Map.of("nowDate", LocalDate.now(), "token", token), Integer.class);
            if (count == 1) return true;
        } catch (Exception e) {
            log.error("Check token error", e);
        }
        return false;
    }

    public boolean newMessage(String token, Message message) {
        try {
            if (checkToken(token)) {
                final String SQL = """
                        insert into messages(loginId, message) 
                        values(:loginId,:message)""";
                Optional<Integer> id = getId(message.login());
                if (id.isEmpty()) return false;
                jdbcTemplate.update(SQL, Map.of("loginId", id.get(), "message", message.message()));
                return true;
            }
        } catch (Exception e) {
            log.error("New message error", e);
        }
        return false;
    }

    public List<Map<String, Object>> getHistory(String token, Message message) {
        try {
            if (checkToken(token)) {
                Integer size = Integer.valueOf(Arrays.stream(message.message().split(" ")).toList().get(1));
                Optional<Integer> id = getId(message.login());
                if (id.isEmpty()) return Collections.emptyList();
                final String sql = """
                        Select message 
                        from messages 
                        where loginId = :id 
                        order by id 
                        limit :size""";
                return jdbcTemplate.queryForList(sql, Map.of("id", id.get(), "size", size));
            }
        } catch (Exception e) {
            log.error("get history error", e);
        }
        return Collections.emptyList();
    }
}
